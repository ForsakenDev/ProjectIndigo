package co.zmc.projectindigo.gui;

import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import co.zmc.projectindigo.IndigoLauncher;
import co.zmc.projectindigo.data.Server;
import co.zmc.projectindigo.gui.components.Label;
import co.zmc.projectindigo.gui.components.RoundedBox;
import co.zmc.projectindigo.gui.components.ServerInfo;
import co.zmc.projectindigo.managers.ServerManager;

@SuppressWarnings("serial")
public class ServerPanel extends BasePanel {

    private ServerManager           _serverManager;

    private RoundedBox              _serverBox;
    private Map<String, ServerInfo> _servers        = new HashMap<String, ServerInfo>();
    private Label                   _addBtn;
    public String                   _selectedServer = "";

    public ServerPanel(MainPanel mainPanel) {
        super(mainPanel, 1);
        loadServerManager();
    }

    public void initComponents() {
        _serverBox = new RoundedBox(MainPanel.BORDER_COLOUR);
        _serverBox.setBounds((getWidth() - (getWidth() - 50)) / 2, (getHeight() - (getHeight() - 50)) / 2, getWidth() - 50, getHeight() - 50);
        _addBtn = new Label(this, "Select the server you wish to join");
        _addBtn.setForeground(Color.WHITE);

        add(_serverBox);

    }

    public void loadServerManager() {
        _serverManager = new ServerManager(_mainPanel);
        _serverManager.execute();
    }

    public ServerManager getServerManager() {
        return _serverManager;
    }

    public ServerInfo getServerInfo(String fullIp) {
        if (_servers.containsKey(fullIp)) { return _servers.get(fullIp); }
        return null;
    }

    public synchronized void addServer(final Server server) {
        try {
            ServerInfo info = new ServerInfo(this, server);
            info.setBounds((getWidth() - ((getWidth() - 50) - (50 * 2))) / 2, ((getHeight() - (getHeight() - 50)) / 2)
                    + (MainPanel.PADDING + (_servers.size() * 32 + MainPanel.PADDING)), (getWidth() - 50) - (50 * 2), 24);
            _servers.put(server.getFullIp(), info);

        } catch (Exception e) {
        }
    }

    public void launchServer(String fullIp) {
        _mainPanel.switchPage(-1);
        try {
            IndigoLauncher._launcher.launchMinecraft(_serverManager.getServer(fullIp), ((LoginPanel) _mainPanel.getPanel(0)).getLoginResponse());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
