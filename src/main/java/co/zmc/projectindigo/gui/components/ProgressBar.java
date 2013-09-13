/*
 * This file is part of Project Indigo.
 *
 * Copyright (c) 2013 ZephyrUnleashed LLC <http://www.zephyrunleashed.com/>
 * Project Indigo is licensed under the ZephyrUnleashed License Version 1.
 *
 * Project Indigo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the ZephyrUnleashed License Version 1.
 *
 * Project Indigo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the ZephyrUnleashed License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License.
 */
/*
 * This file is part of Indigo Launcher.
 *
 * Copyright (c) 2013 ZephyrUnleashed LLC <http://www.zephyrunleashed.com/>
 * Indigo Launcher is licensed under the ZephyrUnleashed License Version 1.
 *
 * Indigo Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the ZephyrUnleashed License Version 1.
 *
 * Indigo Launcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the ZephyrUnleashed License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License.
 */
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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JProgressBar;

import co.zmc.projectindigo.IndigoLauncher;

@SuppressWarnings("serial")
public class ProgressBar extends JProgressBar {

    private float _transparency = 1f;
    private Color activeColor   = Color.BLUE;

    public ProgressBar(float transparency) {
        _transparency = transparency;
        setFocusable(false);
        setOpaque(true);
        setStringPainted(true);
        setFont(IndigoLauncher.getMinecraftFont(14));
    }

    public Graphics cleanup(Graphics g) {
        return g;
    }

    public void updateProgress(String reason, int percent) {
        if (percent >= 0 && percent <= 100) {
            if (!isVisible()) {
                setVisible(true);
            }
            setValue(percent);
            String text = reason;
            if (text.length() > 60) {
                text = text.substring(0, 60) + "...";
            }
            setString(percent + "% " + text);
            setValue(percent);
        }
    }

    public void updateProgress(int percent) {
        if (percent >= 0 && percent <= 100) {
            if (!isVisible()) {
                setVisible(true);
            }
            setString(percent + "%");
            setValue(percent);
        }
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, _transparency));

        g2.clearRect(0, 0, getWidth(), getHeight());

        // Draw bar
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Draw progress
        g2.setColor(activeColor);
        int x = (int) (getWidth() * getPercentComplete());
        g2.fillRect(0, 0, x, getHeight());

        g2.dispose();
        g2 = (Graphics2D) g;

        if (this.isStringPainted() && getString().length() > 0) {
            g2.setFont(getFont());

            final int startWidth = (getWidth() - g2.getFontMetrics().stringWidth(getString())) / 2;
            String white = "";
            int whiteWidth = 0;
            int chars = 0;
            for (int i = 0; i < getString().length(); i++) {
                white += getString().charAt(i);
                whiteWidth = g2.getFontMetrics().stringWidth(white);
                if (startWidth + whiteWidth > x) {
                    break;
                }
                chars++;
            }
            if (chars != getString().length()) {
                white = white.substring(0, white.length() - 1);
                whiteWidth = g2.getFontMetrics().stringWidth(white);
            }
            float height = getFont().getSize();
            g2.setColor(Color.WHITE);
            g2.drawString(white, startWidth, height * 1.5F);
            g2.setColor(Color.BLACK);
            g2.drawString(this.getString().substring(chars), whiteWidth + startWidth, height * 1.5F);
        }

        // Draw outline
        g2.setColor(Color.BLACK);
        g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        g.dispose();
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setVisible(enabled);
    }

}
