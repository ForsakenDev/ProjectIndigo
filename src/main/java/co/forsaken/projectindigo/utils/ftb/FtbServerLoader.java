package co.forsaken.projectindigo.utils.ftb;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import co.forsaken.projectindigo.data.Mod;
import co.forsaken.projectindigo.data.Server;
import co.forsaken.projectindigo.data.Mod.ModType;
import co.forsaken.projectindigo.data.log.Logger;
import co.forsaken.projectindigo.log.LogManager;
import co.forsaken.projectindigo.utils.DirectoryLocations;
import co.forsaken.projectindigo.utils.FileUtils;
import co.forsaken.projectindigo.utils.ServerLoader;
import co.forsaken.projectindigo.utils.ftb.tokens.Artifact;
import co.forsaken.projectindigo.utils.ftb.tokens.VersionToken;
import co.forsaken.projectindigo.utils.ftb.tokens.VersionToken.Lib;

import com.google.gson.Gson;

public class FtbServerLoader extends ServerLoader {

  private static final String              FTB_BaseInfoLoc     = "http://ftb.cursecdn.com/FTB2/static/";
  private static final String              FTB_BaseDownloadLoc = "http://ftb.cursecdn.com/FTB2/modpacks/";

  private static HashMap<String, Document> ftbModpackInfo      = new HashMap<String, Document>();

  public FtbServerLoader(Server _server) {
    super(_server, true);
  }

  private static Document getFtbModpackInfo(String secondaryInfo) {
    String url = FTB_BaseInfoLoc + secondaryInfo;
    if (!ftbModpackInfo.containsKey(secondaryInfo)) {
      File modpackFile = new File(DirectoryLocations.DATA_DIR_LOCATION, "ftb" + File.separator + secondaryInfo);
      try {
        if (!modpackFile.exists()) modpackFile.createNewFile();
        FileUtils.writeStreamToFile(new URL(url).openStream(), modpackFile);
      } catch (IOException e) {
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
          Logger.logError("Completely unable to download the modpack file - check your connection");
        }
      }
      if (modPackStream != null) {
        try {
          ftbModpackInfo.put(secondaryInfo, FileUtils.getXML(modPackStream));
        } catch (Exception e) {
          LogManager.error("Exception reading modpack file");
          return null;
        }
        if (ftbModpackInfo == null) {
          LogManager.error("Error: could not load modpack data!");
          return null;
        }
      }
    }
    return ftbModpackInfo.get(secondaryInfo);
  }

  @Override public void load(Server server) {
    if (!loadPack("modpacks.xml", server)) {
      loadPack("thirdparty.xml", server);
    }
  }

  private boolean loadPack(String type, Server server) {
    Document doc = getFtbModpackInfo(type);
    NodeList modPacks = doc.getElementsByTagName("modpack");
    boolean found = false;
    for (int i = 0; i < modPacks.getLength(); i++) {
      Node modPackNode = modPacks.item(i);
      NamedNodeMap modPackAttr = modPackNode.getAttributes();
      try {
        if (!modPackAttr.getNamedItem("dir").getTextContent().equalsIgnoreCase(server.getToken().modpackRefName)) {
          continue;
        }
        server.setUrl(modPackAttr.getNamedItem("url").getTextContent());
        server.setDesc(modPackAttr.getNamedItem("description") == null ? null : modPackAttr.getNamedItem("description").getTextContent().replace("\\n", "\n"));
        server.setModList(new HashMap<String, Mod>());
        if (modPackAttr.getNamedItem("mods") != null) {
          for (String line : modPackAttr.getNamedItem("mods").getTextContent().split(";")) {
            Matcher urlMatch = REGEX_URL.matcher(line);
            Matcher modMatch = REGEX_MODNAME.matcher(line);
            String urlM = "";
            String modName = "";
            List<String> authors = new ArrayList<String>();
            if (urlMatch.find()) {
              urlM = urlMatch.group(1);
            }
            if (modMatch.find()) {
              modName = modMatch.group(1).trim();
              authors = Arrays.asList(modMatch.replaceAll("").trim().replaceAll("by ", "").trim().split(","));
            }
            server.getModList().put(modName, new Mod(modName, authors, "", urlM, ModType.mod));
          }
          server.getModList().put("server_download",
              new Mod("server_download", new ArrayList<String>(), MOJANG_DOWNLOAD_BASE + "versions/" + modPackAttr.getNamedItem("mcVersion").getTextContent() + "/" + modPackAttr.getNamedItem("mcVersion").getTextContent() + ".jar", "", ModType.minecraft));
          for (Mod m : getMojangLibraries(server, modPackAttr.getNamedItem("mcVersion").getTextContent())) {
            server.getModList().put(m.getName(), m);
          }
          String url = String.format("http://ftb.cursecdn.com/FTB2/static/mcjsons/versions/%s/%s.json", modPackAttr.getNamedItem("mcVersion").getTextContent(), modPackAttr.getNamedItem("mcVersion").getTextContent());
          VersionToken t = new Gson().fromJson(IOUtils.toString(new URL(url).openStream(), "UTF-8"), VersionToken.class);
          Artifact a;
          outer: for (Lib l : t.libraries) {
            a = new Artifact(l.name);
            String name = a.getDownloadUrl();
            List<String> replace = new ArrayList<String>();
            for (String s : server.getModList().keySet()) {
              if (s.contains("-")) {
                if (name.substring(0, name.lastIndexOf('-')).equalsIgnoreCase(s.substring(0, s.lastIndexOf('-')))) {
                  if (parseVersion(s.substring(s.lastIndexOf('-') + 1, s.lastIndexOf("."))) < parseVersion(name.substring(name.lastIndexOf('-') + 1, name.lastIndexOf(".")))) {
                    replace.add(name);
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
            server.getModList().put(l.name, new Mod(l.name, new ArrayList<String>(), "https://libraries.minecraft.net/" + a.getDownloadUrl(), "", ModType.library));
          }
          if (!found) found = true;
        } else {
          LogManager.info("Failed to get mods list");
        }
      } catch (Exception e) {
        LogManager.error("Error while updating modpack info");
      }
    }
    return found;
  }

  public String getDownloadUrl(Server server) {
    return FTB_BaseDownloadLoc + server.getToken().modpackRefName + "/" + server.getToken().version.replace(".", "_") + "/" + server.getUrl();
  }

  public String getDownloadUrl(Server server, String mod) {
    return null;
  }

}
