package cc.unknown.cosmetics;

import cc.unknown.Haru;
import cc.unknown.cosmetics.impl.accessories.Bandana;
import cc.unknown.cosmetics.impl.aura.BlazeAura;
import cc.unknown.cosmetics.impl.aura.CreeperAura;
import cc.unknown.cosmetics.impl.aura.EnchantingAura;
import cc.unknown.cosmetics.impl.aura.OrbitAura;
import cc.unknown.cosmetics.impl.cape.Cape;
import cc.unknown.cosmetics.impl.halo.Halo;
import cc.unknown.cosmetics.impl.hat.DimmadomeHat;
import cc.unknown.cosmetics.impl.hat.Tophat;
import cc.unknown.cosmetics.impl.hat.WitchHat;
import cc.unknown.cosmetics.impl.pet.DogPet;
import cc.unknown.cosmetics.impl.wings.AngelicWings;
import cc.unknown.cosmetics.impl.wings.DemonWings;
import cc.unknown.cosmetics.impl.wings.GalaxyWings;
import cc.unknown.cosmetics.impl.wings.KuroyukihimeWings;
import cc.unknown.cosmetics.impl.wings.MechWings;
import cc.unknown.cosmetics.impl.wings.MetalWings;
import cc.unknown.cosmetics.impl.wings.ShanaWings;
import cc.unknown.module.impl.visual.Cosmetics;
import cc.unknown.util.render.enums.AccesoriesType;
import cc.unknown.util.render.enums.AuraType;
import cc.unknown.util.render.enums.CosmeticType;
import cc.unknown.util.render.enums.HatType;
import cc.unknown.util.render.enums.PetType;
import cc.unknown.util.render.enums.WingsType;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;

public class CosmeticController {
	private static boolean shouldRenderCosmetic(AbstractClientPlayer player, CosmeticType type, String cosmeName) {
	    Cosmetics cosmetics = Haru.instance.getModuleManager().getModule(Cosmetics.class);
	    if (!cosmetics.isEnabled()) return false;

	    switch (type) {
	        case HALO:
	            return cosmetics.haloType.get().equalsIgnoreCase(cosmeName);

	        case CAPE:
	            return cosmetics.capeType.get().equalsIgnoreCase(cosmeName);

	        case HAT:
	            HatType hat = cosmetics.hatType.getMode(HatType.class);
	            return hat != null && hat.toString().equalsIgnoreCase(cosmeName);

	        case PET:
	            PetType pet = cosmetics.petType.getMode(PetType.class);
	            return pet != null && pet.toString().equalsIgnoreCase(cosmeName);

	        case AURA:
	            AuraType aura = cosmetics.auraType.getMode(AuraType.class);
	            return aura != null && aura.toString().equalsIgnoreCase(cosmeName);

	        case WINGS:
	            WingsType wings = cosmetics.wingsType.getMode(WingsType.class);
	            return wings != null && wings.toString().equalsIgnoreCase(cosmeName);

	        case ACCESORIES:
	            AccesoriesType acc = cosmetics.accesoriesType.getMode(AccesoriesType.class);
	            return acc != null && acc.toString().equalsIgnoreCase(cosmeName);

	        default:
	            return false;
	    }
	}

	
    public static boolean shouldRenderCosmeticForPlayer(AbstractClientPlayer player, CosmeticType type, String name) {
        return shouldRenderCosmetic(player, type, name);
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