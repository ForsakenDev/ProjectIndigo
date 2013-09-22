package co.zmc.projectindigo.data;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import co.zmc.projectindigo.data.log.Logger;
import co.zmc.projectindigo.gui.MainPanel;
import co.zmc.projectindigo.gui.ProgressPanel;
import co.zmc.projectindigo.gui.ServerPanel;
import co.zmc.projectindigo.managers.DownloadHandler;
import co.zmc.projectindigo.utils.DirectoryLocations;
import co.zmc.projectindigo.utils.FileUtils;
import co.zmc.projectindigo.utils.Settings;
import co.zmc.projectindigo.utils.Utils;

public class Server {
    private MainPanel            _mainPanel;

    private String               _rawJSON;
    private String               _name;
    private String               _ip;
    private int                  _port;
    private String               _version;
    private String               _logo;
    private String               _mcVersion;
    private String               _modpackDownloadURL;
    private String               _modpackInfoURL;
    private String               _forgeVersion;

    private List<Mod>            _mods              = new ArrayList<Mod>();
    private List<Mod>            _modsToUpdate      = new ArrayList<Mod>();
    private List<Mod>            _modsToRemove      = new ArrayList<Mod>();
    private List<FileDownloader> _downloads         = new ArrayList<FileDownloader>();

    private boolean              canLaunch          = false;

    private int                  _totalLaunchSize   = 0;
    private int                  _currentLaunchSize = 0;

    private File                 _baseDir;
    private File                 _minecraftDir;
    private File                 _binDir;

    public Server(MainPanel section, JSONObject json) {
        _mainPanel = section;
        _rawJSON = json.toJSONString();
        loadJSON(json);
    }

    private void loadJSON(JSONObject json) {
        _name = (String) json.get("name");
        _ip = (String) json.get("ip");
        _port = Integer.parseInt((String) json.get("port"));
        _version = (String) json.get("modpack_version");
        _modpackInfoURL = (String) json.get("modpack_info_url");
        _modpackDownloadURL = (String) json.get("modpack_url");
        _logo = (String) json.get("logo_url");
        _mcVersion = (String) json.get("mc_version");
        _forgeVersion = (String) json.get("forge_version");

        updateDir();

        JSONObject coreMods = (JSONObject) json.get("coremods");
        for (Object key : coreMods.keySet()) {
            if (key instanceof String) {
                JSONObject jsonMod = (JSONObject) coreMods.get((String) key);
                _mods.add(new Mod((String) key, (String) jsonMod.get("version"), (String) jsonMod.get("authors"), (String) jsonMod.get("info_url"), (String) jsonMod.get("download_url"), true,
                        getBaseDir().getAbsolutePath()));
            }
        }

        JSONObject mods = (JSONObject) json.get("mods");
        for (Object key : mods.keySet()) {
            if (key instanceof String) {
                JSONObject jsonMod = (JSONObject) mods.get((String) key);
                _mods.add(new Mod((String) key, (String) jsonMod.get("version"), (String) jsonMod.get("authors"), (String) jsonMod.get("info_url"), (String) jsonMod.get("download_url"), false,
                        getBaseDir().getAbsolutePath()));
            }
        }

        _downloads.add(new FileDownloader(_modpackDownloadURL, getBaseDir().getAbsolutePath(), true));
        _downloads.add(new FileDownloader("http://files.minecraftforge.net/minecraftforge/minecraftforge-universal-" + getMCVersion() + "-" + getForgeVersion() + ".zip", getBaseDir()
                .getAbsolutePath() + "/instMods/"));
        _downloads.add(new FileDownloader("http://s3.amazonaws.com/MinecraftDownload/lwjgl.jar", getBaseDir().getAbsolutePath() + "/minecraft/bin/"));
        _downloads.add(new FileDownloader("http://s3.amazonaws.com/MinecraftDownload/lwjgl_util.jar", getBaseDir().getAbsolutePath() + "/minecraft/bin/"));
        _downloads.add(new FileDownloader("http://s3.amazonaws.com/MinecraftDownload/jinput.jar", getBaseDir().getAbsolutePath() + "/minecraft/bin/"));
        _downloads.add(new FileDownloader("http://assets.minecraft.net/" + getMCVersion().replace(".", "_") + "/minecraft.jar", getBaseDir().getAbsolutePath() + "/minecraft/bin/", true));
        switch (Utils.getCurrentOS()) {
            case WINDOWS:
                _downloads.add(new FileDownloader("http://s3.amazonaws.com/MinecraftDownload/windows_natives.jar", getBaseDir().getAbsolutePath() + "/minecraft/bin/natives/", true));
                break;
            case MACOSX:
                _downloads.add(new FileDownloader("http://s3.amazonaws.com/MinecraftDownload/macosx_natives.jar", getBaseDir().getAbsolutePath() + "/minecraft/bin/natives/", true));
                break;
            case UNIX:
                _downloads.add(new FileDownloader("http://s3.amazonaws.com/MinecraftDownload/linux_natives.jar", getBaseDir().getAbsolutePath() + "/minecraft/bin/natives/", true));
                break;
            default:
                break;
        }
    }

    public void loadFileSize(ProgressPanel panel) {
        int total = _mods.size() + _downloads.size();
        int totalCompletion = 0;
        for (Mod mod : _mods) {
            panel.stateChanged("Validating " + mod.getName() + "...", (int) (((double) totalCompletion / (double) total) * 100D));
            _totalLaunchSize += mod.getFileSize();
            totalCompletion++;
        }

        for (FileDownloader res : _downloads) {
            panel.stateChanged("Validating " + res.getRawDownloadURL() + "...", (int) (((double) totalCompletion / (double) total) * 100D));
            _totalLaunchSize += res.getFileSize() * (res.shouldExtract() ? 2 : 1);
            totalCompletion++;
        }
    }

    public Server getNewServer() {
        try {
            String server = IOUtils.toString(new URL(_modpackInfoURL));
            JSONObject sData = (JSONObject) new JSONParser().parse(server);
            return new Server(_mainPanel, sData);
        } catch (MalformedURLException e) {
            Logger.logError(e.getMessage(), e);
        } catch (ParseException e) {
            Logger.logError(e.getMessage(), e);
        } catch (IOException e) {
            Logger.logError(e.getMessage(), e);
        }
        return null;
    }

    public boolean shouldUpdate() {
        boolean shouldUpdate = false;
        Server server = getNewServer();
        if (server != null) {
            modLoop: for (Mod updatedMod : server.getMods()) {
                for (Mod mod : _mods) {
                    if (mod.getName().equalsIgnoreCase(updatedMod.getName()) && !mod.getVersion().equalsIgnoreCase(updatedMod.getVersion())) {
                        _modsToUpdate.add(updatedMod);
                        if (!shouldUpdate) {
                            shouldUpdate = true;
                        }
                        continue modLoop;
                    }
                }
                _modsToUpdate.add(updatedMod);
            }
            for (Mod mod : _mods) {
                if (!server.containsMod(mod.getName())) {
                    _modsToRemove.add(mod);
                    if (!shouldUpdate) {
                        shouldUpdate = true;
                    }
                    continue;
                }
            }
        }
        if (shouldUpdate) {
            _rawJSON = server._rawJSON;
        }
        return shouldUpdate;
    }

    public boolean containsMod(String modName) {
        for (Mod mod : _mods) {
            if (mod.getName().equalsIgnoreCase(modName)) { return true; }
        }
        return false;
    }

    public void updateDir() {
        _baseDir = new File(String.format(DirectoryLocations.SERVER_DIR_LOCATION, getFullIp().replaceAll(":", "_")));
        _minecraftDir = new File(String.format(DirectoryLocations.SERVER_MINECRAFT_DIR_LOCATION, getFullIp().replaceAll(":", "_")));
        _binDir = new File(String.format(DirectoryLocations.SERVER_MINECRAFT_BIN_DIR_LOCATION, getFullIp().replaceAll(":", "_")));
        if (!isDownloaded()) {
            mkdir();
        }
    }

    public void forceUpdate() {
        FileUtils.deleteDirectory(_baseDir);
        mkdir();
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
        return !isDownloaded();
    }

    public void download(LoginResponse response, Settings settings) {
        new DownloadHandler(this, _mainPanel, response, settings).execute();
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

    public String getModpackInfoURL() {
        return _modpackInfoURL;
    }

    public String getVersion() {
        return _version;
    }

    public String getMCVersion() {
        return _mcVersion;
    }

    public String getForgeVersion() {
        return _forgeVersion;
    }

    public List<Mod> getMods() {
        return _mods;
    }

    private boolean downloadResources(ProgressPanel panel) throws IOException {
        int numLoaded = 0;
        for (FileDownloader download : _downloads) {
            download.download(this, panel, false);
            numLoaded++;
        }
        return numLoaded == _downloads.size();
    }

    private boolean downloadMods(ProgressPanel panel) throws IOException {
        int numLoaded = 0;
        for (Mod mod : _mods) {
            mod.download(this, panel, true);
            numLoaded++;
        }
        return numLoaded == _downloads.size();
    }

    private int _lastDownloadProgress = 0;

    public void addDownloadSize(ProgressPanel panel, int amount) {
        _currentLaunchSize += amount;
        if (getDownloadProgress() > _lastDownloadProgress) {
            _lastDownloadProgress = getDownloadProgress();
            panel.stateChanged("Downloading mods...", _lastDownloadProgress);
        }
    }

    public int getDownloadProgress() {
        int prog = (int) ((double) ((double) _currentLaunchSize / (double) _totalLaunchSize) * 100);
        if (prog > 100) {
            prog = 100;
        } else if (prog < 0) {
            prog = 0;
        }
        return prog;
    }

    public boolean isFinishedDownloading() {
        return _mods.size() + _downloads.size() <= _numLoadedDownloads;
    }

    private int _numLoadedDownloads = 0;

    public void addLoadedDownload() {
        _numLoadedDownloads++;
    }

    private boolean updateNewMods(ProgressPanel panel) throws IOException {
        int numLoaded = 0;
        List<Integer> idsToRemove = new ArrayList<Integer>();
        for (Mod mod : _modsToUpdate) {
            for (int i = 0; i < _mods.size(); i++) {
                Mod oldMod = _mods.get(i);
                if (mod.getName().equalsIgnoreCase(oldMod.getName())) {
                    oldMod.delete();
                    if (!idsToRemove.contains(i)) {
                        idsToRemove.add(i);
                    }
                }
            }
            mod.download(this, panel, true);
            numLoaded++;
            _mods.add(mod);
        }
        for (Mod mod : _modsToRemove) {
            for (int i = 0; i < _mods.size(); i++) {
                Mod oldMod = _mods.get(i);
                if (mod.getName().equalsIgnoreCase(oldMod.getName())) {
                    oldMod.delete();
                    if (!idsToRemove.contains(i)) {
                        idsToRemove.add(i);
                    }
                }
            }
        }
        for (Integer i : idsToRemove) {
            _mods.remove(i);
        }
        return numLoaded == _modsToUpdate.size();
    }

    public boolean download(ProgressPanel panel) throws IOException {
        loadFileSize(panel);
        if (shouldUpdate()) {
            System.out.println("Performing update");
            if (!updateNewMods(panel)) { return false; }
            _modsToUpdate.clear();
            _modsToRemove.clear();
        } else {
            System.out.println("Downloading");
            if (!(downloadResources(panel) && downloadMods(panel))) { return false; }
        }
        ((ServerPanel) _mainPanel.getPanel(1)).getServerManager().save();
        return true;
    }

    @Override
    public String toString() {
        return _rawJSON;
    }
}
