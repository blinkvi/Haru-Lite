package cc.unknown.util.client.math;

import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

import cc.unknown.util.Accessor;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MathUtil implements Accessor {

    /* ------------------------- RANDOM BÁSICO ------------------------- */

    public int nextRandomInt(int origin, int bound) {
        return origin == bound ? origin : ThreadLocalRandom.current().nextInt(origin, bound);
    }

    public long nextRandomLong(long origin, long bound) {
        return origin == bound ? origin : ThreadLocalRandom.current().nextLong(origin, bound);
    }

    public float nextRandomFloat(float origin, float bound) {
        return origin == bound ? origin : (float) ThreadLocalRandom.current().nextDouble(origin, bound);
    }

    public double nextRandomDouble(double origin, double bound) {
        return origin == bound ? origin : ThreadLocalRandom.current().nextDouble(origin, bound);
    }

    public double nextDouble(double min, double max) {
        if (min == max || max - min <= 0D) return min;
        return min + ((max - min) * Math.random());
    }

    public long getSafeRandom(long min, long max) {
        double randomPercent = nextRandomDouble(0.7, 1.3);
        return (long) (randomPercent * nextRandomLong(min, max + 1));
    }

    public int randomizeInt(float min, float max) {
        return (int) randomizeDouble(min, max);
    }

    public float randomizeFloat(float min, float max) {
        return (float) randomizeDouble(min, max);
    }

    public double randomizeDouble(double min, double max) {
        return Math.random() * (max - min) + min;
    }

    /* ------------------------- RANDOM SEGURO ------------------------- */

    public int nextSecureInt(int origin, int bound) {
        return origin + new SecureRandom().nextInt(bound - origin);
    }

    public float nextSecureFloat(float origin, float bound) {
        return origin + new SecureRandom().nextFloat() * (bound - origin);
    }

    public double nextSecureDouble(double origin, double bound) {
        return origin + new SecureRandom().nextDouble() * (bound - origin);
    }

    /* ------------------------- LERP ------------------------- */

    public int lerpInt(int a, int b, int c) {
        return a + c * (b - a);
    }

    public float lerpFloat(float a, float b, float c) {
        return a + c * (b - a);
    }

    public double lerpDouble(double a, double b, double c) {
        return a + c * (b - a);
    }

    public long lerpLong(long a, long b, long c) {
        return a + c * (b - a);
    }

    /* ------------------------- UTILIDADES ------------------------- */

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
        return nextRandomInt(0, 100) < (value * 100);
    }

    /* ------------------------- GAUSSIANA ------------------------- */

    public double getRandomGaussian(double average) {
        return ThreadLocalRandom.current().nextGaussian() * average;
    }

    public float calculateGaussian(float x, float sigma) {
        double PI = Math.PI;
        double output = 1.0 / Math.sqrt(2.0 * PI * (sigma * sigma));
        return (float) (output * Math.exp(-(x * x) / (2.0 * (sigma * sigma))));
    }

    /* ------------------------- TRIGONOMETRÍA ------------------------- */

    public double randomSin() {
        return Math.sin(nextDouble(0.0, Math.PI * 2));
    }
}
