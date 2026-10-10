/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArtifactRecharge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;

public class ScrollOfMysticalEnergy
extends ExoticScroll {
    public ScrollOfMysticalEnergy() {
        this.icon = ItemSpriteSheet.Icons.SCROLL_MYSTENRG;
    }

    @Override
    public void doRead() {
        this.detach(ScrollOfMysticalEnergy.curUser.belongings.backpack);
        Buff.affect((Char)ScrollOfMysticalEnergy.curUser, ArtifactRecharge.class).set((float)30.0f).ignoreHornOfPlenty = false;
        Sample.INSTANCE.play("sounds/read.mp3");
        Sample.INSTANCE.play("sounds/chargeup.mp3");
        SpellSprite.show(curUser, 2, 0.0f, 1.0f, 1.0f);
        this.identify();
        ScrollOfRecharging.charge(curUser);
        this.readAnimation();
    }
}

