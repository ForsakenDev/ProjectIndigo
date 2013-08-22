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
package co.zmc.projectindigo.gui;

import java.awt.Container;
import java.util.ArrayList;
import java.util.List;

import co.zmc.projectindigo.gui.components.Image;
import co.zmc.projectindigo.gui.components.SidePanel;
import co.zmc.projectindigo.gui.pages.AccountPage;
import co.zmc.projectindigo.gui.pages.BasePage;
import co.zmc.projectindigo.gui.pages.MainPage;
import co.zmc.projectindigo.gui.pages.ServersPage;
import co.zmc.projectindigo.gui.pages.SettingsPage;

@SuppressWarnings("serial")
public class MainFrame extends BaseFrame {
    protected static List<BasePage> pages          = new ArrayList<BasePage>();
    protected int                   _currentPageId = 0;
    protected SidePanel             sidePanel;

    public MainFrame() {
        super(900, 580);
    }

    @Override
    public void setupLook() {
        Container contentPane = getContentPane();
        contentPane.setLayout(null);

        sidePanel = new SidePanel(this);
        sidePanel.setBounds(0, 0, sidePanel.getWidth(), sidePanel.getHeight());
        sidePanel.setVisible(true);

        if (pages == null) {
            pages = new ArrayList<BasePage>();
        }

        pages.add(new MainPage(this));
        pages.add(new ServersPage(this));
        pages.add(new SettingsPage(this));
        pages.add(new AccountPage(this));

        sidePanel.reload(this);
        contentPane.add(sidePanel);

        for (BasePage page : pages) {
            contentPane.add(page);
        }

        contentPane.add(new Image("bg", getWidth(), getHeight()));
    }

    public SidePanel getSidePanel() {
        return sidePanel;
    }

    public List<BasePage> getPages() {
        return pages;
    }

    public int getCurrentPageId() {
        return _currentPageId;
    }

    public void setCurrentPageId(int currentPageId) {
        pages.get(_currentPageId).setVisible(false);
        pages.get(currentPageId).setVisible(true);
        _currentPageId = currentPageId;
    }

}
