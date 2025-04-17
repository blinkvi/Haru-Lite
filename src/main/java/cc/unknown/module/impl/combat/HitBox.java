package cc.unknown.module.impl.combat;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;

import cc.unknown.Haru;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.client.MathUtil;
import cc.unknown.util.player.FriendUtil;
import cc.unknown.util.player.InventoryUtil;
import cc.unknown.util.value.impl.BoolValue;
import cc.unknown.util.value.impl.MultiBoolValue;
import cc.unknown.util.value.impl.SliderValue;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "HitBox", description = "Modifies hitbox size of players and entities.", category = Category.COMBAT)
public class HitBox extends Module {

	private final SliderValue multiplier = new SliderValue("Multiplier", this, 1.2f, 1.0f, 5.0f, 0.05f);

	public final MultiBoolValue conditionals = new MultiBoolValue("Conditionals", this, Arrays.asList(
			new BoolValue("OnlyWeapon", false), 
			new BoolValue("OnlyPlayers", false),
			new BoolValue("ShowNewHitbox", false)));

	private MovingObjectPosition moving;
	
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onMouse(MouseEvent event) {
		if (isInGame()) {
			if (event.button != 0 || !event.buttonstate || multiplier.getValue() == 1 || mc.thePlayer.isBlocking() || mc.currentScreen != null) {
				return;
			}
			call();
		}
	}

	@SubscribeEvent
	public void onRender3D(RenderWorldLastEvent event) {
		if (conditionals.isEnabled("ShowNewHitbox") && isInGame()) {
			for (Entity entity : mc.theWorld.loadedEntityList) {
				if (entity != mc.thePlayer && entity instanceof EntityLivingBase && ((EntityLivingBase) entity).deathTime == 0 && !(entity instanceof EntityArmorStand)) {
					displayHitbox(entity, Color.WHITE, event.partialTicks);
				}
			}
		}
	}

	public void call() {
	    if (conditionals.isEnabled("OnlyWeapon") && !InventoryUtil.isSword()) return;

	    EntityLivingBase entity = getEntity(1.0F);
	    if (entity == null) return;

	    if (conditionals.isEnabled("OnlyPlayers") && !(entity instanceof EntityPlayer)) {
	        return;
	    }

	    if (entity instanceof EntityPlayer && FriendUtil.isFriend((EntityPlayer) entity)) {
	        return;
	    }

	    mc.objectMouseOver = moving;
	}
	
	double getExpand(Entity en) {
		return (this != null && this.isEnabled()) ? multiplier.getValue() : 1.0D;
	}

	private EntityLivingBase getEntity(float partialTicks) {
		Reach reach = Haru.instance.getModuleManager().getModule(Reach.class);
		
		if (mc.getRenderViewEntity() != null && mc.theWorld != null) {
			mc.pointedEntity = null;
			Entity pointedEntity = null;
			double d0 = mc.playerController.extendedReach() ? 6.0
					: (reach.isEnabled() ? MathUtil.randomizeDouble(reach.min.getMin(), reach.max.getMax()) : 3.0);
			moving = mc.getRenderViewEntity().rayTrace(d0, partialTicks);
			double d2 = d0;
			Vec3 vec3 = mc.getRenderViewEntity().getPositionEyes(partialTicks);

			if (moving != null) {
				d2 = moving.hitVec.distanceTo(vec3);
			}

			Vec3 vec4 = mc.getRenderViewEntity().getLook(partialTicks);
			Vec3 vec5 = vec3.addVector(vec4.xCoord * d0, vec4.yCoord * d0, vec4.zCoord * d0);
			Vec3 vec6 = null;
			float f1 = 1.0F;
			List<Entity> list = mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.getRenderViewEntity(),
					mc.getRenderViewEntity().getEntityBoundingBox()
							.addCoord(vec4.xCoord * d0, vec4.yCoord * d0, vec4.zCoord * d0).expand(f1, f1, f1));
			double d3 = d2;

			for (Object o : list) {
				Entity entity = (Entity) o;
				if (entity.canBeCollidedWith()) {
					float ex = (float) ((double) entity.getCollisionBorderSize() * getExpand(entity));
					AxisAlignedBB ax = entity.getEntityBoundingBox().expand(ex, ex, ex);
					MovingObjectPosition mop = ax.calculateIntercept(vec3, vec5);
					if (ax.isVecInside(vec3)) {
						if (0.0D < d3 || d3 == 0.0D) {
							pointedEntity = entity;
							vec6 = mop == null ? vec3 : mop.hitVec;
							d3 = 0.0D;
						}
					} else if (mop != null) {
						double d4 = vec3.distanceTo(mop.hitVec);
						if (d4 < d3 || d3 == 0.0D) {
							if (entity == mc.getRenderViewEntity().ridingEntity && !entity.canRiderInteract()) {
								if (d3 == 0.0D) {
									pointedEntity = entity;
									vec6 = mop.hitVec;
								}
							} else {
								pointedEntity = entity;
								vec6 = mop.hitVec;
								d3 = d4;
							}
						}
					}
				}
			}

			if (pointedEntity != null && (d3 < d2 || moving == null)) {
				moving = new MovingObjectPosition(pointedEntity, vec6);
				if (pointedEntity instanceof EntityLivingBase || pointedEntity instanceof EntityItemFrame) {
					return (EntityLivingBase) pointedEntity;
				}
			}
		}
		return null;
	}

	private void displayHitbox(Entity entity, Color color, float partialTicks) {
	    if (!(entity instanceof EntityLivingBase)) return;
	    
	    if (conditionals.isEnabled("OnlyPlayers") && !(entity instanceof EntityPlayer)) return;

	    double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - mc.getRenderManager().viewerPosX;
	    double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - mc.getRenderManager().viewerPosY;
	    double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - mc.getRenderManager().viewerPosZ;
	    float ex = (float) (entity.getCollisionBorderSize() * multiplier.getValue());

	    AxisAlignedBB bbox = entity.getEntityBoundingBox().expand(ex, ex, ex);
	    AxisAlignedBB axis = new AxisAlignedBB(bbox.minX - entity.posX + x, bbox.minY - entity.posY + y, bbox.minZ - entity.posZ + z, bbox.maxX - entity.posX + x, bbox.maxY - entity.posY + y, bbox.maxZ - entity.posZ + z);

	    GL11.glBlendFunc(770, 771);
	    GL11.glEnable(GL11.GL_BLEND);
	    GL11.glDisable(GL11.GL_TEXTURE_2D);
	    GL11.glDisable(GL11.GL_DEPTH_TEST);
	    GL11.glDepthMask(false);
	    GL11.glLineWidth(2.0F);
	    GL11.glColor3d(color.getRed(), color.getGreen(), color.getBlue());
	    
	    RenderGlobal.drawSelectionBoundingBox(axis);

	    GL11.glEnable(GL11.GL_TEXTURE_2D);
	    GL11.glEnable(GL11.GL_DEPTH_TEST);
	    GL11.glDepthMask(true);
	    GL11.glDisable(GL11.GL_BLEND);
	}

}
