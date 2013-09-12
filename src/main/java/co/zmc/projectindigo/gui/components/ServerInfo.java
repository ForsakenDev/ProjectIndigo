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
package co.zmc.projectindigo.gui.components;

import java.awt.Color;
import java.awt.font.FontRenderContext;

import javax.swing.JLabel;
import javax.swing.JLayeredPane;

import co.zmc.projectindigo.IndigoLauncher;
import co.zmc.projectindigo.data.Server;
import co.zmc.projectindigo.gui.MainPanel;

@SuppressWarnings("serial")
public class ServerInfo extends JLabel {
    private JLabel     _ip;
    private JLabel     _users;
    private Server     _server;
    private boolean    _active = false;
    private RoundedBox _serverBox;

    public ServerInfo(JLayeredPane pane, Server server) {
        _server = server;
        _ip = new JLabel(_server.getFullIp());
        _users = new JLabel(_server.getPlayers() + "/" + _server.getMaxPlayers());
        setText(server.getName());
        _serverBox = new RoundedBox(MainPanel.HIGHLIGHT_COLOUR);
        _serverBox.setVisible(_active);
        pane.add(_serverBox);
        pane.add(this, 0);
        pane.add(_ip, 0);
        pane.add(_users, 0);

        setForeground(Color.WHITE);
        setFont(IndigoLauncher.getMinecraftFont(20));

        _ip.setForeground(Color.GRAY);
        _ip.setFont(IndigoLauncher.getMinecraftFont(12));

        _users.setForeground(Color.WHITE);
        _users.setFont(IndigoLauncher.getMinecraftFont(20));
    }

    public void setBounds(int x, int y, int w, int h) {
        _serverBox.setBounds(x - MainPanel.PADDING, y - MainPanel.PADDING, w + (MainPanel.PADDING * 2), h + 12 + 5 + (MainPanel.PADDING * 2));
        w -= getUserWidth() + MainPanel.PADDING;
        super.setBounds(x, y, w, h);
        _users.setBounds(x + w + MainPanel.PADDING, y, getUserWidth(), h);
        _ip.setBounds(x, y + h + 5, w, 12);
    }

    private int getUserWidth() {
        FontRenderContext frc = new FontRenderContext(_users.getFont().getTransform(), true, true);
        return (int) (_users.getFont().getStringBounds(_users.getText(), frc).getWidth());
    }

    public void setActive(boolean active) {
        System.out.println("Active");
        _active = active;
        _serverBox.setVisible(active);

    }
}