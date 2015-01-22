package co.forsaken.projectindigo.utils.ftb.tokens;

import lombok.Getter;

public class PackToken {
  public String    minecraftArguments;
  public Library[] libraries;
  public String    mainClass;
  public String    id;

  public class Library {
    public String   name;
    public String   url;
    public String[] checksums;
    public boolean  download;
    public boolean  serverreq;
    public boolean  clientreq;
  }

}
