/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.noosa.Image;
import com.watabou.utils.Random;

public class Wayward
extends Weapon.Enchantment {
    private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing(0);

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        float procChance = 0.25f * this.procChanceMultiplier(attacker);
        if (attacker.buff(WaywardBuff.class) != null) {
            Buff.detach(attacker, WaywardBuff.class);
        } else if (Random.Float() < procChance) {
            Buff.prolong(attacker, WaywardBuff.class, 10.0f);
        }
        return damage;
    }

    @Override
    public boolean curse() {
        return true;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return BLACK;
    }

    public static class WaywardBuff
    extends FlavourBuff {
        public static final float DURATION = 10.0f;

        public WaywardBuff() {
            this.type = Buff.buffType.NEGATIVE;
            this.announced = true;
        }

        @Override
        public int icon() {
            return 14;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(1.0f, 1.0f, 0.0f);
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0.0f, (10.0f - this.visualcooldown()) / 10.0f);
        }
    }
}

