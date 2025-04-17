package cc.unknown.util.render.enums;

import cc.unknown.util.render.client.ColorUtil;
import net.minecraft.util.EnumChatFormatting;

public enum RankType {
	USU("Usu", ColorUtil.reset),
	JUP("Jup", ColorUtil.aqua),
	NEP("Nep", ColorUtil.blue),
	MER("Mer", ColorUtil.darkGreen),
	SAT("Sat", ColorUtil.darkPurple),
	STR("Str", ColorUtil.pink),
	AYU("Ayu", ColorUtil.yellow),
	BUI("Bui", ColorUtil.green),
	MOD("Mod", ColorUtil.darkAqua),
	ADM("Adm", ColorUtil.red);
	
	private final String name;
	private final EnumChatFormatting color;

	private RankType(String name, EnumChatFormatting color) {
		this.name = name;
		this.color = color;
	}

	public String getName() {
		return name;
	}

	public EnumChatFormatting getColor() {
		return color;
	}
	
	@Override
    public String toString() {
        return name;
    }
}