/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.journal.Bestiary;

public abstract class NPC
extends Mob {
    public NPC() {
        this.HT = 1;
        this.HP = 1;
        this.EXP = 0;
        this.alignment = Char.Alignment.NEUTRAL;
        this.state = this.PASSIVE;
    }

    @Override
    protected boolean act() {
        if (Dungeon.level.heroFOV[this.pos]) {
            Bestiary.setSeen(this.getClass());
        }
        return super.act();
    }

    @Override
    public void beckon(int cell) {
    }
}

