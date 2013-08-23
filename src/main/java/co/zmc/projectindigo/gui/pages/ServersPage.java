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

import java.util.Date;

import javax.swing.JLabel;

import co.zmc.projectindigo.data.Server;
import co.zmc.projectindigo.gui.BaseFrame;
import co.zmc.projectindigo.gui.MainFrame;
import co.zmc.projectindigo.gui.components.Image;
import co.zmc.projectindigo.gui.components.TransparentImage;
import co.zmc.projectindigo.managers.ServerManager;
import co.zmc.projectindigo.utils.Utils;

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
        int sidebarSize = ((MainFrame) baseFrame).getSidePanel().getWidth();
        int padding = 50;
        int numServers = serverManager.getServers().size();
        int[][] tableCoords = Utils.getDynamicTableCoords(getWidth() - sidebarSize - padding, getHeight() - padding, numServers);
        for (int i = 0; i < numServers; i++) {
            Server s = serverManager.getServers().get(i);
            JLabel slogo = s.getLogo(this, tableCoords[i][2], tableCoords[i][3]);
            slogo.setOpaque(true);
            slogo.setBounds(sidebarSize + (padding / 2) + tableCoords[i][0], (padding / 2) + tableCoords[i][1], tableCoords[i][2], tableCoords[i][3]);
        }
    }

    @Override
    public void setupBackgroundImage() {
        add(new TransparentImage("main_bg", 0.75F, getWidth(), getHeight()));
    }

}
