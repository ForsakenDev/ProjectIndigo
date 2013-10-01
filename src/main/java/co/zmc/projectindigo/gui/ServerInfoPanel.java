package co.zmc.projectindigo.gui;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import co.zmc.projectindigo.IndigoLauncher;
import co.zmc.projectindigo.data.Mod;
import co.zmc.projectindigo.data.Server;
import co.zmc.projectindigo.gui.components.Button;
import co.zmc.projectindigo.gui.components.CachedImage;
import co.zmc.projectindigo.gui.components.Label;
import co.zmc.projectindigo.gui.components.RoundedBox;
import co.zmc.projectindigo.gui.components.ScrollBarUI;
import co.zmc.projectindigo.utils.Utils;

@SuppressWarnings("serial")
public class ServerInfoPanel extends BasePanel implements ActionListener {

    private Server           server;

    private static final int PADDING = 20;

    private RoundedBox       actionsBox;
    private RoundedBox       descriptionBox;
    private RoundedBox       modListBox;
    private RoundedBox       headerBox;

    private Button           joinButton;
    private Button           editButton;
    private Button           settingsButton;
    private Button           forceUpdateButton;
    private Button           backButton;
    private Button           deleteButton;

    private JScrollPane      descriptionScrollPane;
    private JTextPane        serverDescriptionPane;
    private Label            serverIPLabel;
    private Label            serverNameLabel;

    private JScrollPane      modScrollPane;
    private JTextPane        modPane;

    private CachedImage      serverImage;

    public ServerInfoPanel(MainPanel mainPanel) {
        super(mainPanel, 2);
    }

    @Override
    public void initComponents() {
        headerBox = new RoundedBox(MainPanel.BORDER_COLOUR);
        headerBox.setBounds((getWidth() - (getWidth() - (PADDING * 2))) / 2, PADDING, getWidth() - (PADDING * 2), 60);

        serverNameLabel = new Label(this, "");
        serverNameLabel.setFont(IndigoLauncher.getMinecraftFont(24));
        serverNameLabel.setBounds(headerBox.getX() + PADDING, headerBox.getY() + ((headerBox.getHeight() - 26) / 2), (int) (headerBox.getWidth() * 0.75), 26);

        serverIPLabel = new Label(this, "");
        serverIPLabel.setFont(IndigoLauncher.getMinecraftFont(18));
        serverIPLabel.setForeground(new Color(255, 255, 255, 100));
        serverIPLabel.setBounds((headerBox.getX() + headerBox.getWidth() + PADDING) - (Utils.getLabelWidth(serverIPLabel) + PADDING), headerBox.getY() + ((headerBox.getHeight() - 18) / 2),
                (int) (headerBox.getWidth() * 0.75), 18);

        actionsBox = new RoundedBox(MainPanel.BORDER_COLOUR);
        actionsBox.setBounds(getWidth() - 200 - PADDING, getHeight() - (getHeight() - (headerBox.getHeight() + PADDING) - (PADDING * 2)) - PADDING, 200, getHeight()
                - (headerBox.getHeight() + PADDING) - (PADDING * 2));

        serverImage = new CachedImage("", "", 150, 150);
        serverImage.setBounds(actionsBox.getX() + ((actionsBox.getWidth() - 150) / 2), actionsBox.getY() + PADDING, 150, 150);

        joinButton = new Button(this, "Join Server");
        joinButton.setBackground(new Color(0x73FF73));
        joinButton.setHoverColour(new Color(0x40FF40));
        joinButton.setBounds(actionsBox.getX() + ((actionsBox.getWidth() - 180) / 2), (serverImage.getY() + serverImage.getHeight()) + PADDING, 180, 25);
        joinButton.addActionListener(this);
        joinButton.setActionCommand("CONNECT");

        editButton = new Button(this, "Edit Modpack");
        editButton.setBounds(actionsBox.getX() + ((actionsBox.getWidth() - 180) / 2), (joinButton.getY() + joinButton.getHeight()) + (PADDING / 2), 180, 25);
        editButton.addActionListener(this);
        editButton.setActionCommand("EDIT");

        settingsButton = new Button(this, "Settings");
        settingsButton.setBounds(actionsBox.getX() + ((actionsBox.getWidth() - 180) / 2), (editButton.getY() + editButton.getHeight()) + (PADDING / 2), 180, 25);
        settingsButton.addActionListener(this);
        settingsButton.setActionCommand("SETTINGS");

        forceUpdateButton = new Button(this, "Force Update");
        forceUpdateButton.setBounds(actionsBox.getX() + ((actionsBox.getWidth() - 180) / 2), (settingsButton.getY() + settingsButton.getHeight()) + (PADDING / 2), 180, 25);
        forceUpdateButton.addActionListener(this);
        forceUpdateButton.setActionCommand("UPDATE");

        deleteButton = new Button(this, "Delete");
        deleteButton.setBackground(new Color(0xFF7373));
        deleteButton.setHoverColour(new Color(0xFF4040));
        deleteButton.setBounds(actionsBox.getX() + ((actionsBox.getWidth() - 180) / 2), (forceUpdateButton.getY() + forceUpdateButton.getHeight()) + (PADDING / 2), 180, 25);
        deleteButton.addActionListener(this);
        deleteButton.setActionCommand("DELETE");

        backButton = new Button(this, "Back");
        backButton.setBounds(actionsBox.getX() + ((actionsBox.getWidth() - 180) / 2), (actionsBox.getY() + actionsBox.getHeight() - 25) - (PADDING / 2), 180, 25);
        backButton.addActionListener(this);
        backButton.setActionCommand("BACK");

        descriptionBox = new RoundedBox(MainPanel.BORDER_COLOUR);
        descriptionBox.setBounds(headerBox.getX(), actionsBox.getY(), getWidth() - 200 - (PADDING * 3), actionsBox.getHeight() - 175 - PADDING);

        serverDescriptionPane = new JTextPane();
        serverDescriptionPane.setForeground(Color.WHITE);
        serverDescriptionPane.setOpaque(false);
        serverDescriptionPane.setEditable(false);
        serverDescriptionPane.setFont(IndigoLauncher.getMinecraftFont(14));

        descriptionScrollPane = new JScrollPane(serverDescriptionPane);
        descriptionScrollPane.setBounds(descriptionBox.getX() + (PADDING / 2), descriptionBox.getY() + (PADDING / 2), descriptionBox.getWidth() - PADDING, descriptionBox.getHeight() - PADDING);
        descriptionScrollPane.setBorder(null);
        descriptionScrollPane.setOpaque(false);
        descriptionScrollPane.getViewport().setOpaque(false);
        descriptionScrollPane.getVerticalScrollBar().setOpaque(false);
        descriptionScrollPane.getVerticalScrollBar().setUI(new ScrollBarUI());

        modListBox = new RoundedBox(MainPanel.BORDER_COLOUR);
        modListBox.setBounds(descriptionBox.getX(), descriptionBox.getY() + descriptionBox.getHeight() + PADDING, descriptionBox.getWidth(), actionsBox.getHeight() - descriptionBox.getHeight()
                - PADDING);

        modPane = new JTextPane();
        modPane.setForeground(Color.WHITE);
        modPane.setOpaque(false);
        modPane.setEditable(false);
        modPane.setContentType("text/html");
        modPane.setFont(IndigoLauncher.getMinecraftFont(14));
        modPane.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    if (Desktop.isDesktopSupported()) {
                        try {
                            Desktop.getDesktop().browse(e.getURL().toURI());
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        } catch (URISyntaxException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });

        modScrollPane = new JScrollPane(modPane);
        modScrollPane.setBounds(modListBox.getX() + (PADDING / 2), modListBox.getY() + (PADDING / 2), modListBox.getWidth() - PADDING, modListBox.getHeight() - PADDING);
        modScrollPane.setBorder(null);
        modScrollPane.setOpaque(false);
        modScrollPane.getViewport().setOpaque(false);
        modScrollPane.getVerticalScrollBar().setOpaque(false);
        modScrollPane.getVerticalScrollBar().setUI(new ScrollBarUI());

        add(descriptionScrollPane, 0);
        add(modScrollPane, 0);
        add(serverImage, 0);
        add(descriptionBox);
        add(modListBox);
        add(headerBox);
        add(actionsBox);
    }

    public void setServer(Server server) {
        this.server = server;
        serverImage.update(server.getFullIp(), server.getLogo());
        serverImage.setBounds(actionsBox.getX() + ((actionsBox.getWidth() - 150) / 2), actionsBox.getY() + PADDING, 150, 150);

        serverNameLabel.setText(server.getName());
        serverIPLabel.setText(server.getFullIp());
        serverIPLabel.setBounds((headerBox.getX() + headerBox.getWidth() + PADDING) - (Utils.getLabelWidth(serverIPLabel) + (PADDING * 2)), headerBox.getY() + ((headerBox.getHeight() - 18) / 2),
                (int) (headerBox.getWidth() * 0.75), 18);
        serverDescriptionPane.setText(server.getDescription());
        serverDescriptionPane.setCaretPosition(0);
        String modsInfo = "<style type=\"text/css\">a {color:white;text-decoration: none} p {color: gray;}</style><table>";
        for (int i = 0; i < server.getMods().size(); i++) {
            Mod m = server.getMods().get(i);
            if (i > 0) {
                modsInfo += "<br />";
            }
            modsInfo += "<tr><td><a href=" + m.getInfoUrl() + ">" + m.getName() + " v" + m.getVersion() + " </a><p>-   by " + m.getAuthorsAsString() + "</p></td></tr>";
        }
        modsInfo += "</table>";
        modPane.setText(modsInfo);
        modPane.setCaretPosition(0);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("CONNECT")) {
            ((ServerPanel) getMainPanel().getPanel(1))._selectedServer = server.getFullIp();
            server.launch();
        } else if (e.getActionCommand().equals("EDIT")) {
            if (!this.server.getMinecraftDir().exists()) {
                JOptionPane.showMessageDialog(_mainPanel, "The server needs to be launched before you can edit it", "Not downloaded", JOptionPane.WARNING_MESSAGE);
            } else {
                try {
                    Desktop.getDesktop().open(this.server.getMinecraftDir());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } else if (e.getActionCommand().equals("SETTINGS")) {
            switchPage(3);
        } else if (e.getActionCommand().equals("BACK")) {
            ((ServerPanel) getMainPanel().getPanel(1)).resetServers();
            switchPage(1);
        } else if (e.getActionCommand().equals("UPDATE")) {
            server.forceUpdate();
            forceUpdateButton.setText("Will force update.");
        } else if (e.getActionCommand().equals("DELETE")) {
            ((ServerPanel) getMainPanel().getPanel(1)).getServerManager().removeServer(server.getFullIp());
            switchPage(1);
        }
    }

}
