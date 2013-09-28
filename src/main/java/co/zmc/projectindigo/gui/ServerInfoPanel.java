package co.zmc.projectindigo.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import co.zmc.projectindigo.IndigoLauncher;
import co.zmc.projectindigo.data.Server;
import co.zmc.projectindigo.gui.components.Button;
import co.zmc.projectindigo.gui.components.Image;
import co.zmc.projectindigo.gui.components.Label;
import co.zmc.projectindigo.gui.components.RoundedBox;

@SuppressWarnings("serial")
public class ServerInfoPanel extends BasePanel implements ActionListener {

	private Server server;

	private JPanel panel;

	private RoundedBox actionsBox;
	private RoundedBox descriptionBox;
	private RoundedBox headerBox;

	private Button joinButton;
	private Button deleteButton;
	private Button backButton;

	private JTextPane serverDescriptionPane;
	
	private Label serverIPLabel;
	private Label serverNameLabel;

	private Image serverImage;
	
	public ServerInfoPanel(MainPanel mainPanel) {
		super(mainPanel, 1);
	}

	@Override
	public void initComponents() {
		int xPadding = 10;
		int yPadding = 10;

		this.setLayout(new FlowLayout());

		panel = new JPanel();
		panel.setOpaque(false);
		panel.setPreferredSize(new Dimension(this.getWidth() - 20, this.getHeight() - 20));
		panel.setLayout(new BorderLayout(xPadding, yPadding));
		
		actionsBox = new RoundedBox(MainPanel.BORDER_COLOUR);
		descriptionBox = new RoundedBox(MainPanel.BORDER_COLOUR);
		headerBox = new RoundedBox(MainPanel.BORDER_COLOUR);

		actionsBox.setPreferredSize(new Dimension(200, 70));
		descriptionBox.setPreferredSize(new Dimension(panel.getPreferredSize().width - 200, panel.getPreferredSize().height - 60));
		headerBox.setPreferredSize(new Dimension(panel.getPreferredSize().width, 45));

		actionsBox.setOpaque(false);
		descriptionBox.setOpaque(false);
		headerBox.setOpaque(false);
		
		// Header box
		headerBox.setLayout(new FlowLayout());

		serverNameLabel = new Label();
		serverIPLabel = new Label();

		serverNameLabel.setFont(IndigoLauncher.getMinecraftFont(30));
		serverNameLabel.setForeground(Color.WHITE);

		serverIPLabel.setForeground(Color.WHITE);

		headerBox.add(serverNameLabel);
		headerBox.add(serverIPLabel);
		
		// Actions box
		actionsBox.setLayout(new BoxLayout(actionsBox, BoxLayout.Y_AXIS));

		joinButton = new Button("Join Server");
		deleteButton = new Button("Delete Server");
		backButton = new Button("Back");

		serverImage = new Image("base_char", 150, 150); // SERVER IMAGE PLACEHOLDER. 
														// I thought would could be the server's image but looks pretty good with user head
		serverImage.setMaximumSize(new Dimension(150, 150));
		serverImage.setAlignmentX(CENTER_ALIGNMENT);

		joinButton.addActionListener(this);
		joinButton.setActionCommand("CONNECT");
		deleteButton.addActionListener(this);
		deleteButton.setActionCommand("DELETE");
		backButton.addActionListener(this);
		backButton.setActionCommand("BACK");

		joinButton.setMaximumSize(new Dimension(180, 25));
		deleteButton.setMaximumSize(new Dimension(180, 25));
		backButton.setMaximumSize(new Dimension(180, 25));

		joinButton.setAlignmentX(CENTER_ALIGNMENT);
		deleteButton.setAlignmentX(CENTER_ALIGNMENT);
		backButton.setAlignmentX(CENTER_ALIGNMENT);

		joinButton.setBackground(new Color(0xFF5500));
		joinButton.setHoverColour(new Color(0xFF0000));

		int buttonSpacing = 10;

		actionsBox.add(Box.createRigidArea(new Dimension(10, buttonSpacing)));
		actionsBox.add(serverImage);
		actionsBox.add(Box.createRigidArea(new Dimension(10, buttonSpacing)));
		actionsBox.add(joinButton);
		actionsBox.add(Box.createRigidArea(new Dimension(10, buttonSpacing)));
		actionsBox.add(backButton);
		actionsBox.add(Box.createRigidArea(new Dimension(10, buttonSpacing)));
		actionsBox.add(deleteButton);
		
		// Description box
		descriptionBox.setLayout(new BorderLayout());
		
		serverDescriptionPane = new JTextPane();
		serverDescriptionPane.setForeground(Color.WHITE);
		serverDescriptionPane.setOpaque(false);
		serverDescriptionPane.setEditable(false);
		//serverDescriptionPane.setFont(IndigoLauncher.getMinecraftFont(14));

		descriptionBox.add(serverDescriptionPane, BorderLayout.CENTER);
		
		panel.add(headerBox, BorderLayout.PAGE_START);
		panel.add(descriptionBox, BorderLayout.CENTER);
		panel.add(actionsBox, BorderLayout.LINE_END);
		
		this.add(panel);

	}

	public void setServer(Server server) {
		this.server = server;

		serverNameLabel.setText(server.getName());
		serverIPLabel.setText(server.getFullIp());

		serverDescriptionPane.setText("This is a server\nThis text is just the description placeholder\nAnd things\nkbye");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("CONNECT")) {
			((ServerPanel) getMainPanel().getPanel(1))._selectedServer = server.getFullIp();
			switchPage(2);
		}

		if (e.getActionCommand().equals("DELETE")) {
			// DELETE SERVER PLACEHOLDER
		}

		if (e.getActionCommand().equals("BACK")) {
			switchPage(1);
		}
	}

}
