package co.forsaken.projectindigo.utils;

public class DirectoryLocations {

  public static final String BASE_DIR_LOCATION                 = Utils.getDynamicStorageLocation();
  public static final String DATA_DIR_LOCATION                 = BASE_DIR_LOCATION + "data/";
  public static final String FTB_DATA_DIR_LOCATION             = BASE_DIR_LOCATION + "data/ftb/";
  public static final String AT_DATA_DIR_LOCATION              = BASE_DIR_LOCATION + "data/atlauncher/";
  public static final String LOG_DIR_LOCATION                  = BASE_DIR_LOCATION + "logs/";
  public static final String ASSETS_DIR_LOCATION               = BASE_DIR_LOCATION + "assets/";
  public static final String IMAGE_DIR_LOCATION                = BASE_DIR_LOCATION + "images/";
  public static final String AVATAR_CACHE_DIR_LOCATION         = BASE_DIR_LOCATION + "images/avatars/";
  public static final String SERVER_CACHE_DIR_LOCATION         = BASE_DIR_LOCATION + "images/servers/";
  public static final String BACKGROUND_DIR_LOCATION           = IMAGE_DIR_LOCATION + "background/";
  public static String       INSTANCE_DIR_LOCATION             = BASE_DIR_LOCATION + "instances/";
  public static String       SERVER_DIR_LOCATION               = INSTANCE_DIR_LOCATION + "%s/";
  public static String       SERVER_MINECRAFT_DIR_LOCATION     = SERVER_DIR_LOCATION + "minecraft/";
  public static String       SERVER_MINECRAFT_BIN_DIR_LOCATION = SERVER_MINECRAFT_DIR_LOCATION + "bin/";

}
