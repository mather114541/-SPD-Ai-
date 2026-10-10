/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.potions;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MindVision;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class PotionOfMindVision
extends Potion {
    public PotionOfMindVision() {
        this.icon = ItemSpriteSheet.Icons.POTION_MINDVIS;
    }

    @Override
    public void apply(Hero hero) {
        this.identify();
        Buff.prolong(hero, MindVision.class, 20.0f);
        SpellSprite.show(hero, 6, 1.0f, 0.77f, 0.9f);
        Dungeon.observe();
        if (Dungeon.level.mobs.size() > 0) {
            GLog.i(Messages.get(this, "see_mobs", new Object[0]), new Object[0]);
        } else {
            GLog.i(Messages.get(this, "see_none", new Object[0]), new Object[0]);
        }
    }

    @Override
    public int value() {
        return this.isKnown() ? 30 * this.quantity : super.value();
    }
}

