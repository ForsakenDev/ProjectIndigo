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

import java.awt.Dimension;

import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;

import co.zmc.projectindigo.gui.MainPanel;
import co.zmc.projectindigo.gui.components.Image;
import co.zmc.projectindigo.gui.components.ProgressBar;

@SuppressWarnings("serial")
public abstract class BasePage extends JLayeredPane {

    protected MainPanel   _mainPanel;
    protected Image       backgroundImage;
    protected Image       _icon;
    protected ProgressBar _progressBar;

    public BasePage(MainPanel mainPanel, boolean defaultPage) {
        _mainPanel = mainPanel;
        setLayout(null);
        setOpaque(false);
        setVisible(defaultPage);
        Dimension dim = new Dimension(mainPanel.getWidth(), mainPanel.getHeight() - 20);
        setSize(dim);
        setPreferredSize(dim);
        setBounds(0, 0, getWidth(), getHeight());
        setIcons();
        setupBackgroundImage();
        addComponents(mainPanel);
        _progressBar = new ProgressBar(0.9F);
    }

    public abstract void setIcons();

    public Image getIcon() {
        return _icon;
    }

    public abstract void addComponents(MainPanel mainPanel);

    public abstract void setupBackgroundImage();

    public void stateChanged(final String status, final float progress) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                if (!_progressBar.isVisible()) {
                    _progressBar.setVisible(true);
                }
                int intProgress = Math.round(progress);
                _progressBar.setValue(intProgress);
                String text = status;
                if (text.length() > 60) {
                    text = text.substring(0, 60) + "...";
                }
                _progressBar.setString(intProgress + "% " + text);
            }
        });
    }
}