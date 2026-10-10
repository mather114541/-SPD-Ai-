/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Brute;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.PlateArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ScaleArmor;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ShieldedSprite;
import com.watabou.utils.Random;

public class ArmoredBrute
extends Brute {
    public ArmoredBrute() {
        this.spriteClass = ShieldedSprite.class;
        this.loot = Generator.Category.ARMOR;
        this.lootChance = 1.0f;
    }

    @Override
    public int damageRoll() {
        return this.buff(ArmoredRage.class) != null ? Random.NormalIntRange(15, 40) : Random.NormalIntRange(5, 25);
    }

    @Override
    public int drRoll() {
        return super.drRoll() + 4;
    }

    @Override
    protected void triggerEnrage() {
        Buff.affect(this, ArmoredRage.class).setShield(this.HT / 2 + 1);
        this.sprite.showStatusWithIcon(65280, Integer.toString(this.HT / 2), FloatingText.SHIELDING, new Object[0]);
        if (Dungeon.level.heroFOV[this.pos]) {
            this.sprite.showStatus(0xFF8800, Messages.get(this, "enraged", new Object[0]), new Object[0]);
        }
        this.spend(1.0f);
        this.hasRaged = true;
    }

    @Override
    public Item createLoot() {
        if (Random.Int(4) == 0) {
            return new PlateArmor().random();
        }
        return new ScaleArmor().random();
    }

    public static class ArmoredRage
    extends Brute.BruteRage {
        @Override
        public boolean act() {
            if (this.target.HP > 0) {
                this.detach();
                return true;
            }
            this.absorbDamage(Math.round(AscensionChallenge.statModifier(this.target)));
            if (this.shielding() <= 0) {
                this.target.die(null);
            }
            this.spend(3.0f);
            return true;
        }
    }
}

