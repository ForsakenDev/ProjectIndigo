package co.zmc.projectindigo.gui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class ScrollBarUI extends BasicScrollBarUI {
    private final Dimension d = new Dimension();

    @SuppressWarnings("serial")
    @Override
    protected JButton createDecreaseButton(int orientation) {
        return new JButton() {
            @Override
            public Dimension getPreferredSize() {
                return d;
            }
        };
    }

    @SuppressWarnings("serial")
    @Override
    protected JButton createIncreaseButton(int orientation) {
        return new JButton() {
            @Override
            public Dimension getPreferredSize() {
                return d;
            }
        };
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color color = null;
        Color highlight = null;
        if (isDragging) {
            color = new Color(50, 50, 50, 160);
            highlight = new Color(155, 155, 155, 160);
        } else if (isThumbRollover()) {
            color = new Color(50, 50, 50, 160);
            highlight = new Color(155, 155, 155, 160);
        } else {
            color = new Color(50, 50, 50, 120);
            highlight = new Color(155, 155, 155, 120);
        }
        g2.setPaint(color);
        g2.fillRoundRect(r.x, r.y, r.width, r.height, 20, 20);
        g2.setPaint(highlight);
        g2.drawRoundRect(r.x, r.y, r.width - 1, r.height - 1, 20, 20);
        g2.dispose();
    }

    @Override
    protected void setThumbBounds(int x, int y, int width, int height) {
        super.setThumbBounds(x, y, width, height);
        scrollbar.repaint();
    }
}