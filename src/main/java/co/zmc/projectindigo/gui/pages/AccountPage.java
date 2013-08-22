/*
 * This file is part of ProjectIndigo.
 *
 * Copyright (c) 2013 ZephyrUnleashed LLC <http://www.zephyrunleashed.com/>
 * ProjectIndigo is licensed under the ZephyrUnleashed License Version 1.
 *
 * ProjectIndigo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the ZephyrUnleashed License Version 1.
 *
 * ProjectIndigo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the ZephyrUnleashed License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license,
 * including the MIT license.
 */
package co.zmc.projectindigo.gui.pages;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import co.zmc.projectindigo.gui.BaseFrame;
import co.zmc.projectindigo.gui.components.Avatar;
import co.zmc.projectindigo.gui.components.Image;
import co.zmc.projectindigo.gui.components.PasswordBox;
import co.zmc.projectindigo.gui.components.TextBox;
import co.zmc.projectindigo.gui.components.TransparentButton;
import co.zmc.projectindigo.gui.components.TransparentImage;

@SuppressWarnings("serial")
public class AccountPage extends BasePage {

    public AccountPage(BaseFrame baseFrame) {
        super(baseFrame, false);
    }

    @Override
    public void setIcons() {
        _icon = new Image("account_hover", "account");
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
        add(new TransparentImage("main_bg", 0.75F, getWidth(), getHeight()));
    }
}
