package co.forsaken.projectindigo.session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.ToString;

@ToString public class OfflineSession implements Session {

  @Getter private final List<Identity> identities;

  public OfflineSession() {
    List<Identity> identities = new ArrayList<Identity>();
    Identity identity = new Identity();
    identity.setId("0");
    identity.setName("Player");
    identities.add(identity);
    this.identities = Collections.unmodifiableList(identities);
  }

  public boolean isValid() {
    return true;
  }

  public String getAccessToken() {
    return null;
  }

  public String getClientToken() {
    return null;
  }

}