package co.zmc.projectindigo.data;

import java.awt.image.BufferedImage;

import javax.swing.JLayeredPane;

import org.json.simple.JSONObject;

import co.zmc.projectindigo.gui.components.ServerIcon;

public class Server {
    private int                _id;
    private String             _name;
    private String             _ip;
    private int                _port;
    private String             _version;
    private int                _playerCount    = 83;
    private int                _maxPlayerCount = 1000;
    private int                _ping           = 20;
    private String             _modpack;
    private String             _icon;
    private ModpackInformation _modpackInfo;
    private BufferedImage      _image          = null;

    public Server(JSONObject server) {
        _id = Integer.parseInt(server.get("id").toString());
        _name = (String) server.get("name");
        _ip = (String) server.get("ip");
        _port = Integer.parseInt(server.get("port").toString());
        _version = (String) server.get("version");
        _modpack = (String) server.get("modpack");
        _icon = (String) server.get("icon");
        loadModpackInformation();
        getIcon(200);
    }

    public int getId() {
        return _id;
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

    public String getVersion() {
        return _version;
    }

    public int getPlayerCount() {
        return _playerCount;
    }

    public int getMaxPlayerCount() {
        return _maxPlayerCount;
    }

    public int getPing() {
        return _ping;
    }

    public String getFullIp() {
        return getIp() + ":" + getPort();
    }

    public void loadModpackInformation() {
    }

    public ServerIcon getLogo(JLayeredPane pane, int width, int height) {
        return new ServerIcon(pane, getId(), getName(), width, height);
    }

    public BufferedImage getIcon(int size) {
        return new BufferedImage(size, size, 2);
        // if (_image == null) {
        // try {
        // URLConnection conn = new URL(_icon).openConnection();
        // conn.setDoInput(true);
        // conn.setDoOutput(false);
        // System.setProperty("http.agent",
        // "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.162 Safari/535.19");
        // conn.setRequestProperty("User-Agent",
        // "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.162 Safari/535.19");
        // HttpURLConnection.setFollowRedirects(true);
        // conn.setUseCaches(false);
        // ((HttpURLConnection) conn).setInstanceFollowRedirects(true);
        // int response = ((HttpURLConnection) conn).getResponseCode();
        // if (response == 200) {
        // _image = ImageIO.read(conn.getInputStream());
        // if ((_image.getWidth() != size) || (_image.getHeight() != size)) {
        // BufferedImage resized = new BufferedImage(size, size,
        // _image.getType());
        // Graphics2D g = resized.createGraphics();
        // g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
        // RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        // g.drawImage(_image, 0, 0, size, size, 0, 0, _image.getWidth(),
        // _image.getHeight(), null);
        // g.dispose();
        // _image = resized;
        // }
        // }
        // } catch (MalformedURLException e) {
        // e.printStackTrace();
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
        // }
        // return _image;
    }

    @Override
    public String toString() {
        return getFullIp();
    }

}
