/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicalSight;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class PotionOfMagicalSight
extends ExoticPotion {
    public PotionOfMagicalSight() {
        this.icon = ItemSpriteSheet.Icons.POTION_MAGISIGHT;
    }

    @Override
    public void apply(Hero hero) {
        this.identify();
        Buff.prolong(hero, MagicalSight.class, 50.0f);
        SpellSprite.show(hero, 6);
        Dungeon.observe();
    }
}

