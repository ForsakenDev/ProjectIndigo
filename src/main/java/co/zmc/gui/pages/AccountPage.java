package co.zmc.gui.pages;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import co.zmc.gui.BaseFrame;
import co.zmc.gui.components.Avatar;
import co.zmc.gui.components.Image;
import co.zmc.gui.components.PasswordBox;
import co.zmc.gui.components.TextBox;
import co.zmc.gui.components.TransparentButton;
import co.zmc.gui.components.TransparentImage;

@SuppressWarnings("serial")
public class AccountPage extends BasePage {

    public AccountPage(BaseFrame baseFrame) {
        super(baseFrame, false);
    }

    @Override
    public void setIcons() {
        _icon = new Image("/icons/account_hover.png", "/icons/account.png");
    }

    @Override
    public void addComponents(final BaseFrame baseFrame) {
        int spaceBetween = 5;
        int ySpace = 18;
        final TextBox username = new TextBox(this, "Username...");
        username.setBounds((getWidth() / 2) - 180 - (spaceBetween / 2), getHeight() - (24 * 2) - ySpace, 180, 24);

        final PasswordBox password = new PasswordBox(this, "Password...");
        password.setBounds((getWidth() / 2) + (spaceBetween / 2), getHeight() - (24 * 2) - ySpace, 180, 24);

        final TransparentButton loginBtn = new TransparentButton("Login", 0.8F);
        add(loginBtn, 0);
        loginBtn.setBounds((getWidth() / 2) - 180 - (spaceBetween / 2), getHeight() - (24 * 1) - ySpace + spaceBetween, 180, 24);

        final TransparentButton rememberMe = new TransparentButton("Remember Me", 0.8F, true);
        add(rememberMe, 0);
        rememberMe.setBounds((getWidth() / 2) + (spaceBetween / 2), getHeight() - (24 * 1) - ySpace + spaceBetween, 180, 24);

        loginBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (username.getText().isEmpty() || new String(password.getPassword()).isEmpty()) { throw new NullPointerException(
                        "You did not enter in a " + (username.getText().isEmpty() ? "username" : "password")); }
                baseFrame.doLogin(username.getText(), new String(password.getPassword()), rememberMe.isClicked());
            }
        });
        KeyListener keyListener = new KeyListener() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loginBtn.doClick();
                }
            }

            public void keyReleased(KeyEvent e) {
            }

            public void keyTyped(KeyEvent e) {
            }
        };
        password.addKeyListener(keyListener);
        username.addKeyListener(keyListener);

        JPanel userList = new JPanel();
        userList.setOpaque(false);
        userList.setLayout(new GridBagLayout());
        for (String user : _baseFrame.getUserManager().getSavedUsernames()) {
            final String name = _baseFrame.getUserManager().getAccountName(user);
            Avatar avatar = new Avatar(this, name, user, 100);
            userList.add(avatar);
            avatar.addMouseListener(new MouseListener() {

                public void mouseClicked(MouseEvent e) {
                    Avatar a = (Avatar) e.getComponent();
                    username.setText(a.getAccountKey());
                    password.setText(_baseFrame.getUserManager().getSavedPassword(a.getAccountKey()));
                    loginBtn.doClick();
                }

                public void mouseEntered(MouseEvent e) {
                }

                public void mouseExited(MouseEvent e) {
                }

                public void mousePressed(MouseEvent e) {
                }

                public void mouseReleased(MouseEvent e) {
                }
            });
        }
        Dimension dim = new Dimension(getWidth(), getHeight() - 78);
        userList.setSize(dim);
        userList.setPreferredSize(dim);
        userList.setBounds(0, 0, dim.width, dim.height);
        add(userList, 0);
    }

    @Override
    public void setupBackgroundImage() {
        add(new TransparentImage("main_bg.jpg", 0.75F, getWidth(), getHeight()));
    }
}
