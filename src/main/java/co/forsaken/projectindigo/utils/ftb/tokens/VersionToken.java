package co.forsaken.projectindigo.utils.ftb.tokens;

public class VersionToken {
  public String id;
  public String time;
  public String releaseTime;
  public String type;
  public String minecraftArguments;
  public int    minimumLauncherVersion;
  public String assets;
  public Lib[]  libraries;

  public class Lib {
    public String  name;
    public Natives natives;
    public Extract extract;
    public Rules[] rules;

  }

  public class Natives {
    public String linux;
    public String windows;
    public String osx;
  }

  public class Extract {
    public String[] exclude;
  }

  public class Rules {
    public String action;
    public OS     os;
  }

  public class OS {
    public String name;
  }
}
