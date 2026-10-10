/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.HoldFast;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShieldBuff;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Blocking
extends Weapon.Enchantment {
    private static ItemSprite.Glowing BLUE = new ItemSprite.Glowing(255);

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        int level = Math.max(0, weapon.buffedLvl());
        float procChance = ((float)level + 4.0f) / ((float)level + 40.0f) * this.procChanceMultiplier(attacker);
        if (Random.Float() < procChance) {
            float powerMulti = Math.max(1.0f, procChance);
            BlockBuff b = Buff.affect(attacker, BlockBuff.class);
            int shield = Math.round(powerMulti * (float)(2 + weapon.buffedLvl()));
            b.setShield(shield);
            attacker.sprite.showStatusWithIcon(65280, Integer.toString(shield), FloatingText.SHIELDING, new Object[0]);
            attacker.sprite.emitter().burst(Speck.factory(2), 5);
        }
        return damage;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return BLUE;
    }

    public static class BlockBuff
    extends ShieldBuff {
        private float left;
        public static String LEFT = "left";

        public BlockBuff() {
            this.type = Buff.buffType.POSITIVE;
            this.shieldUsePriority = 2;
            this.left = 5.0f;
        }

        @Override
        public boolean act() {
            this.left -= HoldFast.buffDecayFactor(this.target);
            if (this.left <= 0.0f) {
                this.detach();
            } else {
                this.spend(1.0f);
            }
            return true;
        }

        @Override
        public void setShield(int shield) {
            super.setShield(shield);
            this.left = 5.0f;
        }

        @Override
        public void fx(boolean on) {
            if (on) {
                this.target.sprite.add(CharSprite.State.SHIELDED);
            } else if (this.target.buff(Barrier.class) == null) {
                this.target.sprite.remove(CharSprite.State.SHIELDED);
            }
        }

        @Override
        public int icon() {
            return 20;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0.5f, 1.0f, 2.0f);
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0.0f, (5.0f - this.left) / 5.0f);
        }

        @Override
        public String iconTextDisplay() {
            return Integer.toString((int)this.left);
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", this.shielding(), this.dispTurns(this.left));
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(LEFT, this.left);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.left = bundle.getFloat(LEFT);
        }
    }
}

