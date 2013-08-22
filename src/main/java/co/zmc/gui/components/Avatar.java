package co.zmc.gui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

import co.zmc.IndigoLauncher;

@SuppressWarnings("serial")
public class Avatar extends JLabel {
    private String       _username;
    private String       _accountKey;
    private final JLabel label;

    public Avatar(JLayeredPane pane, String username, String accountKey) {
        this(pane, username, accountKey, 100);
    }

    public Avatar(JLayeredPane pane, String username, String accountKey, int width) {
        this.label = new JLabel(username);
        _username = username;
        _accountKey = accountKey;
        pane.add(this, 0);
        pane.add(this.label, 0);
        Dimension dim = new Dimension(width, width);
        setSize(dim);
        setPreferredSize(dim);
        setVerticalAlignment(0);
        setHorizontalAlignment(0);
        setIcon(new ImageIcon(getImage().getScaledInstance(width, width, 4)));
        setVerticalAlignment(1);
        setHorizontalAlignment(2);
        this.label.setForeground(Color.WHITE);
        this.label.setFont(IndigoLauncher.getMinecraftFont(14));
        setBounds(0, 0, width, width);
    }

    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, y, w, h);
        label.setBounds(x + ((getWidth() / 2) - (getLabelWidth() / 2)), y + getHeight() + 5, getWidth(), 24);
    }

    private int getLabelWidth() {
        FontRenderContext frc = new FontRenderContext(label.getFont().getTransform(), true, true);
        return (int) (label.getFont().getStringBounds(label.getText(), frc).getWidth());
    }

    public String getAccountKey() {
        return _accountKey;
    }

    public String getUsername() {
        return _username;
    }

    private BufferedImage getImage() {
        BufferedImage image = null;
        try {
            URLConnection conn = new URL("http://www.zephyrunleashed.com/avatar/" + _username + "/" + getWidth()).openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(false);
            System.setProperty("http.agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.162 Safari/535.19");
            conn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.162 Safari/535.19");
            HttpURLConnection.setFollowRedirects(true);
            conn.setUseCaches(false);
            ((HttpURLConnection) conn).setInstanceFollowRedirects(true);
            int response = ((HttpURLConnection) conn).getResponseCode();
            if (response == 200) {
                image = ImageIO.read(conn.getInputStream());
                if ((image.getWidth() != getWidth()) || (image.getHeight() != getHeight())) {
                    BufferedImage resized = new BufferedImage(getWidth(), getHeight(), image.getType());
                    Graphics2D g = resized.createGraphics();
                    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g.drawImage(image, 0, 0, getWidth(), getHeight(), 0, 0, image.getWidth(), image.getHeight(), null);
                    g.dispose();
                    image = resized;
                }
            }
            if (image != null) { return makeColorTransparent(image, Color.magenta); }
            return ImageIO.read(this.getClass().getResourceAsStream("/assets/images/char.png"));
        } catch (Exception e) {
            try {
                return ImageIO.read(this.getClass().getResourceAsStream("/assets/images/char.png"));
            } catch (IOException e1) {
                throw new RuntimeException("Error loading cached image resource", e1);
            }
        }
    }

    private BufferedImage makeColorTransparent(BufferedImage im, final Color color) {
        ImageFilter filter = new RGBImageFilter() {
            public int markerRGB = color.getRGB() | 0xFFFF0000;

            public final int filterRGB(int x, int y, int rgb) {
                if ((rgb | 0xFF000000) == markerRGB) {
                    return 0x00FFFFFF & rgb;
                } else {
                    return rgb;
                }
            }
        };
        ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
        return getBI(Toolkit.getDefaultToolkit().createImage(ip));
    }

    private BufferedImage getBI(Image image) {
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bufferedImage.createGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();

        return bufferedImage;

    }
}
