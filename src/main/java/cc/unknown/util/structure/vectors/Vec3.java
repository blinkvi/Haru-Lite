package cc.unknown.util.structure.vectors;

import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class Vec3 {
    public static final Vec3 ZERO = new Vec3(0, 0, 0);

    public double x, y, z;

    public Vec3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3(net.minecraft.util.Vec3 vec3) {
        this(vec3.xCoord, vec3.yCoord, vec3.zCoord);
    }

    public Vec3(Entity entity) {
        this(entity.posX, entity.posY, entity.posZ);
    }
    
    public Vec3(S18PacketEntityTeleport wrapper) {
    	this(wrapper.getX() / 32.0D, wrapper.getY() / 32.0D, wrapper.getZ() / 32.0D);
    }

    public Vec3(BlockPos blockPos) {
        this(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
    }
    
    public double distanceTo(Vec3 vec3) {
        double deltaX = this.x - vec3.x;
        double deltaY = this.y - vec3.y;
        double deltaZ = this.z - vec3.z;
        return MathHelper.sqrt_double(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
    }

    public double distanceTo(net.minecraft.util.Vec3 vec3) {
        double deltaX = this.x - vec3.xCoord;
        double deltaY = this.y - vec3.yCoord;
        double deltaZ = this.z - vec3.zCoord;
        return MathHelper.sqrt_double(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
    }

    public double distanceTo(Entity entity) {
        double deltaX = this.x - entity.posX;
        double deltaY = this.y - entity.posY;
        double deltaZ = this.z - entity.posZ;
        return MathHelper.sqrt_double(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
    }
    
    public Vec3 add(Vec3 vec3) {
        return add(vec3.x(), vec3.y(), vec3.z());
    }

    public Vec3 add(final double x, final double y, final double z) {
        return new Vec3(x() + x, y() + y, z() + z);
    }
    
    public net.minecraft.util.Vec3 toVec3() {
        return new net.minecraft.util.Vec3(x, y, z);
    }
    
    public double x() { return x; }
    public double y() { return y; }
    public double z() { return z; }
}
