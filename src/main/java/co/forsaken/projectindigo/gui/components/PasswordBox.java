package co.forsaken.projectindigo.gui.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPasswordField;

import co.forsaken.projectindigo.IndigoLauncher;

@SuppressWarnings("serial")
public class PasswordBox extends JPasswordField implements FocusListener {
    private final JLabel label;

    public PasswordBox(JComponent parent, String label) {
        this.label = new JLabel(label);
        addFocusListener(this);
        parent.add(this, 0);
        parent.add(this.label, 0);
        setBackground(Color.WHITE);
        setBorder(new Border(5, getBackground()));
        setEchoChar('*');
        this.label.setForeground(Color.BLACK);
        setFont(IndigoLauncher.getMinecraftFont(14));
    }

    public void setFont(Font font) {
        super.setFont(font);
        if (this.label != null) this.label.setFont(font);
    }

    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, y, w, h);
        this.label.setBounds(x + 5, y + 3, w - 5, h - 5);
    }

    public void setText(String text) {
        super.setText(text);
        this.label.setVisible((text == null) || (text.length() <= 0));
    }

    public void focusGained(FocusEvent e) {
        this.label.setVisible(false);
    }

    public void focusLost(FocusEvent e) {
        if (getPassword().length == 0) this.label.setVisible(true);
    }
}