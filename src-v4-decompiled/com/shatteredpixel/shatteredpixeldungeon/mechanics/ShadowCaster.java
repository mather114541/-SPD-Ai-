/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.mechanics;

import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.watabou.utils.BArray;

public final class ShadowCaster {
    public static final int MAX_DISTANCE = 20;
    public static int[][] rounding = new int[21][];

    public static void castShadow(int x, int y, int w, boolean[] fieldOfView, boolean[] blocking, int distance) {
        if (distance >= 20) {
            distance = 20;
        }
        BArray.setFalse(fieldOfView);
        fieldOfView[y * w + x] = true;
        try {
            ShadowCaster.scanOctant(distance, fieldOfView, blocking, 1, x, y, w, 0.0, 1.0, 1, -1, false);
            ShadowCaster.scanOctant(distance, fieldOfView, blocking, 1, x, y, w, 0.0, 1.0, -1, 1, true);
            ShadowCaster.scanOctant(distance, fieldOfView, blocking, 1, x, y, w, 0.0, 1.0, 1, 1, true);
            ShadowCaster.scanOctant(distance, fieldOfView, blocking, 1, x, y, w, 0.0, 1.0, 1, 1, false);
            ShadowCaster.scanOctant(distance, fieldOfView, blocking, 1, x, y, w, 0.0, 1.0, -1, 1, false);
            ShadowCaster.scanOctant(distance, fieldOfView, blocking, 1, x, y, w, 0.0, 1.0, 1, -1, true);
            ShadowCaster.scanOctant(distance, fieldOfView, blocking, 1, x, y, w, 0.0, 1.0, -1, -1, true);
            ShadowCaster.scanOctant(distance, fieldOfView, blocking, 1, x, y, w, 0.0, 1.0, -1, -1, false);
        }
        catch (Exception e) {
            ShatteredPixelDungeon.reportException(e);
            BArray.setFalse(fieldOfView);
        }
    }

    private static void scanOctant(int distance, boolean[] fov, boolean[] blocking, int row, int x, int y, int w, double lSlope, double rSlope, int mX, int mY, boolean mXY) {
        int[] roundingAtDist;
        boolean inBlocking = false;
        if (distance == 2) {
            roundingAtDist = (int[])rounding[distance].clone();
            roundingAtDist[2] = 2;
        } else {
            roundingAtDist = rounding[distance];
        }
        while (row <= distance) {
            if (rSlope < lSlope) {
                return;
            }
            int start = lSlope == 0.0 ? 0 : (int)Math.floor(((double)row - 0.5) * lSlope + 0.499);
            int end = rSlope == 1.0 ? roundingAtDist[row] : Math.min(roundingAtDist[row], (int)Math.ceil(((double)row + 0.5) * rSlope - 0.499));
            int cell = x + y * w;
            cell = mXY ? (cell += mX * start * w + mY * row) : (cell += mX * start + mY * row * w);
            for (int col = start; !(col > end || col == end && inBlocking && (int)Math.ceil(((double)row - 0.5) * rSlope - 0.499) != end); ++col) {
                fov[cell] = true;
                if (blocking[cell]) {
                    if (!inBlocking) {
                        inBlocking = true;
                        if (col != start) {
                            ShadowCaster.scanOctant(distance, fov, blocking, row + 1, x, y, w, lSlope, ((double)col - 0.5) / ((double)row + 0.5), mX, mY, mXY);
                        }
                    }
                } else if (inBlocking) {
                    inBlocking = false;
                    lSlope = ((double)col - 0.5) / ((double)row - 0.5);
                }
                if (!mXY) {
                    cell += mX;
                    continue;
                }
                cell += mX * w;
            }
            if (inBlocking) {
                return;
            }
            ++row;
        }
    }

    static {
        for (int i = 1; i <= 20; ++i) {
            ShadowCaster.rounding[i] = new int[i + 1];
            for (int j = 1; j <= i; ++j) {
                ShadowCaster.rounding[i][j] = (int)Math.min((long)j, Math.round((double)i * Math.cos(Math.asin((double)j / ((double)i + 0.5)))));
            }
        }
    }
}

