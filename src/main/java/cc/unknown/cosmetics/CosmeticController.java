package cc.unknown.cosmetics;

import cc.unknown.Haru;
import cc.unknown.cosmetics.impl.accessories.Bandana;
import cc.unknown.cosmetics.impl.aura.BlazeAura;
import cc.unknown.cosmetics.impl.aura.CreeperAura;
import cc.unknown.cosmetics.impl.aura.EnchantingAura;
import cc.unknown.cosmetics.impl.aura.OrbitAura;
import cc.unknown.cosmetics.impl.cape.Cape;
import cc.unknown.cosmetics.impl.hat.DougDimmadome;
import cc.unknown.cosmetics.impl.hat.Tophat;
import cc.unknown.cosmetics.impl.hat.WitchHat;
import cc.unknown.cosmetics.impl.pet.DogPet;
import cc.unknown.cosmetics.impl.pet.WhiterPet;
import cc.unknown.cosmetics.impl.wings.CrystalWings;
import cc.unknown.cosmetics.impl.wings.GalaxyWings;
import cc.unknown.file.cosmetics.SuperCosmetic;
import cc.unknown.module.impl.visual.Cosmetics;
import cc.unknown.socket.impl.CosmeticSocket;
import cc.unknown.util.render.enums.CosmeticType;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;

public class CosmeticController {

    private static Cosmetics getCosmetics() {
        return Haru.instance.getModuleManager().getModule(Cosmetics.class);
    }

    private static boolean shouldRenderCosmetic(AbstractClientPlayer player, CosmeticType type) {
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
        renderPlayer.addLayer(new WhiterPet(renderPlayer));
        renderPlayer.addLayer(new DogPet(renderPlayer));
        renderPlayer.addLayer(new Bandana(renderPlayer));
        renderPlayer.addLayer(new GalaxyWings(renderPlayer));
        renderPlayer.addLayer(new WitchHat(renderPlayer));
        renderPlayer.addLayer(new OrbitAura(renderPlayer));
        renderPlayer.addLayer(new BlazeAura(renderPlayer));
        renderPlayer.addLayer(new CreeperAura(renderPlayer));
        renderPlayer.addLayer(new EnchantingAura(renderPlayer));
        renderPlayer.addLayer(new CrystalWings(renderPlayer));
        renderPlayer.addLayer(new Cape(renderPlayer));
    }
}