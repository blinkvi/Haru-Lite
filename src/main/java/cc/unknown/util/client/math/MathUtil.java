package cc.unknown.util.client.math;

import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

import cc.unknown.util.Accessor;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MathUtil implements Accessor {

    public int randomInt(int origin, int bound) {
        return origin == bound ? origin : ThreadLocalRandom.current().nextInt(origin, bound);
    }

    public float randomFloat(float origin, float bound) {
        return origin == bound ? origin : (float) ThreadLocalRandom.current().nextDouble(origin, bound);
    }

    public double randomDouble(double origin, double bound) {
        return origin == bound ? origin : ThreadLocalRandom.current().nextDouble(origin, bound);
    }
    
    public double randomGaussian(double average) {
        return ThreadLocalRandom.current().nextGaussian() * average;
    }

    public double nextDouble(double min, double max) {
        if (min == max || max - min <= 0D) return min;
        return min + ((max - min) * Math.random());
    }
    
    public int randomizeSafeInt(int min, int max) {
    	double randomPercent = randomDouble(0.7, 1.3);
    	return (int) (randomPercent * randomInt(min, max + 1));
    }

    public int randomizeInt(int min, int max) {
        return (int) randomizeDouble(min, max);
    }

    public float randomizeFloat(float min, float max) {
        return (float) randomizeDouble(min, max);
    }

    public double randomizeDouble(double min, double max) {
        return Math.random() * (max - min) + min;
    }

    public int nextSecureInt(int origin, int bound) {
        return origin + new SecureRandom().nextInt(bound - origin);
    }

    public float nextSecureFloat(float origin, float bound) {
        return origin + new SecureRandom().nextFloat() * (bound - origin);
    }

    public double nextSecureDouble(double origin, double bound) {
        return origin + new SecureRandom().nextDouble() * (bound - origin);
    }
    
    public double nextSecureGaussian(double origin, double bound) {
        return origin + new SecureRandom().nextGaussian() * (bound - origin);
    }

    public int lerpInt(int a, int b, int c) {
        return a + c * (b - a);
    }

    public float lerpFloat(float a, float b, float c) {
        return a + c * (b - a);
    }

    public double lerpDouble(double a, double b, double c) {
        return a + c * (b - a);
    }

    public double clamp(double min, double max, double n) {
        return Math.max(min, Math.min(max, n));
    }

    public double incValue(double val, double inc) {
        double one = 1.0 / inc;
        return Math.round(val * one) / one;
    }

    public double round(double n, int d) {
        if (d == 0) return (double) Math.round(n);
        double p = Math.pow(10.0D, d);
        return Math.round(n * p) / p;
    }

    public boolean isWholeNumber(double num) {
        return num == Math.floor(num);
    }

    public boolean chanceApply(float value) {
        return randomInt(0, 100) < (value * 100);
    }

    public double randomSin() {
        return Math.sin(nextDouble(0.0, Math.PI * 2));
    }
}
