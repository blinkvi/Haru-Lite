package cc.unknown.cosmetics;

import cc.unknown.cosmetics.impl.accessories.*;
import cc.unknown.cosmetics.impl.aura.*;
import cc.unknown.cosmetics.impl.cape.*;
import cc.unknown.cosmetics.impl.halo.*;
import cc.unknown.cosmetics.impl.hat.*;
import cc.unknown.cosmetics.impl.pet.*;
import cc.unknown.cosmetics.impl.wings.*;
import cc.unknown.socket.impl.CosmeticSocket;
import cc.unknown.util.render.enums.CosmeticType;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;

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
        addLayer(
            renderPlayer,
            new Bandana(renderPlayer),
            new Cape(renderPlayer),
            new Halo(renderPlayer),
            
            // pet
            new DogPet(renderPlayer),
            
            // hat
            new DimmadomeHat(renderPlayer),
            new Tophat(renderPlayer),
            new WitchHat(renderPlayer),
            
            // aura
            new OrbitAura(renderPlayer),
            new BlazeAura(renderPlayer),
            new CreeperAura(renderPlayer),
            new EnchantingAura(renderPlayer),
            
            // Wings
            new GalaxyWings(renderPlayer),
            new ShanaWings(renderPlayer),
            new KuroyukihimeWings(renderPlayer),
            new AngelicWings(renderPlayer),
            new DemonWings(renderPlayer),
            new MetalWings(renderPlayer),
            new MechWings(renderPlayer)
        );
    }

    @SafeVarargs
    private static void addLayer(RenderPlayer renderPlayer, LayerRenderer<AbstractClientPlayer>... layers) {
        for (LayerRenderer<AbstractClientPlayer> layer : layers) {
            renderPlayer.addLayer(layer);
        }
    }
}