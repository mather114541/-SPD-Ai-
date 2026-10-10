/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.scrolls;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Drowsy;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class ScrollOfLullaby
extends Scroll {
    public ScrollOfLullaby() {
        this.icon = ItemSpriteSheet.Icons.SCROLL_LULLABY;
    }

    @Override
    public void doRead() {
        this.detach(ScrollOfLullaby.curUser.belongings.backpack);
        ScrollOfLullaby.curUser.sprite.centerEmitter().start(Speck.factory(9), 0.3f, 5);
        Sample.INSTANCE.play("sounds/lullaby.mp3");
        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
            if (!Dungeon.level.heroFOV[mob.pos]) continue;
            Buff.affect(mob, Drowsy.class, 5.0f);
            mob.sprite.centerEmitter().start(Speck.factory(9), 0.3f, 5);
        }
        Buff.affect(curUser, Drowsy.class, 5.0f);
        GLog.i(Messages.get(this, "sooth", new Object[0]), new Object[0]);
        this.identify();
        this.readAnimation();
    }

    @Override
    public int value() {
        return this.isKnown() ? 40 * this.quantity : super.value();
    }
}

