/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dread;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;

public class ScrollOfDread
extends ExoticScroll {
    public ScrollOfDread() {
        this.icon = ItemSpriteSheet.Icons.SCROLL_DREAD;
    }

    @Override
    public void doRead() {
        this.detach(ScrollOfDread.curUser.belongings.backpack);
        new Flare(5, 32.0f).color(0xFF0000, true).show(ScrollOfDread.curUser.sprite, 2.0f);
        Sample.INSTANCE.play("sounds/read.mp3");
        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
            if (mob.alignment == Char.Alignment.ALLY || !Dungeon.level.heroFOV[mob.pos]) continue;
            if (!mob.isImmune(Dread.class)) {
                Buff.affect((Char)mob, Dread.class).object = curUser.id();
                continue;
            }
            Buff.affect((Char)mob, Terror.class, (float)20.0f).object = curUser.id();
        }
        this.identify();
        this.readAnimation();
    }
}

