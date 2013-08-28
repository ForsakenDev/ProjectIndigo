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
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

import co.zmc.projectindigo.IndigoLauncher;
import co.zmc.projectindigo.utils.ResourceUtils;

@SuppressWarnings("serial")
public class ServerIcon extends JLabel {
    private String       _serverName;
    private final JLabel info;
    private final JLabel edit;
    private final JLabel label;

    public ServerIcon(JLayeredPane pane, String serverName) {
        this(pane, serverName, 150, 150);
    }

    public ServerIcon(JLayeredPane pane, String serverName, int width, int height) {
        this.label = new JLabel(serverName);
        this.edit = new JLabel();
        this.info = new JLabel();
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

        Dimension dim = new Dimension(edit.getIcon().getIconWidth(), edit.getIcon().getIconHeight());
        edit.setSize(dim);
        edit.setPreferredSize(dim);
        edit.setEnabled(false);
        dim = new Dimension(info.getIcon().getIconWidth(), info.getIcon().getIconHeight());
        info.setSize(dim);
        info.setPreferredSize(dim);
        info.setEnabled(false);
        int iconHeight = height - 24 - info.getIcon().getIconHeight();
        dim = new Dimension(iconHeight, iconHeight);
        setSize(dim);
        setPreferredSize(dim);

        setVerticalAlignment(0);
        setHorizontalAlignment(0);
        setVerticalAlignment(1);
        setHorizontalAlignment(2);
        this.label.setForeground(Color.WHITE);
        this.label.setFont(IndigoLauncher.getMinecraftFont(14));
        label.setCursor(null);
        label.setOpaque(false);
    }

    public void setBounds(int x, int y, int w, int h) {
        int fontSize = getFontSize(w);
        y += info.getHeight();
        h -= (info.getHeight() + fontSize + 5);
        w = h;
        super.setBounds(x, y, w, h);
        label.setBounds(x + (w / 2) - (getLabelWidth() / 2), y + h + 5, getLabelWidth(), fontSize);
        info.setBounds(x + (w / 2) - info.getWidth(), y - info.getHeight(), info.getWidth(), info.getHeight());
        edit.setBounds(x + (w / 2), y - edit.getHeight(), edit.getWidth(), edit.getHeight());
    }

    private int getFontSize(int width) {
        for (int i = 15; i > 1; i--) {
            this.label.setFont(IndigoLauncher.getMinecraftFont(i));
            if (getLabelWidth() <= width) { return i - (i % 2); }
        }
        return 2;
    }

    private int getLabelWidth() {
        FontRenderContext frc = new FontRenderContext(label.getFont().getTransform(), true, true);
        return (int) (label.getFont().getStringBounds(label.getText(), frc).getWidth());
    }

    public String getServerName() {
        return _serverName;
    }

}
