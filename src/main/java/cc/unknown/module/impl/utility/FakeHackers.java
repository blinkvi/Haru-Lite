package cc.unknown.module.impl.utility;

import cc.unknown.event.player.PrePositionEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.structure.vectors.Vector2f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "FakeHackers", description = "", category = Category.VISUAL)

public class FakeHackers extends Module {

	private String playerName = "Fabzky";

	@Override
	public void onDisable() {
		if (mc.theWorld != null) {
			if (playerName != null && !playerName.isEmpty()) {
				EntityPlayer player = mc.theWorld.getPlayerEntityByName(playerName);
				if (player != null) {
					player.setSneaking(false);
				}
			}
		}
		super.onDisable();
	}

	@SubscribeEvent
	public void onPreMotion(PrePositionEvent event) {
		if (mc.theWorld != null) {
			if (playerName != null && !playerName.isEmpty()) {
				EntityPlayer player = mc.theWorld.getPlayerEntityByName(playerName);
				if (player != null) {
					player.setSneaking(true);
					float range = 6;
					if (mc.thePlayer.getDistanceToEntity(player) < range) {
						Vector2f rot = getAnglesForThisEntityToHitYou(player);
						float yaw = rot.x;
						float pitch = rot.y;
						player.rotationYaw = yaw;
						player.setRotationYawHead(yaw);
						player.rotationPitch = pitch;
						player.cameraPitch = pitch;
						player.swingItem();
					}
				}
			}
		}

	}

	private Vector2f getAnglesForThisEntityToHitYou(EntityLivingBase entityLiving) {
		double difX = mc.thePlayer.posX - entityLiving.posX;
		double difY = mc.thePlayer.posY - entityLiving.posY + (double) (mc.thePlayer.getEyeHeight() / 1.4f);
		double difZ = mc.thePlayer.posZ - entityLiving.posZ;
		double hypo = entityLiving.getDistanceToEntity((Entity) mc.thePlayer);
		float yaw = (float) Math.toDegrees(Math.atan2(difZ, difX)) - 90.0f;
		float pitch = (float) (-Math.toDegrees(Math.atan2(difY, hypo)));
		return new Vector2f(yaw, pitch);
	}
}
