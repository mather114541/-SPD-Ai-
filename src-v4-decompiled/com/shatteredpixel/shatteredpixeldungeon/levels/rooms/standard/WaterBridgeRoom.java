/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard;

import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardBridgeRoom;
import com.watabou.utils.Point;

public class WaterBridgeRoom
extends StandardBridgeRoom {
    @Override
    protected int maxBridgeWidth(int roomDimension) {
        return roomDimension >= 8 ? 3 : 2;
    }

    @Override
    protected int spaceTile() {
        return 29;
    }

    @Override
    public boolean canPlaceWater(Point p) {
        return false;
    }
}

