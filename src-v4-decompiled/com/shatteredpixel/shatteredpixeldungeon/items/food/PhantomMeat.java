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
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class PhantomMeat
extends Food {
    public PhantomMeat() {
        this.image = ItemSpriteSheet.PHANTOM_MEAT;
        this.energy = 450.0f;
    }

    @Override
    protected void satisfy(Hero hero) {
        super.satisfy(hero);
        PhantomMeat.effect(hero);
    }

    @Override
    public int value() {
        return 30 * this.quantity;
    }

    public static void effect(Hero hero) {
        Barkskin.conditionallyAppend(hero, hero.HT / 4, 1);
        Buff.affect(hero, Invisibility.class, 20.0f);
        hero.HP = Math.min(hero.HP + hero.HT / 4, hero.HT);
        hero.sprite.showStatusWithIcon(65280, Integer.toString(hero.HT / 4), FloatingText.HEALING, new Object[0]);
        PotionOfHealing.cure(hero);
    }
}

