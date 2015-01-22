package co.forsaken.projectindigo.gui;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import co.forsaken.api.json.AsyncJsonWebCall;
import co.forsaken.projectindigo.IndigoLauncher;
import co.forsaken.projectindigo.data.Mod;
import co.forsaken.projectindigo.data.Mod.ModType;
import co.forsaken.projectindigo.data.Server;
import co.forsaken.projectindigo.data.tokens.ActiveServersToken;
import co.forsaken.projectindigo.data.tokens.ServerToken;
import co.forsaken.projectindigo.gui.components.Button;
import co.forsaken.projectindigo.gui.components.CachedImage;
import co.forsaken.projectindigo.gui.components.Label;
import co.forsaken.projectindigo.gui.components.RoundedBox;
import co.forsaken.projectindigo.gui.components.ScrollBarUI;
import co.forsaken.projectindigo.log.LogManager;
import co.forsaken.projectindigo.utils.Callback;
import co.forsaken.projectindigo.utils.Utils;

@SuppressWarnings("serial") public class ServerBasePanel extends BasePanel implements ActionListener {

  private Map<String, Server> servers             = new HashMap<String, Server>();
  private static final int    PADDING             = 20;

  private RoundedBox          actionsBox;
  private RoundedBox          descriptionBox;
  private RoundedBox          modListBox;
  private RoundedBox          headerBox;
  private RoundedBox          otherServersBox;
  private Map<String, Button> otherServersButtons = new HashMap<String, Button>();
  private Button              joinButton;
  private Button              editButton;
  private Button              settingsButton;
  private Button              forceUpdateButton;
  private CachedImage         forsakenLogo;
  private JScrollPane         descriptionScrollPane;
  private JTextPane           serverDescriptionPane;
  private Label               serverIPLabel;
  private Label               serverNameLabel;

  private JScrollPane         modScrollPane;
  private JTextPane           modPane;

  public CachedImage          serverImage;
  private Server              activeServer;
  private boolean             launched            = false;

  public ServerBasePanel(MainPanel mainPanel) {
    super(mainPanel, 1);
    new AsyncJsonWebCall("http://info.forsaken.co/server/getActive").execute(ActiveServersToken.class, new Callback<ActiveServersToken>() {
      public void run(final ActiveServersToken result) {
        for (final ServerToken t : result.servers) {
          Server s = new Server(t);
          if (s != null && s.online) {
            if (activeServer == null) {
              setServer(s);
            }
            addServer(s);
            servers.put(t.name, s);
          } else {
            LogManager.error(t.friendlyName + " repository seems to be offline, this is " + t.modpackType + "'s fault");
          }
        }
      }
    });
  }

  public void initComponents() {
    headerBox = new RoundedBox(MainPanel.BORDER_COLOUR);
    headerBox.setBounds((getWidth() - (getWidth() - (PADDING * 2))) / 2, PADDING, getWidth() - (PADDING * 2), 60);

    serverNameLabel = new Label(this, "");
    serverNameLabel.setFont(IndigoLauncher.getMinecraftFont(24));
    serverNameLabel.setBounds(headerBox.getX() + PADDING, headerBox.getY() + ((headerBox.getHeight() - 26) / 2), (int) (headerBox.getWidth() * 0.75), 26);

    serverIPLabel = new Label(this, "");
    serverIPLabel.setFont(IndigoLauncher.getMinecraftFont(18));
    serverIPLabel.setForeground(new Color(255, 255, 255, 100));
    serverIPLabel.setBounds((headerBox.getX() + headerBox.getWidth() + PADDING) - (Utils.getLabelWidth(serverIPLabel) + PADDING), headerBox.getY() + ((headerBox.getHeight() - 18) / 2), (int) (headerBox.getWidth() * 0.75), 18);

    otherServersBox = new RoundedBox(MainPanel.BORDER_COLOUR);
    otherServersBox.setBounds(headerBox.getX(), getHeight() - (getHeight() - (headerBox.getHeight() + PADDING) - (PADDING * 2)) - PADDING, 200, getHeight() - (headerBox.getHeight() + PADDING) - (PADDING * 2));

    actionsBox = new RoundedBox(MainPanel.BORDER_COLOUR);
    actionsBox.setBounds(getWidth() - 200 - PADDING, getHeight() - (getHeight() - (headerBox.getHeight() + PADDING) - (PADDING * 2)) - PADDING, 200, getHeight() - (headerBox.getHeight() + PADDING) - (PADDING * 2));

    serverImage = new CachedImage("", "", 150, 150);
    serverImage.setBounds(actionsBox.getX() + ((actionsBox.getWidth() - 150) / 2), actionsBox.getY() + PADDING, 150, 150);

    forsakenLogo = new CachedImage("forsaken_logo", "http://i.imgur.com/E1DmV87.png", 150, 150);
    forsakenLogo.setBounds(otherServersBox.getX() + ((otherServersBox.getWidth() - 150) / 2), (otherServersBox.getY() + otherServersBox.getHeight() - ((otherServersBox.getWidth() - 150) / 2)) - 150, 150, 150);
    forsakenLogo.addMouseListener(new MouseListener() {

      @Override public void mouseClicked(MouseEvent e) {
        if (Desktop.isDesktopSupported()) {
          try {
            Desktop.getDesktop().browse(new URL("http://www.forsaken.co").toURI());
          } catch (IOException e1) {
            e1.printStackTrace();
          } catch (URISyntaxException e1) {
            e1.printStackTrace();
          }
        }
      }

      @Override public void mousePressed(MouseEvent e) {}

      @Override public void mouseReleased(MouseEvent e) {}

      @Override public void mouseEntered(MouseEvent e) {}

      @Override public void mouseExited(MouseEvent e) {}
    });
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
    settingsButton.setBounds(actionsBox.getX() + ((actionsBox.getWidth() - 180) / 2), (actionsBox.getY() + actionsBox.getHeight() - 25) - (PADDING / 2), 180, 25);
    settingsButton.addActionListener(this);
    settingsButton.setActionCommand("SETTINGS");

    forceUpdateButton = new Button(this, "Force Update");
    forceUpdateButton.setBounds(actionsBox.getX() + ((actionsBox.getWidth() - 180) / 2), (editButton.getY() + editButton.getHeight()) + (PADDING / 2), 180, 25);
    forceUpdateButton.addActionListener(this);
    forceUpdateButton.setActionCommand("UPDATE");

    descriptionBox = new RoundedBox(MainPanel.BORDER_COLOUR);
    descriptionBox.setBounds(otherServersBox.getX() + otherServersBox.getWidth() + PADDING, actionsBox.getY(), getWidth() - 400 - (PADDING * 4), actionsBox.getHeight() - 175 - PADDING);

    serverDescriptionPane = new JTextPane();
    serverDescriptionPane.setForeground(Color.WHITE);
    serverDescriptionPane.setOpaque(false);
    serverDescriptionPane.setEditable(false);
    serverDescriptionPane.setFont(IndigoLauncher.getMinecraftFont(14));
    serverDescriptionPane.setContentType("text/html");
    serverDescriptionPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
    serverDescriptionPane.addHyperlinkListener(new HyperlinkListener() {
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
    descriptionScrollPane = new JScrollPane(serverDescriptionPane);
    descriptionScrollPane.setBounds(descriptionBox.getX() + (PADDING / 2), descriptionBox.getY() + (PADDING / 2), descriptionBox.getWidth() - PADDING, descriptionBox.getHeight() - PADDING);
    descriptionScrollPane.setBorder(null);
    descriptionScrollPane.setOpaque(false);
    descriptionScrollPane.getViewport().setOpaque(false);
    descriptionScrollPane.getVerticalScrollBar().setOpaque(false);
    descriptionScrollPane.getVerticalScrollBar().setUI(new ScrollBarUI());

    modListBox = new RoundedBox(MainPanel.BORDER_COLOUR);
    modListBox.setBounds(descriptionBox.getX(), descriptionBox.getY() + descriptionBox.getHeight() + PADDING, descriptionBox.getWidth(), actionsBox.getHeight() - descriptionBox.getHeight() - PADDING);

    modPane = new JTextPane();
    modPane.setForeground(Color.WHITE);
    modPane.setOpaque(false);
    modPane.setEditable(false);
    modPane.setContentType("text/html");
    modPane.setFont(IndigoLauncher.getMinecraftFont(14));
    modPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
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
    Dimension size = new Dimension((int) modScrollPane.getVerticalScrollBar().getSize().getWidth(), 200);
    modScrollPane.getVerticalScrollBar().setMinimumSize(size);
    modScrollPane.getVerticalScrollBar().setMaximumSize(size);
    modScrollPane.getVerticalScrollBar().setSize(size);

    add(descriptionScrollPane, 0);
    add(modScrollPane, 0);
    add(serverImage, 0);
    add(forsakenLogo, 0);
    add(descriptionBox);
    add(modListBox);
    add(headerBox);
    add(actionsBox);
    add(otherServersBox);
  }

  public void addServer(Server server) {
    Button btn = new Button(this, server.getToken().friendlyName);
    btn.setBounds(otherServersBox.getX() + ((otherServersBox.getWidth() - 180) / 2), (otherServersBox.getY()) + (PADDING / 2) + ((25 + (PADDING / 2)) * (9 - Integer.parseInt(server.getToken().order))), 180, 25);
    btn.addActionListener(this);
    btn.setActionCommand(server.getToken().name);
    otherServersButtons.put(server.getToken().name, btn);
  }

  public void setServer(Server _server) {
    if (!_server.online) {
      LogManager.error(_server.getToken().friendlyName + " could not be activated.. It had some residual errors");
      return;
    }
    activeServer = _server;
    serverNameLabel.setText(activeServer.getToken().friendlyName + " v" + activeServer.getToken().version);
    serverIPLabel.setText(activeServer.getToken().friendlyIp);
    serverIPLabel.setBounds((headerBox.getX() + headerBox.getWidth() + PADDING) - (Utils.getLabelWidth(serverIPLabel) + (PADDING * 2)), headerBox.getY() + ((headerBox.getHeight() - 18) / 2), (int) (headerBox.getWidth() * 0.75), 18);

    serverDescriptionPane.setText(activeServer.getDesc().replace("\n", "<br />"));
    serverDescriptionPane.setCaretPosition(0);
    String modsInfo = "";
    SortedSet<String> keys = new TreeSet<String>(activeServer.getModList().keySet());
    for (String key : keys) {
      Mod m = activeServer.getModList().get(key);
      if (m.getType() != ModType.mod) continue;

      if (!modsInfo.isEmpty()) modsInfo += "<br />";
      modsInfo += "<tr><td><a href=" + m.getInfoUrl() + ">" + m.getName() + " </a>" + (m.getAuthorsAsString() != null && !m.getAuthorsAsString().isEmpty() ? "<p>-   by " + m.getAuthorsAsString() + "</p>" : "") + "</td></tr>";
    }
    modsInfo += "</table>";
    modPane.setText("<style type=\"text/css\">a {color:white;text-decoration: none} p {color: gray;}</style><table>" + modsInfo);
    modPane.setCaretPosition(0);
  }

  public void updateUser(String username) {
    serverImage.update(((LoginPanel) getMainPanel().getPanel(0)).getLoginResponse().getName(), "http://avatar.forsaken.co/" + ((LoginPanel) getMainPanel().getPanel(0)).getLoginResponse().getName());
    serverImage.setBounds(actionsBox.getX() + ((actionsBox.getWidth() - 150) / 2), actionsBox.getY() + PADDING, 150, 150);
  }

  @Override public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("CONNECT") || e.getActionCommand().equals("UPDATE")) {
      if (e.getActionCommand().equals("UPDATE")) {
        forceUpdateButton.setText("Forcing update");
        launched = false;
      } else {
        launched = true;
      }
      if (activeServer.needsDownload() || e.getActionCommand().equals("UPDATE")) {
        try {
          getMainPanel().switchPage(-1);
          ((ProgressPanel) getMainPanel().getPanel(-1)).stateChanged("Checking closest download server", 0);
          activeServer.cleanup();
          activeServer.download((ProgressPanel) getMainPanel().getPanel(-1));
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      } else {
        activeServer.launch(getMainPanel());
      }

    } else if (e.getActionCommand().equals("EDIT")) {
      if (!activeServer.getMinecraftDir().exists()) {
        JOptionPane.showMessageDialog(_mainPanel, "The server needs to be launched before you can edit it", "Not downloaded", JOptionPane.WARNING_MESSAGE);
      } else {
        try {
          Desktop.getDesktop().open(activeServer.getMinecraftDir());
        } catch (IOException ex) {
          ex.printStackTrace();
        }
      }
    } else if (e.getActionCommand().equals("SETTINGS")) {
      switchPage(3);
    } else {
      for (String s : servers.keySet()) {
        if (e.getActionCommand().equalsIgnoreCase(s)) {
          setServer(servers.get(s));
        }
      }
    }
  }

}
