package cc.unknown.util.player;
import java.util.List;

import cc.unknown.handlers.CPSHandler;
import cc.unknown.util.Accessor;
import cc.unknown.util.client.MathUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class PlayerUtil implements Accessor {
	
    public static boolean checkAir(double x, double y, double z) {
        return mc.theWorld.isAirBlock(new BlockPos(x, y - 0.5, z));
    }
    
    public static boolean checkAir() {
    	double x = mc.thePlayer.posX;
    	double y = mc.thePlayer.posY;
    	double z = mc.thePlayer.posZ;
        return mc.theWorld.isAirBlock(new BlockPos(x, y - 0.5, z));
    }
    
    public static boolean checkJump() {
        return mc.gameSettings.keyBindJump.isKeyDown();
    }
    
    private static AxisAlignedBB setMinY(AxisAlignedBB box, double minY) {
        return new AxisAlignedBB(box.minX, box.minY - minY, box.minZ, box.maxX, box.maxY, box.maxZ);
    }
    
    public static Block blockRelativeToPlayer(final double offsetX, final double offsetY, final double offsetZ) {
        return mc.theWorld.getBlockState(new BlockPos(mc.thePlayer).add(offsetX, offsetY, offsetZ)).getBlock();
    }
    
    public static boolean checkEdge(double offset) {
    	AxisAlignedBB box = mc.thePlayer.getEntityBoundingBox();
    	AxisAlignedBB adjustedBox = setMinY(box.offset(0, -0.2, 0).expand(-offset, 0, -offset), 0);
        return mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, adjustedBox).isEmpty();
    }
    
    public static boolean isOnEdgeWithBlockCheck(double negativeOffset, double positiveOffset) {
        AxisAlignedBB box = mc.thePlayer.getEntityBoundingBox();
        
        AxisAlignedBB adjustedBox = box
            .offset(0, -0.2, 0)
            .expand(-negativeOffset, 0, -negativeOffset);

        boolean noCollisionBelow = mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, adjustedBox).isEmpty();

        BlockPos blockPos = new BlockPos(mc.thePlayer).add(positiveOffset, 0, positiveOffset);
        Block blockBelow = mc.theWorld.getBlockState(blockPos).getBlock();

        boolean isAirOrReplaceable = blockBelow.getMaterial().isReplaceable();

        return noCollisionBelow && isAirOrReplaceable;
    }
    
    public static boolean isOnEdgeWithBlockCheck(double offset) {
        BlockPos blockPos = new BlockPos(mc.thePlayer).add(offset, -1, offset);
        Block blockBelow = mc.theWorld.getBlockState(blockPos).getBlock();

        boolean isAirOrReplaceable = blockBelow.getMaterial().isReplaceable();

        return isAirOrReplaceable;
    }
    
    public static boolean isOverVoid(double x, double y, double z) {
        for (double posY = y; posY > 0.0; posY--) {
            if (!(mc.theWorld.getBlockState(
                    new BlockPos(x, posY, z)).getBlock() instanceof BlockAir))
                return false;
        }

        return true;
    }

    public static boolean isOverVoid() {
        return isOverVoid(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
    }
 
	public static boolean isTeam(EntityPlayer player) {
		String entityName = player.getDisplayName().getUnformattedText();
		String playerName = mc.thePlayer.getDisplayName().getUnformattedText();

		if (entityName.length() >= 3 && playerName.startsWith(entityName.substring(0, 3))) {
			return true;
		}

		if (mc.thePlayer.isOnSameTeam(player)) {
			return true;
		}
		
		if (unusedNames(player)) {
			return false;
		}

		if (mc.thePlayer.getTeam() != null && player.getTeam() != null && mc.thePlayer.getTeam().isSameTeam(player.getTeam())) {
			return true;
		}

		if (playerName != null && player.getDisplayName() != null) {
			String targetName = player.getDisplayName().getFormattedText().replace("§r", "");
			String clientName = playerName.replace("§r", "");
			return targetName.startsWith("§" + clientName.charAt(1));
		}

		return false;
	}
	
    public static boolean unusedNames(EntityPlayer player) {
    	String name = player.getName();
    	return name.contains("CLICK DERECHO") || name.contains("MEJORAS") || name.contains("[NPC]") || name.contains("[SHOP]") || name.contains("CLIQUE PARA ABRIR");
    }
    
    public static void leftClick(boolean state) {
    	setState(mc.gameSettings.keyBindAttack.getKeyCode(), state);
    	if (!mc.thePlayer.isBlocking()) CPSHandler.addLeftClicks();
    }
    
    public static void rightClick(boolean state) {
    	setState(mc.gameSettings.keyBindUseItem.getKeyCode(), state);
		if (InventoryUtil.getAnyBlock() || InventoryUtil.getProjectiles())
			CPSHandler.addRightClicks();
    }
    
    public static void setShift(boolean state) {
        setState(mc.gameSettings.keyBindSneak.getKeyCode(), state);
    }
    
	public static void setState(int keycode, boolean state) {
		KeyBinding.setKeyBindState(keycode, state);
		KeyBinding.onTick(keycode);
	}
	
	public static double getFov(final double posX, final double posZ) {
		return getFov(mc.thePlayer.rotationYaw, posX, posZ);
	}
	
	public static double getFov(final float yaw, final double posX, final double posZ) {
		double angle = (yaw - angle(posX, posZ)) % 360.0;
		return MathHelper.wrapAngleTo180_double(angle);
	}
	
	public static float angle(final double n, final double n2) {
		return (float) (Math.atan2(n - mc.thePlayer.posX, n2 - mc.thePlayer.posZ) * 57.295780181884766 * -1.0);
	}
	
	public static double fovFromTarget(Entity entity) {
	    return ((mc.thePlayer.rotationYaw - fovToTarget(entity)) % 360.0 + 540.0) % 360.0 - 180.0;
	}

	private static float fovToTarget(Entity entity) {
		double diffX = entity.posX - mc.thePlayer.posX;
		double diffZ = entity.posZ - mc.thePlayer.posZ;
		return (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F;
	}
	
    public static double pitchFromTarget(Entity en) {
        return (double) (mc.thePlayer.rotationPitch - pitchToEntity(en, 0));
    }

    public static float pitchToEntity(Entity ent, float offset) {
        double x = mc.thePlayer.getDistanceToEntity(ent);
        double y = mc.thePlayer.posY - (ent.posY + offset);
        double pitch = (((Math.atan2(x, y) * 180.0D) / Math.PI));
        return (float) (90 - pitch);
    }

	public static boolean fov(float fov, Entity entity) {
		fov = (float) ((double) fov * 0.5D);
		double v = ((double) (mc.thePlayer.rotationYaw - fovToTarget(entity)) % 360.0D + 540.0D) % 360.0D - 180.0D;
		return v > 0.0D && v < (double) fov || (double) (-fov) < v && v < 0.0D;
	}

	
    public static Entity getPointedEntityRayTrace(double reachDistance) {
        if (mc.getRenderViewEntity() == null) return null;

        Entity pointedEntity = null;
        Vec3 vec3 = mc.getRenderViewEntity().getPositionEyes(1.0F);
        Vec3 lookVec = mc.getRenderViewEntity().getLook(1.0F);
        Vec3 vec32 = vec3.addVector(lookVec.xCoord * reachDistance, lookVec.yCoord * reachDistance, lookVec.zCoord * reachDistance);
        float f1 = 1.0F;
        List<Entity> list = mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.getRenderViewEntity(), mc.getRenderViewEntity().getEntityBoundingBox().addCoord(lookVec.xCoord * reachDistance, lookVec.yCoord * reachDistance, lookVec.zCoord * reachDistance).expand(f1, f1, f1));
        double d2 = reachDistance;

        for (Entity entity : list) {
            if (entity.canBeCollidedWith()) {
                float collisionBorderSize = 0.13F;
                AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().expand(collisionBorderSize, collisionBorderSize, collisionBorderSize);
                MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

                if (axisalignedbb.isVecInside(vec3)) {
                    if (0.0D < d2 || d2 == 0.0D) {
                        pointedEntity = entity;
                        d2 = 0.0D;
                    }
                } else if (movingobjectposition != null) {
                    double distance = vec3.distanceTo(movingobjectposition.hitVec);
                    if (distance < d2 || d2 == 0.0D) {
                        if (entity == mc.getRenderViewEntity().ridingEntity && !entity.canRiderInteract()) {
                            if (d2 == 0.0D) {
                                pointedEntity = entity;
                            }
                        } else {
                            pointedEntity = entity;
                            d2 = distance;
                        }
                    }
                }
            }
        }
        return pointedEntity;
    }
    
    public static float getCompleteHealth(EntityLivingBase entity) {
        if (entity == null) return 0;
        return entity.getHealth() + entity.getAbsorptionAmount();
    }

    public static String getHealthStr(EntityLivingBase entity) {
        float completeHealth = getCompleteHealth(entity);
        return getColorForHealth(entity.getHealth() / entity.getMaxHealth(), completeHealth);
    }
    
    private static String getColorForHealth(double n, double n2) {
        double health = MathUtil.round(n2, 1);
        return ((n < 0.3) ? "§c" : ((n < 0.5) ? "§6" : ((n < 0.7) ? "§e" : "§a"))) + (MathUtil.isWholeNumber(health) ? (int) health + "" : health);
    }
    
    public static String getHitsToKill(final EntityPlayer entityPlayer, final ItemStack itemStack) {
        final int n = (int) Math.ceil(ap(entityPlayer, itemStack));
        return "§" + ((n <= 1) ? "c" : ((n <= 3) ? "6" : ((n <= 5) ? "e" : "a"))) + n;
    }
    
    private static double ap(final EntityPlayer entityPlayer, final ItemStack itemStack) {
        double n = 1.0;
        if (itemStack != null && (itemStack.getItem() instanceof ItemSword || itemStack.getItem() instanceof ItemAxe)) {
            n += BlockUtil.getDamage(itemStack);
        }
        double n2 = 0.0;
        double n3 = 0.0;
        for (int i = 0; i < 4; ++i) {
            final ItemStack armorItemInSlot = entityPlayer.inventory.armorItemInSlot(i);
            if (armorItemInSlot != null) {
                if (armorItemInSlot.getItem() instanceof ItemArmor) {
                    n2 += ((ItemArmor) armorItemInSlot.getItem()).damageReduceAmount * 0.04;
                    final int getEnchantmentLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, armorItemInSlot);
                    if (getEnchantmentLevel != 0) {
                        n3 += Math.floor(0.75 * (6 + getEnchantmentLevel * getEnchantmentLevel) / 3.0);
                    }
                }
            }
        }
        return MathUtil.round((double) getCompleteHealth(entityPlayer) / (n * (1.0 - (n2 + 0.04 * Math.min(Math.ceil(Math.min(n3, 25.0) * 0.75), 20.0) * (1.0 - n2)))), 1);
    }
}
