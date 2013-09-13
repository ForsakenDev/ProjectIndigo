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
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import co.zmc.projectindigo.utils.ResourceUtils;

@SuppressWarnings("serial")
public class TransparentImage extends JLabel {
    private String _fileName;
    private float  _transparencyLevel = 1f;

    public TransparentImage(String fileName, float transparencyLevel) {
        _fileName = fileName;
        _transparencyLevel = transparencyLevel;
        BufferedImage image = getTransparentImage();
        int width = image.getWidth();
        int height = image.getHeight();
        setVerticalAlignment(0);
        setHorizontalAlignment(0);
        setBounds(0, 0, width, height);
        setIcon(new ImageIcon(image));
        setVerticalAlignment(1);
        setHorizontalAlignment(2);
    }

    public TransparentImage(String fileName, float transparencyLevel, int width, int height) {
        _fileName = fileName;
        _transparencyLevel = transparencyLevel;
        setVerticalAlignment(0);
        setHorizontalAlignment(0);
        setBounds(0, 0, width, height);
        setIcon(new ImageIcon(getTransparentImage().getScaledInstance(width, height, 4)));
        setVerticalAlignment(1);
        setHorizontalAlignment(2);
    }

    private BufferedImage getRawImage() {
        try {
            BufferedImage image = ImageIO.read(ResourceUtils.getResource(_fileName));
            return image;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new BufferedImage(getWidth(), getHeight(), 2);
    }

    private BufferedImage getTransparentImage() {
        BufferedImage image = getRawImage();
        BufferedImage dest = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = dest.createGraphics();
        Composite comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, _transparencyLevel);
        g2.setComposite(comp);
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
        return dest;
    }
}
