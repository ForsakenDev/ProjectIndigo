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
 * License.
 */
package co.zmc.projectindigo.managers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import co.zmc.projectindigo.IndigoLauncher;
import co.zmc.projectindigo.data.LoginEvents;
import co.zmc.projectindigo.data.LoginResponse;
import co.zmc.projectindigo.exceptions.AccountMigratedException;
import co.zmc.projectindigo.exceptions.BadLoginException;
import co.zmc.projectindigo.exceptions.MCNetworkException;
import co.zmc.projectindigo.exceptions.MinecraftUserNotPremiumException;
import co.zmc.projectindigo.exceptions.OutdatedMCLauncherException;
import co.zmc.projectindigo.exceptions.PermissionDeniedException;
import co.zmc.projectindigo.gui.LoginPanel;

public class LoginHandler extends SwingWorker<String, Void> {

    private LoginPanel          _loginFrame;
    private String              _username;
    private String              _password;
    private static final Logger logger = Logger.getLogger("launcher");

    public LoginHandler(LoginPanel loginFrame, String username, String password) {
        _loginFrame = loginFrame;
        _username = username;
        _password = password;
    }

    @Override
    protected String doInBackground() {
        try {
            _loginFrame.stateChanged("Logging in as " + _username + "...", 33);
            String result = doLogin();
            _loginFrame.stateChanged("Reading response...", 99);
            LoginResponse response = new LoginResponse(result);
            _loginFrame.stateChanged("Logged in and lauching...", 100);
            logger.log(Level.INFO, "Login successful, Starting minecraft..");
            _loginFrame.getUserManager().saveUsername(_username, response.getUsername(), _password);
            _loginFrame.setResponse(response);
            _loginFrame.onEvent(LoginEvents.SAVE_USER_LAUNCH);
        } catch (AccountMigratedException e) {
            _loginFrame.onEvent(LoginEvents.ACCOUNT_MIGRATED);
        } catch (BadLoginException e) {
            _loginFrame.onEvent(LoginEvents.BAD_LOGIN);
        } catch (MinecraftUserNotPremiumException e) {
            _loginFrame.onEvent(LoginEvents.USER_NOT_PREMIUM);
        } catch (PermissionDeniedException e) {
            _loginFrame.onEvent(LoginEvents.PERMISSION_DENIED);
            this.cancel(true);
        } catch (MCNetworkException e) {
            _loginFrame.onEvent(LoginEvents.NETWORK_DOWN);
            this.cancel(true);
        } catch (OutdatedMCLauncherException e) {
            JOptionPane.showMessageDialog(_loginFrame.getParent(), "Incompatible login version. Contact " + IndigoLauncher.TITLE
                    + " about updating the launcher!");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            this.cancel(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String doLogin() throws BadLoginException, MinecraftUserNotPremiumException, MCNetworkException, OutdatedMCLauncherException,
            UnsupportedEncodingException, IOException {
        String result = null;
        try {
            result = getString(new URL("https://login.minecraft.net/?user=" + URLEncoder.encode(_username, "UTF-8") + "&password="
                    + URLEncoder.encode(_password, "UTF-8") + "&version=13"));
            _loginFrame.stateChanged("Sending username and password...", 66);
        } catch (MalformedURLException e) {
        } catch (IOException e) {
            throw new MCNetworkException();
        }
        if (result == null) { throw new MCNetworkException(); }
        if (!result.contains(":")) {
            if (result.toLowerCase().contains("bad login")) {
                throw new BadLoginException();
            } else if (result.toLowerCase().contains("not premium")) {
                throw new MinecraftUserNotPremiumException();
            } else if (result.toLowerCase().contains("old version")) {
                throw new OutdatedMCLauncherException();
            } else if (result.toLowerCase().contains("migrated")) {
                throw new AccountMigratedException();
            } else {
                System.err.print("Unknown login result: " + result);
            }
            throw new MCNetworkException();
        }
        return result;
    }

    private String getString(URL url) throws IOException {
        Scanner scanner = new Scanner(url.openStream()).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }
}
