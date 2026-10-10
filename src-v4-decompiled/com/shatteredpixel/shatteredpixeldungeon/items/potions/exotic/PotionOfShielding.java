/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class PotionOfShielding
extends ExoticPotion {
    public PotionOfShielding() {
        this.icon = ItemSpriteSheet.Icons.POTION_SHIELDING;
    }

    @Override
    public void apply(Hero hero) {
        this.identify();
        if (Dungeon.isChallenged(4)) {
            PotionOfHealing.pharmacophobiaProc(hero);
        } else {
            Buff.affect(hero, Barrier.class).setShield((int)(0.6f * (float)hero.HT + 10.0f));
            hero.sprite.showStatusWithIcon(65280, Integer.toString((int)(0.6f * (float)hero.HT + 10.0f)), FloatingText.SHIELDING, new Object[0]);
        }
    }
}

