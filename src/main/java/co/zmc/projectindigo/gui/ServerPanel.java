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

import java.awt.Color;
import java.awt.Dimension;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import co.zmc.projectindigo.IndigoLauncher;
import co.zmc.projectindigo.gui.components.Image;
import co.zmc.projectindigo.gui.components.ProgressBar;
import co.zmc.projectindigo.gui.components.TextBox;
import co.zmc.projectindigo.gui.components.TransparentButton;
import co.zmc.projectindigo.gui.components.TransparentImage;
import co.zmc.projectindigo.managers.ServerManager;

@SuppressWarnings("serial")
public class ServerPanel extends JPanel {
    protected IndigoLauncher    _launcher;
    protected final Logger      logger = Logger.getLogger("launcher");
    protected TextBox           _serverIP;
    protected TransparentButton _launchBtn;
    protected ProgressBar       _progressBar;
    protected ServerManager     _serverManager;

    public ServerPanel(IndigoLauncher launcher, int width, int height) {
        _launcher = launcher;
        setLayout(null);
        setOpaque(false);
        setFont(IndigoLauncher.getMinecraftFont(14));
        Dimension dim = new Dimension(width, height);
        setSize(dim);
        setPreferredSize(dim);
        setBounds(0, 0, width, height);
        setupLook();
    }

    public void setupLook() {
        _serverManager = new ServerManager();
        _serverManager.execute();

        int padding = 25;
        int btnWidth = 149;
        int btnHeight = 24;

        _serverIP = new TextBox(this, "Server IP Address...");
        _launchBtn = new TransparentButton(this, "Launch", 0.8F, Color.GREEN);

        _launchBtn.setBounds(getWidth() - padding - 149, getHeight() - btnHeight - (padding / 2) * 3, btnWidth, btnHeight);
        _serverIP.setBounds(padding, getHeight() - btnHeight - (padding / 2) * 3, (getWidth() - (padding * 3) - 149), btnHeight);

        add(new TransparentImage("main_bg", 0.7F, getWidth(), getHeight()));
        add(new Image("bg", getWidth(), getHeight()));
    }

    public Logger getLogger() {
        return logger;
    }

    public void stateChanged(final String status, final float progress) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                if (!_progressBar.isVisible()) {
                    _progressBar.setVisible(true);
                }
                int intProgress = Math.round(progress);
                _progressBar.setValue(intProgress);
                String text = status;
                if (text.length() > 60) {
                    text = text.substring(0, 60) + "...";
                }
                _progressBar.setString(intProgress + "% " + text);
            }
        });
    }

}
