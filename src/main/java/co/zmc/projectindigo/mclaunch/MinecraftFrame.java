package co.zmc.projectindigo.mclaunch;

import java.applet.Applet;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import net.minecraft.Launcher;

@SuppressWarnings("serial")
public class MinecraftFrame extends JFrame {
    private Launcher appletWrap = null;

    public MinecraftFrame(String title) {
        super(title);
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

        // setIconImage(Toolkit.getDefaultToolkit().createImage(imagePath));
        super.setVisible(true);
        setResizable(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                new Thread() {
                    public void run() {
                        try {
                            Thread.sleep(30000L);
                        } catch (InterruptedException localInterruptedException) {
                        }
                        Logger.getLogger("launcher").log(Level.SEVERE, "FORCING EXIT!");
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

        try {
            appletWrap = new Launcher(mcApplet, new URL("http://www.minecraft.net/game"));
        } catch (MalformedURLException ignored) {
        }
        appletWrap.addParameter("working_directory", basePath);
        appletWrap.addParameter("username", user);
        appletWrap.addParameter("sessionid", session);
        appletWrap.addParameter("stand-alone", "true");
        // appletWrap.addParameter("server", ip);
        // appletWrap.addParameter("port", port);
        mcApplet.setStub(appletWrap);
        add(appletWrap);

        Dimension size = new Dimension(900, 480);
        appletWrap.setPreferredSize(size);
        pack();
        validate();
        appletWrap.init();
        appletWrap.start();
        setVisible(true);
    }
}
