package co.zmc.data;

public class LoginResponse {

    private boolean _validResponse = false;
    private String  _version;
    private String  _downloadTicket;
    private String  _username;
    private String  _sessionId;

    public LoginResponse(String response) {
        String[] responseValues = response.split(":");
        if (responseValues.length < 4) {
            throw new NullPointerException("Invalid login response from Minecraft");
        } else {
            _validResponse = true;
            _version = responseValues[0];
            _downloadTicket = responseValues[1];
            _username = responseValues[2];
            _sessionId = responseValues[3];
        }
    }

    public boolean isValidResponse() {
        return _validResponse;
    }

    public String getVersion() {
        return _version;
    }

    public String getDownloadTicket() {
        return _downloadTicket;
    }

    public String getUsername() {
        return _username;
    }

    public String getSessionId() {
        return _sessionId;
    }

}
