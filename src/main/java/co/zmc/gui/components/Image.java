package co.zmc.gui.components;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class Image extends JLabel {
    private String _fileName;

    public Image(String fileName, String disabledFileName) {
        this(fileName);
        setDisabledIcon(new ImageIcon(getImage(disabledFileName)));
    }

    public Image(String fileName, String disabledFileName, int width, int height) {
        this(fileName, width, height);
        setDisabledIcon(new ImageIcon(getImage(disabledFileName)));
    }

    public Image(String fileName) {
        _fileName = fileName;
        BufferedImage image = getImage();
        int width = image.getWidth();
        int height = image.getHeight();
        setVerticalAlignment(0);
        setHorizontalAlignment(0);
        setBounds(0, 0, width, height);
        setIcon(new ImageIcon(image));
        setVerticalAlignment(1);
        setHorizontalAlignment(2);
    }

    public Image(String fileName, int width, int height) {
        _fileName = fileName;
        setVerticalAlignment(0);
        setHorizontalAlignment(0);
        setBounds(0, 0, width, height);
        setIcon(new ImageIcon(getImage().getScaledInstance(width, height, 4)));
        setVerticalAlignment(1);
        setHorizontalAlignment(2);
    }

    private BufferedImage getImage() {
        return getImage(_fileName);
    }

    private BufferedImage getImage(String fileName) {
        InputStream stream = null;
        try {
            stream = this.getClass().getResourceAsStream("/assets/images/" + fileName);
            BufferedImage image = ImageIO.read(stream);
            return image;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
            }
        }
        return new BufferedImage(getWidth(), getHeight(), 2);
    }
}
