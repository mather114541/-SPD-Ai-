/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.tiles.custom;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.watabou.noosa.Tilemap;
import com.watabou.utils.Bundle;
import com.watabou.utils.SparseArray;

public class Carpet
extends CustomTilemap {
    protected SparseArray<Integer> tileOverrides;
    public static final int SKIP = -1;
    public static final int CITY_STATUE = 80;
    public static final int CITY_PEDESTAL = 81;
    public static final int CITY_ENTRANCE = 82;
    public static final int CITY_STATUE_TR = 83;
    public static final int CITY_STATUE_BR = 84;
    public static final int CITY_STATUE_TL = 85;
    public static final int CITY_STATUE_BL = 86;
    public static final int CITY_PEDESTAL_TR = 87;
    public static final int CITY_PEDESTAL_TL = 88;
    public static final String OVERRIDES_KEYS = "overrides_keys";
    public static final String OVERRIDES_VALUES = "overrides_values";

    public Carpet() {
        this.texture = "environment/custom_tiles/carpet.png";
        this.tileOverrides = new SparseArray();
    }

    public void overrideTile(int x, int y, int override) {
        this.tileOverrides.put(x + this.tileW * y, override);
    }

    public void overrideTile(Level level, int x, int y, int override) {
        this.tileOverrides.put((x -= this.tileX) + this.tileW * (y -= this.tileY), override);
    }

    public void overrideTile(int tile, Level level, int override) {
        int x = tile % level.width() - this.tileX;
        int y = tile / level.width() - this.tileY;
        this.tileOverrides.put(x + this.tileW * y, override);
    }

    @Override
    public Tilemap create() {
        Tilemap v = super.create();
        int[] data = new int[this.tileW * this.tileH];
        int regionOfs = 16 * ((Dungeon.depth - 1) / 5);
        int i = 0;
        for (int y = 0; y < this.tileH; ++y) {
            for (int x = 0; x < this.tileW; ++x) {
                if (this.tileOverrides.containsKey(i)) {
                    data[i] = (Integer)this.tileOverrides.get(i);
                } else {
                    data[i] = regionOfs;
                    if (y == 0) {
                        int n = i;
                        data[n] = data[n] + 1;
                    }
                    if (x == this.tileW - 1) {
                        int n = i;
                        data[n] = data[n] + 2;
                    }
                    if (y == this.tileH - 1) {
                        int n = i;
                        data[n] = data[n] + 4;
                    }
                    if (x == 0) {
                        int n = i;
                        data[n] = data[n] + 8;
                    }
                }
                ++i;
            }
        }
        v.map(data, this.tileW);
        return v;
    }

    @Override
    public String name(int tileX, int tileY) {
        int cell = tileX + this.tileW * tileY;
        if (this.tileOverrides.containsKey(cell)) {
            return null;
        }
        return Messages.get(this, "name", new Object[0]);
    }

    @Override
    public String desc(int tileX, int tileY) {
        int cell = tileX + this.tileW * tileY;
        if (this.tileOverrides.containsKey(cell)) {
            return null;
        }
        return Messages.get(this, "desc", new Object[0]);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        int[] keys = this.tileOverrides.keyArray();
        int[] values = new int[keys.length];
        for (int i = 0; i < keys.length; ++i) {
            values[i] = (Integer)this.tileOverrides.get(keys[i]);
        }
        bundle.put(OVERRIDES_KEYS, keys);
        bundle.put(OVERRIDES_VALUES, values);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.tileOverrides.clear();
        int[] keys = bundle.getIntArray(OVERRIDES_KEYS);
        int[] values = bundle.getIntArray(OVERRIDES_VALUES);
        for (int i = 0; i < keys.length; ++i) {
            this.tileOverrides.put(keys[i], values[i]);
        }
    }
}

