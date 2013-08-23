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
import java.awt.Dimension;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

import co.zmc.projectindigo.IndigoLauncher;
import co.zmc.projectindigo.utils.DirectoryLocations;
import co.zmc.projectindigo.utils.DrawingUtils;
import co.zmc.projectindigo.utils.ResourceUtils;

@SuppressWarnings("serial")
public class ServerIcon extends JLabel {
    private int          _serverId;
    private String       _serverName;
    private final JLabel info;
    private final JLabel edit;
    private final JLabel label;

    public ServerIcon(JLayeredPane pane, int serverId, String serverName) {
        this(pane, serverId, serverName, 150);
    }

    public ServerIcon(JLayeredPane pane, int serverId, String serverName, int width) {
        this.label = new JLabel(serverName);
        this.edit = new JLabel();
        this.info = new JLabel();
        _serverId = serverId;
        _serverName = serverName;
        pane.add(this, 0);
        pane.add(this.label, 0);
        pane.add(this.edit, 0);
        pane.add(this.info, 0);

        try {
            info.setDisabledIcon(new ImageIcon(ImageIO.read(ResourceUtils.getResource("server_info"))));
            info.setIcon(new ImageIcon(ImageIO.read(ResourceUtils.getResource("server_info_hover"))));
            edit.setDisabledIcon(new ImageIcon(ImageIO.read(ResourceUtils.getResource("server_edit"))));
            edit.setIcon(new ImageIcon(ImageIO.read(ResourceUtils.getResource("server_edit_hover"))));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Dimension dim = new Dimension(width, width);
        setSize(dim);
        setPreferredSize(dim);
        dim = new Dimension(edit.getIcon().getIconWidth(), edit.getIcon().getIconHeight());
        edit.setSize(dim);
        edit.setPreferredSize(dim);
        edit.setEnabled(false);
        dim = new Dimension(info.getIcon().getIconWidth(), info.getIcon().getIconHeight());
        info.setSize(dim);
        info.setPreferredSize(dim);
        info.setEnabled(false);

        setVerticalAlignment(0);
        setHorizontalAlignment(0);
        setIcon(new ImageIcon(getImage().getScaledInstance(width, width, 4)));
        setVerticalAlignment(1);
        setHorizontalAlignment(2);
        this.label.setForeground(Color.WHITE);
        this.label.setFont(IndigoLauncher.getMinecraftFont(14));
        label.setCursor(null);
        label.setOpaque(false);
        setBounds(0, 0, width, width);
    }

    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, y, w, h);
        label.setBounds(x + ((getWidth() / 2) - (getLabelWidth() / 2)), y + getHeight() + 5, getLabelWidth(), 24);
        info.setBounds(x + ((getWidth() / 2) - info.getWidth()), y - info.getHeight(), info.getWidth(), info.getHeight());
        edit.setBounds(x + ((getWidth() / 2)), y - edit.getHeight(), edit.getWidth(), edit.getHeight());

    }

    private int getLabelWidth() {
        FontRenderContext frc = new FontRenderContext(label.getFont().getTransform(), true, true);
        return (int) (label.getFont().getStringBounds(label.getText(), frc).getWidth()) + 10;
    }

    public String getServerName() {
        return _serverName;
    }

    private BufferedImage getImage() {
        try {
            return DrawingUtils.overlayImage(ResourceUtils.loadCachedImage("http://www.zephyrunleashed.com/data/server/" + _serverId + ".png",
                    DirectoryLocations.SERVER_CACHE_DIR_LOCATION, ImageIO.read(ResourceUtils.getResource("server_default"))), ImageIO
                    .read(ResourceUtils.getResource("overlay")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new BufferedImage(getWidth(), getHeight(), 2);
    }
}
