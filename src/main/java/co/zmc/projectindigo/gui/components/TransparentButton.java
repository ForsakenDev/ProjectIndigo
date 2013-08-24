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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JComponent;

import co.zmc.projectindigo.IndigoLauncher;

@SuppressWarnings("serial")
public class TransparentButton extends JButton implements MouseListener {
    private boolean clicked            = false;
    private float   _transparencyLevel = 1F;
    private boolean _isToggle          = false;
    private boolean _isHover           = false;
    private Color   _rolloverColor;

    public TransparentButton(JComponent frame, String label, float transparencyLevel, Color rolloverColor) {
        this(frame, label, transparencyLevel);
        _rolloverColor = rolloverColor;
    }

    public TransparentButton(JComponent frame, String label, float transparencyLevel, boolean isToggle) {
        this(frame, label, transparencyLevel);
        _isToggle = isToggle;
    }

    public TransparentButton(JComponent frame, String label, float transparencyLevel) {
        _transparencyLevel = transparencyLevel;
        setText(label);
        setBackground(Color.WHITE);
        _rolloverColor = Color.GRAY;
        addMouseListener(this);
        setFont(IndigoLauncher.getMinecraftFont(14));
        frame.add(this, 0);
        this.setRolloverEnabled(true);
    }

    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Color old = g2d.getColor();
        Composite comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, _transparencyLevel);
        g2d.setComposite(comp);

        g2d.setColor(this.clicked ? Color.BLACK : (_isHover ? _rolloverColor : getBackground()));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setColor(this.clicked ? (_isHover ? _rolloverColor : getBackground()) : Color.BLACK);
        g2d.setFont(getFont());
        int width = g2d.getFontMetrics().stringWidth(getText());
        g2d.drawString(getText(), (getWidth() - width) / 2, getFont().getSize() + 4);

        g2d.setColor(old);
        g2d.dispose();
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.clicked = !enabled;
        repaint();
    }

    public boolean isClicked() {
        return clicked;
    }

    public void mouseClicked(MouseEvent e) {
        if (_isToggle) {
            this.clicked = !this.clicked;
        }
    }

    public void mousePressed(MouseEvent e) {
        if (!_isToggle) {
            this.clicked = true;
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (!_isToggle) {
            this.clicked = false;
        }
    }

    public void mouseEntered(MouseEvent e) {
        if (!_isHover) {
            _isHover = true;
        }
    }

    public void mouseExited(MouseEvent e) {
        if (_isHover) {
            _isHover = false;
        }
    }
}