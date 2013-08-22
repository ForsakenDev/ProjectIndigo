package co.zmc.gui.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.border.AbstractBorder;

@SuppressWarnings("serial")
public class Border extends AbstractBorder {
    private final int   thickness;
    private final Color color;

    public Border(int thick, Color color) {
        this.thickness = thick;
        this.color = color;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(this.color);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawRect(x, y, width, height);
    }

    public Insets getBorderInsets(Component c) {
        return new Insets(this.thickness, this.thickness, this.thickness, this.thickness);
    }

    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = (insets.top = insets.right = insets.bottom = this.thickness);
        return insets;
    }
}