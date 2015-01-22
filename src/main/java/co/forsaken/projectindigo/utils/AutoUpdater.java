package co.forsaken.projectindigo.utils;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import co.forsaken.projectindigo.IndigoLauncher;
import co.forsaken.projectindigo.Main;
import co.forsaken.projectindigo.log.LogManager;
import co.forsaken.projectindigo.utils.Utils.OS;

public class AutoUpdater {

  public static void main(String[] args) {
    if (shouldUpdate()) {
      if (downloadNew()) {
        LogManager.info("Download done.");
        relaunch();
        System.exit(0);
        return;
      }
    }
    new IndigoLauncher((args.length == 1 ? args[0] : ""));
  }

  private static void relaunch() {
    String javaDir = System.getProperty("java.home") + "/bin/java";
    String classpath = System.getProperty("java.class.path");
    String className = Main.class.getCanonicalName();

    try {
      new ProcessBuilder(javaDir, "-cp", classpath, className).start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static int parseVersion(String version) {
    Pattern pat = Pattern.compile("\\.");
    Matcher match = pat.matcher(version);
    if (match.find()) {
      version = match.replaceAll("");
    }
    return Integer.parseInt(version);
  }

  public static boolean shouldUpdate() {
    try {
      URL url = new URL("https://raw.github.com/ForsakenDev/ProjectIndigo/master/pom.xml");

      URLConnection connection = url.openConnection();

      Document doc = parseXML(connection.getInputStream());
      NodeList descNodes = doc.getElementsByTagName("project");

      String version = (String) ((Element) descNodes.item(0)).getElementsByTagName("version").item(0).getChildNodes().item(0).getNodeValue();

      InputStream pomStream = AutoUpdater.class.getClassLoader().getResourceAsStream("META-INF/maven/co.forsaken/projectindigo/pom.xml");
      if (pomStream != null) {
        doc = parseXML(pomStream);
        descNodes = doc.getElementsByTagName("project");

        String clientVersion = (String) ((Element) descNodes.item(0)).getElementsByTagName("version").item(0).getChildNodes().item(0).getNodeValue();
        return parseVersion(version) > parseVersion(clientVersion);
      }

    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    }
    return false;
  }

  private static Document parseXML(InputStream stream) throws SAXException, IOException, ParserConfigurationException {
    DocumentBuilderFactory objDocumentBuilderFactory = null;
    DocumentBuilder objDocumentBuilder = null;
    objDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
    objDocumentBuilder = objDocumentBuilderFactory.newDocumentBuilder();
    return (Document) objDocumentBuilder.parse(stream);
  }

  private static boolean downloadNew() {
    try {
      String jarLocation = AutoUpdater.class.getProtectionDomain().getCodeSource().getLocation().getPath();
      URL url = new URL("http://indigo.forsaken.co/downloads/jar/ProjectIndigo.jar");
      if (Utils.getCurrentOS() == OS.WINDOWS && jarLocation.contains(".exe")) {
        url = new URL("http://indigo.forsaken.co/downloads/exe/ProjectIndigo.zip");
        jarLocation = jarLocation.substring(0, jarLocation.indexOf(".exe") + 4);
      } else if (Utils.getCurrentOS() == OS.MACOSX && jarLocation.contains(".app")) {
        url = new URL("http://indigo.forsaken.co/downloads/app/ProjectIndigo.zip");
        jarLocation = jarLocation.substring(0, jarLocation.indexOf(".app") + 4);
      }
      HttpURLConnection connection = (HttpURLConnection) url.openConnection(); 
      connection.addRequestProperty("User-Agent", "Mozilla/4.76"); 
      File file = new File(jarLocation);
      FileUtils.deleteDirectory(file);

      if (Utils.getCurrentOS() == OS.WINDOWS && jarLocation.contains(".exe")) {
        file = new File(jarLocation.replaceAll(".exe", ".zip").replaceAll("%20", " "));
      } else if (Utils.getCurrentOS() == OS.MACOSX && jarLocation.contains(".app")) {
        file = new File(jarLocation.replaceAll(".app", ".zip").replaceAll("%20", " "));
      }
      LogManager.info("Update detected. Attempting to download.");
      JOptionPane.showMessageDialog(null, "An update to the launcher was found! Attempting to download");
      InputStream input = connection.getInputStream();
      saveStreamToFileAndUnZip(input, file);
      input.close();
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return true;
  }

  public static void goToPage() throws IOException {
    JOptionPane.showMessageDialog(null, "An update to the launcher was found! You need to download it to use this launcher");
    if (Desktop.isDesktopSupported()) {
      try {
        Desktop.getDesktop().browse(new URI("http://indigo.forsaken.co/"));
      } catch (URISyntaxException e) {
        e.printStackTrace();
      }
    }
    System.exit(0);
  }

  public static void saveStreamToFileAndUnZip(InputStream input, File file) throws IOException {
    OutputStream output = new FileOutputStream(file);
    byte[] buffer = new byte[1024];
    int read;
    while ((read = input.read(buffer)) > 0) {
      output.write(buffer, 0, read);
    }
    output.close();
    if (file.getAbsolutePath().contains(".zip")) {
      File parentDir = file.getParentFile();
      ZipInputStream zipIn = null;
      try {
        input = new FileInputStream(file);
        zipIn = new ZipInputStream(input);
        ZipEntry currentEntry = zipIn.getNextEntry();
        while (currentEntry != null) {
          if (currentEntry.getName().contains("META-INF") || currentEntry.getName().contains("__MACOSX") || currentEntry.getName().contains(".DS_Store")) {
            currentEntry = zipIn.getNextEntry();
            continue;
          }
          if (currentEntry.isDirectory()) {
            File tmp = new File(parentDir, currentEntry.getName());
            if (!tmp.exists()) {
              tmp.mkdir();
            }
            currentEntry = zipIn.getNextEntry();
            continue;
          }
          FileOutputStream outStream = new FileOutputStream(new File(parentDir, currentEntry.getName()));
          int readLen;
          buffer = new byte[1024];
          while ((readLen = zipIn.read(buffer, 0, buffer.length)) > 0) {
            outStream.write(buffer, 0, readLen);
          }
          outStream.close();
          currentEntry = zipIn.getNextEntry();
        }
      } catch (IOException e) {
        LogManager.error(e.getMessage());
      } finally {
        try {
          zipIn.close();
          input.close();
        } catch (IOException e) {
          LogManager.error(e.getMessage());
        }
      }
      file.delete();
    }
  }
}
