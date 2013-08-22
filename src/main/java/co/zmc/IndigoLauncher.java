package co.zmc;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import co.zmc.gui.MainFrame;
import co.zmc.utils.DirectoryLocations;
import co.zmc.utils.Utils;

public class IndigoLauncher {
    public static final String TITLE  = "Project Indigo";
    private static Logger      logger = null;

    public IndigoLauncher() {
        main(new String[0]);
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        long startup = start;
        System.setProperty("java.net.preferIPv4Stack", "true");
        // SplashScreen splash = new
        // SplashScreen(Toolkit.getDefaultToolkit().getImage(SplashScreen.class.getResource("/imgs/splash_screen.png")));
        // splash.setVisible(true);

        logger = setupLogger();
        cleanup();

        setLookandFeel();

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MainFrame frame = new MainFrame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        // splash.dispose();
    }

    private static Logger setupLogger() {
        Logger logger = Logger.getLogger("launcher");
        File logDirectory = new File(Utils.getDynamicStorageLocation(), "logs");
        if (!logDirectory.exists()) {
            logDirectory.mkdir();
        }
        File logs = new File(logDirectory, "Indigo%D.log");

        return logger;
    }

    private static void cleanup() {
        File file = new File(DirectoryLocations.BASE_DIR_LOCATION);
        if (!file.exists()) {
            file.mkdir();
        }
        file = new File(DirectoryLocations.DATA_DIR_LOCATION);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    private static void setLookandFeel() {
        if (Utils.getCurrentOS() == Utils.OS.MACOSX) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Indigo");
        }
    }

    public static final Font getMinecraftFont(int size) {
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, IndigoLauncher.getResourceAsStream("/assets/fonts/minecraft.ttf"));
            font = font.deriveFont((float) size);
            return font;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FontFormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static InputStream getResourceAsStream(String path) {
        InputStream stream = IndigoLauncher.class.getResourceAsStream(path);
        String[] split = path.split("/");
        path = split[(split.length - 1)];
        if (stream == null) {
            File resource = new File(".\\src\\main\\resources\\" + path);
            if (resource.exists()) try {
                stream = new BufferedInputStream(new FileInputStream(resource));
            } catch (IOException ignore) {
            }
        }
        return stream;
    }
}
