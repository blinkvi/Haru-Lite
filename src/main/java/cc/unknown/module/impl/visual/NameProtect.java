package cc.unknown.module.impl.visual;

import cc.unknown.event.render.RenderTextEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.render.client.ColorUtil;
import cc.unknown.util.render.enums.RankType;
import cc.unknown.value.impl.ModeValue;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "NameProtect", description = "Hide ur minecraft name", category = Category.VISUAL)
public class NameProtect extends Module {

	private final ModeValue mode = new ModeValue("Mode", this, "Universocraft", "Normal", "Universocraft");
	private final ModeValue ranks = new ModeValue("Rank", this, () -> mode.is("Universocraft"), RankType.JUP, RankType.values());

	public String name = "";

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onRenderText(RenderTextEvent event) {
		if (!isInGame()) return;

		String text = event.string;
		String ownName = mc.getSession().getUsername();
		String displayName = name.isEmpty() ? "You" : name;

		if (text.startsWith("/") || text.startsWith(".")) {
			return;
		}

		if (text.contains(ownName)) {
			text = text.replaceAll(ColorUtil.usu, "");			
			if (mode.is("Universocraft")) {
			    RankType selectedRank = ranks.getMode(RankType.class);
			    String rank = selectedRank.getName();
			    EnumChatFormatting rankColor = selectedRank.getColor();

			    if (rank != null) {
			        displayName = ColorUtil.darkGray + "[" + rankColor + rank + ColorUtil.darkGray + "] " + rankColor + displayName;
			    }
			}

			text = text.replace(ownName, displayName);
			event.string = text;
		}
	}
}