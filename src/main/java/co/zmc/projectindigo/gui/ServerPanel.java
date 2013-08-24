package co.zmc.projectindigo.gui;

import java.awt.Dimension;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import co.zmc.projectindigo.IndigoLauncher;
import co.zmc.projectindigo.gui.components.Image;
import co.zmc.projectindigo.gui.components.ProgressBar;
import co.zmc.projectindigo.managers.ServerManager;

@SuppressWarnings("serial")
public class ServerPanel extends JPanel {
    protected IndigoLauncher _launcher;
    protected final Logger   logger = Logger.getLogger("launcher");
    protected ProgressBar    _progressBar;
    protected ServerManager  _serverManager;

    public ServerPanel(IndigoLauncher launcher, int width, int height) {
        _launcher = launcher;
        setLayout(null);
        setOpaque(false);
        setFont(IndigoLauncher.getMinecraftFont(14));
        Dimension dim = new Dimension(width, height);
        setSize(dim);
        setPreferredSize(dim);
        setBounds(0, 0, width, height);
        setupLook();
    }

    public void setupLook() {
        _serverManager = new ServerManager();
        _serverManager.execute();
        add(new Image("bg", getWidth(), getHeight()));
    }

    public Logger getLogger() {
        return logger;
    }

    public void stateChanged(final String status, final float progress) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                if (!_progressBar.isVisible()) {
                    _progressBar.setVisible(true);
                }
                int intProgress = Math.round(progress);
                _progressBar.setValue(intProgress);
                String text = status;
                if (text.length() > 60) {
                    text = text.substring(0, 60) + "...";
                }
                _progressBar.setString(intProgress + "% " + text);
            }
        });
    }

}
