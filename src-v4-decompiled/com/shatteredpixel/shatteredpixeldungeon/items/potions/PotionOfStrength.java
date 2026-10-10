/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.potions;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class PotionOfStrength
extends Potion {
    public PotionOfStrength() {
        this.icon = ItemSpriteSheet.Icons.POTION_STRENGTH;
        this.unique = true;
        this.talentFactor = 2.0f;
    }

    @Override
    public void apply(Hero hero) {
        this.identify();
        ++hero.STR;
        if (!rl.RLMode.headless) {
            hero.sprite.showStatusWithIcon(65280, "1", FloatingText.STRENGTH, new Object[0]);
        }
        GLog.p(Messages.get(this, "msg", hero.STR()), new Object[0]);
        Badges.validateStrengthAttained();
        Badges.validateDuelistUnlock();
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

