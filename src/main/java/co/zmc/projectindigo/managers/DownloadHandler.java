package co.zmc.projectindigo.managers;

import java.io.IOException;
import java.net.URL;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import co.zmc.projectindigo.IndigoLauncher;
import co.zmc.projectindigo.data.LoginResponse;
import co.zmc.projectindigo.data.Server;
import co.zmc.projectindigo.data.log.Logger;
import co.zmc.projectindigo.gui.MainPanel;
import co.zmc.projectindigo.gui.ProgressPanel;
import co.zmc.projectindigo.mclaunch.MinecraftLauncher;
import co.zmc.projectindigo.utils.Settings;

public class DownloadHandler extends SwingWorker<Boolean, Void> {
    protected String        _status;
    protected MainPanel     _mainPanel;
    protected LoginResponse _response;
    protected Settings      _settings;
    protected Server        _server;
    protected URL[]         _jarURLs;
    protected double        totalDownloadSize   = 0;
    protected double        totalDownloadedSize = 0;

    public DownloadHandler(Server server, MainPanel section, LoginResponse response, Settings settings) {
        _server = server;
        _mainPanel = section;
        _response = response;
        _settings = settings;
        _status = "";
    }

    @Override
    protected Boolean doInBackground() {
        if (_server.shouldUpdate() || _server.shouldDownload()) {
            JOptionPane.showMessageDialog(null, "An update was found for " + _server.getName());
            try {
                if (_server.download(((ProgressPanel) _mainPanel.getPanel(-1)))) {
                    ((ProgressPanel) _mainPanel.getPanel(-1)).stateChanged("Download complete. Launching Game...", 100);
                    return true;
                }
            } catch (IOException e) {
                Logger.logError(e.getMessage(), e);
            }
        }
        return false;
    }

    protected void done() {
        Thread.yield();
        while (!_server.isFinishedDownloading()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        launch();
    }

    public void launch() {
        Logger.logInfo("Download complete");
        if (_server != null) {
            try {
                MinecraftLauncher.launchMinecraft(_server, _response.getUsername(), _response.getSessionId(), "MinecraftForge.zip", _settings);
                try {
                    Thread.sleep(3500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                IndigoLauncher._launcher.setVisible(false);
                IndigoLauncher._launcher.dispose();
                System.exit(0);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}