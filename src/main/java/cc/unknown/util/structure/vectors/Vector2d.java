package cc.unknown.util.structure.vectors;

public final class Vector2d {
    public double x, y;

    public Vector2d(double x, double y) {
		this.x = x;
		this.y = y;
	}
    
    public Vector2d() {
    	
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

	public Vector2d offset(double x, double y) {
        return new Vector2d(this.x + x, this.y + y);
    }

    public Vector2d offset(Vector2d xy) {
        return offset(xy.x, xy.y);
    }
}
