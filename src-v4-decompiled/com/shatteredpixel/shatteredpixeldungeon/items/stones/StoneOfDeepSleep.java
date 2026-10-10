/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.stones;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicalSleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.Runestone;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;

public class StoneOfDeepSleep
extends Runestone {
    public StoneOfDeepSleep() {
        this.image = ItemSpriteSheet.STONE_SLEEP;
    }

    @Override
    protected void activate(int cell) {
        Char c;
        if (Actor.findChar(cell) != null && (c = Actor.findChar(cell)) instanceof Mob) {
            Buff.affect(c, MagicalSleep.class);
            c.sprite.centerEmitter().start(Speck.factory(9), 0.3f, 5);
        }
        Sample.INSTANCE.play("sounds/lullaby.mp3");
    }
}

