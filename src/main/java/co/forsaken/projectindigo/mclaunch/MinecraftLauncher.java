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
  public static Process launch(Server server, Identity identity, Settings settings) throws IOException {
    StringBuilder cpb = new StringBuilder();
    File binFolder = server.getLibraryDir();
    File[] libraryFiles = binFolder.listFiles();
    if (binFolder.exists() && libraryFiles != null && libraryFiles.length != 0) {
      for (File file : libraryFiles) {
        if (file.isDirectory() || file.getName().equalsIgnoreCase("minecraft.jar")) {
          continue;
        }

        LogManager.info("Added in custom library " + file.getName());

        cpb.append(File.pathSeparator);
        cpb.append(file);
      }
    }

    cpb.append(File.pathSeparator);
    cpb.append(new File(server.getLibraryDir(), "minecraft.jar").getAbsolutePath());

    List<String> arguments = new ArrayList<String>();

    String path = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
    if (Utils.getCurrentOS() == OS.WINDOWS) {
      path += "w";
    }
    arguments.add(path);

    if (Utils.getCurrentOS() == OS.WINDOWS) {
      arguments.add("-XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump");
    }

    arguments.add("-XX:-OmitStackTraceInFastThrow");

    if (Settings.getToken().javaParams.isEmpty()) {
      // Mojang launcher defaults if user has no custom java arguments
      arguments.add("-XX:+UseConcMarkSweepGC");
      arguments.add("-XX:+CMSIncrementalMode");
      arguments.add("-XX:-UseAdaptiveSizePolicy");
    }

    setMemory(arguments, Settings.getToken().maxRam);
    arguments.add("-XX:PermSize=512M");

    arguments.add("-Duser.language=en");
    arguments.add("-Duser.country=US");

    arguments.add("-Dfml.ignorePatchDiscrepancies=true");
    arguments.add("-Dfml.ignoreInvalidMinecraftCertificates=true");

    arguments.add("-Dfml.log.level=INFO");

    if (Utils.getCurrentOS() == OS.MACOSX) {
      arguments.add("-Dapple.laf.useScreenMenuBar=true");
      arguments.add("-Xdock:icon=" + new File(DirectoryLocations.ASSETS_DIR_LOCATION, "icons/minecraft.icns").getAbsolutePath());
      arguments.add("-Xdock:name=\"" + server.getToken().friendlyName + "\"");
    }

    if (!Settings.getToken().javaParams.isEmpty()) {
      for (String arg : Settings.getToken().javaParams.split(" ")) {
        if (!arg.isEmpty()) {
          if (arguments.toString().contains(arg)) {
            LogManager.warn("Duplicate argument " + arg + " found and not added!");
            continue;
          }

          arguments.add(arg);
        }
      }
    }

    arguments.add("-Djava.library.path=" + server.getNativesDir().getAbsolutePath());
    arguments.add("-cp");
    arguments.add(System.getProperty("java.class.path") + cpb.toString());
    arguments.add("net.minecraft.launchwrapper.Launch");

    String props = "";

    if (!server.getLaunchArgs().isEmpty()) {
      String[] minecraftArguments = server.getLaunchArgs().split(" ");
      for (String argument : minecraftArguments) {
        argument = argument.replace("${auth_player_name}", identity.getName());
        argument = argument.replace("${profile_name}", identity.getName());
        argument = argument.replace("${user_properties}", props);
        argument = argument.replace("${version_name}", "1.7.10");
        argument = argument.replace("${game_directory}", server.getMinecraftDir().getAbsolutePath());
        argument = argument.replace("${game_assets}", DirectoryLocations.ASSETS_DIR_LOCATION);
        argument = argument.replace("${assets_root}", DirectoryLocations.ASSETS_DIR_LOCATION);
        argument = argument.replace("${assets_index_name}", "1.7.10");
        argument = argument.replace("${auth_uuid}", identity.getId());
        argument = argument.replace("${auth_access_token}", identity.getAccessToken());
        argument = argument.replace("${auth_session}", identity.getClientToken());
        argument = argument.replace("${user_type}", "mojang");
        arguments.add(argument);
      }
    } else {
      arguments.add("--username=" + identity.getName());
      arguments.add("--session=" + identity.getAccessToken());

      // This is for 1.7
      arguments.add("--accessToken=" + identity.getAccessToken());
      arguments.add("--uuid=" + identity.getId());
      // End of stuff for 1.7

      arguments.add("--version=" + "1.7.10");
      arguments.add("--gameDir=" + server.getMinecraftDir().getAbsolutePath());
      arguments.add("--assetsDir=" + DirectoryLocations.ASSETS_DIR_LOCATION);
    }
    arguments.add("--width=" + Utils.getMaximumWindowWidth());
    arguments.add("--height=" + Utils.getMaximumWindowHeight());
    arguments.add("--tweakClass=cpw.mods.fml.common.launcher.FMLTweaker");
    String argsString = arguments.toString();
    argsString = argsString.replace(identity.getName(), "REDACTED");
    argsString = argsString.replace(identity.getId(), "REDACTED");
    argsString = argsString.replace(identity.getAccessToken(), "REDACTED");
    argsString = argsString.replace(identity.getClientToken(), "REDACTED");
    argsString = argsString.replace(props, "REDACTED");

    LogManager.info("Launching Minecraft with the following arguments " + "(user related stuff has been removed):" + " " + argsString);
    ProcessBuilder processBuilder = new ProcessBuilder(arguments);
    processBuilder.directory(server.getMinecraftDir());
    processBuilder.redirectErrorStream(true);
    processBuilder.environment().remove("_JAVA_OPTIONS");
    return processBuilder.start();
  }

  public static Process launchMinecraft(Server server, Identity identity, Settings settings) throws IOException {
    // return MinecraftLauncher.launch(server, identity, settings);
    String[] jarFiles = new String[] { "minecraft.", "lwjgl-", "lwjgl_util-", "jinput-" };
    List<String> addedCP = new ArrayList<String>();
    StringBuilder cpb = new StringBuilder("");

    File jarMods = server.getJarModsDir();
    for (File f : jarMods.listFiles()) {
      cpb.append(File.pathSeparator);
      cpb.append(f.getAbsolutePath());
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
            cpb.append(thisFile.getAbsolutePath().replaceAll(" ", "\\ "));
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
        cpb.append(file.getAbsolutePath().replaceAll(" ", "\\ "));
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
        cpb.append(file.getAbsolutePath().replaceAll(" ", "\\ "));
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
    arguments.add("-Djava.library.path=" + server.getNativesDir().getAbsolutePath());
    arguments.add("-Duser.home=" + server.getMinecraftDir());
    //
    arguments.add("-XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump");
    // arguments.add("-noverify");
    arguments.add("-cp");
    // arguments
    // .add(System.getProperty("java.class.path")
    // +
    // "C:\\Users\\Ryan\\Development\\ATLauncher\\target\\classes;C:\\Users\\Ryan\\.m2\\repository\\com\\google\\code\\gson\\gson\\2.2.4\\gson-2.2.4.jar;C:\\Users\\Ryan\\.m2\\repository\\com\\googlecode\\json-simple\\json-simple\\1.1.1\\json-simple-1.1.1.jar;C:\\Users\\Ryan\\.m2\\repository\\org\\tukaani\\xz\\1.5\\xz-1.5.jar;C:\\Users\\Ryan\\.m2\\repository\\com\\mojang\\authlib\\1.5.17\\authlib-1.5.17.jar;C:\\Users\\Ryan\\.m2\\repository\\org\\apache\\commons\\commons-lang3\\3.3.2\\commons-lang3-3.3.2.jar;C:\\Users\\Ryan\\.m2\\repository\\commons-codec\\commons-codec\\1.9\\commons-codec-1.9.jar;C:\\Users\\Ryan\\.m2\\repository\\com\\google\\code\\findbugs\\jsr305\\2.0.1\\jsr305-2.0.1.jar;C:\\Users\\Ryan\\.m2\\repository\\commons-io\\commons-io\\2.4\\commons-io-2.4.jar;C:\\Users\\Ryan\\.m2\\repository\\org\\apache\\logging\\log4j\\log4j-api\\2.0-beta9\\log4j-api-2.0-beta9.jar;C:\\Users\\Ryan\\.m2\\repository\\org\\apache\\logging\\log4j\\log4j-core\\2.0-beta9\\log4j-core-2.0-beta9.jar;C:\\Users\\Ryan\\.m2\\repository\\com\\google\\guava\\guava\\17.0\\guava-17.0.jar;C:\\Users\\Ryan\\Development\\ATLauncher\\Instances\\SkyFactory\\jarmods\\forge-1.7.10-10.13.2.1277-universal.jar;C:\\Users\\Ryan\\Development\\ATLauncher\\Instances\\SkyFactory\\bin\\launchwrapper-1.11.jar;C:\\Users\\Ryan\\Development\\ATLauncher\\Instances\\SkyFactory\\bin\\asm-all-5.0.3.jar;C:\\Users\\Ryan\\Development\\ATLauncher\\Instances\\SkyFactory\\bin\\akka-actor_2.11-2.3.3.jar;C:\\Users\\Ryan\\Development\\ATLauncher\\Instances\\SkyFactory\\bin\\config-1.2.1.jar;C:\\Users\\Ryan\\Development\\ATLauncher\\Instances\\SkyFactory\\bin\\scala-actors-migration_2.11-1.1.0.jar;C:\\Users\\Ryan\\Development\\ATLauncher\\Instances\\SkyFactory\\bin\\scala-compiler-2.11.1.jar;C:\\Users\\Ryan\\Development\\ATLauncher\\Instances\\SkyFactory\\bin\\scala-continuations-library_2.11-1.0.2.jar;C:\\Users\\Ryan\\Development\\ATLauncher\\Instances\\SkyFactory\\bin\\scala-continuations-plugin_2.11.1-1.0.2.jar;C:\\Users\\Ryan\\Development\\ATLauncher\\Instances\\SkyFactory\\bin\\scala-library-2.11.1.jar;C:\\Users\\Ryan\\Development\\ATLauncher\\Instances\\SkyFactory\\bin\\scala-parser-combinators_2.11-1.0.1.jar;C:\\Users\\Ryan\\Development\\ATLauncher\\Instances\\SkyFactory\\bin\\scala-reflect-2.11.1.jar;C:\\Users\\Ryan\\Development\\ATLauncher\\Instances\\SkyFactory\\bin\\scala-swing_2.11-1.0.1.jar;C:\\Users\\Ryan\\Development\\ATLauncher\\Instances\\SkyFactory\\bin\\scala-xml_2.11-1.0.2.jar;C:\\Users\\Ryan\\Development\\ATLauncher\\Instances\\SkyFactory\\bin\\jopt-simple-4.5.jar;C:\\Users\\Ryan\\Development\\ATLauncher\\Instances\\SkyFactory\\bin\\lzma-0.0.1.jar;C:\\Users\\Ryan\\Development\\ATLauncher\\Instances\\SkyFactory\\bin\\guava-16.0.jar;C:\\Users\\Ryan\\Development\\ATLauncher\\Instances\\SkyFactory\\bin\\commons-lang3-3.2.1.jar;C:\\Users\\Ryan\\Development\\ATLauncher\\Instances\\SkyFactory\\bin\\realms-1.3.5.jar;C:\\Users\\Ryan\\Development\\ATLauncher\\Instances\\SkyFactory\\bin\\commons-compress-1.8.1.jar;C:\\Users\\Ryan\\Development\\ATLauncher\\Instances\\SkyFactory\\bin\\httpclient-4.3.3.jar;C:\\Users\\Ryan\\Development\\ATLauncher\\Instances\\SkyFactory\\bin\\commons-logging-1.1.3.jar;C:\\Users\\Ryan\\Development\\ATLauncher\\Instances\\SkyFactory\\bin\\httpcore-4.3.2.jar;C:\\Users\\Ryan\\Development\\ATLauncher\\Instances\\SkyFactory\\bin\\vecmath-1.3.1.jar;C:\\Users\\Ryan\\Development\\ATLauncher\\Instances\\SkyFactory\\bin\\trove4j-3.0.3.jar;C:\\Users\\Ryan\\Development\\ATLauncher\\Instances\\SkyFactory\\bin\\icu4j-core-mojang-51.2.jar;C:\\Users\\Ryan\\Development\\ATLauncher\\Instances\\SkyFactory\\bin\\codecjorbis-20101023.jar;C:\\Users\\Ryan\\Development\\ATLauncher\\Instances\\SkyFactory\\bin\\codecwav-20101023.jar;C:\\Users\\Ryan\\Development\\ATLauncher\\Instances\\SkyFactory\\bin\\libraryjavasound-20101123.jar;C:\\Users\\Ryan\\Development\\ATLauncher\\Instances\\SkyFactory\\bin\\librarylwjglopenal-20100824.jar;C:\\Users\\Ryan\\Development\\ATLauncher\\Instances\\SkyFactory\\bin\\soundsystem-20120107.jar;C:\\Users\\Ryan\\Development\\ATLauncher\\Instances\\SkyFactory\\bin\\netty-all-4.0.10.Final.jar;C:\\Users\\Ryan\\Development\\ATLauncher\\Instances\\SkyFactory\\bin\\commons-io-2.4.jar;C:\\Users\\Ryan\\Development\\ATLauncher\\Instances\\SkyFactory\\bin\\commons-codec-1.9.jar;C:\\Users\\Ryan\\Development\\ATLauncher\\Instances\\SkyFactory\\bin\\jinput-2.0.5.jar;C:\\Users\\Ryan\\Development\\ATLauncher\\Instances\\SkyFactory\\bin\\jutils-1.0.0.jar;C:\\Users\\Ryan\\Development\\ATLauncher\\Instances\\SkyFactory\\bin\\gson-2.2.4.jar;C:\\Users\\Ryan\\Development\\ATLauncher\\Instances\\SkyFactory\\bin\\authlib-1.5.16.jar;C:\\Users\\Ryan\\Development\\ATLauncher\\Instances\\SkyFactory\\bin\\log4j-api-2.0-beta9.jar;C:\\Users\\Ryan\\Development\\ATLauncher\\Instances\\SkyFactory\\bin\\log4j-core-2.0-beta9.jar;C:\\Users\\Ryan\\Development\\ATLauncher\\Instances\\SkyFactory\\bin\\lwjgl-2.9.1.jar;C:\\Users\\Ryan\\Development\\ATLauncher\\Instances\\SkyFactory\\bin\\lwjgl_util-2.9.1.jar;C:\\Users\\Ryan\\Development\\ATLauncher\\Instances\\SkyFactory\\bin\\twitch-5.16.jar;C:\\Users\\Ryan\\Development\\ATLauncher\\Instances\\SkyFactory\\bin\\minecraft.jar");
    arguments.add(cpb.toString().replaceAll(" ", "\\\\ "));
    arguments.add("net.minecraft.launchwrapper.Launch");
    for (String s : server.getLaunchArgs().split(" ")) {
      if (s.equalsIgnoreCase("${auth_player_name}")) {
        s = identity.getName();
      } else if (s.equalsIgnoreCase("${game_directory}")) {
        s = server.getMinecraftDir().getAbsolutePath();
      } else if (s.equalsIgnoreCase("${assets_root}")) {
        s = DirectoryLocations.ASSETS_DIR_LOCATION.replaceAll(" ", "\\\\ ");
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
    arguments.add("--tweakClass=cpw.mods.fml.common.launcher.FMLTweaker");
    arguments.add("--server="+server.getToken().friendlyIp);
    ProcessBuilder processBuilder = new ProcessBuilder(arguments);
    LogManager.info(arguments.toString());
    LogManager.info("Setting working dir to " + server.getMinecraftDir().getAbsolutePath());
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
