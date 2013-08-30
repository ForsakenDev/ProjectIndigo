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
package co.zmc.projectindigo;

import java.applet.Applet;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import co.zmc.projectindigo.data.LoginResponse;
import co.zmc.projectindigo.data.Server;
import co.zmc.projectindigo.gui.LoginPanel;
import co.zmc.projectindigo.gui.ServerPanel;
import co.zmc.projectindigo.gui.components.ProgressSplashScreen;
import co.zmc.projectindigo.mclaunch.MinecraftFrame;
import co.zmc.projectindigo.utils.DirectoryLocations;
import co.zmc.projectindigo.utils.ResourceUtils;

@SuppressWarnings("serial")
public class IndigoLauncher extends JFrame {
    public static final String   TITLE            = "Project Indigo";
    public static IndigoLauncher _launcher;
    public Dimension             _loginPanelSize  = new Dimension(400, 200);
    public Dimension             _serverPanelSize = new Dimension(900, 580);
    private LoginResponse        _loginResponse;
    public ServerPanel           _serverPanel;
    public LoginPanel            _loginPanel;
    public ProgressSplashScreen  _splash;

    public IndigoLauncher() {
        _launcher = this;
        _splash = new ProgressSplashScreen("Loading assets...", 20);
        _splash.setVisible(true);
        _splash.updateProgress("Cleaning directories...", 40);
        cleanup();
        _splash.updateProgress("Setting system values...", 60);
        setLookandFeel();
        initComponents();
        _splash.updateProgress("Launching login...", 100);
        launchLoginFrame();
        _splash.dispose();
        setVisible(true);
    }

    private void setLookandFeel() {
        setTitle(IndigoLauncher.TITLE);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        setSize(_loginPanelSize);
        setPreferredSize(_loginPanelSize);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        _loginPanel = new LoginPanel(_launcher, _loginPanelSize.width, _loginPanelSize.height);
        _loginPanel.setVisible(true);
        add(_loginPanel);

        _serverPanel = new ServerPanel(_launcher, _serverPanelSize.width, _serverPanelSize.height);
        _serverPanel.setVisible(false);
        add(_serverPanel);
    }

    private static void cleanup() {
        File file = new File(DirectoryLocations.BASE_DIR_LOCATION);
        if (!file.exists()) {
            file.mkdir();
        }
        file = new File(DirectoryLocations.DATA_DIR_LOCATION);
        if (!file.exists()) {
            file.mkdir();
        }
        file = new File(DirectoryLocations.IMAGE_DIR_LOCATION);
        if (!file.exists()) {
            file.mkdir();
        }
        file = new File(DirectoryLocations.AVATAR_CACHE_DIR_LOCATION);
        if (!file.exists()) {
            file.mkdir();
        }
        file = new File(DirectoryLocations.SERVERS_BASE_DIR_LOCATION);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    public static final Font getMinecraftFont(int size) {
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, ResourceUtils.getResourceAsStream("minecraft_font"));
            font = font.deriveFont((float) size);
            return font;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FontFormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    public final void setResponse(LoginResponse loginResponse) {
        _loginResponse = loginResponse;
    }

    public void refresh() {
        repaint();
    }

    public void launchLoginFrame() {
        setSize(_loginPanelSize);
        setPreferredSize(_loginPanelSize);
        setLocationRelativeTo(null);

        _loginPanel.setVisible(true);
        _serverPanel.setVisible(false);
    }

    private long startTime = 0;

    public void launchServerFrame() {
        _serverPanel.setVisible(true);
        _loginPanel.setVisible(false);

        final int playTime = 400;
        double xDist = _serverPanelSize.width - _loginPanelSize.width;
        double yDist = _serverPanelSize.height - _loginPanelSize.height;
        final double xAccel = 2 * xDist * Math.pow(playTime, -2);
        final double yAccel = 2 * yDist * Math.pow(playTime, -2);

        Timer timer = new Timer(10, new ActionListener() {
            double xVelocity = 0;
            double yVelocity = 0;
            double xSize     = _launcher.getSize().width;
            double ySize     = _launcher.getSize().height;
            double xLocation = _launcher.getLocation().x;
            double yLocation = _launcher.getLocation().y;
            long   lastTime  = 0;

            public void actionPerformed(ActionEvent e) {
                if (lastTime == 0) {
                    lastTime = System.currentTimeMillis();
                }

                long deltaTime = System.currentTimeMillis() - lastTime;
                lastTime = System.currentTimeMillis();

                xVelocity += xAccel * deltaTime;
                yVelocity += yAccel * deltaTime;

                xSize += xVelocity * deltaTime;
                ySize += yVelocity * deltaTime;

                xLocation -= (xVelocity * deltaTime) / 2;
                yLocation -= (yVelocity * deltaTime) / 2;

                _launcher.setSize((int) xSize, (int) ySize);
                _launcher.setLocation((int) xLocation, (int) yLocation);

                if (System.currentTimeMillis() - startTime > playTime) {
                    Dimension screenRes = Toolkit.getDefaultToolkit().getScreenSize();
                    _launcher.setSize(_serverPanelSize);
                    _launcher.setLocation((screenRes.width - _launcher.getWidth()) / 2, (screenRes.height - _launcher.getHeight()) / 2);
                    ((Timer) e.getSource()).stop();
                }
            }
        });
        startTime = System.currentTimeMillis();
        timer.start();

        setLocationRelativeTo(null);

    }

    public LoginResponse getLoginResponse() {
        return _loginResponse;
    }

    public void launchMinecraft(Server server) {
        try {
            System.out.println("Loading jars...");
            String[] jarFiles = new String[] { "minecraft.jar", "lwjgl.jar", "lwjgl_util.jar", "jinput.jar" };
            ArrayList<File> classPathFiles = new ArrayList<File>();

            for (String jarFile : jarFiles) {
                classPathFiles.add(new File(server.getBinDir(), jarFile));
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
            String nativesDir = new File(server.getBinDir(), "natives").toString();
            System.out.println("Natives loaded...");

            System.setProperty("org.lwjgl.librarypath", nativesDir);
            System.setProperty("net.java.games.input.librarypath", nativesDir);

            System.setProperty("user.home", server.getBaseDir().getParent());

            URLClassLoader cl = new URLClassLoader(urls, IndigoLauncher.class.getClassLoader());

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
                f.set(null, server.getBaseDir());
                System.out.println("Fixed Minecraft Path: Field was " + f.toString());
                break;
            }

            String mcDir = mc.getMethod("a", String.class).invoke(null, (Object) "minecraft").toString();

            System.out.println("MCDIR: " + mcDir);

            System.out.println("Launching with applet wrapper...");

            try {
                Class<?> MCAppletClass = cl.loadClass("net.minecraft.client.MinecraftApplet");
                Applet mcappl = (Applet) MCAppletClass.newInstance();
                MinecraftFrame mcWindow = new MinecraftFrame(TITLE);
                mcWindow.start(mcappl, _loginResponse.getUsername(), _loginResponse.getSessionId());
            } catch (InstantiationException e) {
                System.out.println("Applet wrapper failed! Falling back to compatibility mode");
                mc.getMethod("main", String[].class).invoke(null,
                        (Object) new String[] { _loginResponse.getUsername(), _loginResponse.getSessionId() });
            }
        } catch (Throwable t) {
            System.out.println("Unhandled error launching minecraft:");
            t.printStackTrace();
        }
        this.setVisible(false);
        this.dispose();
    }
}
