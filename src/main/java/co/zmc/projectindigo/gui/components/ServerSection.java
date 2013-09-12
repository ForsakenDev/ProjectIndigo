package co.zmc.projectindigo.gui.components;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import co.zmc.projectindigo.IndigoLauncher;
import co.zmc.projectindigo.data.Server;
import co.zmc.projectindigo.gui.MainPanel;
import co.zmc.projectindigo.managers.ServerManager;

@SuppressWarnings("serial")
public class ServerSection extends JLayeredPane {
    private ServerManager           _serverManager;

    private MainPanel               _mainPanel;
    private RoundedBox              _serverBox;
    private String                  _activeUser     = "";
    private Dimension               _baseSize;
    private Map<String, ServerInfo> _servers        = new HashMap<String, ServerInfo>();
    private ProgressBar             _progressBar;
    private Button                  _addBtn;
    public String                   _selectedServer = "";

    public ServerSection(MainPanel mainPanel) {
        _mainPanel = mainPanel;
        _baseSize = new Dimension(mainPanel.getWidth() - ((MainPanel.BTN_SIZE.width * 2) + (MainPanel.PADDING * 6)), mainPanel.getHeight()
                - (MainPanel.PADDING * 2));
        setLayout(null);
        setOpaque(false);
        setupLook();
        _mainPanel.add(this);
        setBounds(MainPanel.PADDING, MainPanel.PADDING, _baseSize.width, _baseSize.height);
        setFormsEnabled(true);
        loadServerManager();
    }

    public String getActiveUser() {
        return _activeUser;
    }

    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        _serverBox.setBounds(0, 0, width, height);
        _progressBar.setBounds(MainPanel.PADDING, height - MainPanel.PADDING - MainPanel.BTN_SIZE.height, width - (MainPanel.PADDING * 2),
                MainPanel.BTN_SIZE.height);
        _addBtn.setBounds(MainPanel.PADDING, height - MainPanel.PADDING - MainPanel.BTN_SIZE.height, width - (MainPanel.PADDING * 2),
                MainPanel.BTN_SIZE.height);
    }

    public void setupLook() {
        _serverBox = new RoundedBox(MainPanel.BORDER_COLOUR);
        add(_serverBox);
        _addBtn = new Button(this, "Add Server");
        _addBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String ip = JOptionPane.showInputDialog(_mainPanel, "Please enter the server IP");
                if (ip != null && !ip.contains(" ")) {
                    int port = 25565;
                    try {
                        if (ip.contains(":")) {
                            int index = ip.indexOf(":");
                            port = Integer.parseInt(ip.substring(index + 1));
                            ip = ip.substring(0, index);
                        }
                        _serverManager.loadServer(ip, port);
                    } catch (NumberFormatException e1) {
                        JOptionPane.showMessageDialog(getParent(), "You need to include a valid port number", "Invalid Port",
                                JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(getParent(), "You need to include a valid IP Address", "Invalid IP Address",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        _progressBar = new ProgressBar(0.75F);
        _progressBar.setFont(IndigoLauncher.getMinecraftFont(10));
        add(_progressBar, 0);
    }

    public void loadServerManager() {
        _serverManager = new ServerManager(this);
        _serverManager.execute();
    }

    public ServerManager getServerManager() {
        return _serverManager;
    }

    public void setActive(String fullIp) {
        for (ServerInfo info : _servers.values()) {
            info.setActive(false);
        }
        if (_servers.containsKey(fullIp)) {
            _servers.get(fullIp).setActive(true);
        }
    }

    public Server getActiveServer() {
        if (_selectedServer != null) { return getServerManager().getServer(_selectedServer); }
        return null;
    }

    public ServerInfo getServerInfo(String fullIp) {
        if (_servers.containsKey(fullIp)) { return _servers.get(fullIp); }
        return null;
    }

    public synchronized void addServer(final Server server) {
        try {
            System.out.println("Adding " + server.getName());
            ServerInfo info = new ServerInfo(this, server);
            info.setBounds(MainPanel.PADDING, MainPanel.PADDING + (_servers.size() * 32 + MainPanel.PADDING), getWidth() - (MainPanel.PADDING * 2),
                    24);
            _servers.put(server.getFullIp(), info);

        } catch (Exception e) {
        }
    }

    public void setFormsEnabled(boolean state) {
        if (state) {
            _progressBar.updateProgress(0);
        }
        _progressBar.setEnabled(!state);
        _addBtn.setEnabled(state);
    }

    public void stateChanged(final String status, final float progress) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (!_progressBar.isVisible()) {
                    _progressBar.setVisible(true);
                }
                int intProgress = Math.round(progress);
                if (intProgress >= 100) {
                    _progressBar.setVisible(false);
                    return;
                }
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
