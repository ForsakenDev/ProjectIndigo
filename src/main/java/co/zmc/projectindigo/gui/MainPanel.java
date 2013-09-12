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
package co.zmc.projectindigo.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.util.logging.Logger;

import javax.swing.JPanel;

import co.zmc.projectindigo.IndigoLauncher;
import co.zmc.projectindigo.data.LoginResponse;
import co.zmc.projectindigo.gui.components.Image;
import co.zmc.projectindigo.gui.components.LoginSection;
import co.zmc.projectindigo.gui.components.ServerSection;
import co.zmc.projectindigo.managers.ServerManager;
import co.zmc.projectindigo.utils.Utils;
import co.zmc.projectindigo.utils.Utils.OS;

@SuppressWarnings("serial")
public class MainPanel extends JPanel {
    protected ServerManager       _serverManager;
    private LoginResponse         _loginResponse;
    public static final Color     BORDER_COLOUR    = new Color(45, 45, 45, 160);
    public static final Color     HIGHLIGHT_COLOUR = new Color(13, 86, 166, 200);

    public static final int       PADDING          = 10;
    public static final Dimension BTN_SIZE         = new Dimension(110, 24);

    protected IndigoLauncher      _launcher;
    protected final Logger        logger           = Logger.getLogger("launcher");
    private LoginSection          _loginSection;
    private ServerSection         _serverSection;

    public MainPanel(IndigoLauncher launcher, int width, int height) {
        _launcher = launcher;
        setLayout(null);
        setOpaque(false);
        if (Utils.getCurrentOS() == OS.MACOSX) {
            height -= 20;
        }
        setFont(IndigoLauncher.getMinecraftFont(14));
        Dimension dim = new Dimension(width, height);
        setSize(dim);
        setPreferredSize(dim);
        setBounds(0, 0, width, height);
        setupLook();

    }

    public void setupLook() {
        setLayout(null);
        _loginSection = new LoginSection(this);
        _serverSection = new ServerSection(this);
        add(new Image("bg", getWidth(), getHeight()));
    }

    public Logger getLogger() {
        return logger;
    }

    public String getUsername() {
        return _loginResponse.getUsername();
    }

    public final void setResponse(LoginResponse loginResponse) {
        _loginResponse = loginResponse;
    }

    public LoginResponse getLoginResponse() {
        return _loginResponse;
    }

    public ServerSection getServerSection() {
        return _serverSection;
    }

    public LoginSection getLoginSection() {
        return _loginSection;
    }

}
