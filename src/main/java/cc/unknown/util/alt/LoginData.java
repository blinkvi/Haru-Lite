package cc.unknown.util.alt;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class LoginData {
    public String mcToken;
    public String newRefreshToken;
    public String uuid, username;

	public boolean isGood() {
        return mcToken != null;
    }
}