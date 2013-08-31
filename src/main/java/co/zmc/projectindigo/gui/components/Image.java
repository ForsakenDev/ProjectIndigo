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

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import co.zmc.projectindigo.utils.ResourceUtils;

@SuppressWarnings("serial")
public class Image extends JLabel {
    private String _fileName;

    public Image(String fileName, String disabledFileName) {
        this(fileName);
        setDisabledIcon(new ImageIcon(getImage(disabledFileName)));
    }

    public Image(String fileName, String disabledFileName, int width, int height) {
        this(fileName, width, height);
        setDisabledIcon(new ImageIcon(getImage(disabledFileName)));
    }

    public Image(String fileName) {
        _fileName = fileName;
        BufferedImage image = getImage();
        int width = image.getWidth();
        int height = image.getHeight();
        setVerticalAlignment(0);
        setHorizontalAlignment(0);
        setBounds(0, 0, width, height);
        setIcon(new ImageIcon(image));
        setVerticalAlignment(1);
        setHorizontalAlignment(2);
    }

    public Image(String fileName, int width, int height) {
        _fileName = fileName;
        setVerticalAlignment(0);
        setHorizontalAlignment(0);
        setBounds(0, 0, width, height);
        setIcon(new ImageIcon(getImage().getScaledInstance(width, height, 4)));
        setVerticalAlignment(1);
        setHorizontalAlignment(2);
    }

    private BufferedImage getImage() {
        return getImage(_fileName);
    }

    private BufferedImage getImage(String fileName) {
        try {
            BufferedImage image = ImageIO.read(ResourceUtils.getResource(fileName));
            return image;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new BufferedImage(getWidth(), getHeight(), 2);
    }
}
