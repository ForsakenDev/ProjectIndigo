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
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import net.minecraft.Launcher;

@SuppressWarnings("serial")
public class MinecraftFrame extends JFrame {
    private Launcher appletWrap = null;

    public MinecraftFrame(String title) {
        super(title);
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception e1) {
            }
        }

//         setIconImage(Toolkit.getDefaultToolkit().createImage(imagePath));
        super.setVisible(true);
        setResizable(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                new Thread() {
                    public void run() {
                        try {
                            Thread.sleep(30000L);
                        } catch (InterruptedException localInterruptedException) {
                        }
                        Logger.getLogger("launcher").log(Level.SEVERE, "FORCING EXIT!");
                        System.exit(0);
                    }
                }.start();
                if (appletWrap != null) {
                    appletWrap.stop();
                    appletWrap.destroy();
                }
                System.exit(0);
            }
        });
    }

    public void start(Applet mcApplet, String basePath, String user, String session, String ip, String port) {

        try {
            appletWrap = new Launcher(mcApplet, new URL("http://www.minecraft.net/game"));
        } catch (MalformedURLException ignored) {
        }
        appletWrap.addParameter("working_directory", basePath);
        appletWrap.addParameter("username", user);
        appletWrap.addParameter("sessionid", session);
        appletWrap.addParameter("stand-alone", "true");
        // appletWrap.addParameter("server", ip);
        // appletWrap.addParameter("port", port);
        mcApplet.setStub(appletWrap);
        add(appletWrap);

        Dimension size = new Dimension(900, 480);
        appletWrap.setPreferredSize(size);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        pack();
        validate();
        appletWrap.init();
        appletWrap.start();
        setVisible(true);
    }
}
