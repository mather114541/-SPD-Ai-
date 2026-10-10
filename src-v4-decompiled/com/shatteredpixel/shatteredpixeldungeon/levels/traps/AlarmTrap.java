/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class AlarmTrap
extends Trap {
    public AlarmTrap() {
        this.color = 0;
        this.shape = 0;
    }

    @Override
    public void activate() {
        for (Mob mob : Dungeon.level.mobs) {
            mob.beckon(this.pos);
        }
        if (Dungeon.level.heroFOV[this.pos]) {
            GLog.w(Messages.get(this, "alarm", new Object[0]), new Object[0]);
            CellEmitter.center(this.pos).start(Speck.factory(5), 0.3f, 3);
        }
        Sample.INSTANCE.play("sounds/alert.mp3");
    }
}

