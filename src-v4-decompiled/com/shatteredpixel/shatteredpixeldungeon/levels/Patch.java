/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.watabou.utils.Random;

public class Patch {
    public static boolean[] generate(int w, int h, float fill, int clustering, boolean forceFillRate) {
        int i;
        int length = w * h;
        boolean[] cur = new boolean[length];
        boolean[] off = new boolean[length];
        int fillDiff = -Math.round((float)length * fill);
        if (forceFillRate && clustering > 0) {
            fill += (0.5f - fill) * 0.5f;
        }
        for (i = 0; i < length; ++i) {
            boolean bl = off[i] = Random.Float() < fill;
            if (!off[i]) continue;
            ++fillDiff;
        }
        for (i = 0; i < clustering; ++i) {
            for (int y = 0; y < h; ++y) {
                for (int x = 0; x < w; ++x) {
                    int pos = x + y * w;
                    int count = 0;
                    int neighbours = 0;
                    if (y > 0) {
                        if (x > 0) {
                            if (off[pos - w - 1]) {
                                ++count;
                            }
                            ++neighbours;
                        }
                        if (off[pos - w]) {
                            ++count;
                        }
                        ++neighbours;
                        if (x < w - 1) {
                            if (off[pos - w + 1]) {
                                ++count;
                            }
                            ++neighbours;
                        }
                    }
                    if (x > 0) {
                        if (off[pos - 1]) {
                            ++count;
                        }
                        ++neighbours;
                    }
                    if (off[pos]) {
                        ++count;
                    }
                    ++neighbours;
                    if (x < w - 1) {
                        if (off[pos + 1]) {
                            ++count;
                        }
                        ++neighbours;
                    }
                    if (y < h - 1) {
                        if (x > 0) {
                            if (off[pos + w - 1]) {
                                ++count;
                            }
                            ++neighbours;
                        }
                        if (off[pos + w]) {
                            ++count;
                        }
                        ++neighbours;
                        if (x < w - 1) {
                            if (off[pos + w + 1]) {
                                ++count;
                            }
                            ++neighbours;
                        }
                    }
                    boolean bl = cur[pos] = 2 * count >= neighbours;
                    if (cur[pos] == off[pos]) continue;
                    fillDiff += cur[pos] ? 1 : -1;
                }
            }
            boolean[] tmp = cur;
            cur = off;
            off = tmp;
        }
        if (forceFillRate && Math.min(w, h) > 2) {
            boolean growing;
            int[] neighbours = new int[]{-w - 1, -w, -w + 1, -1, 0, 1, w - 1, w, w + 1};
            boolean bl = growing = fillDiff < 0;
            while (fillDiff != 0) {
                int cell;
                int tries = 0;
                while (off[cell = Random.Int(1, w - 1) + Random.Int(1, h - 1) * w] != growing && ++tries * 10 < length) {
                }
                for (int i2 : neighbours) {
                    if (fillDiff == 0 || off[cell + i2] == growing) continue;
                    off[cell + i2] = growing;
                    fillDiff += growing ? 1 : -1;
                }
            }
        }
        return off;
    }
}

