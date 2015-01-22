package co.forsaken.projectindigo.utils;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import co.forsaken.projectindigo.IndigoLauncher;
import co.forsaken.projectindigo.gui.components.Label;
import co.forsaken.projectindigo.log.LogEvent.LogType;
import co.forsaken.projectindigo.log.LogManager;

public class Utils {

  public static enum OS {
    WINDOWS, UNIX, MACOSX, OTHER,
  }

  private static Logger logger = Logger.getLogger("launcher");

  public static String getDefInstallPath() {
    try {
      CodeSource codeSource = Utils.class.getProtectionDomain().getCodeSource();
      File jarFile;
      jarFile = new File(codeSource.getLocation().toURI().getPath());
      return jarFile.getParentFile().getPath();
    } catch (URISyntaxException e) {
      logger.log(Level.WARNING, e.getMessage());
    }
    logger.log(Level.WARNING, "Failed to get path for current directory - falling back to user's home directory.");
    return System.getProperty("user.dir") + "//" + getTitle() + "_install";
  }

  private static String getTitle() {
    return IndigoLauncher.TITLE.toLowerCase().replaceAll(" ", "_");
  }

  public static Object[] prepareMessageForMinecraftLog(String text) {
    LogType type = null; // The log message type
    String message = null; // The log message

    if (text.contains("[INFO] [STDERR]")) {
      message = text.substring(text.indexOf("[INFO] [STDERR]"));
      type = LogType.WARN;
    } else if (text.contains("[INFO]")) {
      message = text.substring(text.indexOf("[INFO]"));
      if (message.contains("CONFLICT")) {
        type = LogType.ERROR;
      } else if (message.contains("overwriting existing item")) {
        type = LogType.WARN;
      } else {
        type = LogType.INFO;
      }
    } else if (text.contains("[WARNING]")) {
      message = text.substring(text.indexOf("[WARNING]"));
      type = LogType.WARN;
    } else if (text.contains("WARNING:")) {
      message = text.substring(text.indexOf("WARNING:"));
      type = LogType.WARN;
    } else if (text.contains("INFO:")) {
      message = text.substring(text.indexOf("INFO:"));
      type = LogType.INFO;
    } else if (text.contains("Exception")) {
      message = text;
      type = LogType.ERROR;
    } else if (text.contains("[SEVERE]")) {
      message = text.substring(text.indexOf("[SEVERE]"));
      type = LogType.ERROR;
    } else if (text.contains("[Sound Library Loader/ERROR]")) {
      message = text.substring(text.indexOf("[Sound Library Loader/ERROR]"));
      type = LogType.ERROR;
    } else if (text.contains("[Sound Library Loader/WARN]")) {
      message = text.substring(text.indexOf("[Sound Library Loader/WARN]"));
      type = LogType.WARN;
    } else if (text.contains("[Sound Library Loader/INFO]")) {
      message = text.substring(text.indexOf("[Sound Library Loader/INFO]"));
      type = LogType.INFO;
    } else if (text.contains("[MCO Availability Checker #1/ERROR]")) {
      message = text.substring(text.indexOf("[MCO Availability Checker #1/ERROR]"));
      type = LogType.ERROR;
    } else if (text.contains("[MCO Availability Checker #1/WARN]")) {
      message = text.substring(text.indexOf("[MCO Availability Checker #1/WARN]"));
      type = LogType.WARN;
    } else if (text.contains("[MCO Availability Checker #1/INFO]")) {
      message = text.substring(text.indexOf("[MCO Availability Checker #1/INFO]"));
      type = LogType.INFO;
    } else if (text.contains("[Client thread/ERROR]")) {
      message = text.substring(text.indexOf("[Client thread/ERROR]"));
      type = LogType.ERROR;
    } else if (text.contains("[Client thread/WARN]")) {
      message = text.substring(text.indexOf("[Client thread/WARN]"));
      type = LogType.WARN;
    } else if (text.contains("[Client thread/INFO]")) {
      message = text.substring(text.indexOf("[Client thread/INFO]"));
      type = LogType.INFO;
    } else if (text.contains("[Server thread/ERROR]")) {
      message = text.substring(text.indexOf("[Server thread/ERROR]"));
      type = LogType.ERROR;
    } else if (text.contains("[Server thread/WARN]")) {
      message = text.substring(text.indexOf("[Server thread/WARN]"));
      type = LogType.WARN;
    } else if (text.contains("[Server thread/INFO]")) {
      message = text.substring(text.indexOf("[Server thread/INFO]"));
      type = LogType.INFO;
    } else if (text.contains("[main/ERROR]")) {
      message = text.substring(text.indexOf("[main/ERROR]"));
      type = LogType.ERROR;
    } else if (text.contains("[main/WARN]")) {
      message = text.substring(text.indexOf("[main/WARN]"));
      type = LogType.WARN;
    } else if (text.contains("[main/INFO]")) {
      message = text.substring(text.indexOf("[main/INFO]"));
      type = LogType.INFO;
    } else {
      message = text;
      type = LogType.INFO;
    }

    return new Object[] { type, message };
  }

  public static String getSHA1(File file) {
    if (!file.exists()) {
      LogManager.error("Cannot get SHA-1 hash of " + file.getAbsolutePath() + " as it doesn't exist");
      return "0"; // File doesn't exists so MD5 is nothing
    }
    StringBuffer sb = null;
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-1");
      FileInputStream fis = new FileInputStream(file);

      byte[] dataBytes = new byte[1024];

      int nread = 0;
      while ((nread = fis.read(dataBytes)) != -1) {
        md.update(dataBytes, 0, nread);
      }

      byte[] mdbytes = md.digest();

      sb = new StringBuffer();
      for (int i = 0; i < mdbytes.length; i++) {
        sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
      }

      if (fis != null) {
        fis.close();
      }
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return sb.toString();
  }

  public static String getDynamicStorageLocation() {
    switch (getCurrentOS()) {
      case WINDOWS:
        return System.getenv("APPDATA") + "/" + getTitle() + "/";
      case MACOSX:
        return System.getProperty("user.home") + "/Library/Application Support/" + getTitle() + "/";
      case UNIX:
        return System.getProperty("user.home") + "/." + getTitle() + "/";
      default:
        return getDefInstallPath() + "/temp/";
    }
  }

  public static String getJavaDelimiter() {
    switch (getCurrentOS()) {
      case WINDOWS:
        return ";";
      case UNIX:
        return ":";
      case MACOSX:
        return ":";
      default:
        return ";";
    }
  }

  public static int getMaximumWindowWidth() {
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Dimension dim = toolkit.getScreenSize();
    return dim.width;
  }

  public static int getMaximumWindowHeight() {
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Dimension dim = toolkit.getScreenSize();
    return dim.height;
  }

  public static OS getCurrentOS() {
    String osString = System.getProperty("os.name").toLowerCase();
    if (osString.contains("win")) {
      return OS.WINDOWS;
    } else if (osString.contains("nix") || osString.contains("nux")) {
      return OS.UNIX;
    } else if (osString.contains("mac")) {
      return OS.MACOSX;
    } else {
      return OS.OTHER;
    }
  }

  public static void removeMetaInf(String filePath) {
    File inputFile = new File(filePath, "minecraft.jar");
    File outputTmpFile = new File(filePath, "minecraft.jar.tmp");
    try {
      JarInputStream input = new JarInputStream(new FileInputStream(inputFile));
      JarOutputStream output = new JarOutputStream(new FileOutputStream(outputTmpFile));
      JarEntry entry;

      while ((entry = input.getNextJarEntry()) != null) {
        if (entry.getName().contains("META-INF")) {
          continue;
        }
        output.putNextEntry(entry);
        byte buffer[] = new byte[1024];
        int amo;
        while ((amo = input.read(buffer, 0, 1024)) != -1) {
          output.write(buffer, 0, amo);
        }
        output.closeEntry();
      }

      input.close();
      output.close();

      if (!inputFile.delete()) {
        logger.log(Level.SEVERE, "Failed to delete Minecraft.jar.");
        return;
      }
      outputTmpFile.renameTo(inputFile);
    } catch (FileNotFoundException e) {
      logger.log(Level.WARNING, e.getMessage());
    } catch (IOException e) {
      logger.log(Level.WARNING, e.getMessage());
    }
  }

  public static String readURL(String urlString) throws Exception {
    BufferedReader reader = null;
    try {
      URL url = new URL(urlString);
      reader = new BufferedReader(new InputStreamReader(url.openStream()));
      StringBuffer buffer = new StringBuffer();
      int read;
      char[] chars = new char[1024];
      while ((read = reader.read(chars)) != -1)
        buffer.append(chars, 0, read);

      return buffer.toString();
    } finally {
      if (reader != null) reader.close();
    }
  }

  public static BufferedImage loadCachedImage(String path) {
    try {
      return ImageIO.read(new File(path));
    } catch (Exception e) {}
    return null;
  }

  public static int[][] getDynamicTableCoords(int totalAreaWidth, int totalAreaHeight, int numData) {
    int numPerRow = 3;
    int[][] tableCoords = Utils.getTableCoords(numPerRow, totalAreaWidth, totalAreaHeight, numData);

    while (tableCoords[tableCoords.length - 1][1] + tableCoords[tableCoords.length - 1][3] > totalAreaHeight) {
      numPerRow++;
      tableCoords = Utils.getTableCoords(numPerRow, totalAreaWidth, totalAreaHeight, numData);
    }
    return tableCoords;
  }

  public static final int MAX_TILE_WIDTH = 250;

  private static int[][] getTableCoords(int numPerRow, int totalAreaWidth, int totalAreaHeight, int numData) {
    int[][] table = new int[numData][4];

    int tileWidth = (int) ((double) (totalAreaWidth / numPerRow) * 0.6D);
    if (tileWidth > MAX_TILE_WIDTH) {
      tileWidth = MAX_TILE_WIDTH;
    }
    int cellPaddingX = (totalAreaWidth - tileWidth) / numPerRow;
    int cellPaddingY = cellPaddingX / 3;
    int tileHeight = tileWidth;
    int numRows = numData / numPerRow;
    int modX = cellPaddingX / 5;
    for (int i = 0; i < numData; i++) {
      int row = (i / numPerRow);
      int col = (i % numPerRow);
      int numColOnCurrRow = (numRows == row ? numData % numPerRow : numPerRow);

      table[i] = new int[] { modX + (totalAreaWidth / 2) + (tileWidth * col) + (((cellPaddingX / 2) * col)) - (((tileWidth + (cellPaddingX / 2)) * numColOnCurrRow) / 2), (tileHeight * row) + ((cellPaddingY * row) / 2), tileWidth, tileHeight };
    }
    return table;
  }

  public static String getClassContainer(Class<?> c) {
    if (c == null) { throw new NullPointerException("The Class passed to this method may not be null"); }
    try {
      while (c.isMemberClass() || c.isAnonymousClass()) {
        c = c.getEnclosingClass(); // Get the actual enclosing file
      }
      if (c.getProtectionDomain().getCodeSource() == null) {
        // This is a proxy or other dynamically generated class, and has
        // no physical container,
        // so just return null.
        return null;
      }
      String packageRoot;
      try {
        // This is the full path to THIS file, but we need to get the
        // package root.
        String thisClass = c.getResource(c.getSimpleName() + ".class").toString();
        packageRoot = replaceLast(thisClass, Pattern.quote(c.getName().replaceAll("\\.", "/") + ".class"), "");
        if (packageRoot.endsWith("!/")) {
          packageRoot = replaceLast(packageRoot, "!/", "");
        }
      } catch (Exception e) {
        // Hmm, ok, try this then
        packageRoot = c.getProtectionDomain().getCodeSource().getLocation().toString();
      }
      packageRoot = URLDecoder.decode(packageRoot, "UTF-8");
      return packageRoot;
    } catch (Exception e) {
      throw new RuntimeException("While interrogating " + c.getName() + ", an unexpected exception was thrown.", e);
    }
  }

  public static String replaceLast(String text, String regex, String replacement) {
    return text.replaceFirst("(?s)" + regex + "(?!.*?" + regex + ")", replacement);
  }

  public static String getArch() {
    if (is64Bit()) {
      return "64";
    } else {
      return "32";
    }
  }

  public static boolean is64Bit() {
    return System.getProperty("sun.arch.data.model").contains("64");
  }

  public static String getRedirectedUrl(String url) throws MalformedURLException, IOException {
    HttpURLConnection connection;
    String finalUrl = url;
    do {
      connection = (HttpURLConnection) new URL(finalUrl).openConnection();
      connection.setInstanceFollowRedirects(false);
      connection.setUseCaches(false);
      connection.setRequestMethod("GET");
      connection.connect();
      int responseCode = connection.getResponseCode();
      if (responseCode >= 300 && responseCode < 400) {
        String redirectedUrl = connection.getHeaderField("Location");
        if (redirectedUrl == null) break;
        finalUrl = redirectedUrl;
      } else break;
    } while (connection.getResponseCode() != HttpURLConnection.HTTP_OK);
    connection.disconnect();
    return finalUrl.replaceAll(" ", "%20");
  }

  public static int getLabelWidth(Label label) {
    FontRenderContext frc = new FontRenderContext(label.getFont().getTransform(), true, true);
    return (int) (label.getFont().getStringBounds(label.getText(), frc).getWidth()) + ((1 * label.getText().length()) / 2);
  }
}
