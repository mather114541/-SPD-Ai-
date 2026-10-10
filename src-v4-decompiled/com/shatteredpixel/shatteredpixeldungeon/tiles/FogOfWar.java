/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.badlogic.gdx.graphics.Pixmap;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTileSheet;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.Image;
import com.watabou.noosa.NoosaScript;
import com.watabou.noosa.NoosaScriptNoLighting;
import com.watabou.utils.Rect;
import java.util.ArrayList;

public class FogOfWar
extends Image {
    private static final int[][] FOG_COLORS = new int[][]{{0, 0, 0}, {-872415232, -1728053248, 0x55000000}, {-871292348, -1726401690, 1428309128}, {-16777216, -16777216, -16777216}};
    private static final int VISIBLE = 0;
    private static final int VISITED = 1;
    private static final int MAPPED = 2;
    private static final int INVISIBLE = 3;
    private int mapWidth;
    private int mapHeight;
    private int mapLength;
    private int pWidth;
    private int pHeight;
    private int width2;
    private int height2;
    private volatile ArrayList<Rect> toUpdate;
    private volatile ArrayList<Rect> updating;
    private static final int PIX_PER_TILE = 2;
    private boolean[] visible;
    private boolean[] visited;
    private boolean[] mapped;
    private int brightness;

    public FogOfWar(int mapWidth, int mapHeight) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.mapLength = mapHeight * mapWidth;
        this.pWidth = mapWidth * 2;
        this.pHeight = mapHeight * 2;
        this.width2 = 1;
        while (this.width2 < this.pWidth) {
            this.width2 <<= 1;
        }
        this.height2 = 1;
        while (this.height2 < this.pHeight) {
            this.height2 <<= 1;
        }
        float size = 8.0f;
        this.width = (float)this.width2 * size;
        this.height = (float)this.height2 * size;
        String key = "FogOfWar" + this.width2 + "x" + this.height2;
        this.texture(TextureCache.create(key, this.width2, this.height2));
        this.texture.bitmap.setColor(255);
        this.texture.bitmap.fill();
        this.texture.bind();
        this.scale.set(size, size);
        this.toUpdate = new ArrayList();
        this.toUpdate.add(new Rect(0, 0, mapWidth, mapHeight));
    }

    public synchronized void updateFog() {
        this.toUpdate.clear();
        this.toUpdate.add(new Rect(0, 0, this.mapWidth, this.mapHeight));
    }

    public synchronized void updateFog(Rect update) {
        for (Rect r : this.toUpdate.toArray(new Rect[0])) {
            if (r.intersect(update).isEmpty()) continue;
            this.toUpdate.remove(r);
            this.toUpdate.add(r.union(update));
            return;
        }
        this.toUpdate.add(update);
    }

    public synchronized void updateFog(int cell, int radius) {
        Rect update = new Rect(cell % this.mapWidth - radius, cell / this.mapWidth - radius, cell % this.mapWidth - radius + 1 + 2 * radius, cell / this.mapWidth - radius + 1 + 2 * radius);
        update.left = Math.max(0, update.left);
        update.top = Math.max(0, update.top);
        update.right = Math.min(this.mapWidth, update.right);
        update.bottom = Math.min(this.mapHeight, update.bottom);
        if (update.isEmpty()) {
            return;
        }
        this.updateFog(update);
    }

    public synchronized void updateFogArea(int x, int y, int w, int h) {
        this.updateFog(new Rect(x, y, x + w, y + h));
    }

    private synchronized void moveToUpdating() {
        this.updating = this.toUpdate;
        this.toUpdate = new ArrayList();
    }

    private void updateTexture(boolean[] visible, boolean[] visited, boolean[] mapped) {
        Rect update;
        this.visible = visible;
        this.visited = visited;
        this.mapped = mapped;
        this.brightness = SPDSettings.brightness() + 1;
        this.moveToUpdating();
        boolean fullUpdate = false;
        if (this.updating.size() == 1 && (update = this.updating.get(0)).height() == this.mapHeight && update.width() == this.mapWidth) {
            fullUpdate = true;
        }
        Pixmap fog = this.texture.bitmap;
        fog.setBlending(Pixmap.Blending.None);
        for (Rect update2 : this.updating) {
            for (int i = update2.top; i < update2.bottom; ++i) {
                int cell = this.mapWidth * i + update2.left;
                for (int j = update2.left; j < update2.right; ++j) {
                    if (cell >= Dungeon.level.length()) continue;
                    if (!Dungeon.level.discoverable[cell] || !visible[cell] && !visited[cell] && !mapped[cell]) {
                        if (fullUpdate) {
                            this.fillCell(fog, j, i, FOG_COLORS[3][this.brightness]);
                        }
                        ++cell;
                        continue;
                    }
                    if (this.wall(cell)) {
                        if (cell + this.mapWidth >= this.mapLength) {
                            this.fillCell(fog, j, i, FOG_COLORS[3][this.brightness]);
                        } else if (this.wall(cell + this.mapWidth)) {
                            if (cell % this.mapWidth != 0) {
                                if (this.wall(cell - 1)) {
                                    if (this.wall(cell + this.mapWidth - 1)) {
                                        this.fillLeft(fog, j, i, FOG_COLORS[3][this.brightness]);
                                    } else {
                                        this.fillLeft(fog, j, i, FOG_COLORS[Math.max(this.getCellFog(cell), Math.max(this.getCellFog(cell + this.mapWidth - 1), this.getCellFog(cell - 1)))][this.brightness]);
                                    }
                                } else {
                                    this.fillLeft(fog, j, i, FOG_COLORS[Math.max(this.getCellFog(cell), this.getCellFog(cell - 1))][this.brightness]);
                                }
                            } else {
                                this.fillLeft(fog, j, i, FOG_COLORS[3][this.brightness]);
                            }
                            if ((cell + 1) % this.mapWidth != 0) {
                                if (this.wall(cell + 1)) {
                                    if (this.wall(cell + this.mapWidth + 1)) {
                                        this.fillRight(fog, j, i, FOG_COLORS[3][this.brightness]);
                                    } else {
                                        this.fillRight(fog, j, i, FOG_COLORS[Math.max(this.getCellFog(cell), Math.max(this.getCellFog(cell + this.mapWidth + 1), this.getCellFog(cell + 1)))][this.brightness]);
                                    }
                                } else {
                                    this.fillRight(fog, j, i, FOG_COLORS[Math.max(this.getCellFog(cell), this.getCellFog(cell + 1))][this.brightness]);
                                }
                            } else {
                                this.fillRight(fog, j, i, FOG_COLORS[3][this.brightness]);
                            }
                        } else {
                            this.fillCell(fog, j, i, FOG_COLORS[Math.max(this.getCellFog(cell), this.getCellFog(cell + this.mapWidth))][this.brightness]);
                        }
                    } else {
                        this.fillCell(fog, j, i, FOG_COLORS[this.getCellFog(cell)][this.brightness]);
                    }
                    ++cell;
                }
            }
        }
        this.texture.bitmap(fog);
    }

    private boolean wall(int cell) {
        return DungeonTileSheet.wallStitcheable(Dungeon.level.map[cell]);
    }

    private int getCellFog(int cell) {
        if (this.visible[cell]) {
            return 0;
        }
        if (this.visited[cell]) {
            return 1;
        }
        if (this.mapped[cell]) {
            return 2;
        }
        return 3;
    }

    private void fillLeft(Pixmap fog, int x, int y, int color) {
        fog.setColor(color << 8 | color >>> 24);
        fog.fillRectangle(x * 2, y * 2, 1, 2);
    }

    private void fillRight(Pixmap fog, int x, int y, int color) {
        fog.setColor(color << 8 | color >>> 24);
        fog.fillRectangle(x * 2 + 1, y * 2, 1, 2);
    }

    private void fillCell(Pixmap fog, int x, int y, int color) {
        fog.setColor(color << 8 | color >>> 24);
        fog.fillRectangle(x * 2, y * 2, 2, 2);
    }

    @Override
    protected NoosaScript script() {
        return NoosaScriptNoLighting.get();
    }

    public static int rlFogDraw = 0;

    @Override
    public void draw() {
        if (!this.toUpdate.isEmpty()) {
            this.updateTexture(Dungeon.level.heroFOV, Dungeon.level.visited, Dungeon.level.mapped);
            if (rl.RLAutoPilot.active && (++rlFogDraw % 60) == 0) {
                int fov = 0, vis = 0;
                for (int i = 0; i < Dungeon.level.heroFOV.length; i++) { if (Dungeon.level.heroFOV[i]) fov++; if (Dungeon.level.visited[i]) vis++; }
                System.out.println("[GL] fogRebuild n=" + rlFogDraw + " fovCells=" + fov + " visitedCells=" + vis
                        + " heroPos=" + (Dungeon.hero == null ? -1 : Dungeon.hero.pos));
            }
        }
        super.draw();
    }

    @Override
    public void destroy() {
        super.destroy();
        if (this.texture != null) {
            TextureCache.remove(FogOfWar.class);
        }
    }
}

