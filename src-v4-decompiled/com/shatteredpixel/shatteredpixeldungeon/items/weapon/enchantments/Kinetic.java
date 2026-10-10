/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class Kinetic
extends Weapon.Enchantment {
    private static ItemSprite.Glowing YELLOW = new ItemSprite.Glowing(0xFFFF00);

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        int conservedDamage = 0;
        if (attacker.buff(ConservedDamage.class) != null) {
            conservedDamage = attacker.buff(ConservedDamage.class).damageBonus();
        }
        Buff.affect((Char)attacker, KineticTracker.class).conservedDamage = conservedDamage;
        return damage + conservedDamage;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return YELLOW;
    }

    public static class ConservedDamage
    extends Buff {
        private float preservedDamage;
        private static final String PRESERVED_DAMAGE = "preserve_damage";

        public ConservedDamage() {
            this.type = Buff.buffType.POSITIVE;
        }

        @Override
        public int icon() {
            return 45;
        }

        @Override
        public void tintIcon(Image icon) {
            if (this.preservedDamage >= 10.0f) {
                icon.hardlight(1.0f, 0.0f, 0.0f);
            } else if (this.preservedDamage >= 5.0f) {
                icon.hardlight(1.0f, 1.0f - (this.preservedDamage - 5.0f) * 0.2f, 0.0f);
            } else {
                icon.hardlight(1.0f, 1.0f, 1.0f - this.preservedDamage * 0.2f);
            }
        }

        @Override
        public String iconTextDisplay() {
            return Integer.toString(this.damageBonus());
        }

        public void setBonus(int bonus) {
            this.preservedDamage = bonus;
        }

        public int damageBonus() {
            return (int)Math.ceil(this.preservedDamage);
        }

        @Override
        public boolean act() {
            this.preservedDamage -= Math.max(this.preservedDamage * 0.025f, 0.1f);
            if (this.preservedDamage <= 0.0f) {
                this.detach();
            }
            this.spend(1.0f);
            return true;
        }

        public void delay(float value) {
            this.spend(value);
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", this.damageBonus());
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(PRESERVED_DAMAGE, this.preservedDamage);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            if (bundle.contains(PRESERVED_DAMAGE)) {
                this.preservedDamage = bundle.getFloat(PRESERVED_DAMAGE);
            } else {
                this.preservedDamage = this.cooldown() / 10.0f;
                this.spend(this.cooldown());
            }
        }
    }

    public static class KineticTracker
    extends Buff {
        public int conservedDamage;

        public KineticTracker() {
            this.actPriority = 100;
        }

        @Override
        public boolean act() {
            this.detach();
            return true;
        }
    }
}

