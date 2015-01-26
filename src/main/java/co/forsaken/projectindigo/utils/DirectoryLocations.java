package co.forsaken.projectindigo.utils;

import java.io.File;

public enum DirectoryLocations {

  BACKEND_INSTALL_DIR(Utils.getDynamicStorageLocation()), BACKEND_DATA_DIR(BACKEND_INSTALL_DIR.format("data/")),
  BACKEND_LOG_DIR(BACKEND_INSTALL_DIR.format("logs/")), BACKEND_ASSET_DIR(BACKEND_INSTALL_DIR.format("assets/")), 
  INSTANCE_DIR(BACKEND_INSTALL_DIR
      .format("instances/")), BACKEND_CACHE_DIR(BACKEND_DATA_DIR.format("cache/"));

  private String basePath;

  private DirectoryLocations(String _basePath) {
    update(_basePath);
  }

  public void update(String _basePath) {
    basePath = _basePath;
  }

  public String format(String path) {
    if (path.startsWith("/")) path = path.substring(1);
    return basePath + path;
  }

  public String get() {
    String path = new File(basePath).getAbsolutePath();
    if (!path.endsWith(File.separator)) path += File.separator;
    return path;
  }

  // public static final String BASE_DIR_LOCATION =
  // Utils.getDynamicStorageLocation();
  // public static final String DATA_DIR_LOCATION = BASE_DIR_LOCATION + "data/";
  // public static final String FTB_DATA_DIR_LOCATION = BASE_DIR_LOCATION +
  // "data/ftb/";
  // public static final String AT_DATA_DIR_LOCATION = BASE_DIR_LOCATION +
  // "data/atlauncher/";
  // public static final String LOG_DIR_LOCATION = BASE_DIR_LOCATION + "logs/";
  // public static final String ASSETS_DIR_LOCATION = BASE_DIR_LOCATION +
  // "assets/";
  // public static final String IMAGE_DIR_LOCATION = BASE_DIR_LOCATION +
  // "images/";
  // public static final String AVATAR_CACHE_DIR_LOCATION = BASE_DIR_LOCATION +
  // "images/avatars/";
  // public static final String SERVER_CACHE_DIR_LOCATION = BASE_DIR_LOCATION +
  // "images/servers/";
  // public static final String BACKGROUND_DIR_LOCATION = IMAGE_DIR_LOCATION +
  // "background/";
  // public static String INSTANCE_DIR_LOCATION = BASE_DIR_LOCATION +
  // "instances/";
  // public static String SERVER_DIR_LOCATION = INSTANCE_DIR_LOCATION + "%s/";
  // public static String SERVER_MINECRAFT_DIR_LOCATION = SERVER_DIR_LOCATION +
  // "minecraft/";
  // public static String SERVER_MINECRAFT_BIN_DIR_LOCATION =
  // SERVER_MINECRAFT_DIR_LOCATION + "bin/";

}
