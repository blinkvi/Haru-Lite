package cc.unknown.mixin.mixins;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.IChatComponent;

@Mixin(ChatLine.class)
public class MixinChatLine {
	
	@Unique
    private NetworkPlayerInfo playerInfo;
    
    @Unique
    private HashSet<WeakReference<ChatLine>> chatLines = new HashSet<>();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(int i, IChatComponent iChatComponent, int j, CallbackInfo ci) {
    	
        chatLines.add(new WeakReference<>((ChatLine) (Object) this));
        NetHandlerPlayClient netHandler = Minecraft.getMinecraft().getNetHandler();
        if (netHandler == null) return;
        Map<String, NetworkPlayerInfo> nicknameCache = new HashMap<>();
        
        try {
            for (String word : iChatComponent.getFormattedText().split("(§.)|\\W")) {
            	
                if (word.isEmpty()) {
                	continue;
                }
                
                playerInfo = netHandler.getPlayerInfo(word);
                
                if (playerInfo == null) {
                    playerInfo = getPlayerFromNickname(word, netHandler, nicknameCache);
                }
                
                if (playerInfo != null) {
                	break;
                }
            }
        } catch (Exception ignored) {
        }
    }

    @Unique
    private NetworkPlayerInfo getPlayerFromNickname(String word, NetHandlerPlayClient connection, Map<String, NetworkPlayerInfo> nicknameCache) {
        if (nicknameCache.isEmpty()) {
            for (NetworkPlayerInfo p : connection.getPlayerInfoMap()) {
            	
                IChatComponent displayName = p.getDisplayName();
                
                if (displayName != null) {
                    String nickname = displayName.getUnformattedTextForChat();
                    
                    if (word.equals(nickname)) {
                        nicknameCache.clear();
                        return p;
                    }
                    
                    nicknameCache.put(nickname, p);
                }
            }
        } else {
            return nicknameCache.get(word);
        }

        return null;
    }
}