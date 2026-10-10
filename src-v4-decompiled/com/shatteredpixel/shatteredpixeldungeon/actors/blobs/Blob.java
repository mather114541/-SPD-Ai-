/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.watabou.utils.Bundle;
import com.watabou.utils.Rect;
import com.watabou.utils.Reflection;
import java.util.ArrayList;

public class Blob
extends Actor {
    public int volume;
    public int[] cur;
    protected int[] off;
    public BlobEmitter emitter;
    public Rect area;
    public boolean alwaysVisible;
    private static final String CUR = "cur";
    private static final String START = "start";
    private static final String LENGTH = "length";
    protected ArrayList<Integer> cellsToFlagUpdate;

    public Blob() {
        this.actPriority = -10;
        this.volume = 0;
        this.area = new Rect();
        this.alwaysVisible = false;
        this.cellsToFlagUpdate = new ArrayList();
        // headless: guarantee a non-null emitter so evolve()/use()-style logic
        // that pours particles never NPEs (render loop never calls BlobEmitter.emit)
        if (rl.RLMode.headless) {
            this.emitter = new rl.RLNoopEmitter(this);
        }
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        if (this.volume > 0) {
            int end;
            int start;
            for (start = 0; start < Dungeon.level.length() && this.cur[start] <= 0; ++start) {
            }
            for (end = Dungeon.level.length() - 1; end > start && this.cur[end] <= 0; --end) {
            }
            bundle.put(START, start);
            bundle.put(LENGTH, this.cur.length);
            bundle.put(CUR, this.trim(start, end + 1));
        }
    }

    private int[] trim(int start, int end) {
        int len = end - start;
        int[] copy = new int[len];
        System.arraycopy(this.cur, start, copy, 0, len);
        return copy;
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if (bundle.contains(CUR)) {
            this.cur = new int[bundle.getInt(LENGTH)];
            this.off = new int[this.cur.length];
            int[] data = bundle.getIntArray(CUR);
            int start = bundle.getInt(START);
            for (int i = 0; i < data.length; ++i) {
                this.cur[i + start] = data[i];
                this.volume += data[i];
            }
        }
    }

    @Override
    public boolean act() {
        this.spend(1.0f);
        if (this.volume > 0) {
            if (this.area.isEmpty()) {
                this.setupArea();
            }
            this.volume = 0;
            this.evolve();
            int[] tmp = this.off;
            this.off = this.cur;
            this.cur = tmp;
            for (int i : this.cellsToFlagUpdate) {
                Dungeon.level.updateCellFlags(i);
            }
            this.cellsToFlagUpdate.clear();
        } else if (!this.area.isEmpty()) {
            this.area.setEmpty();
            System.arraycopy(this.cur, 0, this.off, 0, this.cur.length);
        }
        return true;
    }

    public void setupArea() {
        for (int cell = 0; cell < this.cur.length; ++cell) {
            if (this.cur[cell] == 0) continue;
            this.area.union(cell % Dungeon.level.width(), cell / Dungeon.level.width());
        }
    }

    public void use(BlobEmitter emitter) {
        this.emitter = emitter;
    }

    protected void evolve() {
        boolean[] blocking = Dungeon.level.solid;
        for (int i = this.area.top - 1; i <= this.area.bottom; ++i) {
            for (int j = this.area.left - 1; j <= this.area.right; ++j) {
                int cell = j + i * Dungeon.level.width();
                if (!Dungeon.level.insideMap(cell)) continue;
                if (!blocking[cell]) {
                    int value;
                    int count = 1;
                    int sum = this.cur[cell];
                    if (j > this.area.left && !blocking[cell - 1]) {
                        sum += this.cur[cell - 1];
                        ++count;
                    }
                    if (j < this.area.right && !blocking[cell + 1]) {
                        sum += this.cur[cell + 1];
                        ++count;
                    }
                    if (i > this.area.top && !blocking[cell - Dungeon.level.width()]) {
                        sum += this.cur[cell - Dungeon.level.width()];
                        ++count;
                    }
                    if (i < this.area.bottom && !blocking[cell + Dungeon.level.width()]) {
                        sum += this.cur[cell + Dungeon.level.width()];
                        ++count;
                    }
                    this.off[cell] = value = sum >= count ? sum / count - 1 : 0;
                    if (value > 0) {
                        if (i < this.area.top) {
                            this.area.top = i;
                        } else if (i >= this.area.bottom) {
                            this.area.bottom = i + 1;
                        }
                        if (j < this.area.left) {
                            this.area.left = j;
                        } else if (j >= this.area.right) {
                            this.area.right = j + 1;
                        }
                    }
                    this.volume += value;
                    continue;
                }
                this.off[cell] = 0;
            }
        }
    }

    public void seed(Level level, int cell, int amount) {
        if (this.cur == null) {
            this.cur = new int[level.length()];
        }
        if (this.off == null) {
            this.off = new int[this.cur.length];
        }
        int n = cell;
        this.cur[n] = this.cur[n] + amount;
        this.volume += amount;
        this.area.union(cell % level.width(), cell / level.width());
    }

    public void clear(int cell) {
        if (this.volume == 0) {
            return;
        }
        this.volume -= this.cur[cell];
        this.cur[cell] = 0;
    }

    public void fullyClear() {
        this.volume = 0;
        this.area.setEmpty();
        this.cur = new int[Dungeon.level.length()];
        this.off = new int[Dungeon.level.length()];
    }

    public void onBuildFlagMaps(Level l) {
    }

    public void onUpdateCellFlags(Level l, int cell) {
    }

    public Notes.Landmark landmark() {
        return null;
    }

    public String tileDesc() {
        return null;
    }

    public static <T extends Blob> T seed(int cell, int amount, Class<T> type) {
        return Blob.seed(cell, amount, type, Dungeon.level);
    }

    public static <T extends Blob> T seed(int cell, int amount, Class<T> type, Level level) {
        Blob gas = level.blobs.get(type);
        if (gas == null) {
            gas = (Blob)Reflection.newInstance(type);
            if (Actor.curActorPriority() < gas.actPriority) {
                gas.spend(1.0f);
            }
        }
        if (gas != null) {
            level.blobs.put(type, gas);
            gas.seed(level, cell, amount);
        }
        return (T)gas;
    }

    public static int volumeAt(int cell, Class<? extends Blob> type) {
        Blob gas = Dungeon.level.blobs.get(type);
        if (gas == null || gas.volume == 0) {
            return 0;
        }
        return gas.cur[cell];
    }
}

