package co.zmc.projectindigo.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Settings {
    public static final String INSTALL_PATH     = "install_folder";
    public static final String MAX_RAM          = "maximum_ram";
    public static final String JAVA_PARAMS      = "additional_java_params";
    public static final String WINDOW_SIZE      = "minecraft_window_size";
    public static final String WINDOW_POSITION  = "minecraft_window_position";
    public static final String WINDOW_MAXIMIZED = "minecraft_window_maximized";
    private static final File  SETTINGS_FILE    = new File(DirectoryLocations.DATA_DIR_LOCATION, "settings");
    private JSONObject         _settings;

    public Settings() {
        try {
            _settings = (JSONObject) new JSONParser().parse(new Scanner(SETTINGS_FILE, "UTF-8").useDelimiter("\\A").next());
            if (get(INSTALL_PATH).isEmpty()) {
                set(INSTALL_PATH, DirectoryLocations.SERVERS_BASE_DIR_LOCATION);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String get(String key) {
        return (String) _settings.get(key);
    }

    @SuppressWarnings("unchecked")
    public void set(String key, String value) {
        _settings.put(key, value);
    }

    public void save() {
        FileUtils.writeStringToFile(_settings.toString(), SETTINGS_FILE);
    }
}
