/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.plants;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Healing;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShaftParticle;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Bundle;

public class Sungrass
extends Plant {
    public Sungrass() {
        this.image = 3;
        this.seedClass = Seed.class;
    }

    @Override
    public void activate(Char ch) {
        if (ch != null) {
            if (ch instanceof Hero && ((Hero)ch).subClass == HeroSubClass.WARDEN) {
                Buff.affect(ch, Healing.class).setHeal(ch.HT, 0.0f, 1);
            } else {
                Buff.affect(ch, Health.class).boost(ch.HT);
            }
        }
        if (Dungeon.level.heroFOV[this.pos]) {
            CellEmitter.get(this.pos).start(ShaftParticle.FACTORY, 0.2f, 3);
        }
    }

    public static class Seed
    extends Plant.Seed {
        public Seed() {
            this.image = ItemSpriteSheet.SEED_SUNGRASS;
            this.plantClass = Sungrass.class;
            this.bones = true;
        }
    }

    public static class Health
    extends Buff {
        private static final float STEP = 1.0f;
        private int pos;
        private float partialHeal;
        private int level;
        private static final String POS = "pos";
        private static final String PARTIAL = "partial_heal";
        private static final String LEVEL = "level";

        public Health() {
            this.type = Buff.buffType.POSITIVE;
            this.announced = true;
        }

        @Override
        public boolean act() {
            if (this.target.pos != this.pos) {
                this.detach();
            }
            this.partialHeal += (float)(40 + this.target.HT) / 150.0f;
            if (this.partialHeal > 1.0f) {
                int healThisTurn = (int)this.partialHeal;
                this.partialHeal -= (float)healThisTurn;
                this.level -= healThisTurn;
                if (this.target.HP < this.target.HT) {
                    this.target.HP += healThisTurn;
                    this.target.sprite.showStatusWithIcon(65280, Integer.toString(healThisTurn), FloatingText.HEALING, new Object[0]);
                    if (this.target.HP >= this.target.HT) {
                        this.target.HP = this.target.HT;
                        if (this.target instanceof Hero) {
                            ((Hero)this.target).resting = false;
                        }
                    }
                }
            }
            if (this.level <= 0) {
                this.detach();
                if (this.target instanceof Hero) {
                    ((Hero)this.target).resting = false;
                }
            }
            this.spend(1.0f);
            return true;
        }

        public void boost(int amount) {
            if (this.target != null) {
                this.level += amount;
                this.pos = this.target.pos;
            }
        }

        @Override
        public int icon() {
            return 19;
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
            return Messages.get(this, "desc", this.level);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(POS, this.pos);
            bundle.put(PARTIAL, this.partialHeal);
            bundle.put(LEVEL, this.level);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.pos = bundle.getInt(POS);
            this.partialHeal = bundle.getFloat(PARTIAL);
            this.level = bundle.getInt(LEVEL);
        }
    }
}

