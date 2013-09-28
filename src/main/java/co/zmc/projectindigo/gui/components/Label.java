package co.zmc.projectindigo.gui.components;

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JLabel;

import co.zmc.projectindigo.IndigoLauncher;

@SuppressWarnings("serial")
public class Label extends JLabel {

	public Label(JComponent component, String data) {
		this(data);
		component.add(this, 0);
	}
	
	public Label(String label) {
		this();
		this.setText(label);
	}
	
	public Label() {
		this.setFont(IndigoLauncher.getMinecraftFont(14));
		this.setForeground(Color.WHITE);
	}
}