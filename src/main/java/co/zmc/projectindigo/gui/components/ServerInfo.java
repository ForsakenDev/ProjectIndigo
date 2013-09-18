package co.zmc.projectindigo.gui.components;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.JLabel;

import co.zmc.projectindigo.IndigoLauncher;
import co.zmc.projectindigo.data.Server;
import co.zmc.projectindigo.gui.MainPanel;
import co.zmc.projectindigo.gui.ServerPanel;

@SuppressWarnings("serial")
public class ServerInfo extends JLabel {
    private JLabel     _ip;
    private Server     _server;
    private boolean    _active = false;
    private RoundedBox _serverBox;
    private Image      _settings;
    private Image      _folder;

    public ServerInfo(final ServerPanel serverPanel, final Server server) {
        _server = server;
        _ip = new JLabel(_server.getFullIp());

        _settings = new Image("settings");
        _settings.setVisible(false);
        _folder = new Image("folder");
        _folder.addMouseListener(new MouseListener() {

            public void mouseClicked(MouseEvent event) {
            }

            public void mouseEntered(MouseEvent event) {
            }

            public void mouseExited(MouseEvent event) {
            }

            public void mousePressed(MouseEvent event) {
                try {
                    Desktop.getDesktop().open(_server.getMinecraftDir());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            public void mouseReleased(MouseEvent event) {
            }

        });
        setText(server.getName());
        _serverBox = new RoundedBox(new Color(0, 0, 0, 0));
        serverPanel.add(_serverBox);
        serverPanel.add(this, 0);
        serverPanel.add(_ip, 0);
        serverPanel.add(_settings, 0);
        serverPanel.add(_folder, 0);

        setForeground(Color.WHITE);
        setFont(IndigoLauncher.getMinecraftFont(20));

        _ip.setForeground(Color.GRAY);
        _ip.setFont(IndigoLauncher.getMinecraftFont(12));

        _serverBox.addMouseListener(new MouseListener() {

            public void mouseClicked(MouseEvent event) {
                serverPanel._selectedServer = server.getFullIp();
                serverPanel.switchPage(2);
                setActive(true);
            }

            public void mouseEntered(MouseEvent event) {
            }

            public void mouseExited(MouseEvent event) {
            }

            public void mousePressed(MouseEvent event) {
            }

            public void mouseReleased(MouseEvent event) {
            }

        });
    }

    public void setBounds(int x, int y, int w, int h) {
        _serverBox.setBounds(x - MainPanel.PADDING, y - MainPanel.PADDING, w + (MainPanel.PADDING * 2), h + 12 + 5 + (MainPanel.PADDING * 2));
        w -= MainPanel.PADDING;
        super.setBounds(x, y, w, h);
        _settings.setBounds(w, ((h + 12 - _settings.getIcon().getIconHeight()) / 2) + y, _settings.getIcon().getIconWidth(), _settings.getIcon()
                .getIconHeight());
        _folder.setBounds(w + _folder.getIcon().getIconWidth() + MainPanel.PADDING, ((h + 12 - _folder.getIcon().getIconHeight()) / 2) + y, _folder
                .getIcon().getIconWidth(), _folder.getIcon().getIconHeight());
        _ip.setBounds(x, y + h + 5, w, 12);
    }

    public boolean isActive() {
        return _active;
    }

    public void setActive(boolean active) {
        _active = active;
        if (active) {
            _serverBox.setBackground(MainPanel.HIGHLIGHT_COLOUR);
        } else {
            _serverBox.setBackground(new Color(0, 0, 0, 0));
        }

    }
}