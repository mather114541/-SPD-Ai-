/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTileSheet;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.Tilemap;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;

public abstract class DungeonTilemap
extends Tilemap {
    public static final int SIZE = 16;
    protected int[] map;

    public DungeonTilemap(String tex) {
        super(tex, new TextureFilm(tex, 16, 16));
    }

    @Override
    public void map(int[] data, int cols) {
        this.map = data;
        super.map(new int[data.length], cols);
    }

    @Override
    public synchronized void updateMap() {
        for (int i = 0; i < this.data.length; ++i) {
            this.data[i] = this.getTileVisual(i, this.map[i], false);
        }
        super.updateMap();
    }

    @Override
    public synchronized void updateMapCell(int cell) {
        if (Dungeon.level.insideMap(cell)) {
            for (int i : PathFinder.NEIGHBOURS9) {
                this.data[cell + i] = this.getTileVisual(cell + i, this.map[cell + i], false);
            }
            super.updateMapCell(cell - this.mapWidth - 1);
            super.updateMapCell(cell + this.mapWidth + 1);
        } else {
            this.data[cell] = this.getTileVisual(cell, this.map[cell], false);
            super.updateMapCell(cell);
        }
    }

    protected abstract int getTileVisual(int var1, int var2, boolean var3);

    public int screenToTile(int x, int y) {
        return this.screenToTile(x, y, false);
    }

    public int screenToTile(int x, int y, boolean wallAssist) {
        PointF p = this.camera().screenToCamera(x, y).offset(this.point().negate()).invScale(16.0f);
        p.x = GameMath.gate(0.0f, p.x, (float)Dungeon.level.width() - 0.001f);
        p.y = GameMath.gate(0.0f, p.y, (float)Dungeon.level.height() - 0.001f);
        int cell = (int)p.x + (int)p.y * Dungeon.level.width();
        if (wallAssist && this.map != null && this.isWallAssistable(cell) && cell + this.mapWidth < this.size && p.y % 1.0f >= 0.75f && !this.isWallAssistable(cell + this.mapWidth)) {
            cell += this.mapWidth;
        }
        return cell;
    }

    private boolean isWallAssistable(int cell) {
        if (this.map == null || cell >= this.size) {
            return false;
        }
        if (DungeonTileSheet.wallStitcheable(this.map[cell])) {
            return true;
        }
        return Dungeon.depth >= 10 && Dungeon.depth <= 15 && (this.map[cell] == 33 || this.map[cell] == 34);
    }

    @Override
    public boolean overlapsPoint(float x, float y) {
        return true;
    }

    public void discover(int pos, int oldValue) {
        int visual = this.getTileVisual(pos, oldValue, false);
        if (visual < 0) {
            return;
        }
        final Image tile = new Image(this.texture);
        tile.frame(this.tileset.get(this.getTileVisual(pos, oldValue, false)));
        tile.point(DungeonTilemap.tileToWorld(pos));
        this.parent.add(tile);
        this.parent.add(new AlphaTweener(tile, 0.0f, 0.6f){

            @Override
            protected void onComplete() {
                tile.killAndErase();
                this.killAndErase();
            }
        });
    }

    public static PointF tileToWorld(int pos) {
        return new PointF(pos % Dungeon.level.width(), pos / Dungeon.level.width()).scale(16.0f);
    }

    public static PointF tileCenterToWorld(int pos) {
        return new PointF(((float)(pos % Dungeon.level.width()) + 0.5f) * 16.0f, ((float)(pos / Dungeon.level.width()) + 0.5f) * 16.0f);
    }

    public static PointF raisedTileCenterToWorld(int pos) {
        return new PointF(((float)(pos % Dungeon.level.width()) + 0.5f) * 16.0f, ((float)(pos / Dungeon.level.width()) + 0.1f) * 16.0f);
    }

    public static int worldToTile(float x, float y, int width) {
        return (int)(x / 16.0f) + (int)(y / 16.0f) * width;
    }

    @Override
    public boolean overlapsScreenPoint(int x, int y) {
        return true;
    }
}

