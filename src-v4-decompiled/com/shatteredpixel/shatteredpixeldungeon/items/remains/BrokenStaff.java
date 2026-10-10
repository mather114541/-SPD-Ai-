/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.remains;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.remains.RemainsItem;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;

public class BrokenStaff
extends RemainsItem {
    public BrokenStaff() {
        this.image = ItemSpriteSheet.BROKEN_STAFF;
    }

    @Override
    protected void doEffect(Hero hero) {
        hero.belongings.charge(1.0f);
        ScrollOfRecharging.charge(hero);
        Sample.INSTANCE.play("sounds/chargeup.mp3");
    }
}

