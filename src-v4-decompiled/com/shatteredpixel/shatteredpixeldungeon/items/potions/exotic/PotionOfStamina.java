/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Stamina;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class PotionOfStamina
extends ExoticPotion {
    public PotionOfStamina() {
        this.icon = ItemSpriteSheet.Icons.POTION_STAMINA;
    }

    @Override
    public void apply(Hero hero) {
        this.identify();
        Buff.prolong(hero, Stamina.class, 100.0f);
        SpellSprite.show(hero, 5, 0.5f, 1.0f, 0.5f);
    }
}

