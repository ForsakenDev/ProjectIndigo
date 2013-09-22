package co.zmc.projectindigo.managers;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.SwingWorker;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import co.zmc.projectindigo.data.Server;
import co.zmc.projectindigo.gui.MainPanel;
import co.zmc.projectindigo.gui.ServerPanel;
import co.zmc.projectindigo.utils.DirectoryLocations;
import co.zmc.projectindigo.utils.FileUtils;

public class ServerManager extends SwingWorker<Boolean, Void> {
    private static final File _saveFile         = new File(DirectoryLocations.DATA_DIR_LOCATION, "servers");
    private String            _status;
    private int               _percentComplete;
    private List<Server>      _servers          = new ArrayList<Server>();
    private MainPanel         _mainPanel;
    private int               numToLoad         = 0;
    private int               currentParseIndex = 0;
    private JSONObject        servers           = null;

    public ServerManager(MainPanel mainPanel) {
        _mainPanel = mainPanel;
    }

    @Override
    protected Boolean doInBackground() {
        loadServers();
        return true;
    }

    public String getStatus() {
        return _status;
    }

    public int getPercentComplete() {
        return _percentComplete;
    }

    public void loadServers() {
        try {
            if (!_saveFile.exists()) {
                _saveFile.createNewFile();
            }
            servers = (JSONObject) new JSONParser().parse(new Scanner(_saveFile, "UTF-8").useDelimiter("\\A").next());

            for (Object key : servers.keySet()) {
                if (key instanceof String) {
                    String name = (String) key;
                    Server s = parseServer((JSONObject) servers.get(name));
                    _servers.add(s);
                    ((ServerPanel) _mainPanel.getPanel(1)).addServer(s);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Server parseServer(String url) throws ParseException, MalformedURLException, IOException {
        JSONObject sData = (JSONObject) new JSONParser().parse(IOUtils.toString(new URL(url)));
        return parseServer(sData);
    }

    public Server parseServer(JSONObject sData) throws NumberFormatException, ParseException, MalformedURLException, IOException {
        return new Server(_mainPanel, sData);

    }

    public Server getServer(String fullIp) {
        for (Server s : _servers) {
            if (s.getFullIp().equalsIgnoreCase(fullIp)) { return s; }
        }
        return null;
    }

    public List<Server> getServers() {
        return _servers;
    }

    public void save() {
        String str = "{\n";
        for (int i = 0; i < _servers.size(); i++) {
            Server s = _servers.get(i);
            if (i > 0) {
                str += ", \n";
            }
            str += "  \"" + s.getFullIp() + "\": " + s.toString();
        }
        str += "\n}";
        FileUtils.writeStringToFile(str, _saveFile);
    }
}
