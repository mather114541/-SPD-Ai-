/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.audio.Sample;

public class FlashingTrap
extends Trap {
    public FlashingTrap() {
        this.color = 7;
        this.shape = 3;
        this.disarmedByActivation = false;
        this.avoidsHallways = true;
    }

    @Override
    public void activate() {
        Char c = Actor.findChar(this.pos);
        if (c != null) {
            int damage = Math.max(0, 4 + this.scalingDepth() / 2 - c.drRoll() / 2);
            Buff.affect(c, Bleeding.class).set(damage);
            Buff.prolong(c, Blindness.class, 10.0f);
            Buff.prolong(c, Cripple.class, 20.0f);
            if (c instanceof Mob) {
                Buff.prolong(c, Trap.HazardAssistTracker.class, 50.0f);
                if (((Mob)c).state == ((Mob)c).HUNTING) {
                    ((Mob)c).state = ((Mob)c).WANDERING;
                }
                ((Mob)c).beckon(Dungeon.level.randomDestination(c));
            }
        }
        if (Dungeon.level.heroFOV[this.pos]) {
            GameScene.flash(-2130706433);
            Sample.INSTANCE.play("sounds/blast.mp3");
        }
    }
}

