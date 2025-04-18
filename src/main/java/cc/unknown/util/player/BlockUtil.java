package cc.unknown.util.player;
import java.util.Map;

import cc.unknown.util.Accessor;
import cc.unknown.util.player.move.RotationUtil;
import cc.unknown.util.structure.vectors.Vector2f;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;

public class BlockUtil implements Accessor {
    public Block getBlock(Vec3 vec3) {
        return getBlock(new BlockPos(vec3));
    }
    
    public static Block getBlock(BlockPos blockPos) {
        if (mc.theWorld != null && blockPos != null) {
            return mc.theWorld.getBlockState(blockPos).getBlock();
        }
        return null;
    }
    
    public static Material getMaterial(BlockPos blockPos) {
        Block block = getBlock(blockPos);
        if (block != null) {
            return block.getMaterial();
        }
        return null;
    }
    
    public static boolean isReplaceable(BlockPos blockPos) {
        Material material = getMaterial(blockPos);
        return material != null && material.isReplaceable();
    }
    
	public static boolean insideBlock() {
		if (mc.thePlayer.ticksExisted < 5) {
			return false;
		}

		final EntityPlayerSP player = mc.thePlayer;
		final WorldClient world = mc.theWorld;
		final AxisAlignedBB bb = player.getEntityBoundingBox();
		for (int x = MathHelper.floor_double(bb.minX); x < MathHelper.floor_double(bb.maxX) + 1; ++x) {
			for (int y = MathHelper.floor_double(bb.minY); y < MathHelper.floor_double(bb.maxY) + 1; ++y) {
				for (int z = MathHelper.floor_double(bb.minZ); z < MathHelper.floor_double(bb.maxZ) + 1; ++z) {
					final Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
					final AxisAlignedBB boundingBox;
					if (block != null && !(block instanceof BlockAir)
							&& (boundingBox = block.getCollisionBoundingBox(world, new BlockPos(x, y, z),
									world.getBlockState(new BlockPos(x, y, z)))) != null
							&& player.getEntityBoundingBox().intersectsWith(boundingBox)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
    public static double getDamage(final ItemStack itemStack) {
        double getAmount = 0;
        for (final Map.Entry<String, AttributeModifier> entry : itemStack.getAttributeModifiers().entries()) {
            if (entry.getKey().equals("generic.attackDamage")) {
                getAmount = entry.getValue().getAmount();
                break;
            }
        }
        return getAmount + EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack) * 1.25;
    }
    

    public static double getClutchPriority(BlockPos blockPos) {
        return getCenterDistance(blockPos) + Math.abs(MathHelper.wrapAngleTo180_double(getCenterRotation(blockPos).x - (mc.thePlayer.rotationYaw)))/130;
    }
    
    public static double getCenterDistance(BlockPos blockPos) {
        return mc.thePlayer.getDistance(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
    }

    public static Vector2f getCenterRotation(BlockPos blockPos) {
        return RotationUtil.getRotations(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
    }
    
    public static EnumFacing getHorizontalFacingEnum(BlockPos blockPos) {
        return getHorizontalFacingEnum(blockPos, mc.thePlayer.posX, mc.thePlayer.posZ);
    }
    
    public static EnumFacing getHorizontalFacingEnum(BlockPos blockPos, double x, double z) {
        double dx = x - (blockPos.getX() + 0.5);
        double dz = z - (blockPos.getZ() + 0.5);

        if (dx > 0) {
            if (dz > dx) {
                return EnumFacing.SOUTH;
            } else if (-dz > dx) {
                return EnumFacing.NORTH;
            } else {
                return EnumFacing.EAST;
            }
        } else {
            if (dz > -dx) {
                return EnumFacing.SOUTH;
            } else if (dz < dx) {
                return EnumFacing.NORTH;
            } else {
                return EnumFacing.WEST;
            }
        }
    }
    
    public static Vector2f getFaceRotation(EnumFacing face, BlockPos blockPos) {
        Vec3i faceVec = face.getDirectionVec();
        Vec3 blockFaceVec = new Vec3(faceVec.getX() * 0.5, faceVec.getY() * 0.5, faceVec.getZ() * 0.5);
        blockFaceVec = blockFaceVec.add(toVec3(blockPos));
        blockFaceVec = blockFaceVec.addVector(0.5, 0.5, 0.5);
        return RotationUtil.getRotations(blockFaceVec);
    }
    
	public static Vec3 toVec3(BlockPos blockPos) {
		return new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ());
	}
}
