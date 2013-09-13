package co.zmc.projectindigo.gui.components;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JWindow;

import co.zmc.projectindigo.utils.ResourceUtils;

@SuppressWarnings("serial")
public class SplashScreen extends JWindow {
    protected final ImageIcon _icon;
    protected final Image     _image = Toolkit.getDefaultToolkit().getImage(ResourceUtils.getResource("splash_screen"));

    public SplashScreen() {
        this._icon = new ImageIcon(_image);

        Container container = getContentPane();
        container.setLayout(null);

        BufferedImage alphaImage = new BufferedImage(_icon.getIconWidth(), _icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = alphaImage.createGraphics();
        g.drawImage(_image, 0, 0, _icon.getIconWidth(), _icon.getIconHeight(), null);
        g.dispose();

        JButton background = new JButton(new ImageIcon(alphaImage));
        background.setBounds(0, 0, _icon.getIconWidth(), _icon.getIconHeight());
        background.setRolloverEnabled(true);
        background.setRolloverIcon(background.getIcon());
        background.setSelectedIcon(background.getIcon());
        background.setDisabledIcon(background.getIcon());
        background.setPressedIcon(background.getIcon());
        background.setFocusable(false);
        background.setContentAreaFilled(false);
        background.setBorderPainted(false);
        background.setOpaque(false);
        container.add(background);

        setSize(_icon.getIconWidth(), _icon.getIconHeight() + 20);
        try {
            this.setBackground(new Color(0, 0, 0, 0));
        } catch (UnsupportedOperationException e) {
            this.setBackground(new Color(0, 0, 0));
        }
        setLocationRelativeTo(null);
    }
}
