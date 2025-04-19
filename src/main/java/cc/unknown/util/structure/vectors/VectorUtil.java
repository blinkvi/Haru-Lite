package cc.unknown.util.structure.vectors;

import lombok.experimental.UtilityClass;

@UtilityClass
public class VectorUtil {
    public Vector2d vec2(double x, double y) {
        return new Vector2d(x, y);
    }

    public Vector3d vec3(double x, double y, double z) {
        return new Vector3d(x, y, z);
    }

    public Vector3d vec3(Vector2d v, double z) {
        return new Vector3d(v.x, v.y, z);
    }

    public Vector4d vec4(double x, double y, double z, double w) {
        return new Vector4d(x, y, z, w);
    }

    public Vector4d vec4(Vector3d v, double w) {
        return new Vector4d(v.x, v.y, v.z, w);
    }

    private double atan2(double y, double x) {
        double ax = x >= 0.0 ? x : -x;
        double ay = y >= 0.0 ? y : -y;
        double a = Math.min(ax, ay) / Math.max(ax, ay);
        double s = a * a;
        double r = ((-0.0464964749 * s + 0.15931422) * s - 0.327622764) * s * a + a;
        if (ay > ax) {
            r = 1.57079637 - r;
        }

        if (x < 0.0) {
            r = 3.14159274 - r;
        }

        return y >= 0.0 ? r : -r;
    }

    public int mod(int a, int b) {
        return a % b;
    }

    public double mod(double a, double b) {
        return a % b;
    }

    public Vector2d mod(Vector2d a, double b) {
        return vec2(mod(a.x, b), mod(a.y, b));
    }

    public double fma(double a, double b, double c) {
        return a * b + c;
    }

    public double dot(Vector2d v1, Vector2d v2) {
        return atan2(v1.x * v2.x + v1.y * v2.y, v1.x * v2.y - v1.y * v2.x);
    }

    public double dot(Vector3d v1, Vector3d v2) {
        return fma(v1.x, v2.x, fma(v1.y, v2.y, v1.z * v2.z));
    }

    public double dot(Vector4d v1, Vector4d v2) {
        return fma(v1.x, v2.x, fma(v1.y, v2.y, fma(v1.z, v2.z, v1.w * v2.w)));
    }

    public double mix(double v1, double v2, double a) {
        return v1 * (1 - a) + v2 * a;
    }

    public Vector2d mix(Vector2d v1, Vector2d v2, double a) {
        return vec2(mix(v1.x, v2.x, a), mix(v1.y, v2.y, a));
    }

    public Vector3d mix(Vector3d v1, Vector3d v2, double a) {
        return vec3(mix(v1.x, v2.x, a), mix(v1.y, v2.y, a), mix(v1.z, v2.z, a));
    }

    public Vector4d mix(Vector4d v1, Vector4d v2, double a) {
        return vec4(mix(v1.x, v2.x, a), mix(v1.y, v2.y, a), mix(v1.z, v2.z, a), mix(v1.w, v2.w, a));
    }

    public double length(Vector2d vec2) {
        return length(vec2.x, vec2.y);
    }

    public double length(double x, double y) {
        return Math.sqrt(x * x + y * y);
    }

    public double exp(double x) {
        return Math.exp(x);
    }

    public float fract(float x) {
        return x - (int) x;
    }

    public Vector3d floor(Vector3d vec3) {
        return vec3(Math.floor(vec3.x), Math.floor(vec3.y), Math.floor(vec3.z));
    }
}
