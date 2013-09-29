package co.zmc.projectindigo.gui.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

import co.zmc.projectindigo.IndigoLauncher;
import co.zmc.projectindigo.data.Server;
import co.zmc.projectindigo.gui.MainPanel;
import co.zmc.projectindigo.gui.ServerInfoPanel;
import co.zmc.projectindigo.gui.ServerPanel;

@SuppressWarnings("serial")
public class ServerInfo extends JLabel {
    private JLabel     _ip;
    private Server     _server;
    private boolean    _active = false;
    private RoundedBox _serverBox;

    public ServerInfo(final ServerPanel serverPanel, final Server server) {
        _server = server;
        _ip = new JLabel(_server.getFullIp());

        setText(server.getName());

        _serverBox = new RoundedBox(new Color(0, 0, 0, 0));
        serverPanel.add(_serverBox);
        serverPanel.add(this, 0);
        serverPanel.add(_ip, 0);

        setForeground(Color.WHITE);
        setFont(IndigoLauncher.getMinecraftFont(20));

        _ip.setForeground(Color.GRAY);
        _ip.setFont(IndigoLauncher.getMinecraftFont(12));

        _serverBox.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent event) {
            }

            public void mouseEntered(MouseEvent event) {
            }

            public void mouseExited(MouseEvent event) {
            }

            public void mousePressed(MouseEvent event) {
                if (!isActive()) {
                    ((ServerInfoPanel) serverPanel.getMainPanel().getPanel(2)).setServer(_server);
                    setActive(true);
                    serverPanel.switchPage(2);
                }
            }

            public void mouseReleased(MouseEvent event) {
            }
        });
    }

    public void setBounds(int x, int y, int w, int h) {
        _serverBox.setBounds(x - MainPanel.PADDING, y - MainPanel.PADDING, w + (MainPanel.PADDING * 2), h + 12 + 5 + (MainPanel.PADDING * 2));
        w -= MainPanel.PADDING;
        super.setBounds(x, y, w, h);
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

    public List<Component> getAllComponents() {
        List<Component> list = new ArrayList<Component>();
        list.add(_serverBox);
        list.add(_ip);
        return list;
    }
}