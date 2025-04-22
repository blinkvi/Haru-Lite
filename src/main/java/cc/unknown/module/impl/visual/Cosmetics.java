package cc.unknown.module.impl.visual;

import cc.unknown.Haru;
import cc.unknown.file.cosmetics.SuperCosmetic;
import cc.unknown.handlers.DiscordHandler;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.socket.impl.CosmeticSocket;
import cc.unknown.util.render.enums.AccesoriesType;
import cc.unknown.util.render.enums.AuraType;
import cc.unknown.util.render.enums.HatType;
import cc.unknown.util.render.enums.PetType;
import cc.unknown.util.render.enums.WingsType;
import cc.unknown.util.value.impl.ModeValue;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

@ModuleInfo(name = "Cosmetics", description = "Cosmetics like lunar.", category = Category.VISUAL)
public class Cosmetics extends Module {
	boolean changed;

	public final ModeValue haloType = new ModeValue("Halo", this, "None", "Aris", "Shiroko", "Reisa", "Natsu", "Hoshino", "None");
	public final ModeValue capeType = new ModeValue("Cape", this, "None", "None", "Canada", "France", "Germany", "India", "Indonesia", "Italy", "Japan", "Korean", "UnitedKingdom", "UnitedStates", "Arcade", "Boost", "Dark", "Eyes", "Flame", "Kocho", "ZeroTwo", "Minecon2011", "Minecon2012", "Minecon2013", "Minecon2015", "Minecon2016");
	public final ModeValue hatType = new ModeValue("Hat", this, HatType.NONE, HatType.values());
	public final ModeValue petType = new ModeValue("Pet", this, PetType.NONE, PetType.values());
	public final ModeValue auraType = new ModeValue("Aura", this, AuraType.NONE, AuraType.values());
	public final ModeValue wingsType = new ModeValue("Wings", this, WingsType.GALAXY, WingsType.values());
	public final ModeValue accesoriesType = new ModeValue("Accesories", this, AccesoriesType.NONE, AccesoriesType.values());

	private String prevCapeMode, prevHaloMode, prevHatMode, prevPetMode, prevAuraMode, prevWingsMode,
			prevAccesoriesMode;
	
	// ok the cosmetics are a bit disorganized, this module will go in a menu

	public Cosmetics() {
		if (!isEnabled())
			toggle();
	}

	@SubscribeEvent
	public void onPreTick(ClientTickEvent event) {
		if (!isInGame()) return;
		if (event.phase == Phase.END) return;
		if (!(mc.thePlayer.ticksExisted % 200 == 0)) return;
		
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