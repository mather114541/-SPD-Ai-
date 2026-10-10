/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.utils;

import com.watabou.utils.Random;

public class ColorMath {
    public static int interpolate(int A, int B, float p) {
        if (p <= 0.0f) {
            return A;
        }
        if (p >= 1.0f) {
            return B;
        }
        int ra = A >> 16;
        int ga = A >> 8 & 0xFF;
        int ba = A & 0xFF;
        int rb = B >> 16;
        int gb = B >> 8 & 0xFF;
        int bb = B & 0xFF;
        float p1 = 1.0f - p;
        int r = (int)(p1 * (float)ra + p * (float)rb);
        int g = (int)(p1 * (float)ga + p * (float)gb);
        int b = (int)(p1 * (float)ba + p * (float)bb);
        return (r << 16) + (g << 8) + b;
    }

    public static int interpolate(float p, int ... colors) {
        if (p <= 0.0f) {
            return colors[0];
        }
        if (p >= 1.0f) {
            return colors[colors.length - 1];
        }
        int segment = (int)((float)(colors.length - 1) * p);
        return ColorMath.interpolate(colors[segment], colors[segment + 1], p * (float)(colors.length - 1) % 1.0f);
    }

    public static int random(int a, int b) {
        return ColorMath.interpolate(a, b, Random.Float());
    }
}

