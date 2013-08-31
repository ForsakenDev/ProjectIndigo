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
package co.zmc.projectindigo.gui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.text.JTextComponent;


@SuppressWarnings("serial")
public class SettingsPair extends JPanel {

	JComponent input;
	String friendlyName;
	String uniqueName;
	
	public SettingsPair(String uniqueName, String friendlyName, JComponent input) {
		this.uniqueName = uniqueName;
		this.friendlyName = friendlyName;
		this.input = input;
		this.input.setPreferredSize(new Dimension(200, 20));
		
		Label label = new Label(friendlyName);
		label.setPreferredSize(new Dimension(150, 25));
		label.setOpaque(false);
		label.setForeground(Color.WHITE);

		this.setLayout(new GridLayout(1, 2));
		this.setPreferredSize(new Dimension(350, 25));
		this.setOpaque(false);
		
		this.add(label);
		this.add(this.input);
	}
	
	public String getFriendlyName() {
		return friendlyName;
	}
	
	public String getUniqueName() {
		return uniqueName;
	}
	
	public String getValue() {
		if (input instanceof JTextComponent) {
			return ((JTextComponent) input).getText();
		} else if (input instanceof JComboBox) {
			return ((ComboBoxItem) ((JComboBox) input).getSelectedItem()).getUniqueName();
		} else {
			return "";
		}
	}
	
	public void setValue(String val) {
		if (input instanceof JTextComponent) {
			((JTextComponent) input).setText(val);
		} else if (input instanceof JComboBox) {
			JComboBox combo = (JComboBox)input;
			for (int i = 0; i < combo.getItemCount(); i++) {
				if (((ComboBoxItem)combo.getItemAt(i)).getUniqueName().equals(val)) {
					combo.setSelectedIndex(i);
				}
			}
		}
	}
	
}