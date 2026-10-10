/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.bombs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class Noisemaker
extends Bomb {
    public Noisemaker() {
        this.image = ItemSpriteSheet.NOISEMAKER;
    }

    @Override
    protected int explosionRange() {
        return 2;
    }

    @Override
    protected Bomb.Fuse createFuse() {
        return new NoisemakerFuse();
    }

    @Override
    public boolean doPickUp(Hero hero, int pos) {
        if (this.fuse instanceof NoisemakerFuse && ((NoisemakerFuse)this.fuse).triggered) {
            return false;
        }
        return super.doPickUp(hero, pos);
    }

    @Override
    public int value() {
        return this.quantity * 60;
    }

    public static class NoisemakerFuse
    extends Bomb.Fuse {
        private boolean triggered = false;
        private int left;
        private static final String LEFT = "left";
        private static final String TRIGGERED = "triggered";

        @Override
        protected boolean act() {
            if (!this.triggered) {
                return super.act();
            }
            for (Heap heap : Dungeon.level.heaps.valueList()) {
                if (!heap.items.contains(this.bomb)) continue;
                if (this.bomb.fuse != this) {
                    this.trigger(heap);
                } else if (Actor.findChar(heap.pos) != null) {
                    heap.items.remove(this.bomb);
                    if (heap.items.isEmpty()) {
                        heap.destroy();
                    }
                    this.trigger(heap);
                } else {
                    this.spend(1.0f);
                    --this.left;
                    if (this.left <= 0) {
                        CellEmitter.center(heap.pos).start(Speck.factory(5), 0.3f, 3);
                        Sample.INSTANCE.play("sounds/alert.mp3");
                        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
                            mob.beckon(heap.pos);
                        }
                        this.left = 6;
                    }
                }
                return true;
            }
            this.bomb.fuse = null;
            Actor.remove(this);
            return true;
        }

        @Override
        protected void trigger(Heap heap) {
            if (!this.triggered) {
                this.triggered = true;
            } else {
                super.trigger(heap);
            }
        }

        @Override
        public boolean freeze() {
            if (!this.triggered) {
                return super.freeze();
            }
            return false;
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(LEFT, this.left);
            bundle.put(TRIGGERED, this.triggered);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.left = bundle.getInt(LEFT);
            this.triggered = bundle.getBoolean(TRIGGERED);
        }
    }
}

