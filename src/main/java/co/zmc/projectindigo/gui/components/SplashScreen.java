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
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JWindow;

import co.zmc.projectindigo.utils.ResourceUtils;

@SuppressWarnings("serial")
public class SplashScreen extends JWindow {
    protected final ImageIcon _icon;
    protected final Image     _image = Toolkit.getDefaultToolkit().getImage(ResourceUtils.getResource("splash_screen"));

    public SplashScreen() {
        this._icon = new ImageIcon(_image);

        Container container = getContentPane();
        container.setLayout(null);

        BufferedImage alphaImage = new BufferedImage(_icon.getIconWidth(), _icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = alphaImage.createGraphics();
        g.drawImage(_image, 0, 0, _icon.getIconWidth(), _icon.getIconHeight(), null);
        g.dispose();

        JButton background = new JButton(new ImageIcon(alphaImage));
        background.setBounds(0, 0, _icon.getIconWidth(), _icon.getIconHeight());
        background.setRolloverEnabled(true);
        background.setRolloverIcon(background.getIcon());
        background.setSelectedIcon(background.getIcon());
        background.setDisabledIcon(background.getIcon());
        background.setPressedIcon(background.getIcon());
        background.setFocusable(false);
        background.setContentAreaFilled(false);
        background.setBorderPainted(false);
        background.setOpaque(false);
        container.add(background);

        setSize(_icon.getIconWidth(), _icon.getIconHeight() + 20);
        try {
            this.setBackground(new Color(0, 0, 0, 0));
        } catch (UnsupportedOperationException e) {
            this.setBackground(new Color(0, 0, 0));
        }
        setLocationRelativeTo(null);
    }
}
