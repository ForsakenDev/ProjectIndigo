package co.zmc.projectindigo.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JOptionPane;

import co.zmc.projectindigo.IndigoLauncher;
import co.zmc.projectindigo.data.LoginEvents;
import co.zmc.projectindigo.data.LoginResponse;
import co.zmc.projectindigo.gui.components.Box;
import co.zmc.projectindigo.gui.components.Button;
import co.zmc.projectindigo.gui.components.CheckBox;
import co.zmc.projectindigo.gui.components.Image;
import co.zmc.projectindigo.gui.components.Label;
import co.zmc.projectindigo.gui.components.PasswordBox;
import co.zmc.projectindigo.gui.components.TextBox;
import co.zmc.projectindigo.managers.LoginHandler;
import co.zmc.projectindigo.managers.UserManager;

@SuppressWarnings("serial")
public class LoginPanel extends BasePanel {

    private LoginResponse _loginResponse;
    private UserManager   _userManager;

    private Box           _loginBg;
    private Image         _header;
    private Label         _usernameLbl;
    private TextBox       _username;
    private Label         _passwordLbl;
    private PasswordBox   _password;
    private CheckBox      _rememberMe;
    private Button        _loginBtn;
    private String        _activeUser;

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

            public void keyReleased(KeyEvent e) {
            }

            public void keyTyped(KeyEvent e) {
            }
        };
        _password.addKeyListener(keyListener);
        _username.addKeyListener(keyListener);

        add(_loginBg);
        add(_header);
    }

    public String getUsername() {
        return _loginResponse.getUsername();
    }

    public final void setResponse(LoginResponse loginResponse) {
        _loginResponse = loginResponse;
    }

    public LoginResponse getLoginResponse() {
        return _loginResponse;
    }

    public final void onEvent(LoginEvents event) {
        switch (event) {
            default:
            case LAUNCH:
                _userManager.writeUsernameList();
                switchPage(1);
                return;
            case BAD_LOGIN:
                JOptionPane.showMessageDialog(_mainPanel, "Invalid username/password combination." + "\n\n" + IndigoLauncher.TITLE
                        + " will not work without a premium Minecraft account.", "Bad Login Information!", JOptionPane.WARNING_MESSAGE);
                break;
            case ACCOUNT_MIGRATED:
                JOptionPane.showMessageDialog(_mainPanel, "Please use your email address instead of your username.", "Account Migrated!",
                        JOptionPane.WARNING_MESSAGE);
                _userManager.removeAccount(_activeUser);
                break;
            case USER_NOT_PREMIUM:
                JOptionPane.showConfirmDialog(_mainPanel, IndigoLauncher.TITLE + " will not work without a premium Minecraft account!",
                        "Premium Minecraft Account Required", JOptionPane.DEFAULT_OPTION);
                break;
            case NETWORK_DOWN:
                JOptionPane.showMessageDialog(_mainPanel, "Minecraft.net seems to be down.... Unable to authenticate");
                break;
            case PERMISSION_DENIED:
                JOptionPane.showMessageDialog(_mainPanel, "Ensure " + IndigoLauncher.TITLE + " is whitelisted with any antivirus applications.",
                        "Permission Denied!", JOptionPane.WARNING_MESSAGE);
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
