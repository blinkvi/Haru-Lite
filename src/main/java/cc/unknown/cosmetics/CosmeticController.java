package cc.unknown.cosmetics;

import cc.unknown.cosmetics.impl.accessories.Bandana;
import cc.unknown.cosmetics.impl.aura.BlazeAura;
import cc.unknown.cosmetics.impl.aura.CreeperAura;
import cc.unknown.cosmetics.impl.aura.EnchantingAura;
import cc.unknown.cosmetics.impl.aura.OrbitAura;
import cc.unknown.cosmetics.impl.cape.Cape;
import cc.unknown.cosmetics.impl.halo.Halo;
import cc.unknown.cosmetics.impl.hat.DougDimmadome;
import cc.unknown.cosmetics.impl.hat.Tophat;
import cc.unknown.cosmetics.impl.hat.WitchHat;
import cc.unknown.cosmetics.impl.pet.DogPet;
import cc.unknown.cosmetics.impl.wings.GalaxyWings;
import cc.unknown.socket.impl.CosmeticSocket;
import cc.unknown.util.render.enums.CosmeticType;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;

public class CosmeticController {

	private static boolean shouldRenderCosmetic(AbstractClientPlayer player, CosmeticType type) {	    
	    return CosmeticSocket.cosmeticList.stream()
	        .filter(cosmetic -> player.getName().equalsIgnoreCase(cosmetic.getName()))
	        .anyMatch(cosmetic -> {
	            switch (type) {
	                case HALO: return !cosmetic.getHalo().equalsIgnoreCase("None");
	                case HAT: return !cosmetic.getHat().equalsIgnoreCase("None");
	                case PET: return !cosmetic.getPet().equalsIgnoreCase("None");
	                case AURA: return !cosmetic.getAura().equalsIgnoreCase("None");
	                case WINGS: return !cosmetic.getWings().equalsIgnoreCase("None");
	                case ACCESORIES: return !cosmetic.getAccesories().equalsIgnoreCase("None");
	                case CAPE: return !cosmetic.getCape().equalsIgnoreCase("None");
	                default: return false;
	            }
	        });
	}


    public static boolean shouldRenderCosmeticForPlayer(AbstractClientPlayer player, CosmeticType type) {
        return shouldRenderCosmetic(player, type);
    }

    public static float[] getTophatColor(AbstractClientPlayer player) {
        return new float[]{1, 0, 0};
    }

    public static void addModels(RenderPlayer renderPlayer) {
    	renderPlayer.addLayer(new DougDimmadome(renderPlayer));
        renderPlayer.addLayer(new Tophat(renderPlayer));
        renderPlayer.addLayer(new DogPet(renderPlayer));
        renderPlayer.addLayer(new Bandana(renderPlayer));
        renderPlayer.addLayer(new GalaxyWings(renderPlayer));
        renderPlayer.addLayer(new WitchHat(renderPlayer));
        renderPlayer.addLayer(new OrbitAura(renderPlayer));
        renderPlayer.addLayer(new BlazeAura(renderPlayer));
        renderPlayer.addLayer(new CreeperAura(renderPlayer));
        renderPlayer.addLayer(new EnchantingAura(renderPlayer));
        renderPlayer.addLayer(new Cape(renderPlayer));
        renderPlayer.addLayer(new Halo(renderPlayer));
    }
}