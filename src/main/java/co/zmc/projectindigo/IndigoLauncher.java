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
package co.zmc.projectindigo;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import co.zmc.projectindigo.data.LoginResponse;
import co.zmc.projectindigo.data.Server;
import co.zmc.projectindigo.gui.LoginPanel;
import co.zmc.projectindigo.gui.ServerPanel;
import co.zmc.projectindigo.gui.components.ProgressSplashScreen;
import co.zmc.projectindigo.utils.DirectoryLocations;
import co.zmc.projectindigo.utils.ResourceUtils;
import co.zmc.projectindigo.utils.Utils;

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
    public static String policyLocation = "";
    private ArrayList<String> additionalPerms = new ArrayList<String>();

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

    private String forgename = "MinecraftForge.zip";

    public Process launchMinecraft(Server server) {
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
        cpb.append(new File(instModsDir, forgename).getAbsolutePath());

        for (String jarFile : jarFiles) {
            cpb.append(Utils.getJavaDelimiter());
            cpb.append(new File(server.getBinDir(), jarFile).getAbsolutePath());
        }
        copySecurityPolicy();
        String nativesDirPermFormatted = server.getBaseDir().getAbsolutePath().replaceAll("\\\\", "/") + "/minecraft/bin/natives/";
        //Mac and linux natives will need to be specified individually as well. I have no idea what they are.
        addAdditionalPerm("permission java.lang.RuntimePermission \"loadLibrary." + nativesDirPermFormatted + "jinput-dx8.dll" + "\"");
        addAdditionalPerm("permission java.lang.RuntimePermission \"loadLibrary." + nativesDirPermFormatted + "jinput-dx8_64.dll" + "\"");
        addAdditionalPerm("permission java.lang.RuntimePermission \"loadLibrary." + nativesDirPermFormatted + "jinput-raw.dll" + "\"");
        addAdditionalPerm("permission java.lang.RuntimePermission \"loadLibrary." + nativesDirPermFormatted + "jinput-raw_64.dll" + "\"");
        addAdditionalPerm("permission java.lang.RuntimePermission \"loadLibrary." + nativesDirPermFormatted + "lwjgl.dll" + "\"");
        addAdditionalPerm("permission java.lang.RuntimePermission \"loadLibrary." + nativesDirPermFormatted + "lwjgl64.dll" + "\"");
        addAdditionalPerm("permission java.lang.RuntimePermission \"loadLibrary." + nativesDirPermFormatted + "OpenAL32.dll" + "\"");
        addAdditionalPerm("permission java.lang.RuntimePermission \"loadLibrary." + nativesDirPermFormatted + "OpenAL64.dll" + "\"");
        
        addAdditionalPerm("permission java.io.FilePermission \"" + server.getBaseDir().getParent().replaceAll("\\\\", "/") + "/-\", \"read, write, delete\"");
        addAdditionalPerm("permission java.io.FilePermission \"" + System.getProperty("java.io.tmpdir").replaceAll("\\\\", "/") + "-\", \"read, write, delete\"");
        
        addAdditionalPerm("permission java.net.SocketPermission \"" + server.getIp() +"\", \"accept, resolve, listen, connect\"");
        
        writeAdditionalPerms(policyLocation);
        
        List<String> arguments = new ArrayList<String>();

        String separator = System.getProperty("file.separator");
        String path = System.getProperty("java.home") + separator + "bin" + separator + "java"
                + (Utils.getCurrentOS() == Utils.OS.WINDOWS ? "w" : "");
        arguments.add(path);

        arguments.add("-XX:+UseConcMarkSweepGC");
        arguments.add("-XX:+CMSIncrementalMode");
        arguments.add("-XX:+AggressiveOpts");
        arguments.add("-XX:+CMSClassUnloadingEnabled");
        arguments.add("-XX:PermSize=128m");

        arguments.add("-cp");
        arguments.add(System.getProperty("java.class.path") + cpb.toString());

        arguments.add(IndigoLauncher.class.getCanonicalName());
        arguments.add(server.getBaseDir().getName());
        arguments.add(forgename);
        arguments.add(_loginResponse.getUsername());
        arguments.add(_loginResponse.getSessionId());
        arguments.add(TITLE);

        for (String arg : arguments) {
        	System.out.println(arg);
        }
        
        ProcessBuilder processBuilder = new ProcessBuilder(arguments);
        processBuilder.redirectErrorStream(true);
        try {
            return processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean copySecurityPolicy() {
    	InputStream policy = IndigoLauncher.class.getResourceAsStream("/co/zmc/projectindigo/resources/security/security.policy");
    	File newPolicyFile = new File(DirectoryLocations.DATA_DIR_LOCATION + "security.policy");
    	policyLocation = newPolicyFile.getAbsolutePath();
    	System.out.println("Copying over new security policy.");
    	try {
			OutputStream newOut = new FileOutputStream(newPolicyFile);
			byte[] buffer = new byte[1024];
			int read;
			while ((read = policy.read(buffer)) > 0) {
				newOut.write(buffer, 0, read);
			}
			newOut.close();
			policy.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	System.out.println("Success.");
    	return true;
    }
    
    public void addAdditionalPerm(String perm) {
    	additionalPerms.add("\ngrant{" + perm + ";};");
    }
    
    public void writeAdditionalPerms(String location) {
    	try {
    		FileWriter out = new FileWriter(location, true);
    		out.write("\n//AUTO-GENERATED PERMS BEGIN\n");
    		for (String perm : additionalPerms) {
        		System.out.println("Writing additional perm " + perm.replaceAll("\n", ""));
    			out.write(perm);
    		}
    		out.flush();
    		out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
}
