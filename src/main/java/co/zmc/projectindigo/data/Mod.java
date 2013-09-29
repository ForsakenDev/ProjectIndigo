package co.zmc.projectindigo.data;

import java.util.ArrayList;
import java.util.List;

public class Mod extends FileDownloader {
    private String       _name         = "";
    private String       _version      = "";
    private List<String> _authors      = new ArrayList<String>();
    private String       _infoUrl      = "";
    private boolean      _coreMod      = false;
    private boolean      _shouldUpdate = false;

    public Mod(String name, String version, String authors, String infoUrl, String downloadUrl, boolean coreMod, String baseDir) {
        super(downloadUrl, baseDir + "/minecraft/" + (coreMod ? "coremods/" : "mods/"), false);
        _name = name;
        _version = version;
        for (String s : authors.split(",")) {
            _authors.add(s.trim());
        }
        _infoUrl = infoUrl;
        _coreMod = coreMod;
    }

    public String getName() {
        return _name;
    }

    public String getVersion() {
        return _version;
    }

    public List<String> getAuthors() {
        return _authors;
    }

    public String getAuthorsAsString() {
        String ret = "";
        for (int i = 0; i < _authors.size(); i++) {
            if (i > 0) {
                ret += ", ";
            }
            ret += _authors.get(i);
        }
        return ret;
    }

    public String getInfoUrl() {
        return _infoUrl;
    }

    public boolean isCoreMod() {
        return _coreMod;
    }

    protected String getFilename() {
        if (_rawDownloadURL == null) { return ""; }
        return (getName() + "-" + getVersion()).replaceAll(" ", "").toLowerCase().trim() + ".jar";
    }

    public void setShouldUpdate(boolean update) {
        _shouldUpdate = update;
    }

    public boolean shouldDownload() {
        return _shouldUpdate || super.shouldDownload();
    }
}
