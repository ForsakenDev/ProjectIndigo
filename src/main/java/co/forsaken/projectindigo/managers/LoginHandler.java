package co.forsaken.projectindigo.managers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

import javax.swing.SwingWorker;

import co.forsaken.projectindigo.data.LoginEvents;
import co.forsaken.projectindigo.data.log.Logger;
import co.forsaken.projectindigo.exceptions.AuthenticationException;
import co.forsaken.projectindigo.gui.LoginPanel;
import co.forsaken.projectindigo.gui.MainPanel;
import co.forsaken.projectindigo.gui.ProgressPanel;
import co.forsaken.projectindigo.log.LogManager;
import co.forsaken.projectindigo.session.Account;
import co.forsaken.projectindigo.session.Identity;
import co.forsaken.projectindigo.session.YggdrasilSession;

public class LoginHandler extends SwingWorker<String, Void> {

  private MainPanel _mainPanel;
  private String    _username;
  private String    _password;
  private boolean   _savePassword;
  private Account   account;

  public LoginHandler(MainPanel mainPanel, String username, String password, boolean savePassword) {
    _mainPanel = mainPanel;
    _username = username;
    _password = password;
    _savePassword = savePassword;
    account = new Account(username);
    account.setPassword(password);
  }

  @Override protected String doInBackground() {
    _mainPanel.switchPage(-1);
    try {
      ((ProgressPanel) _mainPanel.getPanel(-1)).stateChanged("Logging in as " + _username, 33);
      Identity result = doLogin();
      ((ProgressPanel) _mainPanel.getPanel(-1)).stateChanged("Reading response", 99);
      ((ProgressPanel) _mainPanel.getPanel(-1)).stateChanged("Logged in", 100);
      LogManager.info("Login successful, Starting minecraft");
      ((LoginPanel) _mainPanel.getPanel(0)).getUserManager().clear();
      if (_savePassword) {
        ((LoginPanel) _mainPanel.getPanel(0)).getUserManager().saveUsername(_username, result.getId(), _password);
      }
      ((LoginPanel) _mainPanel.getPanel(0)).getUserManager().writeUsernameList();
      ((LoginPanel) _mainPanel.getPanel(0)).setResponse(result);
      ((LoginPanel) _mainPanel.getPanel(0)).onEvent(LoginEvents.LAUNCH);
    } catch (AuthenticationException e) {
      ((LoginPanel) _mainPanel.getPanel(0)).onEvent(LoginEvents.AUTH_ERROR, e.getLocalizedMessage());
    } catch (UnsupportedEncodingException e) {
      Logger.logError(e.getMessage(), e);
      this.cancel(true);
    } catch (Exception e) {
      Logger.logError(e.getMessage(), e);
    }
    return "";
  }

  public Identity doLogin() throws AuthenticationException, InterruptedException, IOException {
    YggdrasilSession session = new YggdrasilSession(_username);
    session.setPassword(_password);
    session.verify();
    List<Identity> identities = session.getIdentities();
    if (identities.size() > 0) {
      account.setIdentities(identities);
      return identities.get(0);
    } else {
      throw new AuthenticationException("Account doesn't own Minecraft", "You do not have a premium account");
    }
  }

  private String getString(URL url) throws IOException {
    Scanner scanner = new Scanner(url.openStream()).useDelimiter("\\A");
    return scanner.hasNext() ? scanner.next() : "";
  }
}
