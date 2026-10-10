/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard;

import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardBridgeRoom;

public class ChasmBridgeRoom
extends StandardBridgeRoom {
    @Override
    protected int maxBridgeWidth(int roomDimension) {
        return roomDimension >= 7 ? 2 : 1;
    }

    @Override
    protected int spaceTile() {
        return 0;
    }
}

