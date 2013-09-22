package co.zmc.projectindigo.mclaunch;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import net.minecraft.Launcher;
import co.zmc.projectindigo.utils.Settings;

@SuppressWarnings("serial")
public class MinecraftFrame extends JFrame {
    private Launcher appletWrap = null;
    private Settings _settings;

    public MinecraftFrame(String title, Settings settings) {
        super(title);
        _settings = settings;
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception e1) {
            }
        }

        super.setVisible(true);
        setResizable(true);
        setSize(getDimension());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (appletWrap != null) {
                    appletWrap.stop();
                    appletWrap.destroy();
                }
                _settings.set(Settings.WINDOW_SIZE, getSize().width + "," + getSize().height);
                _settings.set(Settings.WINDOW_POSITION, getX() + "," + getY());
                Toolkit tk = Toolkit.getDefaultToolkit();
                int xSize = ((int) tk.getScreenSize().getWidth());
                int ySize = ((int) tk.getScreenSize().getHeight());
                _settings.set(Settings.WINDOW_MAXIMIZED, (xSize == getSize().width && ySize == getSize().height) + "");
                _settings.save();
                System.exit(0);
            }
        });
    }

    public Dimension getDimension() {
        // Toolkit tk = Toolkit.getDefaultToolkit();
        // int xSize = ((int) tk.getScreenSize().getWidth());
        // int ySize = ((int) tk.getScreenSize().getHeight());
        return new Dimension(854, 480);
    }

    public void start(Applet mcApplet, String basePath, String user, String session, String ip, String port) {
        getContentPane().setBackground(Color.black);

        setSize(getDimension());
        setPreferredSize(getDimension());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            appletWrap = new Launcher(mcApplet, new URL("http://www.minecraft.net/game"));
        } catch (MalformedURLException ignored) {
        }
        appletWrap.addParameter("working_directory", basePath);
        appletWrap.addParameter("username", user);
        appletWrap.addParameter("sessionid", session);
        appletWrap.addParameter("stand-alone", "true");
        appletWrap.addParameter("server", ip);
        appletWrap.addParameter("port", port);
        mcApplet.setStub(appletWrap);
        add(appletWrap);
        appletWrap.setSize(getWidth(), getHeight());
        appletWrap.init();
        appletWrap.start();
        setSize(getDimension());
        setPreferredSize(getDimension());
        pack();
        validate();
        setVisible(true);
    }

    public void setSize(Applet component) {
        setSize(getSize());
        setPreferredSize(getSize());
    }
}
