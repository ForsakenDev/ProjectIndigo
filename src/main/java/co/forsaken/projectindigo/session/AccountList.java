package co.forsaken.projectindigo.session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import lombok.NonNull;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.annotate.JsonProperty;

@SuppressWarnings({ "serial", "rawtypes" }) public class AccountList extends AbstractListModel implements ComboBoxModel {

  private List<Account>     accounts = new ArrayList<Account>();
  private transient Account selected;

  public synchronized void add(@NonNull Account account) {
    if (!accounts.contains(account)) {
      accounts.add(account);
      Collections.sort(accounts);
      fireContentsChanged(this, 0, accounts.size());
    }
  }

  public synchronized void remove(@NonNull Account account) {
    Iterator<Account> it = accounts.iterator();
    while (it.hasNext()) {
      Account other = it.next();
      if (other.equals(account)) {
        it.remove();
        fireContentsChanged(this, 0, accounts.size() + 1);
        break;
      }
    }
  }

  @JsonProperty public synchronized List<Account> getAccounts() {
    return accounts;
  }

  public synchronized void setAccounts(@NonNull List<Account> accounts) {
    this.accounts = accounts;
    Collections.sort(accounts);
  }

  @JsonIgnore public synchronized int getSize() {
    return accounts.size();
  }

  public synchronized Account getElementAt(int index) {
    try {
      return accounts.get(index);
    } catch (IndexOutOfBoundsException e) {
      return null;
    }
  }

  public void setSelectedItem(Object item) {
    if (item == null) {
      selected = null;
      return;
    }

    if (item instanceof Account) {
      this.selected = (Account) item;
    } else {
      String id = String.valueOf(item).trim();
      Account account = new Account(id);
      for (Account test : accounts) {
        if (test.equals(account)) {
          account = test;
          break;
        }
      }
      selected = account;
    }

    if (selected.getId() == null || selected.getId().isEmpty()) {
      selected = null;
    }
  }

  @JsonIgnore public Account getSelectedItem() {
    return selected;
  }

}