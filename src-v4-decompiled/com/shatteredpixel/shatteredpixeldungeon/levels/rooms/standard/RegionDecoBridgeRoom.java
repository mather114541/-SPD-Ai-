/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard;

import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardBridgeRoom;

public class RegionDecoBridgeRoom
extends StandardBridgeRoom {
    @Override
    public float[] sizeCatProbs() {
        return new float[]{2.0f, 1.0f, 0.0f};
    }

    @Override
    protected int maxBridgeWidth(int roomDimension) {
        return 1;
    }

    @Override
    protected int spaceTile() {
        return 34;
    }

    @Override
    protected int bridgeTile() {
        return 14;
    }
}

