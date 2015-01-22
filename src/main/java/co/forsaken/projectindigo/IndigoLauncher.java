package co.forsaken.projectindigo;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import co.forsaken.projectindigo.gui.LauncherConsole;
import co.forsaken.projectindigo.gui.MainPanel;
import co.forsaken.projectindigo.log.LogManager;
import co.forsaken.projectindigo.utils.DirectoryLocations;
import co.forsaken.projectindigo.utils.ResourceUtils;

@SuppressWarnings("serial") public class IndigoLauncher extends JFrame {

  public static final String   TITLE            = "Indigo Launcher";
  public static IndigoLauncher _launcher;
  public static Dimension      _serverPanelSize = new Dimension(900, 580);
  public Dimension             _loginPanelSize  = new Dimension(400, 200);
  public MainPanel             _mainPanel;
  public LauncherConsole       console;

  public IndigoLauncher(String defaultLogin) {

    _launcher = this;
    setLookandFeel();
    launchMainPanel(defaultLogin);
    console = new LauncherConsole();
    addWindowListener(new WindowListener() {

      @Override public void windowOpened(WindowEvent e) {}

      @Override public void windowIconified(WindowEvent e) {}

      @Override public void windowDeiconified(WindowEvent e) {}

      @Override public void windowDeactivated(WindowEvent e) {}

      @Override public void windowClosing(WindowEvent e) {}

      @Override public void windowClosed(WindowEvent e) {
        console.dispose();
        LogManager.join();
        System.exit(0);
      }

      @Override public void windowActivated(WindowEvent e) {}
    });
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
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setResizable(false);
    setSize(_loginPanelSize);
    setPreferredSize(_loginPanelSize);
    setLocationRelativeTo(null);
    setIconImage(ResourceUtils.getImage("icon.png"));
  }

  private void initComponents(String defaultLogin) {
    _mainPanel = new MainPanel(_launcher, _serverPanelSize.width, _serverPanelSize.height);
    _mainPanel.setVisible(true);
    add(_mainPanel);
  }

  public static void cleanup() {
    for (DirectoryLocations s : DirectoryLocations.values()) {
      File file = new File(s.get());
      if (file.exists()) continue;
      file.mkdirs();
    }
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

}
