package co.zmc.projectindigo.mclaunch;

import java.applet.Applet;
import java.awt.Dimension;
import java.awt.Window;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.UIManager;

import net.minecraft.Launcher;
import co.zmc.projectindigo.utils.Utils;
import co.zmc.projectindigo.utils.Utils.OS;

@SuppressWarnings("serial")
public class MinecraftFrame extends JFrame {
    private Launcher appletWrap = null;
    Dimension        size       = new Dimension(900, 480);

    public MinecraftFrame(String title) {
        super(title);

        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Utils.getCurrentOS() == OS.MACOSX) {
            try {
                Class<?> fullScreenUtilityClass = Class.forName("com.apple.eawt.FullScreenUtilities");
                java.lang.reflect.Method setWindowCanFullScreenMethod = fullScreenUtilityClass.getDeclaredMethod("setWindowCanFullScreen",
                        new Class[] { Window.class, Boolean.TYPE });
                setWindowCanFullScreenMethod.invoke(null, new Object[] { this, Boolean.valueOf(true) });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        super.setVisible(true);
        setResizable(true);
        fixSize(size);
    }

    public void start(Applet mcApplet, String user, String session) {
        try {
            appletWrap = new Launcher(mcApplet, new URL("http://www.minecraft.net/game"));
        } catch (MalformedURLException ignored) {
        }
        appletWrap.setParameter("username", user);
        appletWrap.setParameter("sessionid", session);
        appletWrap.setParameter("stand-alone", "true");
        mcApplet.setStub(appletWrap);
        add(appletWrap);

        appletWrap.setPreferredSize(size);

        pack();
        validate();
        appletWrap.init();
        appletWrap.start();
        fixSize(size);
        setVisible(true);

    }

    private void fixSize(Dimension size) {
        setSize(size);
    }
}
