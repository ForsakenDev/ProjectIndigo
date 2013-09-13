package co.zmc.projectindigo.data;

public class UserPassword {
    private String  _username     = null;
    private byte[]  _passwordHash = null;
    private String  _password     = null;
    private boolean _isHash;

    public UserPassword(String password, String username) {
        _isHash = false;
        _password = password;
        _username = username;
    }

    public UserPassword(byte[] hash, String username) {
        _isHash = true;
        _passwordHash = hash;
        _username = username;
    }

    public String getUsername() {
        return _username;
    }

    public void setUsername(String username) {
        _username = username;
    }

    public byte[] getPasswordHash() {
        return _passwordHash;
    }

    public void setPasswordHash(byte[] passwordHash) {
        _passwordHash = passwordHash;
    }

    public String getPassword() {
        return _password;
    }

    public void setPassword(String password) {
        _password = password;
    }

    public boolean isHash() {
        return _isHash;
    }

    public void setHash(boolean isHash) {
        _isHash = isHash;
    }
}
