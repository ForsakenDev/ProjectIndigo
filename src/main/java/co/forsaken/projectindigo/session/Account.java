package co.forsaken.projectindigo.session;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class Account implements Comparable<Account> {

  @Getter @Setter private String         id;
  @Getter private String                 password;
  @Getter @Setter private Date           lastUsed;
  @Getter @Setter private List<Identity> identities;

  public Account() {}

  public Account(String id) {
    setId(id);
  }

  public void setPassword(String password) {
    if (password != null && password.isEmpty()) {
      password = null;
    }
    this.password = password;
  }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Account account = (Account) o;

    if (!id.equalsIgnoreCase(account.id)) return false;

    return true;
  }

  public int hashCode() {
    return id.toLowerCase().hashCode();
  }

  public int compareTo(@NonNull Account o) {
    Date otherDate = o.getLastUsed();

    if (otherDate == null && lastUsed == null) {
      return 0;
    } else if (otherDate == null) {
      return -1;
    } else if (lastUsed == null) {
      return 1;
    } else {
      return -lastUsed.compareTo(otherDate);
    }
  }

  @Override public String toString() {
    return getId();
  }

}