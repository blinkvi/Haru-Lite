package cc.unknown.socket.api;

import java.util.concurrent.TimeUnit;

import cc.unknown.socket.security.AESUtil;

public interface HookRetriever {	
	// i r c
	String ircChannel = AESUtil.decrypt("7bVBDDRXww475JPvFb4Die8hsjZd82bNLqSAtDA3shI=") /*"1356750457720143946"*/;
	String endpoint = AESUtil.decrypt("FiKn32mYiOlZesWGjcNf6ot9CoqjfomfVWvS5XGhZeNYklv6OyAu1ApgFV6wpteEPBwbKzRqeRqzQeSfmMUdgV1uj7H4oXl50KK4VSdrJ0yzJkWUO9zTiA2xmKwqg2cGb+454vSBymiZNoc5pmT/IXczwwAKTVZRXDzGG6VQtFA=") /*"https://discord.com/api/webhooks/1361004883997626549/hFUVazfOpo8UWGGjMvBVRGoj1c-rBg91-ok0bIxs1ScBV_pXvItRy_NpLbsTnbBDXLxk"*/;
	String token = AESUtil.decrypt("5VNjWHP/5VfYteFV5QYiI0V71qkp94qTR5dw2OQi0EN/Tx2egxKLAX3iuP5G5eio2/qYjXtDl5qfyKl+Q2/DiZw7sGnTPyWa0hl42gu4ZJs=") /*"MTM2Mjg1NjczMzE1MTg1NDg1NA.GVDlHZ.dSnz1c94wEFWak-SEzW0WlgqyMOFeWagkNKNVw"*/;
	
	// c o s m e t i c s
	String cosmeticChannel = AESUtil.decrypt("3VxYKe1G6iZxj1Xm3Q+cEpkC1ROCXXSGixMM7nRsO3Q=") /*"1360731034278428976"*/;
    long cooldown = TimeUnit.SECONDS.toMillis(5);
    
	// S e c r e t    K e y
	String secretKey = "5QHSwNhiWc750w41L6h1uWXRB39P67";
}