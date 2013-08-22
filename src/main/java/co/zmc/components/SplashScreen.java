package co.zmc.components;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JWindow;

@SuppressWarnings("serial")
public class SplashScreen extends JWindow {
    protected final ImageIcon icon;

    public SplashScreen(Image image) {
        this.icon = new ImageIcon(image);

        Container container = getContentPane();
        container.setLayout(null);

        BufferedImage alphaImage = new BufferedImage(this.icon.getIconWidth(), this.icon.getIconHeight(), 2);
        Graphics2D g = alphaImage.createGraphics();
        g.drawImage(image, 0, 0, this.icon.getIconWidth(), this.icon.getIconHeight(), null);
        g.dispose();

        JButton background = new JButton(new ImageIcon(alphaImage));
        background.setBounds(0, 0, this.icon.getIconWidth(), this.icon.getIconHeight());
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
        setSize(this.icon.getIconWidth(), this.icon.getIconHeight() + 20);
        try {
            setBackground(new Color(0, 0, 0, 0));
        } catch (UnsupportedOperationException e) {
            setBackground(new Color(0, 0, 0));
        }
        setLocationRelativeTo(null);
    }
}
