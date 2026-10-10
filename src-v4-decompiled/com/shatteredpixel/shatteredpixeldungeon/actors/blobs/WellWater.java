/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.WaterOfAwareness;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.WaterOfHealth;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public abstract class WellWater
extends Blob {
    public WellWater() {
        this.alwaysVisible = true;
    }

    @Override
    protected void evolve() {
        boolean seen = false;
        for (int i = this.area.top - 1; i <= this.area.bottom; ++i) {
            for (int j = this.area.left - 1; j <= this.area.right; ++j) {
                int cell = j + i * Dungeon.level.width();
                if (!Dungeon.level.insideMap(cell)) continue;
                this.off[cell] = this.cur[cell];
                this.volume += this.off[cell];
            }
        }
    }

    protected boolean affect(int pos) {
        if (pos == Dungeon.hero.pos && this.affectHero(Dungeon.hero)) {
            this.clear(pos);
            return true;
        }
        Heap heap = (Heap)Dungeon.level.heaps.get(pos);
        if (heap != null) {
            int newPlace;
            Item oldItem = heap.peek();
            Item newItem = this.affectItem(oldItem, pos);
            if (newItem != null) {
                if (newItem != oldItem) {
                    if (oldItem.quantity() > 1) {
                        oldItem.quantity(oldItem.quantity() - 1);
                        heap.drop(newItem);
                    } else {
                        heap.replace(oldItem, newItem);
                    }
                }
                heap.sprite.link();
                this.clear(pos);
                return true;
            }
            while (!Dungeon.level.passable[newPlace = pos + PathFinder.NEIGHBOURS8[Random.Int(8)]] && !Dungeon.level.avoid[newPlace]) {
            }
            Dungeon.level.drop((Item)heap.pickUp(), (int)newPlace).sprite.drop(pos);
            return false;
        }
        return false;
    }

    protected abstract boolean affectHero(Hero var1);

    protected abstract Item affectItem(Item var1, int var2);

    public static void affectCell(int cell) {
        Class[] waters;
        for (Class waterClass : waters = new Class[]{WaterOfHealth.class, WaterOfAwareness.class}) {
            WellWater water = (WellWater)Dungeon.level.blobs.get(waterClass);
            if (water == null || water.volume <= 0 || water.cur[cell] <= 0 || !water.affect(cell)) continue;
            Level.set(cell, 3);
            GameScene.updateMap(cell);
            if (water.landmark() != null) {
                if (water.volume <= 0) {
                    Notes.remove(water.landmark());
                } else {
                    boolean removing = true;
                    for (int i = 0; i < water.cur.length; ++i) {
                        if (water.cur[i] <= 0 || !Dungeon.level.visited[i]) continue;
                        removing = false;
                        break;
                    }
                    if (removing) {
                        Notes.remove(water.landmark());
                    }
                }
            }
            return;
        }
    }
}

