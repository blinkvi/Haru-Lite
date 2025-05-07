package cc.unknown.util.client.math;

import java.security.SecureRandom;

import cc.unknown.util.Accessor;
import io.netty.util.internal.ThreadLocalRandom;

public class MathUtil implements Accessor {

    public static int randomInt(int origin, int bound) {
        return origin == bound ? origin : ThreadLocalRandom.current().nextInt(origin, bound);
    }

    public static float randomFloat(float origin, float bound) {
        return origin == bound ? origin : (float) ThreadLocalRandom.current().nextDouble(origin, bound);
    }

    public static double randomDouble(double origin, double bound) {
        return origin == bound ? origin : ThreadLocalRandom.current().nextDouble(origin, bound);
    }
    
    public static double randomGaussian(double average) {
        return ThreadLocalRandom.current().nextGaussian() * average;
    }

    public static double nextDouble(double min, double max) {
        if (min == max || max - min <= 0D) return min;
        return min + ((max - min) * Math.random());
    }
    
    public static int randomizeSafeInt(int min, int max) {
    	double randomPercent = randomDouble(0.7, 1.3);
    	return (int) (randomPercent * randomInt(min, max + 1));
    }

    public static int randomizeInt(int min, int max) {
        return (int) randomizeDouble(min, max);
    }

    public static float randomizeFloat(float min, float max) {
        return (float) randomizeDouble(min, max);
    }

    public static double randomizeDouble(double min, double max) {
        return Math.random() * (max - min) + min;
    }

    public static int nextSecureInt(int origin, int bound) {
        return origin + new SecureRandom().nextInt(bound - origin);
    }

    public static float nextSecureFloat(float origin, float bound) {
        return origin + new SecureRandom().nextFloat() * (bound - origin);
    }

    public static double nextSecureDouble(double origin, double bound) {
        return origin + new SecureRandom().nextDouble() * (bound - origin);
    }
    
    public static double nextSecureGaussian(double origin, double bound) {
        return origin + new SecureRandom().nextGaussian() * (bound - origin);
    }

    public static int lerpInt(int a, int b, int c) {
        return a + c * (b - a);
    }

    public static float lerpFloat(float a, float b, float c) {
        return a + c * (b - a);
    }

    public static double lerpDouble(double a, double b, double c) {
        return a + c * (b - a);
    }

    public static double clamp(double min, double max, double n) {
        return Math.max(min, Math.min(max, n));
    }

    public static double incValue(double val, double inc) {
        double one = 1.0 / inc;
        return Math.round(val * one) / one;
    }

    public static double round(double n, int d) {
        if (d == 0) return (double) Math.round(n);
        double p = Math.pow(10.0D, d);
        return Math.round(n * p) / p;
    }

    public static boolean isWholeNumber(double num) {
        return num == Math.floor(num);
    }

    public static boolean chanceApply(float value) {
        return randomInt(0, 100) < (value * 100);
    }

    public static double randomSin() {
        return Math.sin(nextDouble(0.0, Math.PI * 2));
    }
}
