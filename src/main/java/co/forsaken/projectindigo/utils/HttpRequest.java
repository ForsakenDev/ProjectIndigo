package co.forsaken.projectindigo.utils;

import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import lombok.extern.java.Log;

import org.codehaus.jackson.map.ObjectMapper;

import co.forsaken.projectindigo.util.concurrent.WorkUnit;

public class HttpRequest extends WorkUnit implements Closeable {

  private static final int          READ_TIMEOUT     = 1000 * 60 * 10;
  private static final int          READ_BUFFER_SIZE = 1024 * 8;

  private final ObjectMapper        mapper           = new ObjectMapper();
  private final Map<String, String> headers          = new HashMap<String, String>();
  private final String              method;
  private final URL                 url;
  private String                    contentType;
  private byte[]                    body;
  private HttpURLConnection         conn;
  private InputStream               inputStream;

  private long                      contentLength    = -1;
  private long                      readBytes        = 0;

  /**
   * Create a new HTTP request.
   *
   * @param method
   *          the method
   * @param url
   *          the URL
   */
  private HttpRequest(String method, URL url) {
    this.method = method;
    this.url = url;
  }

  /**
   * Set the content body to a JSON object with the content type of
   * "application/json".
   *
   * @param object
   *          the object to serialize as JSON
   * @return this object
   * @throws IOException
   *           if the object can't be mapped
   */
  public HttpRequest bodyJson(Object object) throws IOException {
    contentType = "application/json";
    body = mapper.writeValueAsBytes(object);
    return this;
  }

  /**
   * Submit form data.
   *
   * @param form
   *          the form
   * @return this object
   */
  public HttpRequest bodyForm(Form form) {
    contentType = "application/x-www-form-urlencoded";
    body = form.toString().getBytes();
    return this;
  }

  /**
   * Add a header.
   *
   * @param key
   *          the header key
   * @param value
   *          the header value
   * @return this object
   */
  public HttpRequest header(String key, String value) {
    headers.put(key, value);
    return this;
  }

  /**
   * Execute the request.
   *
   * After execution, {@link #close()} should be called.
   *
   * @return this object
   * @throws IOException
   *           on I/O error
   */
  public HttpRequest execute() throws IOException {
    boolean successful = false;

    try {
      if (conn != null) { throw new IllegalArgumentException("Connection already executed"); }

      conn = (HttpURLConnection) reformat(url).openConnection();

      if (body != null) {
        conn.setRequestProperty("Content-Type", contentType);
        conn.setRequestProperty("Content-Length", Integer.toString(body.length));
        conn.setDoInput(true);
      }

      for (Map.Entry<String, String> entry : headers.entrySet()) {
        conn.setRequestProperty(entry.getKey(), entry.getValue());
      }

      conn.setRequestMethod(method);
      conn.setUseCaches(false);
      conn.setDoOutput(true);
      conn.setReadTimeout(READ_TIMEOUT);

      conn.connect();

      if (body != null) {
        DataOutputStream out = new DataOutputStream(conn.getOutputStream());
        out.write(body);
        out.flush();
        out.close();
      }

      inputStream = conn.getResponseCode() == HttpURLConnection.HTTP_OK ? conn.getInputStream() : conn.getErrorStream();

      successful = true;
    } finally {
      if (!successful) {
        close();
      }
    }

    return this;
  }

  /**
   * Require that the response code is one of the given response codes.
   *
   * @param codes
   *          a list of codes
   * @return this object
   * @throws IOException
   *           if there is an I/O error or the response code is not expected
   */
  public HttpRequest expectResponseCode(int... codes) throws IOException {
    int responseCode = getResponseCode();

    for (int code : codes) {
      if (code == responseCode) { return this; }
    }

    close();
    throw new IOException("Did not get expected response code, got " + responseCode);
  }

  /**
   * Get the response code.
   *
   * @return the response code
   * @throws IOException
   *           on I/O error
   */
  public int getResponseCode() throws IOException {
    if (conn == null) { throw new IllegalArgumentException("No connection has been made"); }

    return conn.getResponseCode();
  }

  /**
   * Get the input stream.
   *
   * @return the input stream
   */
  public InputStream getInputStream() {
    return inputStream;
  }

  /**
   * Buffer the returned response.
   *
   * @return the buffered response
   * @throws IOException
   *           on I/O error
   * @throws InterruptedException
   *           on interruption
   */
  public BufferedResponse returnContent() throws IOException, InterruptedException {
    if (inputStream == null) { throw new IllegalArgumentException("No input stream available"); }

    try {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      int b = 0;
      while ((b = inputStream.read()) != -1) {
        checkInterrupted();
        bos.write(b);
      }
      return new BufferedResponse(bos.toByteArray());
    } finally {
      close();
    }
  }

  /**
   * Save the result to a file.
   *
   * @param file
   *          the file
   * @return this object
   * @throws IOException
   *           on I/O error
   * @throws InterruptedException
   *           on interruption
   */
  public HttpRequest saveContent(File file) throws IOException, InterruptedException {
    FileOutputStream fos = null;
    BufferedOutputStream bos = null;

    try {
      fos = new FileOutputStream(file);
      bos = new BufferedOutputStream(fos);

      saveContent(bos);
    } finally {
      closeQuietly(bos);
      closeQuietly(fos);
    }

    return this;
  }

  /**
   * Save the result to an output stream.
   *
   * @param out
   *          the output stream
   * @return this object
   * @throws IOException
   *           on I/O error
   * @throws InterruptedException
   *           on interruption
   */
  public HttpRequest saveContent(OutputStream out) throws IOException, InterruptedException {
    BufferedInputStream bis;

    try {
      String field = conn.getHeaderField("Content-Length");
      if (field != null) {
        long len = Long.parseLong(field);
        if (len >= 0) { // Let's just not deal with really big numbers
          contentLength = len;
        }
      }
    } catch (NumberFormatException e) {}

    try {
      bis = new BufferedInputStream(inputStream);

      byte[] data = new byte[READ_BUFFER_SIZE];
      int len = 0;
      while ((len = bis.read(data, 0, READ_BUFFER_SIZE)) >= 0) {
        out.write(data, 0, len);
        readBytes += len;
        checkInterrupted();
      }
    } finally {
      close();
    }

    return this;
  }

  public void updateProgress() {
    double progress = -1;

    if (contentLength >= 0) {
      progress = readBytes / (double) contentLength;
    }

    push(progress, url.toString());
  }

  public void close() throws IOException {
    if (conn != null) conn.disconnect();
  }

  public static HttpRequest get(URL url) {
    return request("GET", url);
  }

  public static HttpRequest post(URL url) {
    return request("POST", url);
  }

  public static HttpRequest request(String method, URL url) {
    return new HttpRequest(method, url);
  }

  public static URL url(String url) {
    try {
      return new URL(url);
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  private static URL reformat(URL existing) {
    try {
      URL url = new URL(existing.toString());
      URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
      url = uri.toURL();
      return url;
    } catch (MalformedURLException e) {
      return existing;
    } catch (URISyntaxException e) {
      return existing;
    }
  }

  public final static class Form {
    public final List<String> elements = new ArrayList<String>();

    private Form() {}

    public Form add(String key, String value) {
      try {
        elements.add(URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8"));
        return this;
      } catch (UnsupportedEncodingException e) {
        throw new RuntimeException(e);
      }
    }

    @Override public String toString() {
      StringBuilder builder = new StringBuilder();
      boolean first = true;
      for (String element : elements) {
        if (first) {
          first = false;
        } else {
          builder.append("&");
        }
        builder.append(element);
      }
      return builder.toString();
    }

    public static Form form() {
      return new Form();
    }
  }

  public class BufferedResponse {
    private final byte[] data;

    private BufferedResponse(byte[] data) {
      this.data = data;
    }

    public byte[] asBytes() {
      return data;
    }

    public String asString(String encoding) throws IOException {
      return new String(data, encoding);
    }

    public <T> T asJson(Class<T> cls) throws IOException {
      return mapper.readValue(asString("UTF-8"), cls);
    }

    public <T> T asXml(Class<T> cls) throws IOException {
      try {
        JAXBContext context = JAXBContext.newInstance(cls);
        Unmarshaller um = context.createUnmarshaller();
        return (T) um.unmarshal(new ByteArrayInputStream(data));
      } catch (JAXBException e) {
        throw new IOException(e);
      }
    }

    public BufferedResponse saveContent(File file) throws IOException, InterruptedException {
      FileOutputStream fos = null;
      BufferedOutputStream bos = null;

      file.getParentFile().mkdirs();

      try {
        fos = new FileOutputStream(file);
        bos = new BufferedOutputStream(fos);

        saveContent(bos);
      } finally {
        closeQuietly(bos);
        closeQuietly(fos);
      }

      return this;
    }

    public BufferedResponse saveContent(OutputStream out) throws IOException, InterruptedException {
      out.write(data);

      return this;
    }
  }

  public static void checkInterrupted() throws InterruptedException {
    if (Thread.interrupted()) { throw new InterruptedException(); }
  }

}