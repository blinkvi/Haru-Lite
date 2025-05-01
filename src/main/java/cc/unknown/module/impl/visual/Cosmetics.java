package cc.unknown.module.impl.visual;

import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.render.enums.AccesoriesType;
import cc.unknown.util.render.enums.AuraType;
import cc.unknown.util.render.enums.HatType;
import cc.unknown.util.render.enums.PetType;
import cc.unknown.util.render.enums.WingsType;
import cc.unknown.value.impl.ModeValue;

@ModuleInfo(name = "Cosmetics", description = "Cosmetics like lunar.", category = Category.VISUAL)
public class Cosmetics extends Module {
	public final ModeValue haloType = new ModeValue("Halo", this, "None", "Aris", "Shiroko", "Reisa", "Natsu", "Hoshino", "None");
	public final ModeValue capeType = new ModeValue("Cape", this, "None", "None", "Japan", "Korean", "Eyes");
	public final ModeValue hatType = new ModeValue("Hat", this, HatType.NONE, HatType.values());
	public final ModeValue petType = new ModeValue("Pet", this, PetType.NONE, PetType.values());
	public final ModeValue auraType = new ModeValue("Aura", this, AuraType.NONE, AuraType.values());
	public final ModeValue wingsType = new ModeValue("Wings", this, WingsType.GALAXY, WingsType.values());
	public final ModeValue accesoriesType = new ModeValue("Accesories", this, AccesoriesType.NONE, AccesoriesType.values());
	
	public Cosmetics() {
		if (!isEnabled())
			toggle();
	}
}