package co.forsaken.projectindigo.utils.atlauncher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import co.forsaken.api.json.JsonWebCall;
import co.forsaken.projectindigo.data.Mod;
import co.forsaken.projectindigo.data.Mod.ModType;
import co.forsaken.projectindigo.data.Server;
import co.forsaken.projectindigo.log.LogManager;
import co.forsaken.projectindigo.utils.DirectoryLocations;
import co.forsaken.projectindigo.utils.FileUtils;
import co.forsaken.projectindigo.utils.ServerLoader;
import co.forsaken.projectindigo.utils.atlauncher.tokens.ModpackToken;
import co.forsaken.projectindigo.utils.atlauncher.tokens.PacksToken;
import co.forsaken.projectindigo.utils.atlauncher.tokens.VersionToken;

public class AtLauncherServerLoader extends ServerLoader {

  private static final String              API_BASE             = "http://download.nodecdn.net/containers/atl/";
  public static final String               API_BASE_PACKS       = "launcher/json/packs.json";
  private static final String              API_BASE_PACK_INFO   = "packs/%s/versions/%s/Configs.xml";
  private static final String              API_BASE_PACK_CONFIG = "packs/%s/versions/%s/Configs.zip";
  private static final String              type                 = "atlauncher";
  private static PacksToken                token;
  private static HashMap<String, Document> modpackInfo          = new HashMap<String, Document>();

  public AtLauncherServerLoader(Server _server) {
    super(_server, false);
  }

  private static Document getModpack(String name, String version) {
    String url = String.format(API_BASE + API_BASE_PACK_INFO, name.replaceAll(" ", ""), version);
    String nme = (name + "-" + version).replaceAll(".", "_").replaceAll(" ", "");
    if (!modpackInfo.containsKey(nme)) {
      File baseModTypeFile = new File(DirectoryLocations.BACKEND_DATA_DIR.format(type + "/"));
      if (!baseModTypeFile.exists()) baseModTypeFile.mkdirs();
      File modpackFile = new File(baseModTypeFile, nme);
      try {
        if (!modpackFile.exists()) modpackFile.createNewFile();
        FileUtils.writeStreamToFile(new URL(url).openStream(), modpackFile);
      } catch (IOException e) {
        e.printStackTrace();
        LogManager.warn("Failed to load modpacks, loading from backup");
      }
      InputStream modPackStream = null;
      try {
        modPackStream = new FileInputStream(modpackFile);
      } catch (IOException e) {
        LogManager.warn("Failed to read modpack file - falling back to direct download");
      }
      if (modPackStream == null) {
        try {
          modPackStream = new URL(url).openStream();
        } catch (IOException e) {
          LogManager.error("Completely unable to download the modpack file - check your connection");
        }
      }
      if (modPackStream != null) {
        try {
          modpackInfo.put(nme, FileUtils.getXML(modPackStream));
        } catch (Exception e) {
          LogManager.error("Exception reading modpack file");
          return null;
        }
        if (modpackInfo == null) {
          LogManager.error("Error: could not load modpack data!");
          return null;
        }
      }
    }
    return modpackInfo.get(nme);
  }

  private static PacksToken getModpackInfo() {
    if (token == null) {
      try {
        token = new JsonWebCall(API_BASE + API_BASE_PACKS).executeGet(PacksToken.class, true);
      } catch (Exception e) {
        LogManager.error(API_BASE + API_BASE_PACKS + " could not be retrieved... its most likely down, try again in a few seconds");
        return null;
      }
    }
    return token;
  }

  @Override public boolean load(Server server) {
    return loadPack(server);
  }

  private boolean loadPack(Server server) {
    PacksToken token = getModpackInfo();
    if (token == null) { return false; }
    boolean found = true;
    for (ModpackToken t : token.data) {
      if (!t.name.equalsIgnoreCase(server.getToken().modpackRefName)) {
        continue;
      }
      server.setDesc(t.description);
      server.setModList(new HashMap<String, Mod>());
      for (VersionToken vt : t.versions) {
        if (!vt.version.equalsIgnoreCase(server.getToken().version)) {
          continue;
        }
        Document doc = getModpack(t.name, vt.version);

        NodeList mods = doc.getElementsByTagName("mod");
        for (int i = 0; i < mods.getLength(); i++) {
          NamedNodeMap mod = mods.item(i).getAttributes();
          String name = mod.getNamedItem("name") != null ? mod.getNamedItem("name").getTextContent() : "";
          List<String> authors = mod.getNamedItem("authors") != null ? Arrays.asList(mod.getNamedItem("authors").getTextContent().split(",")) : new ArrayList<String>();
          String url = mod.getNamedItem("url") != null ? mod.getNamedItem("url").getTextContent() : "";
          String website = mod.getNamedItem("website") != null ? mod.getNamedItem("website").getTextContent() : "";
          String type = mod.getNamedItem("type") != null ? mod.getNamedItem("type").getTextContent() : "";
          ModType ty = ModType.mod;
          if (type.equalsIgnoreCase("resourcepack")) {
            ty = ModType.resourcePack;
          } else if (type.equalsIgnoreCase("forge")) {
            ty = ModType.forge;
          }
          type = mod.getNamedItem("optional") != null ? mod.getNamedItem("optional").getTextContent() : "";
          if (!type.isEmpty()) {
            if (type.equalsIgnoreCase("yes")) {
              ty = ModType.optionalMod;
            }
          }
          if (ty == ModType.optionalMod) {
            if (!server.hasOptionalMod(name)) {
              continue;
            }
          }
          Mod m = new Mod(name, authors, API_BASE + url, website, ty);
          server.getModList().put(mod.getNamedItem("name").getTextContent(), m);
        }
        for (Mod m : getMojangLibraries(server, vt.minecraft)) {
          server.getModList().put(m.getName(), m);
        }
        NodeList libraries = doc.getElementsByTagName("library");
        outer: for (int i = 0; i < libraries.getLength(); i++) {
          NamedNodeMap library = libraries.item(i).getAttributes();
          String name = library.getNamedItem("file") != null ? library.getNamedItem("file").getTextContent() : "";
          List<String> replace = new ArrayList<String>();
          for (String s : server.getModList().keySet()) {
            String sTotal = s;
            if (s.contains("-")) {
              if (s.contains("/")) {
                s = s.substring(s.lastIndexOf("/") + 1);
              }
              if (name.substring(0, name.lastIndexOf('-')).equalsIgnoreCase(s.substring(0, s.lastIndexOf('-')))) {
                if (parseVersion(s.substring(s.lastIndexOf('-') + 1, s.lastIndexOf("."))) < parseVersion(name.substring(name.lastIndexOf('-') + 1, name.lastIndexOf(".")))) {
                  replace.add(sTotal);
                  break;
                }
                continue outer;
              }
            }
          }
          for (String s : replace) {
            LogManager.info("Removing " + s + " to make way for " + name);
            server.getModList().remove(s);
          }
          List<String> authors = new ArrayList<String>();
          String url = library.getNamedItem("url") != null ? library.getNamedItem("url").getTextContent() : "";
          String website = "";
          ModType ty = ModType.library;
          Mod m = new Mod(name, authors, API_BASE + url, website, ty);
          server.getModList().put(name, m);
        }

        server.getModList().put("forge_config", new Mod("forge_config", new ArrayList<String>(), String.format(API_BASE + API_BASE_PACK_CONFIG, t.name.replaceAll(" ", ""), vt.version), "", ModType.config));
        server.getModList().put("server_download", new Mod("server_download", new ArrayList<String>(), MOJANG_DOWNLOAD_BASE + "versions/" + vt.minecraft + "/" + vt.minecraft + ".jar", "", ModType.minecraft));
        break;
      }
    }
    return found;
  }

  public String getDownloadUrl(Server server) {
    return null;
  }

  public String getDownloadUrl(Server server, String modname) {
    return API_BASE + server.getModList().get(modname).getDownloadUrl();
  }

}
