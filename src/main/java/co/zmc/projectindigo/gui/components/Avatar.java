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
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import co.zmc.projectindigo.IndigoLauncher;
import co.zmc.projectindigo.gui.LoginPanel;
import co.zmc.projectindigo.utils.DirectoryLocations;
import co.zmc.projectindigo.utils.ResourceUtils;

@SuppressWarnings("serial")
public class Avatar extends JLabel implements MouseListener {
    private String       _username;
    private String       _accountKey;
    private final JLabel label;
    private LoginPanel   _loginFrame;

    public Avatar(LoginPanel loginFrame, String username, String accountKey) {
        this(loginFrame, username, accountKey, 150);
    }

    public Avatar(LoginPanel loginFrame, String username, String accountKey, int width) {
        this.label = new JLabel(username);
        _username = username;
        _accountKey = accountKey;
        _loginFrame = loginFrame;
        loginFrame.add(this, 0);
        loginFrame.add(this.label, 0);
        setBorder(null);
        setFocusable(false);
        addMouseListener(this);
        Dimension dim = new Dimension(width, width);
        setSize(dim);
        setPreferredSize(dim);
        dim = new Dimension(getLabelWidth(), 24);
        label.setSize(dim);
        label.setPreferredSize(dim);
        setVerticalAlignment(0);
        setHorizontalAlignment(0);
        setVerticalAlignment(1);
        setHorizontalAlignment(2);
        setIcon(new ImageIcon(getImage().getScaledInstance(width, width, 4)));
        label.setForeground(Color.WHITE);
        label.setFont(IndigoLauncher.getMinecraftFont(14));
        label.setVisible(false);
        setBounds(0, 0, width, width);
    }

    @Override
    public void setIcon(Icon icon) {
        super.setIcon(icon);
        setDisabledIcon(getIcon());
    }

    public void setBounds(int x, int y, int w, int h) {
        int fontSize = getFontSize(w);
        h -= (fontSize + 5);
        w = h;
        super.setBounds(x, y, w, h);
        setIcon(new ImageIcon(getImage().getScaledInstance(w, h, 4)));
        label.setBounds(x + (w / 2) - (getLabelWidth() / 2), y + h + 5, getLabelWidth(), fontSize);
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
        return (int) (label.getFont().getStringBounds(label.getText(), frc).getWidth()) + ((1 * label.getText().length()) / 2);
    }

    public String getAccountKey() {
        return _accountKey;
    }

    public String getUsername() {
        return _username;
    }

    private BufferedImage getImage() {
        try {
            return ResourceUtils.loadCachedImage("http://www.zephyrunleashed.com/avatar/" + _username, DirectoryLocations.AVATAR_CACHE_DIR_LOCATION,
                    ImageIO.read(ResourceUtils.getResource("base_char")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new BufferedImage(getWidth(), getHeight(), 2);
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
        if (super.isEnabled()) {
            this.label.setVisible(true);
        }
    }

    public void mouseExited(MouseEvent e) {
        if (super.isEnabled()) {
            this.label.setVisible(false);
        }
    }

    public void mousePressed(MouseEvent e) {
        if (super.isEnabled()) {
            _loginFrame.tryLogin(getAccountKey());
        }
    }

    public void mouseReleased(MouseEvent e) {
    }

}
