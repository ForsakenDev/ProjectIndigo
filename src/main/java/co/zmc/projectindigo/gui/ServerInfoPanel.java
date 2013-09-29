package co.zmc.projectindigo.gui;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.JTextPane;

import co.zmc.projectindigo.IndigoLauncher;
import co.zmc.projectindigo.data.Server;
import co.zmc.projectindigo.gui.components.Button;
import co.zmc.projectindigo.gui.components.Image;
import co.zmc.projectindigo.gui.components.Label;
import co.zmc.projectindigo.gui.components.RoundedBox;
import co.zmc.projectindigo.utils.Utils;

@SuppressWarnings("serial")
public class ServerInfoPanel extends BasePanel implements ActionListener {

    private Server           server;

    private static final int PADDING = 20;

    private RoundedBox       actionsBox;
    private RoundedBox       descriptionBox;
    private RoundedBox       headerBox;

    private Button           joinButton;
    private Button           editButton;
    private Button           settingsButton;
    private Button           deleteButton;
    private Button           backButton;

    private JTextPane        serverDescriptionPane;

    private Label            serverIPLabel;
    private Label            serverNameLabel;

    private Image            serverImage;

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
        serverIPLabel.setBounds((headerBox.getX() + headerBox.getWidth() + PADDING) - (Utils.getLabelWidth(serverIPLabel) + PADDING), headerBox.getY() + ((headerBox.getHeight() - 18) / 2),
                (int) (headerBox.getWidth() * 0.75), 18);

        actionsBox = new RoundedBox(MainPanel.BORDER_COLOUR);
        actionsBox.setBounds(getWidth() - 200 - PADDING, getHeight() - (getHeight() - (headerBox.getHeight() + PADDING) - (PADDING * 2)) - PADDING, 200, getHeight()
                - (headerBox.getHeight() + PADDING) - (PADDING * 2));

        serverImage = new Image("base_char", 150, 150);
        serverImage.setBounds(actionsBox.getX() + ((actionsBox.getWidth() - 150) / 2), actionsBox.getY() + PADDING, 150, 150);

        joinButton = new Button(this, "Join Server");
        joinButton.setBackground(new Color(0x73FF73));
        joinButton.setHoverColour(new Color(0x40FF40));
        joinButton.setBounds(actionsBox.getX() + ((actionsBox.getWidth() - 180) / 2), (serverImage.getY() + serverImage.getHeight()) + PADDING, 180, 25);
        joinButton.addActionListener(this);
        joinButton.setActionCommand("CONNECT");

        editButton = new Button(this, "Edit Modpack");
        editButton.setBackground(new Color(0xCCCCCC));
        editButton.setHoverColour(new Color(0xAAAAAA));
        editButton.setBounds(actionsBox.getX() + ((actionsBox.getWidth() - 180) / 2), (joinButton.getY() + joinButton.getHeight()) + PADDING, 180, 25);
        editButton.addActionListener(this);
        editButton.setActionCommand("EDIT");

        settingsButton = new Button(this, "Settings");
        settingsButton.setBackground(new Color(0xCCCCCC));
        settingsButton.setHoverColour(new Color(0xAAAAAA));
        settingsButton.setBounds(actionsBox.getX() + ((actionsBox.getWidth() - 180) / 2), (editButton.getY() + editButton.getHeight()) + PADDING, 180, 25);
        settingsButton.addActionListener(this);
        settingsButton.setActionCommand("SETTINGS");

        deleteButton = new Button(this, "Delete");
        deleteButton.setBackground(new Color(0xFF7373));
        deleteButton.setHoverColour(new Color(0xFF4040));
        deleteButton.setBounds(actionsBox.getX() + ((actionsBox.getWidth() - 180) / 2), (actionsBox.getY() + actionsBox.getHeight() - 25) - (PADDING / 2), 180, 25);
        deleteButton.addActionListener(this);
        deleteButton.setActionCommand("DELETE");

        descriptionBox = new RoundedBox(MainPanel.BORDER_COLOUR);
        descriptionBox.setBounds(headerBox.getX(), actionsBox.getY(), getWidth() - 200 - (PADDING * 3), getHeight() - (headerBox.getHeight() + PADDING) - (PADDING * 2));

        serverDescriptionPane = new JTextPane();
        serverDescriptionPane.setForeground(Color.WHITE);
        serverDescriptionPane.setOpaque(false);
        serverDescriptionPane.setEditable(false);
        serverDescriptionPane.setFont(IndigoLauncher.getMinecraftFont(14));
        serverDescriptionPane.setBounds(descriptionBox.getX() + (PADDING / 2), descriptionBox.getY() + (PADDING / 2), descriptionBox.getWidth() - (PADDING * 2), descriptionBox.getY() - (PADDING * 2));

        add(serverDescriptionPane, 0);
        add(serverImage, 0);
        add(descriptionBox);
        add(headerBox);
        add(actionsBox);
    }

    public void setServer(Server server) {
        this.server = server;

        serverNameLabel.setText(server.getName());
        serverIPLabel.setText(server.getFullIp());
        serverIPLabel.setBounds((headerBox.getX() + headerBox.getWidth() + PADDING) - (Utils.getLabelWidth(serverIPLabel) + (PADDING * 2)), headerBox.getY() + ((headerBox.getHeight() - 18) / 2),
                (int) (headerBox.getWidth() * 0.75), 18);
        serverDescriptionPane.setText("This is a server\nThis text is just the description placeholder\nAnd things\nkbye");
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
        } else if (e.getActionCommand().equals("DELETE")) {
            // DELETE SERVER PLACEHOLDER
        } else if (e.getActionCommand().equals("BACK")) {
            switchPage(1);
        }
    }

}
