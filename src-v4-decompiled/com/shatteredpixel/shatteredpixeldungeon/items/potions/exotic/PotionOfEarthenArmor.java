/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barkskin;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class PotionOfEarthenArmor
extends ExoticPotion {
    public PotionOfEarthenArmor() {
        this.icon = ItemSpriteSheet.Icons.POTION_EARTHARMR;
    }

    @Override
    public void apply(Hero hero) {
        this.identify();
        Barkskin.conditionallyAppend(hero, 2 + hero.lvl / 3, 50);
    }
}

