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
package co.zmc.projectindigo.gui.components;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

import co.zmc.projectindigo.IndigoLauncher;
import co.zmc.projectindigo.data.Server;

@SuppressWarnings("serial")
public class ServerInfo extends JLabel {
    private final JLabel _info;

    public ServerInfo(JPanel pane, final Server server) {
        _info = new JLabel(server.getIp() + ":" + server.getPort() + " (" + server.getPlayersOnline() + "/" + server.getTotalOnline() + ")");
        setText(server.getName());
        pane.add(this, 0);
        pane.add(_info, 0);
        setVerticalAlignment(0);
        setHorizontalAlignment(0);
        setVerticalAlignment(1);
        setHorizontalAlignment(2);
        setForeground(Color.WHITE);
        setFont(IndigoLauncher.getMinecraftFont(20));
        _info.setForeground(Color.GRAY);
        _info.setFont(IndigoLauncher.getMinecraftFont(12));
        this.addMouseListener(new MouseListener() {

            public void mouseClicked(MouseEvent e) {
                IndigoLauncher._launcher.launchMinecraft(server);
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }
        });
    }

    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, y, w, h);
        _info.setBounds(x, y + 16, w, h);
    }

}