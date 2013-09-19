package co.zmc.projectindigo.gui.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JButton;
import javax.swing.JComponent;

import co.zmc.projectindigo.IndigoLauncher;

@SuppressWarnings("serial")
public class Button extends JButton implements MouseListener {
    private boolean clicked   = false;
    private boolean _isToggle = false;

    public Button(JComponent frame, String label) {
        this(frame, label, false);
    }

    public Button(JComponent frame, String label, boolean isToggle) {
        _isToggle = isToggle;
        setText(label);
        setBackground(Color.WHITE);
        addMouseListener(this);
        setFont(IndigoLauncher.getMinecraftFont(14));
        frame.add(this, 0);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        RoundRectangle2D rect = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(!this.clicked ? getBackground() : Color.BLACK);
        g2d.fill(rect);
        g2d.setFont(getFont());
        int width = g2d.getFontMetrics().stringWidth(getText());
        g2d.setColor(this.clicked ? getBackground() : Color.BLACK);
        g2d.drawString(getText(), (getWidth() - width) / 2, getFont().getSize() + 4);

        g2d.dispose();
    }

    public boolean isClicked() {
        return clicked || !isEnabled();
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