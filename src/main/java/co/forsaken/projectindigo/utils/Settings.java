package co.forsaken.projectindigo.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import com.google.gson.Gson;

public class Settings {
  private static final File    SETTINGS_FILE = new File(DirectoryLocations.DATA_DIR_LOCATION, "settings.json");
  private static SettingsToken token;

  public static SettingsToken getToken() {
    if (token == null) {
      try {
        if (!SETTINGS_FILE.exists()) {
          SETTINGS_FILE.createNewFile();
          FileUtils.writeStringToFile("{ \"installDir\": \"\", \"maxRam\": \"1024\", \"javaParams\": \"-XX:PermSize=128M\", \"windowSize\": \"1280,720\", \"windowPos\": \"0,0\", \"windowMax\": \"true\" }", SETTINGS_FILE);
        }
        token = new Gson().fromJson(new Scanner(SETTINGS_FILE, "UTF-8").useDelimiter("\\A").next(), SettingsToken.class);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return token;
  }

  public static void save() {
    FileUtils.writeStringToFile(new Gson().toJson(token), SETTINGS_FILE);
  }

  public class SettingsToken {
    public String installPath;
    public String maxRam;
    public String javaParams;
    public String windowSize;
    public String windowPos;
    public String windowMax;
  }
}
