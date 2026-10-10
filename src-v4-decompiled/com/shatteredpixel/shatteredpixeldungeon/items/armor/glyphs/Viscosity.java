/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

public class Viscosity
extends Armor.Glyph {
    private static ItemSprite.Glowing PURPLE = new ItemSprite.Glowing(0x8844CC);

    @Override
    public int proc(Armor armor, Char attacker, Char defender, int damage) {
        Buff.affect((Char)defender, ViscosityTracker.class).level = armor.buffedLvl();
        return damage;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return PURPLE;
    }

    public static class ViscosityTracker
    extends Buff {
        private int level;

        public ViscosityTracker() {
            this.actPriority = 100;
            this.level = 0;
        }

        public int deferDamage(int dmg) {
            int amount;
            if (this.target.buff(Talent.WarriorFoodImmunity.class) != null) {
                return dmg;
            }
            int level = Math.max(0, this.level);
            float percent = (float)(level + 1) / (float)(level + 6);
            if ((amount = (percent *= Armor.Glyph.genericProcChanceMultiplier(this.target)) > 1.0f ? (dmg = Math.round((float)dmg / percent)) : (int)Math.ceil((float)dmg * percent)) > 0) {
                DeferedDamage deferred = Buff.affect(this.target, DeferedDamage.class);
                deferred.extend(amount);
                this.target.sprite.showStatus(0xFF8800, Messages.get(Viscosity.class, "deferred", amount), new Object[0]);
            }
            return dmg - amount;
        }

        @Override
        public boolean act() {
            this.detach();
            return true;
        }
    }

    public static class DeferedDamage
    extends Buff
    implements Buff.DOTbuff {
        protected int damage;
        private static final String DAMAGE = "damage";

        public DeferedDamage() {
            this.type = Buff.buffType.NEGATIVE;
            this.damage = 0;
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(DAMAGE, this.damage);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.damage = bundle.getInt(DAMAGE);
        }

        public void extend(float damage) {
            if (this.damage == 0) {
                this.postpone(1.0f);
            }
            this.damage = (int)((float)this.damage + damage);
            if (this.target != null) {
                this.target.needsIncomingDOTUpdate = true;
            }
        }

        @Override
        public int icon() {
            return 28;
        }

        @Override
        public String iconTextDisplay() {
            return Integer.toString(this.damage);
        }

        @Override
        public boolean act() {
            if (this.target.isAlive()) {
                int damageThisTick = Math.max(1, (int)((float)this.damage * 0.1f));
                this.target.damage(damageThisTick, this);
                if (this.target == Dungeon.hero && !this.target.isAlive()) {
                    Badges.validateDeathFromFriendlyMagic();
                    Dungeon.fail(this);
                    GLog.n(Messages.get(this, "ondeath", new Object[0]), new Object[0]);
                }
                this.spend(1.0f);
                this.damage -= damageThisTick;
                if (this.damage <= 0) {
                    this.detach();
                }
            } else {
                this.detach();
            }
            this.target.needsIncomingDOTUpdate = true;
            return true;
        }

        @Override
        public void detach() {
            this.target.needsIncomingDOTUpdate = true;
            super.detach();
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", this.damage);
        }

        @Override
        public int totalIncomingDMG() {
            return this.damage;
        }
    }
}

