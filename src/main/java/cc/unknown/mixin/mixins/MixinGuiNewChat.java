package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.IChatComponent;

@Mixin(GuiNewChat.class)
public abstract class MixinGuiNewChat extends Gui {

	@Shadow
	@Final
	private Minecraft mc;

	@Shadow
	public abstract int getLineCount();

	@Shadow
	private boolean isScrolled;

	@Shadow
	public abstract float getChatScale();

	@Shadow
	public abstract void printChatMessageWithOptionalDeletion(IChatComponent chatComponent, int chatLineId);

	@Unique
	private String lastMessage = "";
	@Unique
	private int sameMessageAmount, line;


	@Overwrite
	public void printChatMessage(IChatComponent component) {
		if (component.getUnformattedText().equals(lastMessage)) {
			mc.ingameGUI.getChatGUI().deleteChatLine(line);
			sameMessageAmount++;
			lastMessage = component.getUnformattedText();
			component.appendText(ChatFormatting.WHITE + " [x" + sameMessageAmount + "]");
		} else {
			sameMessageAmount = 1;
			lastMessage = component.getUnformattedText();
		}

		line++;

		if (line > 256) {
			line = 0;
		}

		printChatMessageWithOptionalDeletion(component, line);
	}
}