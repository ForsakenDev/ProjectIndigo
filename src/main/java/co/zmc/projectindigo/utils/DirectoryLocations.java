package co.zmc.projectindigo.utils;

public class DirectoryLocations {

    public static final String BASE_DIR_LOCATION                 = Utils.getDynamicStorageLocation();
    public static final String DATA_DIR_LOCATION                 = BASE_DIR_LOCATION + "data/";
    public static final String ASSETS_DIR_LOCATION               = BASE_DIR_LOCATION + "assets/";
    public static final String IMAGE_DIR_LOCATION                = BASE_DIR_LOCATION + "images/";
    public static final String AVATAR_CACHE_DIR_LOCATION         = BASE_DIR_LOCATION + "images/avatars/";
    public static final String SERVER_CACHE_DIR_LOCATION         = BASE_DIR_LOCATION + "images/servers/";
    public static final String BACKGROUND_DIR_LOCATION           = IMAGE_DIR_LOCATION + "background/";
    public static final String SERVERS_BASE_DIR_LOCATION         = BASE_DIR_LOCATION + "servers/";
    public static final String SERVER_DIR_LOCATION               = SERVERS_BASE_DIR_LOCATION + "%s/";
    public static final String SERVER_MINECRAFT_DIR_LOCATION     = SERVER_DIR_LOCATION + "minecraft/";
    public static final String SERVER_MINECRAFT_BIN_DIR_LOCATION = SERVER_MINECRAFT_DIR_LOCATION + "bin/";
}
