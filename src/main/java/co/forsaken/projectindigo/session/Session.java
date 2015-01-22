package co.forsaken.projectindigo.session;

import java.util.List;

public interface Session {

  boolean isValid();

  List<Identity> getIdentities();

  String getClientToken();

  String getAccessToken();

}