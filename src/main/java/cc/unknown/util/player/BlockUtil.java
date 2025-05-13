package cc.unknown.util.player;
import java.util.Map;
import java.util.stream.IntStream;

import org.lwjgl.input.Mouse;

import cc.unknown.util.Accessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDropper;
import net.minecraft.block.BlockEnchantmentTable;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockJukebox;
import net.minecraft.block.BlockNote;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
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
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

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

	    int minX = MathHelper.floor_double(bb.minX);
	    int maxX = MathHelper.floor_double(bb.maxX) + 1;
	    int minY = MathHelper.floor_double(bb.minY);
	    int maxY = MathHelper.floor_double(bb.maxY) + 1;
	    int minZ = MathHelper.floor_double(bb.minZ);
	    int maxZ = MathHelper.floor_double(bb.maxZ) + 1;

	    return IntStream.range(minX, maxX).boxed().flatMap(x -> IntStream.range(minY, maxY).boxed().flatMap(y -> IntStream.range(minZ, maxZ).mapToObj(z -> new BlockPos(x, y, z)))).anyMatch(pos -> {
	    	Block block = world.getBlockState(pos).getBlock();
	    	AxisAlignedBB box = block != null && !(block instanceof BlockAir) ? block.getCollisionBoundingBox(world, pos, world.getBlockState(pos)) : null;
	    	return box != null && player.getEntityBoundingBox().intersectsWith(box);
	    });
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
    
    public static double getCenterDistance(BlockPos blockPos) {
        return mc.thePlayer.getDistance(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
    }

    public static EnumFacing getHorizontalFacingEnum(BlockPos blockPos) {
        return getHorizontalFacingEnum(blockPos, mc.thePlayer.posX, mc.thePlayer.posZ);
    }
    
    public static IBlockState getBlockState(BlockPos blockPos) {
        return mc.theWorld.getBlockState(blockPos);
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
    
	public static Vec3 toVec3(BlockPos blockPos) {
		return new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ());
	}
	
	public static boolean isMining() {
		return mc.currentScreen == null && mc.inGameHasFocus && Mouse.isButtonDown(0) && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && mc.objectMouseOver.getBlockPos() != null;
	}
	
	public static boolean lookingAtBlock() {
		return mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && mc.objectMouseOver.getBlockPos() != null;
	}
	
    public static boolean isInteractable(Block block) {
        return block instanceof BlockFurnace || block instanceof BlockTrapDoor || block instanceof BlockDoor || block instanceof BlockContainer || block instanceof BlockJukebox || block instanceof BlockFenceGate || block instanceof BlockChest || block instanceof BlockEnderChest || block instanceof BlockEnchantmentTable || block instanceof BlockBrewingStand || block instanceof BlockBed || block instanceof BlockDropper || block instanceof BlockDispenser || block instanceof BlockHopper || block instanceof BlockAnvil || block instanceof BlockNote || block instanceof BlockWorkbench;
    }

    public static boolean isInteractable(MovingObjectPosition mv) {
        if (mv == null || mv.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK || mv.getBlockPos() == null) {
            return false;
        }
        if (!mc.thePlayer.isSneaking() || mc.thePlayer.getHeldItem() == null) {
            return isInteractable(getBlock(mv.getBlockPos()));
        }
        return false;
    }
}
