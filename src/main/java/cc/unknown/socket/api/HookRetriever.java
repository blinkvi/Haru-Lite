package cc.unknown.socket.api;

import java.util.concurrent.TimeUnit;

import cc.unknown.socket.security.AESUtil;

public interface HookRetriever {
	// h o s t
	String socket = "";
	
	// i r c
	String ircChannel = AESUtil.decrypt("7bVBDDRXww475JPvFb4Die8hsjZd82bNLqSAtDA3shI=") /*"1356750457720143946"*/;
	String endpoint = AESUtil.decrypt("FiKn32mYiOlZesWGjcNf6ot9CoqjfomfVWvS5XGhZeNYklv6OyAu1ApgFV6wpteEPBwbKzRqeRqzQeSfmMUdgV1uj7H4oXl50KK4VSdrJ0yzJkWUO9zTiA2xmKwqg2cGb+454vSBymiZNoc5pmT/IXczwwAKTVZRXDzGG6VQtFA=") /*"https://discord.com/api/webhooks/1361004883997626549/hFUVazfOpo8UWGGjMvBVRGoj1c-rBg91-ok0bIxs1ScBV_pXvItRy_NpLbsTnbBDXLxk"*/;
	String token = AESUtil.decrypt("Beoj8czvsfFuR8S1OXgg3YiOSmUhz3aR4FUM+tfXkQ7nUvBbek60Z+CPRvWxb5e/O5VL/PAuFo2AG3TSqVJXtDpyNvx8xAJLU+ixEWW3UkI=") /*"MTMwNTkzODQ4MDgwMjgyODM1MA.G8aPzS.LAo8IOiqU7s_g1ZMz4P-N2iwrmrxDvgchdy8LM"*/;
	
	// c o s m e t i c s
	String cosmeticChannel = AESUtil.decrypt("3VxYKe1G6iZxj1Xm3Q+cEpkC1ROCXXSGixMM7nRsO3Q=") /*"1360731034278428976"*/;
    long cooldown = TimeUnit.SECONDS.toMillis(5);
    
	// S e c r e t    K e y
	String secretKey = "5QHSwNhiWc750w41L6h1uWXRB39P67";
	String secretKey2 = "";

}