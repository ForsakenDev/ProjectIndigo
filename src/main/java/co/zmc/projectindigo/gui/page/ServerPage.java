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
package co.zmc.projectindigo.gui.page;

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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import co.zmc.projectindigo.data.Server;
import co.zmc.projectindigo.gui.MainPanel;
import co.zmc.projectindigo.gui.components.Image;
import co.zmc.projectindigo.gui.components.ProgressBar;
import co.zmc.projectindigo.gui.components.TextBox;
import co.zmc.projectindigo.gui.components.TransparentButton;
import co.zmc.projectindigo.managers.ServerManager;

@SuppressWarnings("serial")
public class ServerPage extends BasePage {
    protected TextBox           _serverIP;
    protected TransparentButton _launchBtn;
    protected ServerManager     _serverManager;

    public ServerPage(MainPanel panel) {
        super(panel, false);
    }

    public void loadServerManager() {

    }

    public ServerManager getServerManager() {
        return _serverManager;
    }

    public void reloadServers() {
        for (int i = 0; i < getServerManager().getServers().size(); i++) {
            Server server = getServerManager().getServers().get(i);
            // int yOffset = (i + 1) * (36 + 5);
            // ServerInfo info = new ServerInfo(this, server);
            // info.setBounds(_mainPanel.getSidePanel().getActualWidth() + 20,
            // yOffset, 200, 26);
            // add(info);
        }
    }

    @Override
    public void setIcons() {
        _icon = new Image("servers_hover", "servers");
    }

    @Override
    public void addComponents(final MainPanel panel) {
        int padding = 25;
        int btnWidth = 149;
        int btnHeight = 24;
        int panelWidth = panel.getSidePanel().getActualWidth();
        _serverIP = new TextBox(this, "Server IP Address...");
        _launchBtn = new TransparentButton(this, "Launch", 0.8F, Color.GREEN);

        _launchBtn.setBounds(getWidth() - padding - 149, getHeight() - btnHeight - (padding / 2) * 3, btnWidth, btnHeight);
        _serverIP.setBounds(panelWidth + padding, getHeight() - btnHeight - (padding / 2) * 3, (getWidth() - (padding * 3) - 149) - panelWidth,
                btnHeight);
        _launchBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String ip = _serverIP.getText().trim();
                if (!ip.contains(" ")) {
                    int port = 25565;
                    try {
                        if (ip.contains(":")) {
                            int index = ip.indexOf(":");
                            port = Integer.parseInt(ip.substring(index + 1));
                            ip = ip.substring(0, index);
                        }
                        _serverManager.loadServer(ip, port);
                    } catch (NumberFormatException e1) {
                        JOptionPane.showMessageDialog(getParent(), "You need to include a valid port number", "Invalid Port",
                                JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(getParent(), "You need to include a valid IP Address", "Invalid IP Address",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });
    }

    public void stateChanged(final String status, final float progress) {

    }

    @Override
    public void setupBackgroundImage() {
    }

}
