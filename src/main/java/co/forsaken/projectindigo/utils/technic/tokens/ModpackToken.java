package co.forsaken.projectindigo.utils.technic.tokens;

public class ModpackToken {
  public String  name;
  public String  displayName;
  public String  user;
  public String  url;
  public String  platformUrl;
  public String  minecraft;
  public int     ratings;
  public int     downloads;
  public int     runs;
  public String  description;
  public String  tags;
  public boolean isServer;
  public boolean isOfficial;
  public String  version;
  public boolean forceDir;
  public Feed[]  feed;
  public Url     icon;
  public Url     logo;
  public Url     background;
  public String  solder;

  public class Feed {
    public String user;
    public long   date;
    public String content;
    public String avatar;
    public String url;
  }

  public class Url {
    public String url;
    public String md5;
  }
}
