package co.forsaken.projectindigo.mclaunch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.forsaken.projectindigo.data.Server;
import co.forsaken.projectindigo.data.log.Logger;
import co.forsaken.projectindigo.log.LogManager;
import co.forsaken.projectindigo.session.Identity;
import co.forsaken.projectindigo.utils.DirectoryLocations;
import co.forsaken.projectindigo.utils.Settings;
import co.forsaken.projectindigo.utils.Utils;
import co.forsaken.projectindigo.utils.Utils.OS;

public class MinecraftLauncher {
  private static String parseFileName(File file) {
    String path = file.getAbsolutePath();
    if (!path.endsWith(File.separator)) path += File.separator;
    return path;
  }

  public static Process launchMinecraft(Server server, Identity identity, Settings settings, boolean autoLogin) throws IOException {
    List<String> addedCP = new ArrayList<String>();
    StringBuilder cpb = new StringBuilder("");

    File jarMods = server.getJarModsDir();
    for (File f : jarMods.listFiles()) {
      cpb.append(File.pathSeparator);
      cpb.append(parseFileName(f));
    }
    File libMods = server.getLibraryDir();
    File[] jarModFiles = libMods.listFiles();
    if (jarMods.exists() && jarModFiles != null) {
      ArrayList<String> jarmods = new ArrayList<String>();
      if (server.getJarOrder() != null && !server.getJarOrder().isEmpty()) {
        jarmods = new ArrayList<String>(Arrays.asList(server.getJarOrder().split(",")));
        for (String mod : jarmods) {
          File thisFile = new File(jarMods, mod);
          String parsedName = thisFile.getName();
          if (parsedName.contains("-")) {
            parsedName = thisFile.getName().substring(0, thisFile.getName().lastIndexOf("-"));
          }
          if (thisFile.isDirectory() || addedCP.contains(parsedName)) {
            continue;
          }
          if (thisFile.exists()) {
            cpb.append(File.pathSeparator);
            cpb.append(parseFileName(thisFile));
            addedCP.add(parsedName);
          }
        }
      }
      for (File file : jarModFiles) {
        if (jarmods.contains(file.getName())) {
          continue;
        }
        String parsedName = file.getName();
        if (parsedName.contains("-")) {
          parsedName = file.getName().substring(0, file.getName().lastIndexOf("-"));
        }
        if (file.isDirectory() || addedCP.contains(parsedName)) continue;

        cpb.append(File.pathSeparator);
        cpb.append(parseFileName(file));
        addedCP.add(parsedName);
      }
    }
    if (server.getLibraryDir().isDirectory()) {
      for (File file : server.getLibraryDir().listFiles()) {
        String parsedName = file.getName();
        if (parsedName.contains("-")) {
          parsedName = file.getName().substring(0, file.getName().lastIndexOf("-"));
        }
        if (file.isDirectory() || addedCP.contains(parsedName)) continue;
        cpb.append(Utils.getJavaDelimiter());
        cpb.append(parseFileName(file));
        addedCP.add(parsedName);
      }
    }
    String title = server.getToken().friendlyName + " v" + server.getToken().version;
    List<String> arguments = new ArrayList<String>();

    String separator = System.getProperty("file.separator");
    String path = System.getProperty("java.home") + separator + "bin" + separator + "java" + (Utils.getCurrentOS() == Utils.OS.WINDOWS ? "w" : "");
    arguments.add(path);
    // String imagePath = DirectoryLocations.IMAGE_DIR_LOCATION +
    // server.getToken().modpackRefName + ".png";
    // if (Utils.getCurrentOS() == OS.MACOSX) {
    // arguments.add("-Xdock:icon=" + imagePath);
    // arguments.add("-Xdock:name=" + title);
    // }
    setMemory(arguments, Settings.getToken().maxRam);
    arguments.add("-XX:+UseConcMarkSweepGC");
    arguments.add("-XX:+CMSIncrementalMode");
    arguments.add("-XX:+AggressiveOpts");
    arguments.add("-XX:+CMSClassUnloadingEnabled");

    if (!Settings.getToken().javaParams.isEmpty() && Settings.getToken().javaParams.split(" ").length > 0) {
      for (String s : Settings.getToken().javaParams.split(" ")) {
        if (!s.isEmpty()) {
          arguments.add(s);
        }
      }
    }
    arguments.add("-Djava.library.path=" + parseFileName(server.getNativesDir()));
    arguments.add("-Duser.home=" + parseFileName(server.getMinecraftDir()));
    if (Utils.getCurrentOS() == OS.WINDOWS) {
      arguments.add("-XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump");
    }
    arguments.add("-cp");
    arguments.add(cpb.toString());
    arguments.add("net.minecraft.launchwrapper.Launch");
    for (String s : server.getLaunchArgs().split(" ")) {
      if (s.equalsIgnoreCase("${auth_player_name}")) {
        s = identity.getName();
      } else if (s.equalsIgnoreCase("${game_directory}")) {
        s = parseFileName(server.getMinecraftDir());
      } else if (s.equalsIgnoreCase("${assets_root}")) {
        s = DirectoryLocations.BACKEND_ASSET_DIR.get();
      } else if (s.equalsIgnoreCase("${assets_index_name}")) {
        s = "1.7.10";
      } else if (s.equalsIgnoreCase("${auth_uuid}")) {
        s = identity.getId();
      } else if (s.equalsIgnoreCase("${version_name}")) {
        s = "1.7.10";
      } else if (s.equalsIgnoreCase("${auth_access_token}")) {
        s = identity.getAccessToken();
      } else if (s.equalsIgnoreCase("${user_properties}")) {
        s = "{}";
      } else if (s.equalsIgnoreCase("${user_type}")) {
        s = "mojang";
      }
      arguments.add(s);
    }

    String argsString = arguments.toString();
    if (!LogManager.showDebug) {
      argsString = argsString.replace(identity.getAccessToken(), "REDACTED");
      argsString = argsString.replace(identity.getId(), "REDACTED");
      argsString = argsString.replace(identity.getClientToken(), "REDACTED");
      argsString = argsString.replace(identity.getName(), "REDACTED");
    }

    arguments.add("--tweakClass=cpw.mods.fml.common.launcher.FMLTweaker");
    if (autoLogin) {
      arguments.add("--server=" + server.getToken().friendlyIp);
    }
    ProcessBuilder processBuilder = new ProcessBuilder(arguments);
    LogManager.info("Setting working dir to " + server.getMinecraftDir().getAbsolutePath());
    LogManager.info("Launch Arguments: " + argsString);
    System.out.println("Launch Arguments: " + argsString);
    processBuilder.directory(new File(server.getMinecraftDir().getAbsolutePath()));
    processBuilder.redirectErrorStream(true);
    processBuilder.environment().remove("_JAVA_OPTIONS");

    return processBuilder.start();
  }

  private static void setMemory(List<String> arguments, String rmax) {
    boolean memorySet = false;
    try {
      int min = 256;
      if (rmax != null && Integer.parseInt(rmax) > 0) {
        arguments.add("-Xms" + min + "M");
        Logger.logInfo("Setting MinMemory to " + min);
        arguments.add("-Xmx" + rmax + "M");
        Logger.logInfo("Setting MaxMemory to " + rmax);
        memorySet = true;
      }
    } catch (Exception e) {
      Logger.logError("Error parsing memory settings", e);
    }
    if (!memorySet) {
      arguments.add("-Xms" + 256 + "M");
      Logger.logInfo("Defaulting MinMemory to " + 256);
      arguments.add("-Xmx" + 1024 + "M");
      Logger.logInfo("Defaulting MaxMemory to " + 1024);
    }
  }
}
