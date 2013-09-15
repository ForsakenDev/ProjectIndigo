package co.zmc.projectindigo.gui.components;

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JLabel;

import co.zmc.projectindigo.IndigoLauncher;

@SuppressWarnings("serial")
public class Label extends JLabel {

    public Label(JComponent component, String data) {
        this.setFont(IndigoLauncher.getMinecraftFont(14));
        this.setText(data);
        this.setForeground(Color.WHITE);
        component.add(this, 0);
    }
}
