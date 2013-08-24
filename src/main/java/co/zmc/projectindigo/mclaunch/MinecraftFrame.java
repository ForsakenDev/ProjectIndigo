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
package co.zmc.projectindigo.mclaunch;

import java.applet.Applet;
import java.awt.Dimension;
import java.awt.Window;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.UIManager;

import net.minecraft.Launcher;
import co.zmc.projectindigo.utils.Utils;
import co.zmc.projectindigo.utils.Utils.OS;

@SuppressWarnings("serial")
public class MinecraftFrame extends JFrame {
    private Launcher appletWrap = null;
    Dimension        size       = new Dimension(900, 480);

    public MinecraftFrame(String title) {
        super(title);

        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Utils.getCurrentOS() == OS.MACOSX) {
            try {
                Class<?> fullScreenUtilityClass = Class.forName("com.apple.eawt.FullScreenUtilities");
                java.lang.reflect.Method setWindowCanFullScreenMethod = fullScreenUtilityClass.getDeclaredMethod("setWindowCanFullScreen",
                        new Class[] { Window.class, Boolean.TYPE });
                setWindowCanFullScreenMethod.invoke(null, new Object[] { this, Boolean.valueOf(true) });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        super.setVisible(true);
        setResizable(true);
        fixSize(size);
    }

    public void start(Applet mcApplet, String user, String session) {
        try {
            appletWrap = new Launcher(mcApplet, new URL("http://www.minecraft.net/game"));
        } catch (MalformedURLException ignored) {
        }
        appletWrap.setParameter("username", user);
        appletWrap.setParameter("sessionid", session);
        appletWrap.setParameter("stand-alone", "true");
        mcApplet.setStub(appletWrap);
        add(appletWrap);

        appletWrap.setPreferredSize(size);

        pack();
        validate();
        appletWrap.init();
        appletWrap.start();
        fixSize(size);
        setVisible(true);

    }

    private void fixSize(Dimension size) {
        setSize(size);
    }
}
