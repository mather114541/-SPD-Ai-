/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.plants;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barkskin;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EarthParticle;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Bundle;

public class Earthroot
extends Plant {
    public Earthroot() {
        this.image = 8;
        this.seedClass = Seed.class;
    }

    @Override
    public void activate(Char ch) {
        if (ch != null) {
            if (ch instanceof Hero && ((Hero)ch).subClass == HeroSubClass.WARDEN) {
                Barkskin.conditionallyAppend(Dungeon.hero, Dungeon.hero.lvl + 5, 5);
            } else {
                Buff.affect(ch, Armor.class).level(ch.HT);
            }
        }
        if (Dungeon.level.heroFOV[this.pos]) {
            CellEmitter.bottom(this.pos).start(EarthParticle.FACTORY, 0.05f, 8);
            PixelScene.shake(1.0f, 0.4f);
        }
    }

    public static class Seed
    extends Plant.Seed {
        public Seed() {
            this.image = ItemSpriteSheet.SEED_EARTHROOT;
            this.plantClass = Earthroot.class;
            this.bones = true;
        }
    }

    public static class Armor
    extends Buff {
        private static final float STEP = 1.0f;
        private int pos;
        private int level;
        private static final String POS = "pos";
        private static final String LEVEL = "level";

        public Armor() {
            this.type = Buff.buffType.POSITIVE;
            this.announced = true;
        }

        @Override
        public boolean act() {
            if (this.target.pos != this.pos) {
                this.detach();
            }
            this.spend(1.0f);
            return true;
        }

        private static int blocking() {
            return (Dungeon.scalingDepth() + 5) / 2;
        }

        public int absorb(int damage) {
            if (this.pos != this.target.pos) {
                this.detach();
                return damage;
            }
            int block = Math.min(damage, Armor.blocking());
            if (this.level <= block) {
                this.detach();
                return damage - block;
            }
            this.level -= block;
            return damage - block;
        }

        public void level(int value) {
            if (this.target != null) {
                if (this.level < value) {
                    this.level = value;
                }
                this.pos = this.target.pos;
            }
        }

        @Override
        public int icon() {
            return 20;
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0.0f, (float)(this.target.HT - this.level) / (float)this.target.HT);
        }

        @Override
        public String iconTextDisplay() {
            return Integer.toString(this.level);
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", Armor.blocking(), this.level);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(POS, this.pos);
            bundle.put(LEVEL, this.level);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.pos = bundle.getInt(POS);
            this.level = bundle.getInt(LEVEL);
        }
    }
}

