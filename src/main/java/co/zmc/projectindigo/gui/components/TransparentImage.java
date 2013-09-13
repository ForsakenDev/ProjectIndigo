package co.zmc.projectindigo.gui.components;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import co.zmc.projectindigo.utils.ResourceUtils;

@SuppressWarnings("serial")
public class TransparentImage extends JLabel {
    private String _fileName;
    private float  _transparencyLevel = 1f;

    public TransparentImage(String fileName, float transparencyLevel) {
        _fileName = fileName;
        _transparencyLevel = transparencyLevel;
        BufferedImage image = getTransparentImage();
        int width = image.getWidth();
        int height = image.getHeight();
        setVerticalAlignment(0);
        setHorizontalAlignment(0);
        setBounds(0, 0, width, height);
        setIcon(new ImageIcon(image));
        setVerticalAlignment(1);
        setHorizontalAlignment(2);
    }

    public TransparentImage(String fileName, float transparencyLevel, int width, int height) {
        _fileName = fileName;
        _transparencyLevel = transparencyLevel;
        setVerticalAlignment(0);
        setHorizontalAlignment(0);
        setBounds(0, 0, width, height);
        setIcon(new ImageIcon(getTransparentImage().getScaledInstance(width, height, 4)));
        setVerticalAlignment(1);
        setHorizontalAlignment(2);
    }

    private BufferedImage getRawImage() {
        try {
            BufferedImage image = ImageIO.read(ResourceUtils.getResource(_fileName));
            return image;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new BufferedImage(getWidth(), getHeight(), 2);
    }

    private BufferedImage getTransparentImage() {
        BufferedImage image = getRawImage();
        BufferedImage dest = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = dest.createGraphics();
        Composite comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, _transparencyLevel);
        g2.setComposite(comp);
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
        return dest;
    }
}
