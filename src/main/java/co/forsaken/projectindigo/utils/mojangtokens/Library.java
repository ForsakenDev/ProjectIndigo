package co.forsaken.projectindigo.utils.mojangtokens;

import java.io.File;
import java.util.List;
import java.util.Map;

import co.forsaken.projectindigo.data.Server;
import co.forsaken.projectindigo.utils.Utils;

public class Library {
  private static final String          LIBRARIES_BASE = "https://libraries.minecraft.net/";

  private String                       name;
  private Map<OperatingSystem, String> natives;
  private List<Rule>                   rules;
  private ExtractRule                  extract;
  private String                       url;

  public boolean shouldInstall() {
    if (this.rules == null) { return true; // No rules setup so we need it
    }
    Action lastAction = Action.DISALLOW;
    for (Rule rule : this.rules) { // Loop through all the rules
      if (rule.ruleApplies()) { // See if this rule applies to this system
        lastAction = rule.getAction();
      }
    }
    return (lastAction == Action.ALLOW); // Check if we are allowing it
  }

  public boolean shouldExtract() {
    return this.extract != null;
  }

  public ExtractRule getExtractRule() {
    return this.extract;
  }

  public String getName() {
    return this.name;
  }

  public String getURL() {
    String path;
    String[] parts = this.name.split(":", 3);
    path = parts[0].replace(".", "/") + "/" + parts[1] + "/" + parts[2] + "/" + parts[1] + "-" + parts[2] + getClassifier() + ".jar";
    return LIBRARIES_BASE + path;
  }

  public File getFile(Server server) {
    String[] parts = this.name.split(":", 3);
    return new File(server.getLibraryDir(), parts[1] + "-" + parts[2] + getClassifier() + ".jar");
  }

  public String getClassifier() {
    if (this.natives == null) { return ""; }
    return "-" + this.natives.get(OperatingSystem.getOS()).replace("${arch}", Utils.getArch());
  }
}
