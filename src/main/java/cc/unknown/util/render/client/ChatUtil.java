package cc.unknown.util.render.client;

import cc.unknown.util.Accessor;
import cc.unknown.util.client.network.PacketUtil;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class ChatUtil implements Accessor {
	
    public static void display(final Object message, final Object... objects) {
        if (mc.thePlayer != null) {
            final String format = String.format(message.toString(), objects);
            mc.thePlayer.addChatMessage(new ChatComponentText(format));
        }
    }
    
    public static void display(final String message) {
        if (mc.thePlayer != null) {
            mc.thePlayer.addChatMessage(new ChatComponentText(message));
        }
    }

    public static void chat(final Object message) {
        if (mc.thePlayer != null) {
            PacketUtil.send(new C01PacketChatMessage(message.toString()));
        }
    }
    
    public static String getPrefix() {
    	return "[" + ColorUtil.pink + "H" + ColorUtil.white + "] ";
    }
    
    public static String getPrefix(EnumChatFormatting color, String name, EnumChatFormatting color2) {
    	return "[" + color + name + color2 + "] ";
    }
}