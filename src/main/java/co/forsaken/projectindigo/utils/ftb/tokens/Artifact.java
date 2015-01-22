package co.forsaken.projectindigo.utils.ftb.tokens;

import lombok.Getter;

public class Artifact {
  @Getter private String domain;
  @Getter private String name;
  @Getter private String version;
  @Getter private String classifier;
  @Getter private String ext = "jar";

  public Artifact(String rep) {
    String[] pts = rep.split(":");
    int idx = pts[pts.length - 1].indexOf('@');
    if (idx != -1) {
      ext = pts[pts.length - 1].substring(idx + 1);
      pts[pts.length - 1] = pts[pts.length - 1].substring(0, idx);
    }
    domain = pts[0];
    name = pts[1];
    version = pts[2];
    if (pts.length > 3) {
      classifier = pts[3];
    }
  }

  public String getPath() {
    return getPath(classifier);
  }

  public String getPath(String classifier) {
    String ret = String.format("%s/%s/%s/", domain.replace('.', '/'), name, version);
    return ret;
  }

  public String getDownloadUrl() {
    return getDownloadUrl(classifier);
  }

  public String getDownloadUrl(String classifier) {
    String ret = String.format("%s/%s/%s/%s-%s", domain.replace('.', '/'), name, version, name, version);
    if (classifier != null) {
      ret += "-" + classifier;
    }
    return ret + "." + ext;
  }
}