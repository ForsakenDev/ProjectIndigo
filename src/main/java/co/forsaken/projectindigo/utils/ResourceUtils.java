package co.forsaken.projectindigo.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;

import co.forsaken.projectindigo.IndigoLauncher;

public class ResourceUtils {

  private static final String BASE_PATH     = "";
  private static final URL    SPLASH_SCREEN = ResourceUtils.class.getResource(BASE_PATH + "/images/splash_screen.png");
  private static final URL    BASE_CHAR     = ResourceUtils.class.getResource(BASE_PATH + "/images/char.png");
  private static final URL    BG            = ResourceUtils.class.getResource(BASE_PATH + "/images/bg.jpg");
  private static final URL    HEADER        = ResourceUtils.class.getResource(BASE_PATH + "/images/header.png");
  private static final URL    SETTINGS      = ResourceUtils.class.getResource(BASE_PATH + "/images/settings.png");
  private static final URL    FOLDER        = ResourceUtils.class.getResource(BASE_PATH + "/images/folder.png");
  private static final URL    ICON_MAC      = ResourceUtils.class.getResource(BASE_PATH + "/build/app/icon.icns");
  private static final URL    ICON_WIN      = ResourceUtils.class.getResource(BASE_PATH + "/build/exe/icon.ico");

  public static URL getResource(String name) {
    if (name.equalsIgnoreCase("splash_screen")) {
      return SPLASH_SCREEN;
    } else if (name.equalsIgnoreCase("base_char")) {
      return BASE_CHAR;
    } else if (name.equalsIgnoreCase("bg")) {
      return BG;
    } else if (name.equalsIgnoreCase("settings")) {
      return SETTINGS;
    } else if (name.equalsIgnoreCase("folder")) {
      return FOLDER;
    } else if (name.equalsIgnoreCase("header")) {
      return HEADER;
    } else if (name.equalsIgnoreCase("icon_file_mac")) {
      return ICON_MAC;
    } else if (name.equalsIgnoreCase("icon_file_win")) { return ICON_WIN; }
    return null;
  }

  public static InputStream getResourceAsStream(String name) {
    if (name.equalsIgnoreCase("minecraft_font")) {
      return getResourceAsStream(BASE_PATH + "/fonts/minecraft.ttf", "");
    } else if (name.equalsIgnoreCase("defaultServers")) {
      return getResourceAsStream(BASE_PATH + "/data/servers", "");
    } else if (name.equalsIgnoreCase("settings")) { return getResourceAsStream(BASE_PATH + "/data/settings", ""); }
    return null;
  }

  private static InputStream getResourceAsStream(String path, String t) {
    InputStream stream = IndigoLauncher.class.getResourceAsStream(path);
    String[] split = path.split("/");
    path = split[(split.length - 1)];
    if (stream == null) {
      File resource = new File(".\\src\\main\\resources\\" + path);
      if (resource.exists()) try {
        stream = new BufferedInputStream(new FileInputStream(resource));
      } catch (IOException ignore) {}
    }
    return stream;
  }

  public static BufferedImage getImage(String img) {
    try {
      return ImageIO.read(ResourceUtils.class.getResource(BASE_PATH + "/images/" + img));
    } catch (Exception ex) {
      ex.printStackTrace(System.err);
      return null;
    }
  }

  public static BufferedImage loadCachedImage(String name, String path, BufferedImage defaultImage) {
    if (!name.contains(".png")) {
      name += ".png";
    }
    BufferedImage image = Utils.loadCachedImage(DirectoryLocations.IMAGE_DIR_LOCATION + name);
    if (image == null) {
      try {
        URLConnection conn = new URL(path).openConnection();
        conn.setDoInput(true);
        conn.setDoOutput(false);
        System.setProperty("http.agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.162 Safari/535.19");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.162 Safari/535.19");
        HttpURLConnection.setFollowRedirects(true);
        conn.setUseCaches(false);
        ((HttpURLConnection) conn).setInstanceFollowRedirects(true);
        int response = ((HttpURLConnection) conn).getResponseCode();
        if (response == 200) {
          image = DrawingUtils.makeColorTransparent(ImageIO.read(conn.getInputStream()), Color.magenta);
          if ((image.getWidth() != defaultImage.getWidth()) || (image.getHeight() != defaultImage.getHeight())) {
            BufferedImage resized = new BufferedImage(defaultImage.getWidth(), defaultImage.getHeight(), image.getType());
            Graphics2D g = resized.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(image, 0, 0, defaultImage.getWidth(), defaultImage.getHeight(), 0, 0, image.getWidth(), image.getHeight(), null);
            g.dispose();
            image = resized;
          }
        }
        if (image != null) {
          ImageIO.write(image, "png", new File(DirectoryLocations.IMAGE_DIR_LOCATION, name));
          return image;
        }
        return defaultImage;
      } catch (Exception e) {
        return defaultImage;
      }
    } else {
      return image;
    }
  }
}
