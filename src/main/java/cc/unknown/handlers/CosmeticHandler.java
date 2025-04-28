package cc.unknown.handlers;

import cc.unknown.Haru;
import cc.unknown.file.cosmetics.SuperCosmetic;
import cc.unknown.module.impl.visual.Cosmetics;
import cc.unknown.socket.impl.CosmeticSocket;
import cc.unknown.util.Accessor;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class CosmeticHandler implements Accessor {

	private String prevCape, prevHalo, prevHat, prevPet, prevAura, prevWings, prevAccesories;
	private boolean changed;
	
	@SubscribeEvent
	public void onPreTick(ClientTickEvent event) {
		if (!isInGame()) return;
		if (event.phase == Phase.END) return;
		if (!(mc.thePlayer.ticksExisted % 200 == 0)) return;
		Cosmetics c = getModule(Cosmetics.class);
		
	    String username = Haru.getUser();
	    boolean capeChanged = !c.capeType.is(prevCape);
	    boolean haloChanged = !c.haloType.is(prevHalo);
	    boolean hatChanged = !c.hatType.is(prevHat);
	    boolean petChanged = !c.petType.is(prevPet);
	    boolean auraChanged = !c.auraType.is(prevAura);
	    boolean wingsChanged = !c.wingsType.is(prevWings);
	    boolean accesoriesChanged = !c.accesoriesType.is(prevAccesories);

		SuperCosmetic currentCosmetic = new SuperCosmetic(username, c.haloType.get(), c.hatType.get(), c.petType.get(), c.auraType.get(), c.wingsType.get(), c.accesoriesType.get(), c.capeType.get());

		changed = (haloChanged || hatChanged || petChanged || auraChanged || wingsChanged || accesoriesChanged || capeChanged);

	    if (changed) {
	        Haru.instance.getCosmeticManager().saveFiles();
	    }

		CosmeticSocket.tick(currentCosmetic);

	    prevCape = c.capeType.get();
	    prevHalo = c.haloType.get();
	    prevHat = c.hatType.get();
	    prevPet = c.petType.get();
	    prevAura = c.auraType.get();
	    prevWings = c.wingsType.get();
	    prevAccesories = c.accesoriesType.get();
	}
}
