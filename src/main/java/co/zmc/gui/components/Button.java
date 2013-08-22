package co.zmc.gui.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;

import co.zmc.IndigoLauncher;

@SuppressWarnings("serial")
public class Button extends JButton implements MouseListener {
    private boolean clicked   = false;
    private boolean _isToggle = false;

    public Button(String label, boolean isToggle) {
        this(label);
        _isToggle = isToggle;
    }

    public Button(String label) {
        setText(label);
        setBackground(Color.WHITE);
        setBorder(new Border(5, getBackground()));
        addMouseListener(this);
        setFont(IndigoLauncher.getMinecraftFont(14));
    }

    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Color old = g2d.getColor();

        g2d.setColor(this.clicked ? Color.BLACK : getBackground());
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setColor(this.clicked ? getBackground() : Color.BLACK);
        g2d.setFont(getFont());
        int width = g2d.getFontMetrics().stringWidth(getText());
        g2d.drawString(getText(), (getWidth() - width) / 2, getFont().getSize() + 4);

        g2d.setColor(old);
    }

    public boolean isClicked() {
        return clicked;
    }

    public void mouseClicked(MouseEvent e) {
        if (_isToggle) {
            this.clicked = !this.clicked;
        }
    }

    public void mousePressed(MouseEvent e) {
        if (!_isToggle) {
            this.clicked = true;
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (!_isToggle) {
            this.clicked = false;
        }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
}