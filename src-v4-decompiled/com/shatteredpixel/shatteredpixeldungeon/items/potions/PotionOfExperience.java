/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.potions;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class PotionOfExperience
extends Potion {
    public PotionOfExperience() {
        this.icon = ItemSpriteSheet.Icons.POTION_EXP;
        this.bones = true;
        this.talentFactor = 2.0f;
    }

    @Override
    public void apply(Hero hero) {
        this.identify();
        hero.sprite.showStatusWithIcon(65280, Integer.toString(hero.maxExp()), FloatingText.EXPERIENCE, new Object[0]);
        hero.earnExp(hero.maxExp(), this.getClass());
        new Flare(6, 32.0f).color(0xFFFF00, true).show(PotionOfExperience.curUser.sprite, 2.0f);
    }

    @Override
    public int value() {
        return this.isKnown() ? 50 * this.quantity : super.value();
    }

    @Override
    public int energyVal() {
        return this.isKnown() ? 10 * this.quantity : super.energyVal();
    }
}

