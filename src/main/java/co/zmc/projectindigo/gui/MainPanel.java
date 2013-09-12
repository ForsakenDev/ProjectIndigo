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
package co.zmc.projectindigo.gui;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JPanel;

import co.zmc.projectindigo.IndigoLauncher;
import co.zmc.projectindigo.gui.components.Image;
import co.zmc.projectindigo.gui.components.SidePanel;
import co.zmc.projectindigo.gui.page.AccountPage;
import co.zmc.projectindigo.gui.page.BasePage;
import co.zmc.projectindigo.gui.page.MainPage;
import co.zmc.projectindigo.gui.page.ServerPage;
import co.zmc.projectindigo.gui.page.SettingsPage;

@SuppressWarnings("serial")
public class MainPanel extends JPanel {
    protected IndigoLauncher _launcher;
    protected final Logger   logger         = Logger.getLogger("launcher");
    protected List<BasePage> _pages         = new ArrayList<BasePage>();
    protected int            _currentPageId = 0;
    protected SidePanel      _sidePanel;

    public MainPanel(IndigoLauncher launcher, int width, int height) {
        _launcher = launcher;
        setLayout(null);
        setOpaque(false);
        setFont(IndigoLauncher.getMinecraftFont(14));
        Dimension dim = new Dimension(width, height);
        setSize(dim);
        setPreferredSize(dim);
        setBounds(0, 0, width, height);
        setupLook();
    }

    public void setupLook() {
        setLayout(null);
        _sidePanel = new SidePanel(this);
        _sidePanel.setBounds(0, 0, _sidePanel.getWidth(), _sidePanel.getHeight());
        _sidePanel.setVisible(true);

        if (_pages == null) {
            _pages = new ArrayList<BasePage>();
        }
        _pages.add(new MainPage(this));
        _pages.add(new ServerPage(this));
        _pages.add(new AccountPage(this));
        _pages.add(new SettingsPage(this));

        _sidePanel.reload(this);
        add(_sidePanel);

        for (BasePage page : _pages) {
            add(page);
        }

        add(new Image("bg", getWidth(), getHeight()));
    }

    public Logger getLogger() {
        return logger;
    }

    public List<BasePage> getPages() {
        return _pages;
    }

    public int getCurrentPageId() {
        return _currentPageId;
    }

    public void setCurrentPageId(int currentPageId) {
        _pages.get(_currentPageId).setVisible(false);
        _pages.get(currentPageId).setVisible(true);
        _currentPageId = currentPageId;
    }

    public ServerPage getMainPage() {
        for (BasePage page : _pages) {
            if (page instanceof ServerPage) { return (ServerPage) page; }
        }
        return (ServerPage) _pages.get(1);
    }

    public SidePanel getSidePanel() {
        return _sidePanel;
    }

}
