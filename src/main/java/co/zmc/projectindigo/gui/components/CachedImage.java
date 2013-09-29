package co.zmc.projectindigo.gui.components;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import co.zmc.projectindigo.utils.ResourceUtils;

@SuppressWarnings("serial")
public class CachedImage extends JLabel {
    private String _url;
    private String _name;

    public CachedImage(String name, String url, int width, int height) {
        _url = url;
        _name = name;
        setVerticalAlignment(0);
        setHorizontalAlignment(0);
        setBounds(0, 0, width, height);
        try {
            setIcon(new ImageIcon(getImage().getScaledInstance(width, height, 4)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setVerticalAlignment(1);
        setHorizontalAlignment(2);
    }

    public void update(String name, String url) {
        _url = url;
        _name = name;
        setBounds(0, 0, getWidth(), getHeight());
        try {
            setIcon(new ImageIcon(getImage().getScaledInstance(getWidth(), getHeight(), 4)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BufferedImage getImage() throws IOException {
        return ResourceUtils.loadCachedImage(_name, _url, ImageIO.read(ResourceUtils.getResource("base_char")));
    }

}
