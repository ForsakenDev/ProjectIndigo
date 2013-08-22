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
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPasswordField;

import co.zmc.projectindigo.IndigoLauncher;

@SuppressWarnings("serial")
public class PasswordBox extends JPasswordField implements FocusListener {
    private final JLabel label;

    public PasswordBox(JLayeredPane parent, String label) {
        this.label = new JLabel(label);
        addFocusListener(this);
        parent.add(this, 0);
        parent.add(this.label, 0);
        setBackground(Color.WHITE);
        setBorder(new Border(5, getBackground()));
        setEchoChar('*');
        this.label.setForeground(Color.BLACK);
        setFont(IndigoLauncher.getMinecraftFont(14));
    }

    public void setFont(Font font) {
        super.setFont(font);
        if (this.label != null) this.label.setFont(font);
    }

    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, y, w, h);
        this.label.setBounds(x + 5, y + 3, w - 5, h - 5);
    }

    public void setText(String text) {
        super.setText(text);
        this.label.setVisible((text == null) || (text.length() <= 0));
    }

    public void focusGained(FocusEvent e) {
        this.label.setVisible(false);
    }

    public void focusLost(FocusEvent e) {
        if (getPassword().length == 0) this.label.setVisible(true);
    }
}