/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.potions;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Haste;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class PotionOfHaste
extends Potion {
    public PotionOfHaste() {
        this.icon = ItemSpriteSheet.Icons.POTION_HASTE;
    }

    @Override
    public void apply(Hero hero) {
        this.identify();
        GLog.w(Messages.get(this, "energetic", new Object[0]), new Object[0]);
        Buff.prolong(hero, Haste.class, 20.0f);
        SpellSprite.show(hero, 5, 1.0f, 1.0f, 0.0f);
    }

    @Override
    public int value() {
        return this.isKnown() ? 40 * this.quantity : super.value();
    }
}

