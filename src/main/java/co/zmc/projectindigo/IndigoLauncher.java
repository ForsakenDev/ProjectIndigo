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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import co.zmc.projectindigo.data.LoginResponse;
import co.zmc.projectindigo.gui.LoginPanel;
import co.zmc.projectindigo.gui.ServerPanel;
import co.zmc.projectindigo.gui.components.ProgressSplashScreen;
import co.zmc.projectindigo.utils.DirectoryLocations;
import co.zmc.projectindigo.utils.ResourceUtils;
import co.zmc.projectindigo.utils.Utils;

@SuppressWarnings("serial")
public class IndigoLauncher extends JFrame {
    public static final String    TITLE            = "Project Indigo";
    private static IndigoLauncher _launcher;
    private LoginResponse         _loginResponse;
    public Dimension              _loginPanelSize  = new Dimension(400, 200);
    public Dimension              _serverPanelSize = new Dimension(900, 580);
    public ServerPanel            _serverPanel;
    public LoginPanel             _loginPanel;
    public ProgressSplashScreen   _splash;

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
        System.setProperty("java.net.preferIPv4Stack", "true");
        if (Utils.getCurrentOS() == Utils.OS.MACOSX) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Indigo");
        }

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

    public void launchServerFrame() {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        this.setMaximizedBounds(new Rectangle((env.getCenterPoint().x - (_serverPanelSize.width / 2)),
                (env.getCenterPoint().y - (_serverPanelSize.height / 2)), _serverPanelSize.width, _serverPanelSize.height));
        this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);

        setLocationRelativeTo(null);
        _loginPanel.setVisible(false);
        _serverPanel.setVisible(true);

    }

    public LoginResponse getLoginResponse() {
        return _loginResponse;
    }
}
