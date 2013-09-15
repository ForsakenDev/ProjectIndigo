package co.zmc.projectindigo.gui.components;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.io.IOException;

import javax.swing.JLabel;

import co.zmc.projectindigo.IndigoLauncher;
import co.zmc.projectindigo.data.Server;
import co.zmc.projectindigo.gui.MainPanel;
import co.zmc.projectindigo.gui.ServerPanel;

@SuppressWarnings("serial")
public class ServerInfo extends JLabel {
    private JLabel     _ip;
    private JLabel     _users;
    private Server     _server;
    private boolean    _active = false;
    private RoundedBox _serverBox;

    public ServerInfo(final ServerPanel serverPanel, final Server server) {
        _server = server;
        _ip = new JLabel(_server.getFullIp());
        _users = new JLabel("Edit");
        _users.addMouseListener(new MouseListener() {

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
        serverPanel.add(_users, 0);

        setForeground(Color.WHITE);
        setFont(IndigoLauncher.getMinecraftFont(20));

        _ip.setForeground(Color.GRAY);
        _ip.setFont(IndigoLauncher.getMinecraftFont(12));

        _users.setForeground(Color.WHITE);
        _users.setFont(IndigoLauncher.getMinecraftFont(20));

        _serverBox.addMouseListener(new MouseListener() {

            public void mouseClicked(MouseEvent event) {
                serverPanel.launchServer(server.getFullIp());
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
        w -= getUserWidth() + MainPanel.PADDING;
        super.setBounds(x, y, w, h);
        _users.setBounds(x + w + MainPanel.PADDING, y, getUserWidth(), h);
        _ip.setBounds(x, y + h + 5, w, 12);
    }

    private int getUserWidth() {
        FontRenderContext frc = new FontRenderContext(_users.getFont().getTransform(), true, true);
        return (int) (_users.getFont().getStringBounds(_users.getText(), frc).getWidth());
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