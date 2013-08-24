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
package co.zmc.projectindigo.gui.components;

import co.zmc.projectindigo.IndigoLauncher;

@SuppressWarnings("serial")
public class ProgressSplashScreen extends SplashScreen {
    private ProgressBar _progressBar = new ProgressBar(0.7F);

    public ProgressSplashScreen(String reason, int initialValue) {
        super();
        _progressBar.setFont(IndigoLauncher.getMinecraftFont(14));
        _progressBar.setMaximum(100);
        _progressBar.setBounds(_icon.getIconWidth() / 2 - (270 / 2), (_icon.getIconHeight() / 2) - (29 / 2), 270, 29);
        updateProgress(reason, initialValue);
        _progressBar.setVisible(true);
        getContentPane().add(_progressBar);
    }

    public void updateProgress(String reason, int percent) {
        _progressBar.updateProgress(reason, percent);
    }

    public void updateProgress(int percent) {
        _progressBar.updateProgress(percent);
    }
}
