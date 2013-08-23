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
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import co.zmc.projectindigo.data.Server;
import co.zmc.projectindigo.gui.BaseFrame;
import co.zmc.projectindigo.gui.components.Image;
import co.zmc.projectindigo.gui.components.TransparentImage;
import co.zmc.projectindigo.managers.ServerManager;

@SuppressWarnings("serial")
public class ServersPage extends BasePage {

    private ServerManager serverManager;

    public ServersPage(BaseFrame baseFrame) {
        super(baseFrame, false);
    }

    @Override
    public void setIcons() {
        _icon = new Image("servers_hover", "servers");
    }

    @Override
    public void addComponents(BaseFrame baseFrame) {
        if (serverManager == null) {
            serverManager = new ServerManager();
        }
        JPanel pane = new JPanel();
        pane.setLayout(new GridBagLayout());
        pane.setOpaque(false);
        for (Server s : serverManager.getServers()) {
            pane.add(s.getLogo(this));
        }
        Dimension dim = new Dimension(getWidth(), getHeight() - 78);

        pane.setSize(dim);
        pane.setPreferredSize(dim);
        pane.setBounds(0, 0, dim.width, dim.height);
        add(pane, 0);
    }

    @Override
    public void setupBackgroundImage() {
        add(new TransparentImage("main_bg", 0.75F, getWidth(), getHeight()));
    }

}
