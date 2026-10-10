/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Foresight;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;

public class ScrollOfForesight
extends ExoticScroll {
    public ScrollOfForesight() {
        this.icon = ItemSpriteSheet.Icons.SCROLL_FORESIGHT;
    }

    @Override
    public void doRead() {
        this.detach(ScrollOfForesight.curUser.belongings.backpack);
        Sample.INSTANCE.play("sounds/read.mp3");
        Buff.affect(curUser, Foresight.class, 400.0f);
        this.identify();
        this.readAnimation();
    }
}

