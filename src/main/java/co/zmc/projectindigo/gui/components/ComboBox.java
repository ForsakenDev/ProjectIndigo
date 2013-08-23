package co.zmc.projectindigo.gui.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.plaf.basic.BasicComboBoxUI;

import co.zmc.projectindigo.IndigoLauncher;

@SuppressWarnings("serial")
public class ComboBox extends JComboBox<String> {

	public ComboBox(String... items) {
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
		
		for (String item : items) {
			this.addItem(item);
		}
	}
	
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, getWidth(), getHeight());
		
		g2.setColor(Color.BLACK);
		g2.setFont(getFont());
		g2.drawString((String) getSelectedItem(), 5, getFont().getSize() + 5);
		
		g2.drawPolygon(new int[] {getWidth() - 10, getWidth() - 15, getWidth() - 20}, new int[] {7, 12, 7}, 3);
		
		if (this.isPopupVisible()) {
			g2.fillPolygon(new int[] {getWidth() - 10, getWidth() - 15, getWidth() - 20}, new int[] {7, 12, 7}, 3);
		}
	}
	
}