package cc.unknown.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import cc.unknown.mixin.interfaces.IS14PacketEntity;
import net.minecraft.network.play.server.S14PacketEntity;

@Mixin(S14PacketEntity.class)
public class MixinS14PacketEntity implements IS14PacketEntity {
	@Shadow
	protected int entityId;

	@Override
	public int getEntityId() {
		return this.entityId;
	}
}
