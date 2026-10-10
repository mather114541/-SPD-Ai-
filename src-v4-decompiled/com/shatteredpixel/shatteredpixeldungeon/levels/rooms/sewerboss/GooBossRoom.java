/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.sewerboss;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.sewerboss.DiamondGooRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.sewerboss.ThickPillarsGooRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.sewerboss.ThinPillarsGooRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.sewerboss.WalledGooRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.watabou.noosa.Image;
import com.watabou.noosa.Tilemap;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public abstract class GooBossRoom
extends StandardRoom {
    @Override
    public float[] sizeCatProbs() {
        return new float[]{0.0f, 1.0f, 0.0f};
    }

    @Override
    public boolean canMerge(Level l, Room other, Point p, int mergeTerrain) {
        return false;
    }

    public static GooBossRoom randomGooRoom() {
        switch (Random.Int(4)) {
            default: {
                return new DiamondGooRoom();
            }
            case 1: {
                return new WalledGooRoom();
            }
            case 2: {
                return new ThinPillarsGooRoom();
            }
            case 3: 
        }
        return new ThickPillarsGooRoom();
    }

    protected void setupGooNest(Level level) {
        GooNest nest = new GooNest();
        nest.setRect(this.left + this.width() / 2 - 2, this.top + this.height() / 2 - 2, 4 + this.width() % 2, 4 + this.height() % 2);
        level.customTiles.add(nest);
    }

    public static class GooNest
    extends CustomTilemap {
        public GooNest() {
            this.texture = "environment/custom_tiles/sewer_boss.png";
        }

        @Override
        public Tilemap create() {
            Tilemap v = super.create();
            int[] data = new int[this.tileW * this.tileH];
            for (int x = 0; x < this.tileW; ++x) {
                for (int y = 0; y < this.tileH; ++y) {
                    data[x + this.tileW * y] = !(x != 0 && x != this.tileW - 1 || y != 0 && y != this.tileH - 1) ? -1 : (x == 1 && y == 0 || x == 0 && y == 1 ? 0 : (x == this.tileW - 2 && y == 0 || x == this.tileW - 1 && y == 1 ? 1 : (x == 1 && y == this.tileH - 1 || x == 0 && y == this.tileH - 2 ? 2 : (x == this.tileW - 2 && y == this.tileH - 1 || x == this.tileW - 1 && y == this.tileH - 2 ? 3 : (x == 0 ? 4 : (y == 0 ? 5 : (x == this.tileW - 1 ? 6 : (y == this.tileH - 1 ? 7 : 8))))))));
                }
            }
            v.map(data, this.tileW);
            return v;
        }

        @Override
        public Image image(int tileX, int tileY) {
            return null;
        }
    }
}

