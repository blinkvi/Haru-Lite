package cc.unknown.handlers;

import cc.unknown.Haru;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.PreTickEvent;
import cc.unknown.file.cosmetics.SuperCosmetic;
import cc.unknown.module.impl.visual.Cosmetics;
import cc.unknown.socket.impl.CosmeticSocket;
import cc.unknown.util.Managers;
import cc.unknown.util.player.PlayerUtil;

public class CosmeticHandler implements Managers {

	private String prevCape, prevHalo, prevHat, prevPet, prevAura, prevWings, prevAccesories;
	private boolean changed;
	
    @EventLink
    public final Listener<PreTickEvent> onPreTick = event -> {
		if (!PlayerUtil.isInGame()) return;
		Cosmetics c = getModule(Cosmetics.class);
		
		if (c.isEnabled()) {
			if (!(mc.thePlayer.ticksExisted % 200 == 0)) return;
			
		    boolean capeChanged = !c.capeType.is(prevCape);
		    boolean haloChanged = !c.haloType.is(prevHalo);
		    boolean hatChanged = !c.hatType.is(prevHat);
		    boolean petChanged = !c.petType.is(prevPet);
		    boolean auraChanged = !c.auraType.is(prevAura);
		    boolean wingsChanged = !c.wingsType.is(prevWings);
		    boolean accesoriesChanged = !c.accesoriesType.is(prevAccesories);
	
			SuperCosmetic currCosme = new SuperCosmetic(Haru.getUser(), c.haloType.getMode(), c.hatType.getMode(), c.petType.getMode(), c.auraType.getMode(), c.wingsType.getMode(), c.accesoriesType.getMode(), c.capeType.getMode());
	
			changed = (haloChanged || hatChanged || petChanged || auraChanged || wingsChanged || accesoriesChanged || capeChanged);
	
		    if (changed) {
		        Haru.cosmeMngr.saveFiles();
		    }
	
			CosmeticSocket.tick(currCosme);
	
		    prevCape = c.capeType.getMode();
		    prevHalo = c.haloType.getMode();
		    prevHat = c.hatType.getMode();
		    prevPet = c.petType.getMode();
		    prevAura = c.auraType.getMode();
		    prevWings = c.wingsType.getMode();
		    prevAccesories = c.accesoriesType.getMode();
		}
	};
}
