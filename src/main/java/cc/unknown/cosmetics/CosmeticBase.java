package cc.unknown.cosmetics;

import cc.unknown.file.cosmetics.SuperCosmetic;
import cc.unknown.socket.impl.CosmeticSocket;
import cc.unknown.util.Accessor;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;

public abstract class CosmeticBase implements LayerRenderer<AbstractClientPlayer>, Accessor {

	protected final RenderPlayer playerRenderer;

	public CosmeticBase(RenderPlayer playerRenderer) {
		this.playerRenderer = playerRenderer;
	}

	@Override
	public void doRenderLayer(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float headYaw, float headPitch, float scale) {
		if (player.hasPlayerInfo() && !player.isInvisible()) {
			render(player, limbSwing, limbSwingAmount, partialTicks, ageInTicks, headYaw, headPitch, scale);
		}
	}

	public abstract void render(AbstractClientPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount,
			float partialTicks, float ageInTicks, float headYaw, float HeadPitch, float scale);

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}

	public String isAccessories(String name) {
		return CosmeticSocket.cosmeticList.stream().filter(cosmetic -> name.equalsIgnoreCase(cosmetic.getName())).map(SuperCosmetic::getAccesories).filter(accessories -> !accessories.equalsIgnoreCase("None")).findFirst().orElse("None");
	}

	public String isAura(String name) {
		return CosmeticSocket.cosmeticList.stream().filter(cosmetic -> name.equalsIgnoreCase(cosmetic.getName())).map(SuperCosmetic::getAura).filter(aura -> !aura.equalsIgnoreCase("None")).findFirst().orElse("None");
	}

	public String isCape(String name) {
		return CosmeticSocket.cosmeticList.stream().filter(cosmetic -> name.equalsIgnoreCase(cosmetic.getName())).map(SuperCosmetic::getCape).filter(cape -> !cape.equalsIgnoreCase("None")).findFirst().orElse("None");
	}

	public String isHat(String name) {
		return CosmeticSocket.cosmeticList.stream().filter(cosmetic -> name.equalsIgnoreCase(cosmetic.getName())).map(SuperCosmetic::getHat).filter(hat -> !hat.equalsIgnoreCase("None")).findFirst().orElse("None");
	}

	public String isPet(String name) {
		return CosmeticSocket.cosmeticList.stream().filter(cosmetic -> name.equalsIgnoreCase(cosmetic.getName())).map(SuperCosmetic::getPet).filter(pet -> !pet.equalsIgnoreCase("None")).findFirst().orElse("None");
	}

	public String isWings(String name) {
		return CosmeticSocket.cosmeticList.stream().filter(cosmetic -> name.equalsIgnoreCase(cosmetic.getName())).map(SuperCosmetic::getWings).filter(wings -> !wings.equalsIgnoreCase("None")).findFirst().orElse("None");
	}
}