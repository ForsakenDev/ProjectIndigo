package co.forsaken.projectindigo.gui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import co.forsaken.projectindigo.IndigoLauncher;
import co.forsaken.projectindigo.utils.ResourceUtils;

@SuppressWarnings("serial")
public class ImageLabel extends JLabel implements MouseListener {
    private final JLabel _label;
    private String       _imageName;

    public ImageLabel(JComponent c, String imageName, String text) {
        this(c, imageName, text, 150);
    }

    public ImageLabel(JComponent c, String imageName, String text, int width) {
        _label = new JLabel(text);
        _imageName = imageName;
        c.add(this, 0);
        c.add(_label, 0);
        setBorder(null);
        setFocusable(false);
        addMouseListener(this);
        Dimension dim = new Dimension(width, width);
        setSize(dim);
        setPreferredSize(dim);
        dim = new Dimension(getLabelWidth(), 24);
        _label.setSize(dim);
        _label.setPreferredSize(dim);
        setVerticalAlignment(0);
        setHorizontalAlignment(0);
        setVerticalAlignment(1);
        setHorizontalAlignment(2);
        setIcon(new ImageIcon(getImage().getScaledInstance(width, width, 4)));
        _label.setForeground(Color.WHITE);
        _label.setFont(IndigoLauncher.getMinecraftFont(14));
        _label.setVisible(false);
        setBounds(0, 0, width, width);
    }

    @Override
    public void setIcon(Icon icon) {
        super.setIcon(icon);
        setDisabledIcon(getIcon());
    }

    public void setBounds(int x, int y, int w, int h) {
        int fontSize = getFontSize(w);
        h -= (fontSize + 5);
        w = h;
        super.setBounds(x, y, w, h);
        setIcon(new ImageIcon(getImage().getScaledInstance(w, h, 4)));
        _label.setBounds(x + (w / 2) - (getLabelWidth() / 2), y + h + 5, getLabelWidth(), fontSize);
    }

    private int getFontSize(int width) {
        for (int i = 15; i > 1; i--) {
            _label.setFont(IndigoLauncher.getMinecraftFont(i));
            if (getLabelWidth() <= width) { return i - (i % 2); }
        }
        return 2;
    }

    private int getLabelWidth() {
        FontRenderContext frc = new FontRenderContext(_label.getFont().getTransform(), true, true);
        return (int) (_label.getFont().getStringBounds(_label.getText(), frc).getWidth()) + ((1 * _label.getText().length()) / 2);
    }

    private BufferedImage getImage() {
        try {
            return ImageIO.read(ResourceUtils.getResource(_imageName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new BufferedImage(getWidth(), getHeight(), 2);
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
        if (super.isEnabled()) {
            _label.setVisible(true);
        }
    }

    public void mouseExited(MouseEvent e) {
        if (super.isEnabled()) {
            _label.setVisible(false);
        }
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

}
