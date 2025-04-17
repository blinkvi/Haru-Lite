package cc.unknown.util.structure.vectors;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class Vector3d {
    public double x;
    public double y;
    public double z;
    
    public Vector3d(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public Vector3d(Entity entity) {
        this(entity.posX, entity.posY, entity.posZ);
    }

    public Vector3d add(final double x, final double y, final double z) {
        return new Vector3d(this.x + x, this.y + y, this.z + z);
    }

    public Vector3d add(final Vector3d vector) {
        return add(vector.x, vector.y, vector.z);
    }

    public Vector3d subtract(final double x, final double y, final double z) {
        return add(-x, -y, -z);
    }

    public Vector3d subtract(final Vector3d vector) {
        return add(-vector.x, -vector.y, -vector.z);
    }

    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public Vector3d multiply(final double v) {
        return new Vector3d(x * v, y * v, z * v);
    }

    public double distance(Vector3d vector3d) {
        return Math.sqrt(Math.pow(vector3d.x - x, 2) + Math.pow(vector3d.y - y, 2) + Math.pow(vector3d.z - z, 2));
    }
    
    public double distanceTo(Entity entity) {
        double deltaX = this.x - entity.posX;
        double deltaY = this.y - entity.posY;
        double deltaZ = this.z - entity.posZ;
        return MathHelper.sqrt_double(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vector3d)) return false;

        Vector3d vector = (Vector3d) obj;
        return ((Math.floor(x) == Math.floor(vector.x)) && Math.floor(y) == Math.floor(vector.y) && Math.floor(z) == Math.floor(vector.z));
    }
}