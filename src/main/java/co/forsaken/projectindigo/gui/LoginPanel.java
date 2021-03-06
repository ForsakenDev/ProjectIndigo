package co.forsaken.projectindigo.gui;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;

import co.forsaken.projectindigo.IndigoLauncher;
import co.forsaken.projectindigo.data.LoginEvents;
import co.forsaken.projectindigo.gui.components.Box;
import co.forsaken.projectindigo.gui.components.Button;
import co.forsaken.projectindigo.gui.components.CheckBox;
import co.forsaken.projectindigo.gui.components.Image;
import co.forsaken.projectindigo.gui.components.Label;
import co.forsaken.projectindigo.gui.components.PasswordBox;
import co.forsaken.projectindigo.gui.components.TextBox;
import co.forsaken.projectindigo.managers.LoginHandler;
import co.forsaken.projectindigo.managers.UserManager;
import co.forsaken.projectindigo.session.Identity;

@SuppressWarnings("serial") public class LoginPanel extends BasePanel {

  private Identity    _loginResponse;
  private UserManager _userManager;

  private Box         _loginBg;
  private Image       _header;
  private Label       _usernameLbl;
  private TextBox     _username;
  private Label       _passwordLbl;
  private PasswordBox _password;
  private CheckBox    _rememberMe;
  private Button      _loginBtn;
  private String      _activeUser;

  public LoginPanel(MainPanel mainPanel) {
    super(mainPanel, 0);
  }

  public void loadUserManager() {
    _userManager = new UserManager(_mainPanel);
    if (getUserManager().getSavedAccounts().size() > 0) {
      _username.setText(getUserManager().getSavedAccounts().get(0));
      _password.setText(getUserManager().getSavedPassword(getUserManager().getSavedAccounts().get(0)));
      _rememberMe.setSelected(true);
    }
  }

  public void initComponents() {
    _loginBg = new Box(MainPanel.BORDER_COLOUR);
    _loginBg.setBounds((getWidth() - 264) / 2, 157, 264, 254);

    _header = new Image("header");
    _header.setBounds((getWidth() - _header.getWidth()) / 2, 20, _header.getWidth(), _header.getHeight());

    _username = new TextBox(this, "");
    _username.setBounds((getWidth() - 210) / 2, _loginBg.getY() + 50, 210, 30);
    _usernameLbl = new Label(this, "Username:");
    _usernameLbl.setBounds((getWidth() - _username.getWidth()) / 2, _username.getY() - 32, 210, 30);

    _password = new PasswordBox(this, "");
    _password.setBounds((getWidth() - 210) / 2, _username.getY() + 70, 210, 30);
    _passwordLbl = new Label(this, "Password:");
    _passwordLbl.setBounds((getWidth() - _password.getWidth()) / 2, _password.getY() - 32, 210, 30);

    _rememberMe = new CheckBox(this, "Remember Me");
    _rememberMe.setBounds(_password.getX() + 45, _password.getY() + 40, 210, 30);
    _loginBtn = new Button(this, "Login");
    _loginBtn.setBounds((getWidth() - 100) / 2, _rememberMe.getY() + 40, 100, 25);
    _loginBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        tryLogin(_username.getText(), new String(_password.getPassword()), _rememberMe.isSelected());
      }
    });

    KeyListener keyListener = new KeyListener() {
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
          _loginBtn.doClick();
        }
      }

      public void keyReleased(KeyEvent e) {}

      public void keyTyped(KeyEvent e) {}
    };
    _password.addKeyListener(keyListener);
    _username.addKeyListener(keyListener);

    add(_loginBg);
//    add(_header);
  }

  public String getUsername() {
    return _loginResponse.getId();
  }

  public final void setResponse(Identity result) {
    _loginResponse = result;
  }

  public Identity getLoginResponse() {
    return _loginResponse;
  }

  public final void onEvent(LoginEvents event, String... msg) {
    switch (event) {
      default:
      case LAUNCH:
        _userManager.writeUsernameList();
        switchPage(1);
        ((ServerBasePanel) getMainPanel().getPanel(1)).updateUser(getLoginResponse().getName());
        return;
      case BAD_LOGIN:
        JOptionPane.showMessageDialog(_mainPanel, "Invalid username/password combination." + "\n\n" + IndigoLauncher.TITLE + " will not work without a premium Minecraft account.", "Bad Login Information!", JOptionPane.WARNING_MESSAGE);
        break;
      case AUTH_ERROR:
        JOptionPane.showMessageDialog(_mainPanel, msg);
        break;
      case NETWORK_DOWN:
        JOptionPane.showMessageDialog(_mainPanel, "Minecraft sessions seem to be down.... Unable to authenticate, check http://help.mojang.com/ for information");
        if (Desktop.isDesktopSupported()) {
          try {
            Desktop.getDesktop().browse(new URI("https://help.mojang.com/"));
          } catch (URISyntaxException e) {
            e.printStackTrace();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
        break;
      case PERMISSION_DENIED:
        JOptionPane.showMessageDialog(_mainPanel, "Ensure " + IndigoLauncher.TITLE + " is whitelisted with any antivirus applications.", "Permission Denied!", JOptionPane.WARNING_MESSAGE);
        break;
    }
    switchPage(0);
  }

  public final void tryLogin(String user) {
    if (!_userManager.hasSavedPassword(user)) {
      onEvent(LoginEvents.BAD_LOGIN);
      return;
    }
    _username.setText(user);
    _password.setText(_userManager.getSavedPassword(user));
    tryLogin(user, _userManager.getSavedPassword(user), false);
  }

  public final void tryLogin(final String user, final String pass, final boolean saveUser) {
    if (pass == null || pass.isEmpty()) {
      onEvent(LoginEvents.BAD_LOGIN);
      return;
    }
    _activeUser = user;
    LoginHandler loginHandler = new LoginHandler(_mainPanel, user, pass, saveUser);
    loginHandler.execute();
  }

  public UserManager getUserManager() {
    return _userManager;
  }

}
