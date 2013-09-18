package co.zmc.projectindigo;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import co.zmc.projectindigo.data.LoginResponse;
import co.zmc.projectindigo.data.Server;
import co.zmc.projectindigo.gui.MainPanel;
import co.zmc.projectindigo.utils.DirectoryLocations;
import co.zmc.projectindigo.utils.FileUtils;
import co.zmc.projectindigo.utils.ResourceUtils;
import co.zmc.projectindigo.utils.Settings;

@SuppressWarnings("serial")
public class IndigoLauncher extends JFrame {
    public static final String   TITLE            = "Indigo Launcher";
    public static IndigoLauncher _launcher;
    public static Dimension      _serverPanelSize = new Dimension(900, 580);
    public Dimension             _loginPanelSize  = new Dimension(400, 200);
    public MainPanel             _mainPanel;

    public IndigoLauncher(String defaultLogin) {
        _launcher = this;
        cleanup();
        setLookandFeel();
        launchMainPanel(defaultLogin);
    }

    public void launchMainPanel(String defaultLogin) {
        initComponents(defaultLogin);
        _mainPanel.setVisible(true);
        setPreferredSize(_serverPanelSize);
        setSize(_serverPanelSize);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void setLookandFeel() {
        setTitle(IndigoLauncher.TITLE);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        setSize(_loginPanelSize);
        setPreferredSize(_loginPanelSize);
        setLocationRelativeTo(null);
    }

    private void initComponents(String defaultLogin) {

        _mainPanel = new MainPanel(_launcher, _serverPanelSize.width, _serverPanelSize.height);
        _mainPanel.setVisible(true);
        add(_mainPanel);
    }

    public static void cleanup() {
        File file = new File(DirectoryLocations.BASE_DIR_LOCATION);
        if (!file.exists()) {
            file.mkdir();
        }
        file = new File(DirectoryLocations.DATA_DIR_LOCATION);
        if (!file.exists()) {
            file.mkdir();
            FileUtils.writeStreamToFile(ResourceUtils.getResourceAsStream("defaultServers"), new File(file, "servers"));
            FileUtils.writeStreamToFile(ResourceUtils.getResourceAsStream("settings"), new File(file, "settings"));
        }
        file = new File(DirectoryLocations.IMAGE_DIR_LOCATION);
        if (!file.exists()) {
            file.mkdir();
        }
        file = new File(DirectoryLocations.AVATAR_CACHE_DIR_LOCATION);
        if (!file.exists()) {
            file.mkdir();
        }
        file = new File(DirectoryLocations.SERVERS_BASE_DIR_LOCATION);
        if (!file.exists()) {
            file.mkdir();
        }
        file = new File(DirectoryLocations.LOG_DIR_LOCATION);
        if (!file.exists()) {
            file.mkdir();
        }
        DirectoryLocations.updateServerDir();
    }

    public static final Font getMinecraftFont(int size) {
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, ResourceUtils.getResourceAsStream("minecraft_font"));
            font = font.deriveFont((float) size);
            return font;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FontFormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void refresh() {
        repaint();
    }

    public void launchMinecraft(Server server, LoginResponse response, Settings settings) throws IOException {
        server.download(response, settings);
    }

}
