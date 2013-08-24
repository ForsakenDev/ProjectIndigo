package co.zmc.projectindigo.data;

import org.json.simple.JSONObject;

public class Server {

    private final JSONObject _server;
    private String           _name;
    private String           _ip;
    private int              _port;
    private String           _logo;
    private String           _version;
    private String           _mcVersion;
    private ModPack          _modPack;

    public Server(JSONObject server) {
        _server = server;
        _name = (String) server.get("name");
        _ip = (String) server.get("ip");
        _port = Integer.parseInt(server.get("port").toString());
        _logo = (String) server.get("logo");
        _version = (String) server.get("version");
        _mcVersion = (String) server.get("mc_version");
        _modPack = new ModPack((String) server.get("pack_host"), (String) server.get("pack_name"), (String) server.get("pack_version"));
    }

    public String getName() {
        return _name;
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

    public String getVersion() {
        return _version;
    }

    public String getMcVersion() {
        return _mcVersion;
    }

    public ModPack getModPack() {
        return _modPack;
    }

    @Override
    public String toString() {
        return _server.toJSONString();
    }
}
