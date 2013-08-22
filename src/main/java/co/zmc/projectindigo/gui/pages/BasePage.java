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
package co.zmc.projectindigo.gui.pages;

import java.awt.Dimension;

import javax.swing.JLayeredPane;

import co.zmc.projectindigo.gui.BaseFrame;
import co.zmc.projectindigo.gui.components.Image;

@SuppressWarnings("serial")
public abstract class BasePage extends JLayeredPane {

    protected BaseFrame _baseFrame;
    protected Image     backgroundImage;
    protected Image     _icon;

    public BasePage(BaseFrame baseFrame, boolean defaultPage) {
        _baseFrame = baseFrame;
        setLayout(null);
        setOpaque(false);
        setVisible(defaultPage);
        Dimension dim = new Dimension(baseFrame.getWidth(), baseFrame.getHeight() - 20);
        setSize(dim);
        setPreferredSize(dim);
        setBounds(0, 0, getWidth(), getHeight());
        setIcons();
        setupBackgroundImage();
        addComponents(baseFrame);
    }

    public abstract void setIcons();

    public Image getIcon() {
        return _icon;
    }

    public abstract void addComponents(BaseFrame baseFrame);

    public abstract void setupBackgroundImage();
}
