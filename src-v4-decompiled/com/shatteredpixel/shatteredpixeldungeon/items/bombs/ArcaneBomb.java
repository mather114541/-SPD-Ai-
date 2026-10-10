/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.bombs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GooSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class ArcaneBomb
extends Bomb {
    public ArcaneBomb() {
        this.image = ItemSpriteSheet.ARCANE_BOMB;
    }

    @Override
    public boolean explodesDestructively() {
        return false;
    }

    @Override
    protected int explosionRange() {
        return 2;
    }

    @Override
    protected Bomb.Fuse createFuse() {
        return new ArcaneBombFuse();
    }

    @Override
    public void explode(int cell) {
        super.explode(cell);
        ArrayList<Char> affected = new ArrayList<Char>();
        PathFinder.buildDistanceMap(cell, BArray.not(Dungeon.level.solid, null), this.explosionRange());
        for (int i = 0; i < PathFinder.distance.length; ++i) {
            if (PathFinder.distance[i] >= Integer.MAX_VALUE) continue;
            CellEmitter.get(i).burst(ElmoParticle.FACTORY, 10);
            Char ch = Actor.findChar(i);
            if (ch == null) continue;
            affected.add(ch);
        }
        for (Char ch : affected) {
            int damage = Math.round(Random.NormalIntRange(4 + Dungeon.scalingDepth(), 12 + 3 * Dungeon.scalingDepth()));
            ch.damage(damage, this);
            if (ch != Dungeon.hero || ch.isAlive()) continue;
            Badges.validateDeathFromFriendlyMagic();
            Dungeon.fail(this);
        }
    }

    @Override
    public int value() {
        return this.quantity * 50;
    }

    public static class ArcaneBombFuse
    extends Bomb.Fuse {
        private ArrayList<Emitter> gooWarnEmitters = new ArrayList();

        @Override
        public Bomb.Fuse ignite(final Bomb bomb) {
            super.ignite(bomb);
            Actor.add(new Actor(){
                {
                    this.actPriority = 100;
                }

                @Override
                protected boolean act() {
                    int bombPos = -1;
                    for (Heap heap : Dungeon.level.heaps.valueList()) {
                        if (!heap.items.contains(bomb)) continue;
                        bombPos = heap.pos;
                    }
                    if (bombPos != -1) {
                        PathFinder.buildDistanceMap(bombPos, BArray.not(Dungeon.level.solid, null), bomb.explosionRange());
                        for (int i = 0; i < PathFinder.distance.length; ++i) {
                            Emitter e;
                            if (PathFinder.distance[i] >= Integer.MAX_VALUE || (e = CellEmitter.get(i)) == null) continue;
                            e.pour(GooSprite.GooParticle.FACTORY, 0.03f);
                            gooWarnEmitters.add(e);
                        }
                    }
                    Actor.remove(this);
                    return true;
                }
            });
            return this;
        }

        @Override
        public void snuff() {
            super.snuff();
            for (Emitter e : this.gooWarnEmitters) {
                e.on = false;
            }
            this.gooWarnEmitters.clear();
        }
    }
}

