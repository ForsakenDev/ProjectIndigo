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

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import co.zmc.projectindigo.gui.BaseFrame;
import co.zmc.projectindigo.gui.components.ComboBox;
import co.zmc.projectindigo.gui.components.ComboBoxItem;
import co.zmc.projectindigo.gui.components.Image;
import co.zmc.projectindigo.gui.components.SettingsPair;
import co.zmc.projectindigo.gui.components.TextBox;
import co.zmc.projectindigo.gui.components.TransparentImage;
import co.zmc.projectindigo.utils.SettingsList;

@SuppressWarnings("serial")
public class SettingsPage extends BasePage {

	public static SettingsList settings = new SettingsList();
	
    public SettingsPage(BaseFrame baseFrame) {
        super(baseFrame, false);
    }

    @Override
    public void setIcons() {
        _icon = new Image("settings_hover", "settings");
    }

    @Override
    public void addComponents(BaseFrame frame) {
    	JPanel settingsPanel = new JPanel();
    	settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
    	
    	settings.add(new SettingsPair("fupdate", "Force Update", new ComboBox(
    			new ComboBoxItem("false", "No"), 
    			new ComboBoxItem("true", "Yes"))));
    	settings.add(new SettingsPair("ram", "Allocated RAM", new ComboBox(
    			new ComboBoxItem("256M", "256MB"), 
    			new ComboBoxItem("512M", "512MB"), 
    			new ComboBoxItem("1024M", "1GB"), 
    			new ComboBoxItem("2048M", "2GB"), 
    			new ComboBoxItem("4096M", "4GB"), 
    			new ComboBoxItem("8192M", "8GB"))));
    	settings.add(new SettingsPair("console", "Toggle Console", new ComboBox(
    			new ComboBoxItem("true", "On"), 
    			new ComboBoxItem("false", "Off"))));
    	settings.add(new SettingsPair("java", "Java Args", new TextBox("")));
    	settings.add(new SettingsPair("automax", "Auto Maximized", new ComboBox(
    			new ComboBoxItem("false", "No"), 
    			new ComboBoxItem("true", "Yes"))));

    	for (SettingsPair pair : settings) {
    		settingsPanel.add(pair);
    		settingsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    	}
    	
    	Dimension size = new Dimension(400, settings.size() * 35);
    	
    	settingsPanel.setBounds((this.getWidth() - size.width) / 2 + 30, (this.getHeight() - size.height) / 2, size.width, size.height);
    	
    	settingsPanel.setOpaque(false);
    	
    	this.add(settingsPanel, 0);
    }
    
    @Override
    public void setupBackgroundImage() {
        add(new TransparentImage("main_bg", 0.75F, getWidth(), getHeight()));
    }

}