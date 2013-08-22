package co.zmc.utils;

public class DirectoryLocations {

    public static final String BASE_DIR_LOCATION       = Utils.getDynamicStorageLocation();
    public static final String DATA_DIR_LOCATION       = BASE_DIR_LOCATION + "data/";
    public static final String ASSETS_DIR_LOCATION     = BASE_DIR_LOCATION + "assets/";
    public static final String IMAGE_DIR_LOCATION     = BASE_DIR_LOCATION + "images/";
    public static final String BACKGROUND_DIR_LOCATION = IMAGE_DIR_LOCATION + "background/";
    public static final String MINECRAFT_DIR_LOCATION  = BASE_DIR_LOCATION + "minecraft/%s/";
    public static final String BIN_DIR_LOCATION        = MINECRAFT_DIR_LOCATION + "bin/";
}
