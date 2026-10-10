/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.scrolls;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class ScrollOfRage
extends Scroll {
    public ScrollOfRage() {
        this.icon = ItemSpriteSheet.Icons.SCROLL_RAGE;
    }

    @Override
    public void doRead() {
        this.detach(ScrollOfRage.curUser.belongings.backpack);
        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
            mob.beckon(ScrollOfRage.curUser.pos);
            if (mob.alignment == Char.Alignment.ALLY || !Dungeon.level.heroFOV[mob.pos]) continue;
            Buff.prolong(mob, Amok.class, 5.0f);
        }
        GLog.w(Messages.get(this, "roar", new Object[0]), new Object[0]);
        this.identify();
        ScrollOfRage.curUser.sprite.centerEmitter().start(Speck.factory(5), 0.3f, 3);
        Sample.INSTANCE.play("sounds/challenge.mp3");
        this.readAnimation();
    }

    @Override
    public int value() {
        return this.isKnown() ? 40 * this.quantity : super.value();
    }
}

