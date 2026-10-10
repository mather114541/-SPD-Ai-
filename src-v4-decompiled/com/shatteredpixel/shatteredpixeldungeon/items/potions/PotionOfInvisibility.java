/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.potions;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class PotionOfInvisibility
extends Potion {
    public PotionOfInvisibility() {
        this.icon = ItemSpriteSheet.Icons.POTION_INVIS;
    }

    @Override
    public void apply(Hero hero) {
        this.identify();
        Buff.prolong(hero, Invisibility.class, 20.0f);
        GLog.i(Messages.get(this, "invisible", new Object[0]), new Object[0]);
        Sample.INSTANCE.play("sounds/meld.mp3");
    }

    @Override
    public int value() {
        return this.isKnown() ? 40 * this.quantity : super.value();
    }
}

