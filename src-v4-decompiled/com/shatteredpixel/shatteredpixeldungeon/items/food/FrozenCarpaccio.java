/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.food;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barkskin;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Random;

public class FrozenCarpaccio
extends Food {
    public FrozenCarpaccio() {
        this.image = ItemSpriteSheet.CARPACCIO;
        this.energy = 150.0f;
    }

    @Override
    protected void satisfy(Hero hero) {
        super.satisfy(hero);
        FrozenCarpaccio.effect(hero);
    }

    @Override
    public int value() {
        return 10 * this.quantity;
    }

    public static void effect(Hero hero) {
        switch (Random.Int(5)) {
            case 0: {
                GLog.i(Messages.get(FrozenCarpaccio.class, "invis", new Object[0]), new Object[0]);
                Buff.affect(hero, Invisibility.class, 20.0f);
                break;
            }
            case 1: {
                GLog.i(Messages.get(FrozenCarpaccio.class, "hard", new Object[0]), new Object[0]);
                Barkskin.conditionallyAppend(hero, hero.HT / 4, 1);
                break;
            }
            case 2: {
                GLog.i(Messages.get(FrozenCarpaccio.class, "refresh", new Object[0]), new Object[0]);
                PotionOfHealing.cure(hero);
                break;
            }
            case 3: {
                GLog.i(Messages.get(FrozenCarpaccio.class, "better", new Object[0]), new Object[0]);
                hero.HP = Math.min(hero.HP + hero.HT / 4, hero.HT);
                hero.sprite.showStatusWithIcon(65280, Integer.toString(hero.HT / 4), FloatingText.HEALING, new Object[0]);
            }
        }
    }

    public static Food cook(MysteryMeat ingredient) {
        FrozenCarpaccio result = new FrozenCarpaccio();
        result.quantity = ingredient.quantity();
        return result;
    }
}

