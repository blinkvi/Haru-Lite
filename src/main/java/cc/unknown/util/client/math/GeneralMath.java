package cc.unknown.util.client.math;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class GeneralMath {
    public float sin(float value, BuildSpeed s) {
        return (s.equals(BuildSpeed.NORMAL))
                ? (float) Math.sin(value)
                : (s.equals(BuildSpeed.FAST)
                ? FastMath.sin(value)
                : FastMath.fastCos(value));
    }

    public float cos(float value, BuildSpeed s) {
        return (s.equals(BuildSpeed.NORMAL))
                ? (float) Math.cos(value)
                : (s.equals(BuildSpeed.FAST)
                ? FastMath.cos(value)
                : FastMath.fastCos(value));
    }
}