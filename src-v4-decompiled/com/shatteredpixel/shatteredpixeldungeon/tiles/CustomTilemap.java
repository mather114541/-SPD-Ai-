/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.watabou.noosa.Image;
import com.watabou.noosa.NoosaScript;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.Tilemap;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

public abstract class CustomTilemap
implements Bundlable {
    protected static final int SIZE = 16;
    public int tileX;
    public int tileY;
    public int tileW = 1;
    public int tileH = 1;
    protected Object texture;
    protected Tilemap vis = null;
    private static final String TILE_X = "tileX";
    private static final String TILE_Y = "tileY";
    private static final String TILE_W = "tileW";
    private static final String TILE_H = "tileH";

    public void pos(int pos) {
        this.pos(pos, Dungeon.level);
    }

    public void pos(int pos, Level level) {
        this.pos(pos % level.width(), pos / level.width());
    }

    public void pos(int tileX, int tileY) {
        this.tileX = tileX;
        this.tileY = tileY;
    }

    public void setRect(int topLeft, int bottomRight) {
        this.setRect(topLeft % Dungeon.level.width(), topLeft / Dungeon.level.width(), bottomRight % Dungeon.level.width() - topLeft % Dungeon.level.width(), bottomRight / Dungeon.level.width() - topLeft / Dungeon.level.width());
    }

    public void setRect(int tileX, int tileY, int tileW, int tileH) {
        this.tileX = tileX;
        this.tileY = tileY;
        this.tileW = tileW;
        this.tileH = tileH;
    }

    protected int[] mapSimpleImage(int txX, int txY, int texW) {
        int[] data = new int[this.tileW * this.tileH];
        int texTileWidth = texW / 16;
        int x = txX;
        int y = txY;
        for (int i = 0; i < data.length; ++i) {
            data[i] = x + texTileWidth * y;
            if (++x - txX != this.tileW) continue;
            x = txX;
            ++y;
        }
        return data;
    }

    public Tilemap create() {
        if (this.vis != null && this.vis.alive) {
            this.vis.killAndErase();
        }
        this.vis = new Tilemap(this.texture, new TextureFilm(this.texture, 16, 16)){

            @Override
            protected NoosaScript script() {
                return NoosaScript.get();
            }
        };
        this.vis.x = this.tileX * 16;
        this.vis.y = this.tileY * 16;
        return this.vis;
    }

    public boolean allowWater(int tileX, int tileY) {
        return this.image(tileX, tileY) == null;
    }

    public Image image(int tileX, int tileY) {
        if (this.vis == null) {
            return null;
        }
        return this.vis.image(tileX, tileY);
    }

    public String name(int tileX, int tileY) {
        return null;
    }

    public String desc(int tileX, int tileY) {
        return null;
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        this.tileX = bundle.getInt(TILE_X);
        this.tileY = bundle.getInt(TILE_Y);
        this.tileW = bundle.getInt(TILE_W);
        this.tileH = bundle.getInt(TILE_H);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(TILE_X, this.tileX);
        bundle.put(TILE_Y, this.tileY);
        bundle.put(TILE_W, this.tileW);
        bundle.put(TILE_H, this.tileH);
    }
}

