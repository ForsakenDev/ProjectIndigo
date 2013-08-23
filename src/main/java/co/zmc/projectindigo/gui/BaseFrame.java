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
 * License and see <http://spout.in/licensev1> for the full license,
 * including the MIT license.
 */
package co.zmc.projectindigo.gui;

import java.applet.Applet;
import java.awt.Dimension;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.ProgressMonitor;
import javax.swing.WindowConstants;

import co.zmc.projectindigo.IndigoLauncher;
import co.zmc.projectindigo.data.LoginResponse;
import co.zmc.projectindigo.managers.DownloadHandler;
import co.zmc.projectindigo.managers.LoginHandler;
import co.zmc.projectindigo.managers.UserManager;
import co.zmc.projectindigo.mclaunch.MinecraftFrame;
import co.zmc.projectindigo.utils.DirectoryLocations;
import co.zmc.projectindigo.utils.Utils;

@SuppressWarnings("serial")
public abstract class BaseFrame extends JFrame {
    protected final UserManager userManager = new UserManager();
    protected final Logger      logger      = Logger.getLogger("launcher");
    private final String        version     = "1.5.2";

    public BaseFrame(int width, int height) {
        setTitle(IndigoLauncher.TITLE);
        setFont(getMinecraftFont(14));
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        Dimension dim = new Dimension(width, height);
        setSize(dim);
        setPreferredSize(dim);
        setBounds(0, 0, width, height);
        setLocationRelativeTo(null);
        setupLook();
    }

    public abstract void setupLook();

    public final void doLogin(String user) {
        if (!userManager.hasSavedPassword(user)) { throw new NullPointerException("There is no saved password for the user '" + user + "'"); }
        doLogin(user, userManager.getSavedPassword(user), false);
    }

    public final void doLogin(String user, String pass, boolean saveUser) {
        if (pass == null) { throw new NullPointerException("The password was null when logging in as user: '" + user + "'"); }
        LoginHandler loginHandler = new LoginHandler(this, user, pass, saveUser);
        loginHandler.execute();
    }

    public void launchGame(LoginResponse response) {
        doUpdate("zephyrunleased.com", response);
    }

    private final void doUpdate(final String serverName, final LoginResponse response) {
        final String installPath = String.format(DirectoryLocations.SERVER_MINECRAFT_DIR_LOCATION, serverName, version);
        final String binPath = String.format(DirectoryLocations.SERVER_MINECRAFT_BIN_DIR_LOCATION, serverName, version);
        if (new File(installPath, "version").exists()) {
            new File(installPath, "version").delete();
        }

        if (!new File(binPath + "/minecraft.jar").exists()) {
            final ProgressMonitor progMonitor = new ProgressMonitor(this, "Downloading minecraft...", "", 0, 100);
            final DownloadHandler updater = new DownloadHandler(serverName, version) {
                @Override
                public void done() {
                    progMonitor.close();
                    try {
                        if (get()) {
                            getLogger().log(Level.INFO, "Game update complete");
                            Utils.removeMetaInf(binPath);
                            launchMinecraft(serverName, response.getUsername(), response.getSessionId());
                        } else {
                            throw new NullPointerException("Error occurred during downloading the game");
                        }
                    } catch (CancellationException e) {
                        throw new NullPointerException("Game update canceled.");
                    } catch (InterruptedException e) {
                        throw new NullPointerException("Game update interrupted.");
                    } catch (ExecutionException e) {
                        throw new NullPointerException("Failed to download game.");
                    }
                }
            };

            updater.addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    if (progMonitor.isCanceled()) {
                        updater.cancel(false);
                    }
                    if (!updater.isDone()) {
                        int prog = updater.getProgress();
                        if (prog < 0) {
                            prog = 0;
                        } else if (prog > 100) {
                            prog = 100;
                        }
                        progMonitor.setProgress(prog);
                        progMonitor.setNote(updater.getStatus());
                    }
                }
            });
            updater.execute();
        } else {
            launchMinecraft(serverName, response.getUsername(), response.getSessionId());
        }
    }

    private void launchMinecraft(String serverName, String username, String sessionId) {
        String basePath = String.format(DirectoryLocations.SERVER_MINECRAFT_DIR_LOCATION, serverName, version);
        try {
            System.out.println("Loading jars...");
            String[] jarFiles = new String[] { "minecraft.jar", "lwjgl.jar", "lwjgl_util.jar", "jinput.jar" };
            ArrayList<File> classPathFiles = new ArrayList<File>();

            for (String jarFile : jarFiles) {
                classPathFiles.add(new File(new File(basePath, "bin"), jarFile));
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
            String nativesDir = new File(new File(basePath, "bin"), "natives").toString();
            System.out.println("Natives loaded...");

            System.setProperty("org.lwjgl.librarypath", nativesDir);
            System.setProperty("net.java.games.input.librarypath", nativesDir);

            System.setProperty("user.home", new File(basePath).getParent());

            URLClassLoader cl = new URLClassLoader(urls, BaseFrame.class.getClassLoader());

            System.out.println("Loading minecraft class");
            Class<?> mc = cl.loadClass("net.minecraft.client.Minecraft");
            System.out.println("mc = " + mc);
            Field[] fields = mc.getDeclaredFields();
            System.out.println("field amount: " + fields.length);

            for (Field f : fields) {
                if (f.getType() != File.class) {
                    continue;
                }
                if (0 == (f.getModifiers() & (Modifier.PRIVATE | Modifier.STATIC))) {
                    continue;
                }
                f.setAccessible(true);
                f.set(null, new File(basePath));
                System.out.println("Fixed Minecraft Path: Field was " + f.toString());
                break;
            }

            String mcDir = mc.getMethod("a", String.class).invoke(null, (Object) "minecraft").toString();

            System.out.println("MCDIR: " + mcDir);

            System.out.println("Launching with applet wrapper...");

            try {
                Class<?> MCAppletClass = cl.loadClass("net.minecraft.client.MinecraftApplet");
                Applet mcappl = (Applet) MCAppletClass.newInstance();
                MinecraftFrame mcWindow = new MinecraftFrame("Project Indigo");
                mcWindow.start(mcappl, username, sessionId);
            } catch (InstantiationException e) {
                System.out.println("Applet wrapper failed! Falling back to compatibility mode");
                mc.getMethod("main", String[].class).invoke(null, (Object) new String[] { username, sessionId });
            }
        } catch (Throwable t) {
            System.out.println("Unhandled error launching minecraft:");
            t.printStackTrace();
        }
        this.setVisible(false);
        this.dispose();
    }

    public void setVisible(boolean visible) {
        super.setVisible(visible);
    }

    public final Font getMinecraftFont(int size) {
        return IndigoLauncher.getMinecraftFont(size);
    }

    public Logger getLogger() {
        return logger;
    }

    public UserManager getUserManager() {
        return userManager;
    }
}
