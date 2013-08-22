package co.zmc.managers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import co.zmc.data.LoginResponse;
import co.zmc.gui.BaseFrame;

public class LoginHandler extends SwingWorker<String, Void> {

    private BaseFrame           _baseFrame;
    private String              _username;
    private String              _password;
    private boolean             _saveUser;
    private static final Logger logger = Logger.getLogger("launcher");

    public LoginHandler(BaseFrame baseFrame, String username, String password, boolean saveUser) {
        _baseFrame = baseFrame;
        _username = username;
        _password = password;
        _saveUser = saveUser;
    }

    @Override
    protected String doInBackground() {
        try {
            return getString(new URL("https://login.minecraft.net/?user=" + URLEncoder.encode(_username, "UTF-8") + "&password="
                    + URLEncoder.encode(_password, "UTF-8") + "&version=13"));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(_baseFrame, "Minecraft sessions appear to be down? Cannot connect to minecraft.net");
            logger.log(Level.SEVERE, "Minecraft sessions appear to be down? Cannot connect to minecraft.net");
        }
        return "";
    }

    @Override
    public void done() {
        String responseStr = "";
        try {
            responseStr = get();
            LoginResponse response = new LoginResponse(responseStr);
            if (_saveUser) {
                _baseFrame.getUserManager().saveUsername(_username, response.getUsername(), _password);
                _baseFrame.getUserManager().writeUsernameList();
            }
            logger.log(Level.INFO, "Login successful, Starting minecraft..");
            _baseFrame.launchGame(response);
        } catch (NullPointerException e) {
            if (responseStr.contains(":")) {
                JOptionPane.showMessageDialog(_baseFrame, "Invalid response from server");
                logger.log(Level.SEVERE, "Invalid response from server");
            } else {
                if (responseStr.equalsIgnoreCase("bad login")) {
                    JOptionPane.showMessageDialog(_baseFrame, "Invalid username or password");
                    logger.log(Level.SEVERE, "Invalid username or password");
                } else if (responseStr.equalsIgnoreCase("old version")) {
                    JOptionPane.showMessageDialog(_baseFrame, "Outdated Launcher");
                    logger.log(Level.SEVERE, "Outdated Launcher");
                } else {
                    JOptionPane.showMessageDialog(_baseFrame, "Login Failed: " + responseStr);
                    logger.log(Level.SEVERE, "Login Failed: " + responseStr);
                }
            }
        } catch (InterruptedException err) {
            logger.log(Level.SEVERE, err.getMessage());
        } catch (ExecutionException err) {
            if (err.getCause() instanceof IOException || err.getCause() instanceof MalformedURLException) {
                JOptionPane.showMessageDialog(_baseFrame, "Minecraft sessions appear to be down? Cannot connect to minecraft.net");
                logger.log(Level.SEVERE, "Minecraft sessions appear to be down? Cannot connect to minecraft.net");
                logger.log(Level.SEVERE, err.getMessage());
            }
        }

    }

    private String getString(URL url) throws IOException {
        Scanner scanner = new Scanner(url.openStream()).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }
}
