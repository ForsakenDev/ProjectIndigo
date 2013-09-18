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
import co.zmc.projectindigo.data.log.Logger;
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
        setSize(this);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                new Thread() {
                    public void run() {
                        try {
                            Thread.sleep(30000L);
                        } catch (InterruptedException localInterruptedException) {
                        }
                        Logger.logError("FORCING EXIT!");
                        System.exit(0);
                    }
                }.start();
                if (appletWrap != null) {
                    appletWrap.stop();
                    appletWrap.destroy();
                }
                System.exit(0);
            }
        });
    }

    public void start(Applet mcApplet, String basePath, String user, String session, String ip, String port) {
        getContentPane().setBackground(Color.black);
        setSize(this);
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
        setSize(appletWrap);
        appletWrap.init();
        appletWrap.start();
        setSize(this);
        setVisible(true);
    }

    public void setSize(JFrame component) {
        try {
            if (_settings.get(Settings.WINDOW_MAXIMIZED).equalsIgnoreCase("true")) {
                Toolkit tk = Toolkit.getDefaultToolkit();
                int xSize = ((int) tk.getScreenSize().getWidth());
                int ySize = ((int) tk.getScreenSize().getHeight());
                component.setSize(xSize, ySize);
            } else {
                Dimension dim = new Dimension(Integer.parseInt(_settings.get(Settings.WINDOW_SIZE.split(",")[0])), Integer.parseInt(_settings
                        .get(Settings.WINDOW_SIZE.split(",")[1])));
                component.setSize(dim);
                component.setPreferredSize(dim);
                component.setLocation(Integer.parseInt(_settings.get(Settings.WINDOW_POSITION.split(",")[0])),
                        Integer.parseInt(_settings.get(Settings.WINDOW_POSITION.split(",")[1])));
            }
        } catch (NumberFormatException e) {
        }
    }

    public void setSize(Applet component) {
        try {
            if (_settings.get(Settings.WINDOW_MAXIMIZED).equalsIgnoreCase("true")) {
                Toolkit tk = Toolkit.getDefaultToolkit();
                int xSize = ((int) tk.getScreenSize().getWidth());
                int ySize = ((int) tk.getScreenSize().getHeight());
                component.setSize(xSize, ySize);
            } else {
                Dimension dim = new Dimension(Integer.parseInt(_settings.get(Settings.WINDOW_SIZE.split(",")[0])), Integer.parseInt(_settings
                        .get(Settings.WINDOW_SIZE.split(",")[1])));
                component.setSize(dim);
                component.setPreferredSize(dim);
                component.setLocation(Integer.parseInt(_settings.get(Settings.WINDOW_POSITION.split(",")[0])),
                        Integer.parseInt(_settings.get(Settings.WINDOW_POSITION.split(",")[1])));
            }
        } catch (NumberFormatException e) {
        }
    }
}
