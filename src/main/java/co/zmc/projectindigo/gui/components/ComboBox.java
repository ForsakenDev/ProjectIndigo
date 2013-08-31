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
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.plaf.basic.BasicComboBoxUI;

import co.zmc.projectindigo.IndigoLauncher;

@SuppressWarnings("serial")
public class ComboBox extends JComboBox {

	public ComboBox(ComboBoxItem... items) {
		this.setFont(IndigoLauncher.getMinecraftFont(14));
		this.setBackground(Color.WHITE);
		this.setForeground(Color.BLACK);
		this.setUI(new BasicComboBoxUI() {
		    protected JButton createArrowButton() {
		        return new JButton() {
		            public boolean isVisible() {
		                return false;
		            }
		        };
		    }
		});
		
		for (ComboBoxItem item : items) {
			this.addItem(item);
		}
	}
	
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, getWidth(), getHeight());
		
		g2.setColor(Color.BLACK);
		g2.setFont(getFont());
		g2.drawString(getSelectedItem().toString(), 5, getFont().getSize() + 5);
		
		g2.drawPolygon(new int[] {getWidth() - 10, getWidth() - 15, getWidth() - 20}, new int[] {7, 12, 7}, 3);
		
		if (this.isPopupVisible()) {
			g2.fillPolygon(new int[] {getWidth() - 10, getWidth() - 15, getWidth() - 20}, new int[] {7, 12, 7}, 3);
		}
	}
	
}