/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class ScrollOfAntiMagic
extends ExoticScroll {
    public ScrollOfAntiMagic() {
        this.icon = ItemSpriteSheet.Icons.SCROLL_ANTIMAGIC;
    }

    @Override
    public void doRead() {
        this.detach(ScrollOfAntiMagic.curUser.belongings.backpack);
        Buff.affect(curUser, MagicImmune.class, 20.0f);
        new Flare(5, 32.0f).color(65280, true).show(ScrollOfAntiMagic.curUser.sprite, 2.0f);
        this.identify();
        this.readAnimation();
    }
}

