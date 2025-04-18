package cc.unknown.module.impl.visual;

import cc.unknown.Haru;
import cc.unknown.event.PreTickEvent;
import cc.unknown.file.cosmetics.SuperCosmetic;
import cc.unknown.handlers.DiscordHandler;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.socket.impl.CosmeticSocket;
import cc.unknown.util.render.HaloRenderer;
import cc.unknown.util.render.enums.AccesoriesType;
import cc.unknown.util.render.enums.AuraType;
import cc.unknown.util.render.enums.HatType;
import cc.unknown.util.render.enums.PetType;
import cc.unknown.util.render.enums.WingsType;
import cc.unknown.util.value.impl.ModeValue;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "Cosmetics", description = "Cosmetics like lunar.", category = Category.VISUAL)
public class Cosmetics extends Module {
	boolean changed;

	public final ModeValue haloType = new ModeValue("Halo", this, "Aris", "Aris", "Shiroko", "Reisa", "Natsu", "Hoshino", "None");
	public final ModeValue capeType = new ModeValue("Cape", this, "None", "None", "Canada", "France", "Germany", "India", "Indonesia", "Italy", "Japan", "Korean", "UnitedKingdom", "UnitedStates", "Arcade", "Boost", "Dark", "Eyes", "Flame", "Kocho", "ZeroTwo", "Minecon2011", "Minecon2012", "Minecon2013", "Minecon2015", "Minecon2016");
	public final ModeValue hatType = new ModeValue("Hat", this, HatType.NONE, HatType.values());
	public final ModeValue petType = new ModeValue("Pet", this, PetType.NONE, PetType.values());
	public final ModeValue auraType = new ModeValue("Aura", this, AuraType.ORBIT, AuraType.values());
	public final ModeValue wingsType = new ModeValue("Wings", this, WingsType.GALAXY, WingsType.values());
	public final ModeValue accesoriesType = new ModeValue("Accesories", this, AccesoriesType.NONE, AccesoriesType.values());

	private String prevCapeMode, prevHaloMode, prevHatMode, prevPetMode, prevAuraMode, prevWingsMode,
			prevAccesoriesMode;

	public Cosmetics() {
		if (!isEnabled())
			toggle();
	}

	@SubscribeEvent
	public void onRender3D(RenderWorldLastEvent event) {
		if (mc.gameSettings.thirdPersonView == 0) return;
		HaloRenderer.drawHalo(haloType.getMode());
	}

	@SubscribeEvent
	public void onPreTick(PreTickEvent event) {
		if (!isInGame()) return;
		if (!(mc.thePlayer.ticksExisted % 400 == 0)) return;
		
	    String username = DiscordHandler.getUser();
	    boolean capeChanged = !capeType.is(prevCapeMode);
	    boolean haloChanged = !haloType.is(prevHaloMode);
	    boolean hatChanged = !hatType.is(prevHatMode);
	    boolean petChanged = !petType.is(prevPetMode);
	    boolean auraChanged = !auraType.is(prevAuraMode);
	    boolean wingsChanged = !wingsType.is(prevWingsMode);
	    boolean accesoriesChanged = !accesoriesType.is(prevAccesoriesMode);

		SuperCosmetic currentCosmetic = new SuperCosmetic(username, haloType.getMode(), hatType.getMode(), petType.getMode(), auraType.getMode(), wingsType.getMode(), accesoriesType.getMode(), capeType.getMode());

		changed = (haloChanged || hatChanged || petChanged || auraChanged || wingsChanged || accesoriesChanged || capeChanged);

	    if (changed) {
	        Haru.instance.getCosmeticManager().saveFiles();
	    }

		CosmeticSocket.tick(currentCosmetic);

	    prevCapeMode = capeType.getMode();
	    prevHaloMode = haloType.getMode();
	    prevHatMode = hatType.getMode();
	    prevPetMode = petType.getMode();
	    prevAuraMode = auraType.getMode();
	    prevWingsMode = wingsType.getMode();
	    prevAccesoriesMode = accesoriesType.getMode();
	}
}