package cc.unknown.util.alt;

public class LoginData {
    public String mcToken;
    public String newRefreshToken;
    public String uuid, username;
    
    public LoginData() {
	}

	public LoginData(String mcToken, String newRefreshToken, String uuid, String username) {
		this.mcToken = mcToken;
		this.newRefreshToken = newRefreshToken;
		this.uuid = uuid;
		this.username = username;
	}

	public boolean isGood() {
        return mcToken != null;
    }
}