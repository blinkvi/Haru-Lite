package cc.unknown.cosmetics;

import cc.unknown.cosmetics.impl.accessories.*;
import cc.unknown.cosmetics.impl.aura.*;
import cc.unknown.cosmetics.impl.cape.*;
import cc.unknown.cosmetics.impl.hat.*;
import cc.unknown.cosmetics.impl.pet.*;
import cc.unknown.cosmetics.impl.wings.*;
import cc.unknown.file.cosmetics.SuperCosmetic;
import cc.unknown.socket.impl.CosmeticSocket;
import cc.unknown.util.render.enums.CosmeticType;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;

public class CosmeticController {

    private static boolean shouldRenderCosmetic(AbstractClientPlayer player, CosmeticType type) {
        for(SuperCosmetic cosme : CosmeticSocket.cosmeticList){
            if(player.getName().equalsIgnoreCase(cosme.getName()) && CosmeticType.TOP){
              // System.out.println(cosme.toString());
               //System.out.println(type.getName());
            }
        }

        return CosmeticSocket.cosmeticList.stream()
                .filter(cosmetic -> player.getName().equalsIgnoreCase(cosmetic.getName()))
                .anyMatch(cosmetic -> {
                    switch (type) {
                        case DIMMADOME: return cosmetic.getHat().equalsIgnoreCase(type.getName());
                        case TOP: return cosmetic.getHat().equalsIgnoreCase(type.getName());
                        case WHITER: return cosmetic.getPet().equalsIgnoreCase(type.getName());
                        case DOG: return cosmetic.getPet().equalsIgnoreCase(type.getName());
                        case BANDANA: return cosmetic.getAccesories().equalsIgnoreCase(type.getName());
                        case GALAXY: return cosmetic.getWings().equalsIgnoreCase(type.getName());
                        case CRYSTAL: return cosmetic.getWings().equalsIgnoreCase(type.getName());
                        case WITCH: return cosmetic.getHat().equalsIgnoreCase(type.getName());
                        case ORBIT: return cosmetic.getAura().equalsIgnoreCase(type.getName());
                        case ENCHANTING: return cosmetic.getAura().equalsIgnoreCase(type.getName());
                        case BLAZE: return cosmetic.getAura().equalsIgnoreCase(type.getName());
                        case CREEPER: return cosmetic.getAura().equalsIgnoreCase(type.getName());
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
    }
}