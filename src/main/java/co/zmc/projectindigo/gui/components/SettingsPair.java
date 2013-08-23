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
			return (String) ((JComboBox<?>) input).getSelectedItem();
		} else {
			return "";
		}
	}
	
	public void setValue(String val) {
		if (input instanceof JTextComponent) {
			((JTextComponent) input).setText(val);
		} else if (input instanceof JComboBox) {
			((JComboBox<?>) input).setSelectedItem(val);
		}
	}
	
}