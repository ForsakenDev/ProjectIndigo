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
import co.zmc.projectindigo.gui.components.LoginSection;

public class LoginHandler extends SwingWorker<String, Void> {

    private LoginSection        _loginSection;
    private String              _username;
    private String              _password;
    private static final Logger logger = Logger.getLogger("launcher");

    public LoginHandler(LoginSection loginSection, String username, String password) {
        _loginSection = loginSection;
        _username = username;
        _password = password;
    }

    @Override
    protected String doInBackground() {
        try {
            _loginSection.stateChanged("Logging in as " + _username + "...", 33);
            String result = doLogin();
            _loginSection.stateChanged("Reading response...", 99);
            LoginResponse response = new LoginResponse(result);
            _loginSection.stateChanged("Logged in and lauching...", 100);
            logger.log(Level.INFO, "Login successful, Starting minecraft..");
            _loginSection.getUserManager().saveUsername(_username, response.getUsername(), _password);
            _loginSection.setResponse(response);
            _loginSection.onEvent(LoginEvents.SAVE_USER_LAUNCH);
        } catch (AccountMigratedException e) {
            _loginSection.onEvent(LoginEvents.ACCOUNT_MIGRATED);
        } catch (BadLoginException e) {
            _loginSection.onEvent(LoginEvents.BAD_LOGIN);
        } catch (MinecraftUserNotPremiumException e) {
            _loginSection.onEvent(LoginEvents.USER_NOT_PREMIUM);
        } catch (PermissionDeniedException e) {
            _loginSection.onEvent(LoginEvents.PERMISSION_DENIED);
            this.cancel(true);
        } catch (MCNetworkException e) {
            _loginSection.onEvent(LoginEvents.NETWORK_DOWN);
            this.cancel(true);
        } catch (OutdatedMCLauncherException e) {
            JOptionPane.showMessageDialog(null, "Incompatible login version. Contact " + IndigoLauncher.TITLE + " about updating the launcher!");
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
            _loginSection.stateChanged("Sending username and password...", 66);
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
