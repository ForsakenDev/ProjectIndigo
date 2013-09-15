package co.zmc.projectindigo.gui.components;

import java.awt.Color;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.SwingConstants;

import co.zmc.projectindigo.IndigoLauncher;

@SuppressWarnings("serial")
public class CheckBox extends JCheckBox {

    public CheckBox(JComponent component, String label) {
        super(label);
        setFont(IndigoLauncher.getMinecraftFont(12));
        setOpaque(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorder(null);
        setForeground(Color.WHITE);
        setHorizontalTextPosition(SwingConstants.RIGHT);
        setIconTextGap(10);
        component.add(this);
    }
}
