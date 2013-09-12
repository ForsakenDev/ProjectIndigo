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

import co.zmc.projectindigo.gui.MainPanel;
import co.zmc.projectindigo.gui.components.Image;

@SuppressWarnings("serial")
public class SettingsPage extends BasePage {

    public SettingsPage(MainPanel panel) {
        super(panel, false);
    }

    @Override
    public void setIcons() {
        _icon = new Image("settings_hover", "settings");
    }

    @Override
    public void addComponents(final MainPanel panel) {

    }

    @Override
    public void setupBackgroundImage() {
    }

}
