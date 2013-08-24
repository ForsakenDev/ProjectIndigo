package co.zmc.projectindigo.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import co.zmc.projectindigo.IndigoLauncher;
import co.zmc.projectindigo.data.LoginEvents;
import co.zmc.projectindigo.data.LoginResponse;
import co.zmc.projectindigo.gui.components.Avatar;
import co.zmc.projectindigo.gui.components.Image;
import co.zmc.projectindigo.gui.components.PasswordBox;
import co.zmc.projectindigo.gui.components.ProgressBar;
import co.zmc.projectindigo.gui.components.TextBox;
import co.zmc.projectindigo.gui.components.TransparentButton;
import co.zmc.projectindigo.managers.LoginHandler;
import co.zmc.projectindigo.managers.UserManager;
import co.zmc.projectindigo.utils.Utils;

@SuppressWarnings("serial")
public class LoginPanel extends JLayeredPane {
    protected UserManager       _userManager;
    protected List<Avatar>      _avatars = new ArrayList<Avatar>();
    protected TextBox           _username;
    protected PasswordBox       _password;
    protected TransparentButton _loginBtn;
    protected TransparentButton _rememberMe;
    protected IndigoLauncher    _launcher;
    protected ProgressBar       _progressBar;
    protected String            _activeUser;

    public LoginPanel(IndigoLauncher launcher, int width, int height) {
        _launcher = launcher;
        _userManager = new UserManager(_launcher._splash);
        setLayout(null);
        setOpaque(false);
        setFont(IndigoLauncher.getMinecraftFont(14));
        Dimension dim = new Dimension(width, height);
        setSize(dim);
        setPreferredSize(dim);
        setBounds(0, 0, width, height);
        setupLook();
    }

    public void setupLook() {
        int padding = 5;
        int btnWidth = 180;
        int btnHeight = 24;
        int totalHeight = (btnHeight * 2) + (padding * 2);

        int x = (getWidth() / 2) - (padding / 2) - btnWidth;
        int y = (getHeight() / 2) - (totalHeight / 2) - (btnHeight / 2);
        if (_userManager.getSavedUsernames().size() > 0) {
            y = (getHeight() - totalHeight) - btnHeight;
        }

        _username = new TextBox(this, "Username...");
        _password = new PasswordBox(this, "Password...");
        _loginBtn = new TransparentButton(this, "Login", 0.8F);
        _rememberMe = new TransparentButton(this, "Remember Me", 0.8F, true);

        _username.setBounds(x, y, btnWidth, btnHeight);
        _loginBtn.setBounds(x, y + btnHeight + padding, btnWidth, btnHeight);
        _password.setBounds(x + btnWidth + padding, y, btnWidth, btnHeight);
        _rememberMe.setBounds(x + btnWidth + padding, y + btnHeight + padding, btnWidth, btnHeight);
        if (_userManager.getSavedUsernames().size() > 0) {
            int[][] table = Utils.getDynamicTableCoords(getWidth(), getHeight() - (getHeight() - y), _userManager.getSavedUsernames().size());
            for (int i = 0; i < _userManager.getSavedUsernames().size(); i++) {
                Avatar avatar = new Avatar(this, _userManager.getUsername(_userManager.getSavedUsernames().get(i)), _userManager.getSavedUsernames()
                        .get(i));
                avatar.setBounds(table[i][0], table[i][1], table[i][2], table[i][3]);
                _avatars.add(avatar);
            }
        }
        _loginBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tryLogin(_username.getText(), new String(_password.getPassword()), _rememberMe.isClicked());
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
        _progressBar = new ProgressBar(0.7F);
        btnHeight += 5;
        _progressBar.setBounds(getWidth() / 2 - (btnWidth + padding / 2), y - (btnHeight + padding), (btnWidth) * 2 + padding, btnHeight);
        _progressBar.setVisible(false);
        _progressBar.setFont(IndigoLauncher.getMinecraftFont(14));
        add(_progressBar, 0);

        add(new Image("bg", getWidth(), getHeight()));
        _launcher.refresh();
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
        _launcher.setResponse(response);
    }

    public UserManager getUserManager() {
        return _userManager;
    }

    public void stateChanged(final String status, final float progress) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                if (!_progressBar.isVisible()) {
                    _progressBar.setVisible(true);
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

    public void setFormsEnabled(boolean state) {
        _username.setEnabled(state);
        _password.setEnabled(state);
        _loginBtn.setEnabled(state);
        _rememberMe.setEnabled(state);
        for (Avatar a : _avatars) {
            a.setEnabled(state);
        }
        if (state) {
            _progressBar.setVisible(false);
            _password.setText("");
            _username.requestFocusInWindow();
        }
    }

    public final void onEvent(LoginEvents event) {
        switch (event) {
            case SAVE_USER_LAUNCH:
                _userManager.writeUsernameList();
                _launcher.launchServerFrame();
                setVisible(false);
                // dispose();
                break;
            case BAD_LOGIN:
                JOptionPane.showMessageDialog(getParent(), "Invalid username/password combination." + "\n\n" + IndigoLauncher.TITLE
                        + " will not work without a premium Minecraft account.", "Bad Login Information!", JOptionPane.WARNING_MESSAGE);
                setFormsEnabled(true);
                break;
            case ACCOUNT_MIGRATED:
                JOptionPane.showMessageDialog(getParent(), "Please use your email address instead of your username.", "Account Migrated!",
                        JOptionPane.WARNING_MESSAGE);
                _userManager.removeAccount(_activeUser);
                setFormsEnabled(true);
                break;
            case USER_NOT_PREMIUM:
                JOptionPane.showConfirmDialog(getParent(), IndigoLauncher.TITLE + " will not work without a premium Minecraft account!",
                        "Premium Minecraft Account Required", JOptionPane.DEFAULT_OPTION);
                setFormsEnabled(true);
                break;
            case NETWORK_DOWN:
                JOptionPane.showMessageDialog(getParent(), "Minecraft.net seems to be down.... Unable to authenticate");
                break;
            case PERMISSION_DENIED:
                JOptionPane.showMessageDialog(getParent(), "Ensure " + IndigoLauncher.TITLE + " is whitelisted with any antivirus applications.",
                        "Permission Denied!", JOptionPane.WARNING_MESSAGE);
                setFormsEnabled(true);
                break;
        }
    }
}
