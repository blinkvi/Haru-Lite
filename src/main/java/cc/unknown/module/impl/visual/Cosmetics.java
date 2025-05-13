package cc.unknown.module.impl.visual;

import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.render.enums.AccesoriesType;
import cc.unknown.util.render.enums.AuraType;
import cc.unknown.util.render.enums.HatType;
import cc.unknown.util.render.enums.PetType;
import cc.unknown.util.render.enums.WingsType;
import cc.unknown.value.impl.Mode;

@ModuleInfo(name = "Cosmetics", description = "Cosmetics like lunar.", category = Category.VISUAL)
public class Cosmetics extends Module {
	public final Mode haloType = new Mode("Halo", this, "None", "Aris", "Shiroko", "Reisa", "Natsu", "Hoshino", "None");
	public final Mode capeType = new Mode("Cape", this, "None", "None", "Japan", "Korean", "Eyes");
	public final Mode hatType = new Mode("Hat", this, HatType.NONE, HatType.values());
	public final Mode petType = new Mode("Pet", this, PetType.NONE, PetType.values());
	public final Mode auraType = new Mode("Aura", this, AuraType.NONE, AuraType.values());
	public final Mode wingsType = new Mode("Wings", this, WingsType.GALAXY, WingsType.values());
	public final Mode accesoriesType = new Mode("Accesories", this, AccesoriesType.NONE, AccesoriesType.values());
	
	public Cosmetics() {
		if (!isEnabled())
			toggle();
	}
}