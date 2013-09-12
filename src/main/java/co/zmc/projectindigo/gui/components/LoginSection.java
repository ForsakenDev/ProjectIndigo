package co.zmc.projectindigo.gui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import co.zmc.projectindigo.IndigoLauncher;
import co.zmc.projectindigo.data.LoginEvents;
import co.zmc.projectindigo.data.LoginResponse;
import co.zmc.projectindigo.gui.MainPanel;
import co.zmc.projectindigo.managers.LoginHandler;
import co.zmc.projectindigo.managers.UserManager;

public class LoginSection implements ActionListener {
    private UserManager _userManager;

    private MainPanel   _mainPanel;
    private PasswordBox _password;
    private TextBox     _username;
    private Button      _loginBtn;
    private JCheckBox   _remember;
    private ProgressBar _progressBar;
    private RoundedBox  _progressBox;
    private RoundedBox  _loginBox;
    private String      _activeUser = "";
    private Dimension   baseSize    = new Dimension((MainPanel.BTN_SIZE.width * 2) + (MainPanel.PADDING * 3), (MainPanel.BTN_SIZE.height * 2)
                                            + (MainPanel.PADDING * 3));

    public LoginSection(MainPanel mainPanel) {
        _mainPanel = mainPanel;
        setupLook();
        int x = mainPanel.getWidth() - baseSize.width - MainPanel.PADDING;
        int y = mainPanel.getHeight() - baseSize.height - MainPanel.PADDING;
        _userManager = new UserManager(this);
        setBounds(x, y, baseSize.width, baseSize.height);
        setFormsEnabled(true);
    }

    public String getActiveUser() {
        return _activeUser;
    }

    public void setBounds(int x, int y, int width, int height) {
        _loginBox.setBounds(x, y, width, height);
        _username.setBounds(_loginBox.getX() + MainPanel.PADDING, _loginBox.getY() + MainPanel.PADDING, MainPanel.BTN_SIZE.width,
                MainPanel.BTN_SIZE.height);
        _loginBtn.setBounds(_loginBox.getX() + MainPanel.PADDING, _loginBox.getY() + MainPanel.BTN_SIZE.height + (MainPanel.PADDING * 2),
                MainPanel.BTN_SIZE.width, MainPanel.BTN_SIZE.height);
        _password.setBounds(_loginBox.getX() + MainPanel.BTN_SIZE.width + (MainPanel.PADDING * 2), _loginBox.getY() + MainPanel.PADDING,
                MainPanel.BTN_SIZE.width, MainPanel.BTN_SIZE.height);
        _remember.setBounds(_loginBox.getX() + MainPanel.BTN_SIZE.width + (MainPanel.PADDING * 2), _loginBox.getY() + MainPanel.BTN_SIZE.height
                + (MainPanel.PADDING * 2), MainPanel.BTN_SIZE.width, MainPanel.BTN_SIZE.height);
        _progressBox.setBounds(x, y - (MainPanel.BTN_SIZE.height + (MainPanel.PADDING * 3)),
                (MainPanel.BTN_SIZE.width * 2) + (MainPanel.PADDING * 3), MainPanel.BTN_SIZE.height + (MainPanel.PADDING * 2));
        _progressBar.setBounds(_progressBox.getX() + MainPanel.PADDING, _progressBox.getY() + MainPanel.PADDING, (MainPanel.BTN_SIZE.width * 2)
                + MainPanel.PADDING, MainPanel.BTN_SIZE.height);
    }

    public void setupLook() {

        _loginBox = new RoundedBox(MainPanel.BORDER_COLOUR);
        _mainPanel.add(_loginBox);
        _username = new TextBox(_mainPanel, "Username...");
        _password = new PasswordBox(_mainPanel, "Password...");
        _loginBtn = new Button(_mainPanel, "Launch");

        _remember = new JCheckBox("Remember");
        _remember.setFont(IndigoLauncher.getMinecraftFont(12));
        _remember.setOpaque(false);
        _remember.setBorderPainted(false);
        _remember.setFocusPainted(false);
        _remember.setContentAreaFilled(false);
        _remember.setBorder(null);
        _remember.setForeground(Color.WHITE);
        _remember.setHorizontalTextPosition(SwingConstants.RIGHT);
        _remember.setIconTextGap(10);
        _mainPanel.add(_remember, 0);

        _progressBox = new RoundedBox(MainPanel.BORDER_COLOUR);

        _mainPanel.add(_progressBox);
        _progressBar = new ProgressBar(0.75F);
        _progressBar.setFont(IndigoLauncher.getMinecraftFont(10));
        _mainPanel.add(_progressBar, 0);

        _loginBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (_mainPanel.getServerSection().getActiveServer() != null) {
                    tryLogin(_username.getText(), new String(_password.getPassword()), _remember.isSelected());
                } else {
                    JOptionPane
                            .showMessageDialog(_mainPanel, "You need to select a server to log in", "Invalid Server!", JOptionPane.WARNING_MESSAGE);
                }
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

    }

    public void setFormsEnabled(boolean state) {
        _username.setEnabled(state);
        _password.setEnabled(state);
        _loginBtn.setEnabled(state);
        _remember.setEnabled(state);
        if (state) {
            _progressBar.updateProgress(0);
            _password.setText("");
            _username.requestFocusInWindow();
        }
        _progressBar.setEnabled(!state);
        _progressBox.setEnabled(!state);
    }

    public final void onEvent(LoginEvents event) {
        switch (event) {
            default:
            case SAVE_USER_LAUNCH:
                _userManager.writeUsernameList();
                try {
                    IndigoLauncher._launcher.launchMinecraft(_mainPanel.getServerSection().getActiveServer(), _mainPanel.getLoginResponse());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                setFormsEnabled(true);
                break;
            case BAD_LOGIN:
                JOptionPane.showMessageDialog(_mainPanel, "Invalid username/password combination." + "\n\n" + IndigoLauncher.TITLE
                        + " will not work without a premium Minecraft account.", "Bad Login Information!", JOptionPane.WARNING_MESSAGE);
                setFormsEnabled(true);
                break;
            case ACCOUNT_MIGRATED:
                JOptionPane.showMessageDialog(_mainPanel, "Please use your email address instead of your username.", "Account Migrated!",
                        JOptionPane.WARNING_MESSAGE);
                _userManager.removeAccount(_activeUser);
                setFormsEnabled(true);
                break;
            case USER_NOT_PREMIUM:
                JOptionPane.showConfirmDialog(_mainPanel, IndigoLauncher.TITLE + " will not work without a premium Minecraft account!",
                        "Premium Minecraft Account Required", JOptionPane.DEFAULT_OPTION);
                setFormsEnabled(true);
                break;
            case NETWORK_DOWN:
                JOptionPane.showMessageDialog(_mainPanel, "Minecraft.net seems to be down.... Unable to authenticate");
                break;
            case PERMISSION_DENIED:
                JOptionPane.showMessageDialog(_mainPanel, "Ensure " + IndigoLauncher.TITLE + " is whitelisted with any antivirus applications.",
                        "Permission Denied!", JOptionPane.WARNING_MESSAGE);
                setFormsEnabled(true);
                break;
        }
    }

    public final void tryLogin(String user) {
        if (!_userManager.hasSavedPassword(user)) {
            onEvent(LoginEvents.BAD_LOGIN);
            return;
        }
        tryLogin(user, _userManager.getSavedPassword(user), false);
        _username.setText(user);
        _password.setText(_userManager.getSavedPassword(user));
    }

    public final void tryLogin(final String user, final String pass, final boolean saveUser) {
        if (pass == null || pass.isEmpty()) {
            onEvent(LoginEvents.BAD_LOGIN);
            return;
        }
        setFormsEnabled(false);
        _activeUser = user;
        LoginHandler loginHandler = new LoginHandler(this, user, pass);
        loginHandler.execute();
    }

    public final void setResponse(LoginResponse response) {
        _mainPanel.setResponse(response);
    }

    public UserManager getUserManager() {
        return _userManager;
    }

    public void stateChanged(final String status, final float progress) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (!_progressBar.isVisible()) {
                    _progressBar.setVisible(true);
                    _progressBox.setVisible(true);

                }
                int intProgress = Math.round(progress);
                _progressBar.setValue(intProgress);
                String text = status;
                if (text.length() > 60) {
                    text = text.substring(0, 60) + "...";
                }
                _progressBar.setString(intProgress + "% " + text);
            }
        });
    }

    public void actionPerformed(ActionEvent event) {

    }
}
