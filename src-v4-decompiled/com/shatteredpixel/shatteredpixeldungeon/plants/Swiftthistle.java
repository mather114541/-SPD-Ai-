/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.plants;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Haste;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.plants.Rotberry;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Image;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;
import java.util.ArrayList;
import java.util.Iterator;

public class Swiftthistle
extends Plant {
    public Swiftthistle() {
        this.image = 2;
        this.seedClass = Seed.class;
    }

    @Override
    public void activate(Char ch) {
        if (ch != null) {
            Buff.affect(ch, TimeBubble.class).reset();
            if (ch instanceof Hero && ((Hero)ch).subClass == HeroSubClass.WARDEN) {
                Buff.affect(ch, Haste.class, 1.0f);
            }
        }
    }

    public static class Seed
    extends Plant.Seed {
        public Seed() {
            this.image = ItemSpriteSheet.SEED_SWIFTTHISTLE;
            this.plantClass = Swiftthistle.class;
        }
    }

    public static class TimeBubble
    extends Buff {
        private float left;
        ArrayList<Integer> presses;
        private static final String PRESSES = "presses";
        private static final String LEFT = "left";

        public TimeBubble() {
            this.type = Buff.buffType.POSITIVE;
            this.announced = true;
            this.presses = new ArrayList();
        }

        @Override
        public int icon() {
            return 7;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(1.0f, 1.0f, 0.0f);
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0.0f, (6.0f - this.left) / 6.0f);
        }

        @Override
        public String iconTextDisplay() {
            return Integer.toString((int)(this.left + 0.001f));
        }

        public void reset() {
            this.reset(6);
        }

        public void reset(int turns) {
            this.left = turns + 1;
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", this.dispTurns(Math.max(0.0f, this.left)));
        }

        public void processTime(float time) {
            this.left -= time;
            if (this.left < -0.001f) {
                this.detach();
            }
        }

        public void setDelayedPress(int cell) {
            if (!this.presses.contains(cell)) {
                this.presses.add(cell);
            }
        }

        public void triggerPresses() {
            final ArrayList<Integer> toTrigger = this.presses;
            this.presses = new ArrayList();
            Actor.add(new Actor(){
                {
                    this.actPriority = 100;
                }

                @Override
                protected boolean act() {
                    Iterator iterator = toTrigger.iterator();
                    while (iterator.hasNext()) {
                        Trap t;
                        int cell = (Integer)iterator.next();
                        Plant p = (Plant)Dungeon.level.plants.get(cell);
                        if (p != null) {
                            p.trigger();
                        }
                        if ((t = (Trap)Dungeon.level.traps.get(cell)) == null) continue;
                        t.trigger();
                    }
                    Actor.remove(this);
                    return true;
                }
            });
        }

        public void disarmPresses() {
            for (int cell : this.presses) {
                Trap t;
                Plant p = (Plant)Dungeon.level.plants.get(cell);
                if (p != null && !(p instanceof Rotberry)) {
                    Dungeon.level.uproot(cell);
                }
                if ((t = (Trap)Dungeon.level.traps.get(cell)) == null || !t.disarmedByActivation) continue;
                t.disarm();
            }
            this.presses = new ArrayList();
        }

        @Override
        public void detach() {
            super.detach();
            this.triggerPresses();
            this.target.next();
        }

        @Override
        public void fx(boolean on) {
            if (!(this.target instanceof Hero)) {
                return;
            }
            Emitter.freezeEmitters = on;
            if (on) {
                for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
                    if (mob.sprite == null) continue;
                    mob.sprite.add(CharSprite.State.PARALYSED);
                }
            } else {
                for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
                    if (mob.paralysed > 0) continue;
                    mob.sprite.remove(CharSprite.State.PARALYSED);
                }
            }
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            int[] values = new int[this.presses.size()];
            for (int i = 0; i < values.length; ++i) {
                values[i] = this.presses.get(i);
            }
            bundle.put(PRESSES, values);
            bundle.put(LEFT, this.left);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            int[] values;
            super.restoreFromBundle(bundle);
            for (int value : values = bundle.getIntArray(PRESSES)) {
                this.presses.add(value);
            }
            this.left = bundle.getFloat(LEFT);
        }
    }
}

