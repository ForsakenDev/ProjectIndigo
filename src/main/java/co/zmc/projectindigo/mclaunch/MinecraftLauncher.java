/*
 * This file is part of ProjectIndigo.
 *
 * Copyright (c) 2013 ZephyrUnleashed LLC <http://www.zephyrunleashed.com/>
 * ProjectIndigo is licensed under the ZephyrUnleashed License Version 1.
 *
 * ProjectIndigo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the ZephyrUnleashed License Version 1.
 *
 * ProjectIndigo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the ZephyrUnleashed License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License.
 */
package co.zmc.projectindigo.mclaunch;

import java.applet.Applet;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.Policy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.zmc.projectindigo.IndigoLauncher;
import co.zmc.projectindigo.Main;
import co.zmc.projectindigo.data.Server;
import co.zmc.projectindigo.security.PolicyManager;
import co.zmc.projectindigo.utils.Utils;

public class MinecraftLauncher {

    public static Process launchMinecraft(Server server, String username, String sessionId, String forgename, String rmax, String maxPermSize)
            throws IOException {
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
        } else {
            System.out.println("Not loading any instMods (minecraft jar mods), as the directory does not exist.");
        }

        cpb.append(Utils.getJavaDelimiter());
        cpb.append(new File(instModsDir, forgename).getAbsolutePath().replaceAll(" ", "\\\\ "));

        for (String jarFile : jarFiles) {
            cpb.append(Utils.getJavaDelimiter());
            cpb.append(new File(server.getBinDir(), jarFile).getAbsolutePath().replaceAll(" ", "\\ "));
        }

        List<String> arguments = new ArrayList<String>();

        String separator = System.getProperty("file.separator");
        String path = System.getProperty("java.home") + separator + "bin" + separator + "java"
                + (Utils.getCurrentOS() == Utils.OS.WINDOWS ? "w" : "");
        arguments.add(path);

        setMemory(arguments, rmax);

        arguments.add("-XX:+UseConcMarkSweepGC");
        arguments.add("-XX:+CMSIncrementalMode");
        arguments.add("-XX:+AggressiveOpts");
        arguments.add("-XX:+CMSClassUnloadingEnabled");
        if (maxPermSize.equalsIgnoreCase("")) {
            arguments.add("-XX:PermSize=128m");
        } else {
            arguments.add("-XX:PermSize=" + maxPermSize);
        }

        arguments.add("-cp");
        arguments.add(System.getProperty("java.class.path") + cpb.toString().replaceAll(" ", "\\\\ "));

        arguments.add(MinecraftLauncher.class.getCanonicalName().replaceAll(" ", "\\\\ "));
        arguments.add(server.getBaseDir().getAbsolutePath() + "/minecraft");
        arguments.add(forgename);
        arguments.add(username);
        arguments.add(sessionId);
        arguments.add(server.getIp());
        arguments.add(server.getPort() + "");
        arguments.add(IndigoLauncher.TITLE);
        String command = "";
        for (String arg : arguments) {
            command += " " + arg;
        }

        System.out.println(command);

        ProcessBuilder processBuilder = new ProcessBuilder(arguments);
        processBuilder.redirectErrorStream(true);
        return processBuilder.start();
    }

    private static void setMemory(List<String> arguments, String rmax) {
        boolean memorySet = false;
        try {
            int min = 256;
            if (rmax != null && Integer.parseInt(rmax) > 0) {
                arguments.add("-Xms" + min + "M");
                System.out.println("Setting MinMemory to " + min);
                arguments.add("-Xmx" + rmax + "M");
                System.out.println("Setting MaxMemory to " + rmax);
                memorySet = true;
            }
        } catch (Exception e) {
            System.out.println("Error parsing memory settings: ");
            e.printStackTrace();
        }
        if (!memorySet) {
            arguments.add("-Xms" + 256 + "M");
            System.out.println("Defaulting MinMemory to " + 256);
            arguments.add("-Xmx" + 1024 + "M");
            System.out.println("Defaulting MaxMemory to " + 1024);
        }
    }

    public static void main(String[] args) {
        if (args.length < 6) {
            new Main();
            return;
        }
        String basepath = args[0], forgename = args[1], username = args[2], sessionId = args[3], ip = args[4], port = args[5], title = args[6];
        try {
            System.out.println("Loading jars...");
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
                System.out.println("Added URL to classpath: " + urls[i].toString());
            }

            System.out.println("Loading natives...");
            String nativesDir = new File(new File(basepath, "bin"), "natives").toString();
            System.out.println("Natives loaded...");
            System.setProperty("org.lwjgl.librarypath", nativesDir);
            System.setProperty("net.java.games.input.librarypath", nativesDir);
            System.setProperty("minecraft.applet.TargetDirectory", basepath);

            URLClassLoader cl = new URLClassLoader(urls, MinecraftLauncher.class.getClassLoader());
            System.out.println("Loading minecraft class");

            PolicyManager policy = new PolicyManager();
            policy.copySecurityPolicy();
            File file = new File(nativesDir);
            if (file.isDirectory()) {
                for (File f : file.listFiles()) {
                    policy.addAdditionalPerm("permission java.lang.RuntimePermission \"loadLibrary." + f.getAbsolutePath().replaceAll("\\\\", "/")
                            + "\"");
                }
            }

            policy.addAdditionalPerm("permission java.io.FilePermission \""
                    + new File(basepath).getParentFile().getAbsolutePath().replaceAll("\\\\", "/") + "/-\", \"read, write, delete\"");
            policy.addAdditionalPerm("permission java.io.FilePermission \"" + nativesDir.replaceAll("\\\\", "/") + "/-\", \"read\"");
            policy.addAdditionalPerm("permission java.io.FilePermission \"" + System.getProperty("java.io.tmpdir").replaceAll("\\\\", "/")
                    + "-\", \"read, write, delete\"");

            policy.addAdditionalPerm("permission java.net.SocketPermission \"" + ip + ":" + port + "\", \"accept, resolve, listen, connect\"");

            policy.writeAdditionalPerms(policy.getPolicyLocation());

            System.out.println("Setting security policy to " + policy.getPolicyLocation());
            System.setProperty("java.security.policy", policy.getPolicyLocation());
            Policy.getPolicy().refresh();
            System.setSecurityManager(new SecurityManager());

            try {
                Class<?> MCAppletClass = cl.loadClass("net.minecraft.client.MinecraftApplet");
                Applet mcappl = (Applet) MCAppletClass.newInstance();
                MinecraftFrame mcWindow = new MinecraftFrame(title);
                mcWindow.start(mcappl, basepath, username, sessionId, ip, port);
            } catch (InstantiationException e) {
                System.out.println("Applet wrapper failed! Falling back to compatibility mode.");
                e.printStackTrace();
            }
        } catch (Throwable t) {
            System.out.println("Unhandled error launching minecraft");
            t.printStackTrace();
        }
    }
}
