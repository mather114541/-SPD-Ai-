/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.potions;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Drowsy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Healing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class PotionOfHealing
extends Potion {
    public PotionOfHealing() {
        this.icon = ItemSpriteSheet.Icons.POTION_HEALING;
        this.bones = true;
    }

    @Override
    public void apply(Hero hero) {
        this.identify();
        PotionOfHealing.cure(hero);
        PotionOfHealing.heal(hero);
    }

    public static void heal(Char ch) {
        if (ch == Dungeon.hero && Dungeon.isChallenged(4)) {
            PotionOfHealing.pharmacophobiaProc(Dungeon.hero);
        } else {
            Healing healing = Buff.affect(ch, Healing.class);
            healing.setHeal((int)(0.8f * (float)ch.HT + 14.0f), 0.25f, 0, true);
            if (ch == Dungeon.hero) {
                GLog.p(Messages.get(PotionOfHealing.class, "heal", new Object[0]), new Object[0]);
            }
        }
    }

    public static void pharmacophobiaProc(Hero hero) {
        Buff.affect(hero, Poison.class).set(4 + hero.lvl / 2);
    }

    public static void cure(Char ch) {
        Buff.detach(ch, Poison.class);
        Buff.detach(ch, Cripple.class);
        Buff.detach(ch, Weakness.class);
        Buff.detach(ch, Vulnerable.class);
        Buff.detach(ch, Bleeding.class);
        Buff.detach(ch, Blindness.class);
        Buff.detach(ch, Drowsy.class);
        Buff.detach(ch, Slow.class);
        Buff.detach(ch, Vertigo.class);
    }

    @Override
    public int value() {
        return this.isKnown() ? 30 * this.quantity : super.value();
    }
}

