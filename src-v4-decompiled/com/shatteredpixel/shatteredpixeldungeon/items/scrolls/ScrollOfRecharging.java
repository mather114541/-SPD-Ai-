/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.scrolls;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EnergyParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;

public class ScrollOfRecharging
extends Scroll {
    public ScrollOfRecharging() {
        this.icon = ItemSpriteSheet.Icons.SCROLL_RECHARGE;
    }

    @Override
    public void doRead() {
        this.detach(ScrollOfRecharging.curUser.belongings.backpack);
        Buff.affect(curUser, Recharging.class, 30.0f);
        ScrollOfRecharging.charge(curUser);
        Sample.INSTANCE.play("sounds/read.mp3");
        Sample.INSTANCE.play("sounds/chargeup.mp3");
        GLog.i(Messages.get(this, "surge", new Object[0]), new Object[0]);
        SpellSprite.show(curUser, 2);
        this.identify();
        this.readAnimation();
    }

    public static void charge(Char user) {
        Emitter e;
        if (user.sprite != null && (e = user.sprite.centerEmitter()) != null) {
            e.burst(EnergyParticle.FACTORY, 15);
        }
    }

    @Override
    public int value() {
        return this.isKnown() ? 30 * this.quantity : super.value();
    }
}

