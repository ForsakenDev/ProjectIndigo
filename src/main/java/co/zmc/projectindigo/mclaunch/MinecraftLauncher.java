package co.zmc.projectindigo.mclaunch;

import java.applet.Applet;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.zmc.projectindigo.Main;
import co.zmc.projectindigo.data.Server;
import co.zmc.projectindigo.data.log.Logger;
import co.zmc.projectindigo.utils.ResourceUtils;
import co.zmc.projectindigo.utils.Settings;
import co.zmc.projectindigo.utils.Utils;
import co.zmc.projectindigo.utils.Utils.OS;

public class MinecraftLauncher {
    public static Process launchMinecraft(Server server, String username, String sessionId, String forgename, Settings settings) throws IOException {
        String[] jarFiles = new String[] { "minecraft.jar", "lwjgl.jar", "lwjgl_util.jar", "jinput.jar" };
        StringBuilder cpb = new StringBuilder("");
        File instModsDir = new File(server.getBaseDir(), "instMods/");
        if (instModsDir.isDirectory()) {
            String[] files = instModsDir.list();
            Arrays.sort(files);
            for (String name : files) {
                if (!name.equals(forgename)) {
                    if (name.toLowerCase().contains("forge") && name.toLowerCase().contains("minecraft") && name.toLowerCase().endsWith(".zip")) {
                        if (new File(instModsDir, forgename).exists()) {
                            if (!new File(instModsDir, forgename).equals(new File(instModsDir, name))) {
                                new File(instModsDir, name).delete();
                            }
                        } else {
                            new File(instModsDir, name).renameTo(new File(instModsDir, forgename));
                        }
                    } else if (!name.equalsIgnoreCase(forgename) && (name.toLowerCase().endsWith(".zip") || name.toLowerCase().endsWith(".jar"))) {
                        cpb.append(Utils.getJavaDelimiter());
                        cpb.append(new File(instModsDir, name).getAbsolutePath());
                    }
                }
            }
        }

        cpb.append(Utils.getJavaDelimiter());
        cpb.append(new File(instModsDir, forgename).getAbsolutePath().replaceAll(" ", "\\\\ "));

        for (String jarFile : jarFiles) {
            cpb.append(Utils.getJavaDelimiter());
            cpb.append(new File(server.getBinDir(), jarFile).getAbsolutePath().replaceAll(" ", "\\ "));
        }
        String title = server.getName() + " v" + server.getVersion();
        List<String> arguments = new ArrayList<String>();

        String separator = System.getProperty("file.separator");
        String path = System.getProperty("java.home") + separator + "bin" + separator + "java" + (Utils.getCurrentOS() == Utils.OS.WINDOWS ? "w" : "");
        arguments.add(path);

        setMemory(arguments, settings.get(Settings.MAX_RAM));
        arguments.add("-XX:+UseConcMarkSweepGC");
        arguments.add("-XX:+CMSIncrementalMode");
        arguments.add("-XX:+AggressiveOpts");
        arguments.add("-XX:+CMSClassUnloadingEnabled");

        if (!settings.get(Settings.JAVA_PARAMS).isEmpty() && settings.get(Settings.JAVA_PARAMS).split(" ").length > 0) {
            for (String s : settings.get(Settings.JAVA_PARAMS).split(" ")) {
                if (!s.isEmpty()) {
                    arguments.add(s);
                }
            }
        }
        if (Utils.getCurrentOS() == OS.MACOSX) {
            try {
                arguments.add("-Xdock:icon=" + new File(ResourceUtils.getResource("icon_file_mac").toURI()).getAbsolutePath());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            arguments.add("-Xdock:name=" + title);
        }

        arguments.add("-noverify");
        arguments.add("-cp");
        arguments.add(System.getProperty("java.class.path") + cpb.toString().replaceAll(" ", "\\\\ "));

        arguments.add(MinecraftLauncher.class.getCanonicalName().replaceAll(" ", "\\\\ "));
        arguments.add(server.getBaseDir().getAbsolutePath() + "/minecraft");
        arguments.add(forgename);
        arguments.add(username);
        arguments.add(sessionId);
        arguments.add(server.getIp());
        arguments.add(server.getPort() + "");
        arguments.add(title);

        ProcessBuilder processBuilder = new ProcessBuilder(arguments);
        Logger.logInfo("Setting working dir to " + server.getBaseDir().getAbsolutePath() + "/minecraft");
        processBuilder.directory(new File(server.getBaseDir().getAbsolutePath() + "/minecraft"));

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

    public static void main(String[] args) {
        if (args.length < 6) {
            new Main();
            return;
        }
        String basepath = args[0], forgename = args[1], username = args[2], sessionId = args[3], ip = args[4], port = args[5], title = args[6];
        try {
            Logger.logInfo("Loading jars...");
            String[] jarFiles = new String[] { "minecraft.jar", "lwjgl.jar", "lwjgl_util.jar", "jinput.jar" };
            ArrayList<File> classPathFiles = new ArrayList<File>();
            File tempDir = new File(new File(basepath).getParentFile(), "instMods/");
            if (tempDir.isDirectory()) {
                for (String name : tempDir.list()) {
                    if (!name.equalsIgnoreCase(forgename)) {
                        if (name.toLowerCase().endsWith(".zip") || name.toLowerCase().endsWith(".jar")) {
                            classPathFiles.add(new File(tempDir, name));
                        }
                    }
                }
            }

            classPathFiles.add(new File(tempDir, forgename));
            for (String jarFile : jarFiles) {
                classPathFiles.add(new File(new File(basepath, "bin"), jarFile));
            }

            URL[] urls = new URL[classPathFiles.size()];
            for (int i = 0; i < classPathFiles.size(); i++) {
                try {
                    urls[i] = classPathFiles.get(i).toURI().toURL();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                Logger.logInfo("Added URL to classpath: " + urls[i].toString());
            }

            Logger.logInfo("Loading natives...");
            String nativesDir = new File(new File(basepath, "bin"), "natives").toString();
            System.setProperty("org.lwjgl.librarypath", nativesDir);
            System.setProperty("net.java.games.input.librarypath", nativesDir);
            System.setProperty("minecraft.applet.TargetDirectory", basepath);

            URLClassLoader cl = new URLClassLoader(urls, MinecraftLauncher.class.getClassLoader());

            Logger.logInfo("Loading security class");
            // PolicyManager manager = new PolicyManager();
            // manager.enforceSecurityManager(basepath, nativesDir);

            Logger.logInfo("Loading minecraft class");

            try {
                Class<?> MCAppletClass = cl.loadClass("net.minecraft.client.MinecraftApplet");
                Applet mcappl = (Applet) MCAppletClass.newInstance();
                MinecraftFrame mcWindow = new MinecraftFrame(title, new Settings());
                mcWindow.start(mcappl, basepath, username, sessionId, ip, port);
            } catch (InstantiationException e) {
                Logger.logError("Applet wrapper failed! Falling back to compatibility mode", e);

            }
        } catch (Throwable t) {
            Logger.logError("Unknown error during launch", t);
        }
    }

}
