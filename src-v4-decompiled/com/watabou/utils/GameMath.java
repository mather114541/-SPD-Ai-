/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.utils;

import com.watabou.noosa.Game;

public class GameMath {
    public static float speed(float speed, float acc) {
        if (acc != 0.0f) {
            speed += acc * Game.elapsed;
        }
        return speed;
    }

    public static float gate(float min, float value, float max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }
}

