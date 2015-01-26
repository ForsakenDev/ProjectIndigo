package co.forsaken.projectindigo.data;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter public class Mod {
  private String       name        = "";
  private List<String> authors     = new ArrayList<String>();
  private String       downloadUrl = "";
  private String       infoUrl     = "";
  private ModType      type;

  public static enum ModType {
    mod, resourcePack, library, optionalMod, config, global, minecraft, forge, natives, resource, assets;
  }

  public Mod(String _name, List<String> _authors, String _downloadUrl, String _infoUrl, ModType _type) {
    name = _name;
    authors = _authors;
    downloadUrl = _downloadUrl;
    infoUrl = _infoUrl;
    type = _type;
  }

  public String getAuthorsAsString() {
    String s = "";
    for (String a : authors) {
      if (!s.isEmpty()) {
        s += ", ";
      }
      s += a;
    }
    return s;
  }

}
