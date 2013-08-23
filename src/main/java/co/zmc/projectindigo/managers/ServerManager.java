package co.zmc.projectindigo.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import co.zmc.projectindigo.data.Server;
import co.zmc.projectindigo.utils.Utils;

public class ServerManager {
    private static final String         SERVER_URL = "http://www.zephyrunleashed.com/data/serverList";
    private static JSONObject           baseObject;
    private static Map<Integer, Server> servers    = new HashMap<Integer, Server>();

    public ServerManager() {
        try {
            baseObject = (JSONObject) new JSONParser().parse(Utils.readURL(SERVER_URL));
            for (Object i : baseObject.keySet()) {
                Server server = new Server((JSONObject) baseObject.get(i));
                servers.put(server.getId(), server);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Server> getServers() {
        return new ArrayList<Server>(servers.values());
    }

    public Server getServer(String name) {
        for (Server s : servers.values()) {
            if (s.getName().equalsIgnoreCase(name)) { return s; }
        }
        return null;
    }
}
