package co.forsaken.projectindigo.utils.technic;

import java.util.ArrayList;
import java.util.HashMap;

import co.forsaken.api.json.JsonWebCall;
import co.forsaken.projectindigo.data.Mod;
import co.forsaken.projectindigo.data.Server;
import co.forsaken.projectindigo.data.Mod.ModType;
import co.forsaken.projectindigo.utils.ServerLoader;
import co.forsaken.projectindigo.utils.technic.tokens.ModpackToken;

public class TechnicServerLoader extends ServerLoader {

  private ModpackToken token;

  public TechnicServerLoader(Server _server) {
    super(_server, true);
  }

  private ModpackToken getModpackInfo(String url) {
    if (token == null) {
      token = new JsonWebCall(url).executeGet(ModpackToken.class, false);
    }
    return token;
  }

  @Override public void load(Server server) {
    loadPack(server);
  }

  private boolean loadPack(Server server) {
    ModpackToken token = getModpackInfo(server.getToken().modpackRefName);
    boolean found = false;
    server.setDesc(token.description);
    server.setUrl(token.platformUrl);
    server.setModList(new HashMap<String, Mod>());
    for (Mod m : getMojangLibraries(server, token.minecraft)) {
      server.getModList().put(m.getName(), m);
    }
    server.getModList().put("server_download", new Mod("server_download", new ArrayList<String>(), MOJANG_DOWNLOAD_BASE + "versions/" + token.minecraft + "/" + token.minecraft + ".jar", "", ModType.minecraft));
    return found;
  }

  public String getDownloadUrl(Server server) {
    return token.url;
  }

  public String getDownloadUrl(Server server, String modname) {
    return null;
  }

}
