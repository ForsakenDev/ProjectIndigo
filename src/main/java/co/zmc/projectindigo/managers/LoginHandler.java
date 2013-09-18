package co.zmc.projectindigo.managers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import co.zmc.projectindigo.IndigoLauncher;
import co.zmc.projectindigo.data.LoginEvents;
import co.zmc.projectindigo.data.LoginResponse;
import co.zmc.projectindigo.data.log.Logger;
import co.zmc.projectindigo.exceptions.AccountMigratedException;
import co.zmc.projectindigo.exceptions.BadLoginException;
import co.zmc.projectindigo.exceptions.MCNetworkException;
import co.zmc.projectindigo.exceptions.MinecraftUserNotPremiumException;
import co.zmc.projectindigo.exceptions.OutdatedMCLauncherException;
import co.zmc.projectindigo.exceptions.PermissionDeniedException;
import co.zmc.projectindigo.gui.LoginPanel;
import co.zmc.projectindigo.gui.MainPanel;
import co.zmc.projectindigo.gui.ProgressPanel;

public class LoginHandler extends SwingWorker<String, Void> {

    private MainPanel _mainPanel;
    private String    _username;
    private String    _password;

    public LoginHandler(MainPanel mainPanel, String username, String password) {
        _mainPanel = mainPanel;
        _username = username;
        _password = password;
    }

    @Override
    protected String doInBackground() {
        _mainPanel.switchPage(-1);
        try {
            ((ProgressPanel) _mainPanel.getPanel(-1)).stateChanged("Logging in as " + _username + "...", 33);
            String result = doLogin();
            ((ProgressPanel) _mainPanel.getPanel(-1)).stateChanged("Reading response...", 99);
            LoginResponse response = new LoginResponse(result);
            ((ProgressPanel) _mainPanel.getPanel(-1)).stateChanged("Logged in...", 100);
            Logger.logInfo("Login successful, Starting minecraft..");
            ((LoginPanel) _mainPanel.getPanel(0)).getUserManager().saveUsername(_username, response.getUsername(), _password);
            ((LoginPanel) _mainPanel.getPanel(0)).setResponse(response);
            ((LoginPanel) _mainPanel.getPanel(0)).onEvent(LoginEvents.SAVE_USER_LAUNCH);
        } catch (AccountMigratedException e) {
            ((LoginPanel) _mainPanel.getPanel(0)).onEvent(LoginEvents.ACCOUNT_MIGRATED);
        } catch (BadLoginException e) {
            ((LoginPanel) _mainPanel.getPanel(0)).onEvent(LoginEvents.BAD_LOGIN);
        } catch (MinecraftUserNotPremiumException e) {
            ((LoginPanel) _mainPanel.getPanel(0)).onEvent(LoginEvents.USER_NOT_PREMIUM);
        } catch (PermissionDeniedException e) {
            ((LoginPanel) _mainPanel.getPanel(0)).onEvent(LoginEvents.PERMISSION_DENIED);
            this.cancel(true);
        } catch (MCNetworkException e) {
            ((LoginPanel) _mainPanel.getPanel(0)).onEvent(LoginEvents.NETWORK_DOWN);
            this.cancel(true);
        } catch (OutdatedMCLauncherException e) {
            JOptionPane.showMessageDialog(null, "Incompatible login version. Contact " + IndigoLauncher.TITLE + " about updating the launcher!");
        } catch (UnsupportedEncodingException e) {
            Logger.logError(e.getMessage(), e);
            this.cancel(true);
        } catch (Exception e) {
            Logger.logError(e.getMessage(), e);
        }
        return "";
    }

    public String doLogin() throws BadLoginException, MinecraftUserNotPremiumException, MCNetworkException, OutdatedMCLauncherException,
            UnsupportedEncodingException, IOException {
        String result = null;
        try {
            result = getString(new URL("https://login.minecraft.net/?user=" + URLEncoder.encode(_username, "UTF-8") + "&password="
                    + URLEncoder.encode(_password, "UTF-8") + "&version=13"));
            ((ProgressPanel) _mainPanel.getPanel(-1)).stateChanged("Sending username and password...", 66);
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
                Logger.logError("Unknown login result: " + result);
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
