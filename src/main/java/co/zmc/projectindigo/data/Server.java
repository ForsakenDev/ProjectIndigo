package co.zmc.projectindigo.data;

import java.io.File;

import org.json.simple.JSONObject;

import co.zmc.projectindigo.gui.MainPanel;
import co.zmc.projectindigo.gui.ServerPanel;
import co.zmc.projectindigo.managers.DownloadHandler;
import co.zmc.projectindigo.utils.DirectoryLocations;

public class Server {
    private MainPanel _mainPanel;
    private String    _name;
    private String    _ip;
    private int       _port;
    private String    _logo;
    private String    _downloadURL;
    private String    _version;
    private String    _mcVersion;

    private File      _baseDir;
    private File      _minecraftDir;
    private File      _binDir;

    public Server(MainPanel section, JSONObject server, int port) {
        _mainPanel = section;
        _name = (String) server.get("name");
        _ip = (String) server.get("ip");
        _port = port;
        _logo = (String) server.get("logo");
        _downloadURL = (String) server.get("download_url");
        _version = (String) server.get("version");
        _mcVersion = (String) server.get("mc_version");

        _baseDir = new File(String.format(DirectoryLocations.SERVER_DIR_LOCATION, getFullIp().replaceAll(":", "_")));
        _minecraftDir = new File(String.format(DirectoryLocations.SERVER_MINECRAFT_DIR_LOCATION, getFullIp().replaceAll(":", "_")));
        _binDir = new File(String.format(DirectoryLocations.SERVER_MINECRAFT_BIN_DIR_LOCATION, getFullIp().replaceAll(":", "_")));
        if (!isDownloaded()) {
            mkdir();
        }
    }

    public void mkdir() {
        if (!_baseDir.exists()) {
            _baseDir.mkdir();
        }
    }

    public boolean isDownloaded() {
        return _binDir.exists() && _minecraftDir.exists() && _baseDir.exists();
    }

    public File getBaseDir() {
        return _baseDir;
    }

    public File getBinDir() {
        return _binDir;
    }

    public File getMinecraftDir() {
        return _minecraftDir;
    }

    public boolean shouldDownload() {
        Server shouldUpdate = ((ServerPanel) _mainPanel.getPanel(1)).getServerManager().shouldUpdate(this);
        if (shouldUpdate != null) {
            _baseDir = shouldUpdate._baseDir;
            _minecraftDir = shouldUpdate._minecraftDir;
            _binDir = shouldUpdate._binDir;
            _name = shouldUpdate._name;
            _ip = shouldUpdate._ip;
            _port = shouldUpdate._port;
            _logo = shouldUpdate._logo;
            _downloadURL = shouldUpdate._downloadURL;
            _version = shouldUpdate._version;
            _mcVersion = shouldUpdate._mcVersion;
            ((ServerPanel) _mainPanel.getPanel(1)).getServerManager().saveServers();
        }
        return !(getMinecraftDir().exists() && getBaseDir().exists() && getBinDir().exists()) || shouldUpdate != null;
    }

    public void download(LoginResponse response) {
        new DownloadHandler(this, _mainPanel, response).execute();
    }

    public String getName() {
        return _name;
    }

    public String getFullIp() {
        if (getPort() != 25565) { return getIp() + ":" + getPort(); }
        return getIp();
    }

    public String getIp() {
        return _ip;
    }

    public int getPort() {
        return _port;
    }

    public String getLogo() {
        return _logo;
    }

    public String getDownloadURL() {
        return _downloadURL;
    }

    public String getVersion() {
        return _version;
    }

    public String getMCVersion() {
        return _mcVersion;
    }

    @Override
    public String toString() {
        String data = "{";
        data += "\n    \"name\": \"" + getName() + "\",";
        data += "\n    \"ip\": \"" + getIp() + "\",";
        data += "\n    \"port\": \"" + getPort() + "\",";
        data += "\n    \"logo\": \"" + getLogo() + "\",";
        data += "\n    \"download_url\": \"" + getDownloadURL() + "\",";
        data += "\n    \"version\": \"" + getVersion() + "\",";
        data += "\n    \"mc_version\": \"" + getMCVersion() + "\",";
        data += "\n  }";
        return data;
    }

    public int getPlayers() {
        return 83;
    }

    public int getMaxPlayers() {
        return 1000;
    }
}
