package co.forsaken.projectindigo.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import co.forsaken.projectindigo.gui.ProgressPanel;
import co.forsaken.projectindigo.log.LogManager;
import co.forsaken.projectindigo.utils.FileUtils;

public class FileDownloader {

  private Server              server;
  protected Thread            _downloadThread;
  protected String            _rawDownloadURL;
  protected String            _baseDir  = "";
  protected boolean           _extract  = false;
  protected File              _downloadedFile;
  protected int               _fileSize = -1;
  protected URL               _downloadURL;
  protected String            overrideName;
  protected boolean           addToOrder;
  protected HttpURLConnection connection;

  public FileDownloader(Server server, String downloadURL, String dir, String filename, boolean _addToOrder) {
    this(server, downloadURL, dir, _addToOrder);
    overrideName = filename;
  }

  public FileDownloader(Server server, String downloadURL, String dir, boolean extract, boolean _addToOrder) {
    this(server, downloadURL, dir, _addToOrder);
    _extract = extract;
  }

  public FileDownloader(Server server, String downloadURL, String dir, boolean _addToOrder) {
    _rawDownloadURL = downloadURL;
    _baseDir = dir;
    addToOrder = _addToOrder;
    if (addToOrder) {
      try {
        server.addToOrder(getFilename());
      } catch (MalformedURLException e) {
        e.printStackTrace();
      }
    }
  }

  public String getRawDownloadURL() {
    return _rawDownloadURL;
  }

  public boolean shouldExtract() {
    return _extract;
  }

  public boolean shouldDownload() {
    return _rawDownloadURL != null && !_rawDownloadURL.isEmpty();
  }

  private HttpURLConnection getConnection() {
    if (this.connection == null) {
      LogManager.debug("Opening connection to " + getDownloadURL(), 3);
      try {
        connection = (HttpURLConnection) getDownloadURL().openConnection();
        connection.setUseCaches(false);
        connection.setDefaultUseCaches(false);
        connection.setRequestProperty("Accept-Encoding", "gzip");
        connection.setRequestProperty("User-Agent", "Mozilla/4.76");
        connection.setRequestProperty("Cache-Control", "no-store,max-age=0,no-cache");
        connection.setRequestProperty("Expires", "0");
        connection.setRequestProperty("Pragma", "no-cache");
        connection.connect();

        if (this.connection.getResponseCode() / 100 != 2) { throw new IOException(getDownloadURL() + " returned response code " + this.connection.getResponseCode()
            + (this.connection.getResponseMessage() != null ? " with message of " + this.connection.getResponseMessage() : "")); }
        LogManager.debug("Connection opened to " + getDownloadURL(), 3);
      } catch (IOException e) {
        LogManager.debug("Exception when opening connection to " + getDownloadURL(), 3);
      }
    }
    return this.connection;
  }

  public Runnable loadFileSize(final Server server, final ProgressPanel panel) {
    return new Runnable() {
      public void run() {
        if (_rawDownloadURL != null && !_rawDownloadURL.isEmpty()) {
          try {
            _fileSize = getFilesize();
            server.addValidatedFile(panel, getFilename(), _fileSize, _extract);
          } catch (IOException e) {
            LogManager.error(e.getMessage());
          }
        }
      }
    };
  }

  public int getFilesize() {
    if (_fileSize == -1) {
      int size = getConnection().getContentLength();
      if (size == -1) {
        _fileSize = 0;
      } else {
        _fileSize = size;
      }
    }
    return _fileSize;
  }

  protected String getFilename() throws MalformedURLException {
    if (_rawDownloadURL != null && !_rawDownloadURL.isEmpty()) {
      if (_rawDownloadURL.toLowerCase().contains("forge-")) { return "MinecraftForge.zip"; }
      if (overrideName != null && !overrideName.isEmpty()) { return overrideName; }
      String string = getDownloadURL().getFile();
      if (string.contains("?")) {
        string = string.substring(0, string.indexOf('?'));
      }
      return URLDecoder.decode(string.substring(string.lastIndexOf('/') + 1));
    }
    return "";
  }

  protected final URL getDownloadURL() {
    if (_rawDownloadURL != null && !_rawDownloadURL.isEmpty()) {
      if (_downloadURL == null) {
        try {
          _downloadURL = new URL(_rawDownloadURL);
        } catch (MalformedURLException e) {
          e.printStackTrace();
        }
      }
      return _downloadURL;
    }
    return null;
  }

  public Runnable download(final Server server, final ProgressPanel panel) throws IOException {
    return new Runnable() {
      public void run() {
        if (_rawDownloadURL != null && !_rawDownloadURL.isEmpty()) {
          downloadFile(server, panel);
        }
      }
    };
  }

  protected String getFileExtension() {
    try {
      String[] filename = getFilename().split(".");
      return filename[filename.length - 1];
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    return "jar";
  }

  private boolean downloadFile(Server server, ProgressPanel panel) {
    HttpURLConnection dlConnection = null;
    int currentDLSize = 0;
    String jarFileName = null;
    try {
      jarFileName = getFilename();
    } catch (MalformedURLException e1) {
      e1.printStackTrace();
    }
    File downloadedFile = new File(_baseDir, jarFileName);
    try {
      LogManager.info("Downloading " + jarFileName + " to \"" + _baseDir + "\"");
      if (!new File(_baseDir).exists()) {
        new File(_baseDir).mkdir();
      }
      dlConnection = getConnection();
      if (downloadedFile.exists()) {
        FileUtils.deleteDirectory(downloadedFile);
      }
      InputStream dlStream = dlConnection.getInputStream();
      FileOutputStream outStream = new FileOutputStream(downloadedFile);
      byte[] buffer = new byte[24000];
      int readLen;
      while ((readLen = dlStream.read(buffer, 0, buffer.length)) != -1) {
        outStream.write(buffer, 0, readLen);
        currentDLSize += readLen;
        server.addDownloadSize(panel, "Downloading", jarFileName, readLen);
      }
      dlStream.close();
      outStream.close();
    } catch (IOException ex) {
      LogManager.error("There was an exception while downloading " + jarFileName + "");
      LogManager.error("Exception: " + ex.getMessage());
      StackTraceElement[] arrOfSTE;
      int max = (arrOfSTE = ex.getStackTrace()).length;
      for (int i = 0; i < max; i++) {
        StackTraceElement trace = arrOfSTE[i];
        LogManager.error(trace.toString());
      }
      return false;
    }
    if (dlConnection != null && dlConnection instanceof HttpURLConnection && (currentDLSize == _fileSize || _fileSize <= 0)) {
      LogManager.info("Finished downloading " + jarFileName);
      _downloadedFile = downloadedFile;
      if (shouldExtract()) {
        extract(server, panel);
      }
      server.addLoadedDownload();
      return true;
    }
    LogManager.info("Could not finish downloading " + jarFileName);
    return false;
  }

  public void extract(Server server, ProgressPanel panel) {
    if (_downloadedFile == null) { return; }
    LogManager.info("Extracting " + _downloadedFile.getName() + " to \"" + _baseDir + "\"");

    if (_downloadedFile.getName().contains("minecraft.jar")) {
      extractJar(server, panel, true);
    } else {
      extractZip(server, panel, true);
    }
    LogManager.info("Extracting " + _downloadedFile.getName() + " to \"" + _baseDir + "\"");

  }

  protected void extractZip(Server server, ProgressPanel panel, boolean overwrite) {
    ZipInputStream zipinputstream = null;
    try {
      byte[] buf = new byte[1024];
      zipinputstream = new ZipInputStream(new FileInputStream(_downloadedFile));
      ZipEntry zipentry = zipinputstream.getNextEntry();
      while (zipentry != null) {
        String entryName = zipentry.getName();
        if (entryName.startsWith("config/") || entryName.startsWith("minecraft/")) {
          entryName = entryName.substring(entryName.indexOf("/"));
        }
        int n;
        if (!zipentry.isDirectory() && !entryName.contains("META-INF") && !entryName.contains("__MACOSX") && !entryName.contains(".DS_Store") && !entryName.equalsIgnoreCase("minecraft") && !entryName.equalsIgnoreCase(".minecraft")
            && !entryName.equalsIgnoreCase("instMods")) {
          new File(_baseDir + File.separator + entryName).getParentFile().mkdirs();
          FileOutputStream fileoutputstream = new FileOutputStream(_baseDir + File.separator + entryName);
          while ((n = zipinputstream.read(buf, 0, 1024)) > -1) {
            fileoutputstream.write(buf, 0, n);
            server.addDownloadSize(panel, "Extracting", getFilename(), n);
          }
          fileoutputstream.close();
        }
        zipinputstream.closeEntry();
        zipentry = zipinputstream.getNextEntry();
      }
    } catch (Exception e) {
      LogManager.error("Error while extracting zip");
    } finally {
      try {
        zipinputstream.close();
      } catch (IOException e) {}
    }
    LogManager.info("Finished Extracting " + _downloadedFile.getName() + " to \"" + _baseDir + "\"");
    _downloadedFile.delete();
  }

  protected boolean extractJar(Server server, ProgressPanel panel, boolean overwrite) {
    File tmpFile = new File(_downloadedFile.getAbsolutePath() + ".tmp");
    try {
      JarInputStream input = new JarInputStream(new FileInputStream(_downloadedFile));
      JarOutputStream output = new JarOutputStream(new FileOutputStream(tmpFile));
      JarEntry entry;

      while ((entry = input.getNextJarEntry()) != null) {
        if (entry.getName().contains("META-INF") || entry.getName().contains("__MACOSX") || entry.getName().contains(".DS_Store")) {
          continue;
        }
        output.putNextEntry(entry);
        byte buffer[] = new byte[1024];
        int readLen;
        while ((readLen = input.read(buffer, 0, 1024)) != -1) {
          output.write(buffer, 0, readLen);
          server.addDownloadSize(panel, "Extracting", getFilename(), readLen);
        }
        output.closeEntry();
      }

      input.close();
      output.close();

      if (!_downloadedFile.delete()) {
        LogManager.error("Failed to delete " + _downloadedFile.getName());
        return false;
      }
      tmpFile.renameTo(_downloadedFile);
    } catch (FileNotFoundException e) {
      LogManager.error(e.getMessage());
      return false;
    } catch (IOException e) {
      LogManager.error(e.getMessage());
      return false;
    }
    LogManager.info("Finished Extracting " + _downloadedFile.getName() + " to \"" + _baseDir + "\"");
    return true;
  }

  public boolean delete() throws MalformedURLException {
    return FileUtils.deleteDirectory(new File(_baseDir, getFilename()));
  }
}
