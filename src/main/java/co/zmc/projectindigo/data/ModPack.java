package co.zmc.projectindigo.data;

public class ModPack {

    private String _packHost;
    private String _packName;
    private String _packVersion;

    public ModPack(String packHost, String packName, String packVersion) {
        _packHost = packHost;
        _packName = packName;
        _packVersion = packVersion;
    }

    public String getPackHost() {
        return _packHost;
    }

    public String getPackName() {
        return _packName;
    }

    public String getPackVersion() {
        return _packVersion;
    }
}
