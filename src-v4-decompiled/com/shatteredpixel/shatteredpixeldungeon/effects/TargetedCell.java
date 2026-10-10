/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.SparseArray;

public class TargetedCell
extends Image
implements Bundlable {
    public int pos;
    public float time;
    private float alpha;
    public static SparseArray<TargetedCell> cells = new SparseArray();
    private static final String POS = "pos";
    private static final String COLOR = "color";
    private static final String TIME = "time";

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void reset(int pos, float time) {
        this.copy(Icons.get(Icons.TARGET));
        this.origin.set(this.width / 2.0f);
        this.camera = null;
        this.pos = pos;
        this.point(DungeonTilemap.tileToWorld(pos));
        this.hardlight(0xFF0000);
        this.alpha = 1.0f;
        this.time = time;
        this.alpha(1.0f);
        this.scale.set(1.0f);
        SparseArray<TargetedCell> sparseArray = cells;
        synchronized (sparseArray) {
            cells.put(pos, this);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void update() {
        this.alpha -= Game.elapsed;
        if (this.time >= Actor.now()) {
            this.alpha = Math.max(this.alpha, 0.6f);
        }
        if (this.alpha > 0.0f) {
            this.alpha(this.alpha);
            this.scale.set((float)Math.pow(this.alpha, 0.33f));
        } else {
            this.time = 0.0f;
            SparseArray<TargetedCell> sparseArray = cells;
            synchronized (sparseArray) {
                cells.remove(this.pos);
            }
            this.killAndErase();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void fixTime(float min) {
        SparseArray<TargetedCell> sparseArray = cells;
        synchronized (sparseArray) {
            for (TargetedCell c : cells.valueList()) {
                c.time -= min;
            }
        }
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(POS, this.pos);
        bundle.put(TIME, this.time);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        this.pos = bundle.getInt(POS);
        this.time = bundle.getInt(TIME);
    }
}

