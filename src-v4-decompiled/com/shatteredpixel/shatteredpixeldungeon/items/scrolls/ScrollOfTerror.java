/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.scrolls;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class ScrollOfTerror
extends Scroll {
    public ScrollOfTerror() {
        this.icon = ItemSpriteSheet.Icons.SCROLL_TERROR;
    }

    @Override
    public void doRead() {
        this.detach(ScrollOfTerror.curUser.belongings.backpack);
        new Flare(5, 32.0f).color(0xFF0000, true).show(ScrollOfTerror.curUser.sprite, 2.0f);
        Sample.INSTANCE.play("sounds/read.mp3");
        int count = 0;
        Char affected = null;
        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
            if (mob.alignment == Char.Alignment.ALLY || !Dungeon.level.heroFOV[mob.pos]) continue;
            Buff.affect((Char)mob, Terror.class, (float)20.0f).object = curUser.id();
            if (mob.buff(Terror.class) == null) continue;
            ++count;
            affected = mob;
        }
        switch (count) {
            case 0: {
                GLog.i(Messages.get(this, "none", new Object[0]), new Object[0]);
                break;
            }
            case 1: {
                GLog.i(Messages.get(this, "one", affected.name()), new Object[0]);
                break;
            }
            default: {
                GLog.i(Messages.get(this, "many", new Object[0]), new Object[0]);
            }
        }
        this.identify();
        this.readAnimation();
    }

    @Override
    public int value() {
        return this.isKnown() ? 40 * this.quantity : super.value();
    }
}

