/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.utils;

import com.watabou.noosa.Game;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Random {
    private static ArrayDeque<java.util.Random> generators;

    public static synchronized void resetGenerators() {
        generators = new ArrayDeque();
        generators.push(new java.util.Random());
    }

    public static synchronized void pushGenerator() {
        generators.push(new java.util.Random());
    }

    public static synchronized void pushGenerator(long seed) {
        generators.push(new java.util.Random(Random.scrambleSeed(seed)));
    }

    private static synchronized long scrambleSeed(long seed) {
        seed ^= seed >>> 32;
        seed *= -4710160504952957587L;
        seed ^= seed >>> 29;
        seed *= -4710160504952957587L;
        seed ^= seed >>> 32;
        seed *= -4710160504952957587L;
        seed ^= seed >>> 29;
        return seed;
    }

    public static synchronized void popGenerator() {
        if (generators.size() == 1) {
            Game.reportException(new RuntimeException("tried to pop the last random number generator!"));
        } else {
            generators.pop();
        }
    }

    public static synchronized float Float() {
        return Random.Float(true);
    }

    public static synchronized float Float(boolean useGeneratorStack) {
        if (useGeneratorStack) {
            return generators.peekFirst().nextFloat();
        }
        return generators.peekLast().nextFloat();
    }

    public static float Float(float max) {
        return Random.Float() * max;
    }

    public static float Float(float min, float max) {
        return min + Random.Float(max - min);
    }

    public static float NormalFloat(float min, float max) {
        return min + (Random.Float(max - min) + Random.Float(max - min)) / 2.0f;
    }

    public static synchronized int Int() {
        return Random.Int(true);
    }

    public static synchronized int Int(boolean useGeneratorStack) {
        if (useGeneratorStack) {
            return generators.peekFirst().nextInt();
        }
        return generators.peekLast().nextInt();
    }

    public static synchronized int Int(int max) {
        return Random.Int(max, true);
    }

    public static synchronized int Int(int max, boolean useGeneratorStack) {
        if (max <= 0) {
            return 0;
        }
        if (useGeneratorStack) {
            return generators.peekFirst().nextInt(max);
        }
        return generators.peekLast().nextInt(max);
    }

    public static int Int(int min, int max) {
        return min + Random.Int(max - min);
    }

    public static int IntRange(int min, int max) {
        return min + Random.Int(max - min + 1);
    }

    public static int NormalIntRange(int min, int max) {
        return min + (int)((Random.Float() + Random.Float()) * (float)(max - min + 1) / 2.0f);
    }

    public static int InvNormalIntRange(int min, int max) {
        float roll1 = Random.Float();
        float roll2 = Random.Float();
        if (Math.abs(roll1 - 0.5f) >= Math.abs(roll2 - 0.5f)) {
            return min + (int)(roll1 * (float)(max - min + 1));
        }
        return min + (int)(roll2 * (float)(max - min + 1));
    }

    public static synchronized long Long() {
        return Random.Long(true);
    }

    public static synchronized long Long(boolean useGeneratorStack) {
        if (useGeneratorStack) {
            return generators.peekFirst().nextLong();
        }
        return generators.peekLast().nextLong();
    }

    public static long Long(long max) {
        long result = Random.Long();
        if (result < 0L) {
            result += Long.MAX_VALUE;
        }
        return result % max;
    }

    public static int chances(float[] chances) {
        int length = chances.length;
        float sum = 0.0f;
        for (int i = 0; i < length; ++i) {
            sum += Math.max(0.0f, chances[i]);
        }
        if (sum <= 0.0f) {
            return -1;
        }
        float value = Random.Float(sum);
        sum = 0.0f;
        for (int i = 0; i < length; ++i) {
            if (!(value < (sum += Math.max(0.0f, chances[i])))) continue;
            return i;
        }
        return -1;
    }

    public static <K> K chances(HashMap<K, Float> chances) {
        int size = chances.size();
        Object[] values = chances.keySet().toArray();
        float[] probs = new float[size];
        float sum = 0.0f;
        for (int i = 0; i < size; ++i) {
            probs[i] = chances.get(values[i]).floatValue();
            sum += probs[i];
        }
        if (sum <= 0.0f) {
            return null;
        }
        float value = Random.Float(sum);
        sum = probs[0];
        for (int i = 0; i < size; ++i) {
            if (value < sum) {
                return (K)values[i];
            }
            sum += probs[i + 1];
        }
        return null;
    }

    public static int index(Collection<?> collection) {
        return Random.Int(collection.size());
    }

    @SafeVarargs
    public static <T> T oneOf(T ... array) {
        return array[Random.Int(array.length)];
    }

    public static <T> T element(T[] array) {
        return Random.element(array, array.length);
    }

    public static <T> T element(T[] array, int max) {
        return array[Random.Int(max)];
    }

    public static <T> T element(Collection<? extends T> collection) {
        int size = collection.size();
        return (T)(size > 0 ? collection.toArray()[Random.Int(size)] : null);
    }

    public static synchronized <T> void shuffle(List<? extends T> list) {
        Collections.shuffle(list, generators.peek());
    }

    public static void shuffle(int[] array) {
        for (int i = 0; i < array.length - 1; ++i) {
            int j = Random.Int(i, array.length);
            if (j == i) continue;
            int t = array[i];
            array[i] = array[j];
            array[j] = t;
        }
    }

    public static <T> void shuffle(T[] array) {
        for (int i = 0; i < array.length - 1; ++i) {
            int j = Random.Int(i, array.length);
            if (j == i) continue;
            T t = array[i];
            array[i] = array[j];
            array[j] = t;
        }
    }

    public static <U, V> void shuffle(U[] u, V[] v) {
        for (int i = 0; i < u.length - 1; ++i) {
            int j = Random.Int(i, u.length);
            if (j == i) continue;
            U ut = u[i];
            u[i] = u[j];
            u[j] = ut;
            V vt = v[i];
            v[i] = v[j];
            v[j] = vt;
        }
    }

    static {
        Random.resetGenerators();
    }
}

