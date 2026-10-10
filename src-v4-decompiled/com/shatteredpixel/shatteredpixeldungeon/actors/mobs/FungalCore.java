/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Blacksmith;
import com.shatteredpixel.shatteredpixeldungeon.sprites.FungalCoreSprite;

public class FungalCore
extends Mob {
    public FungalCore() {
        this.HT = 300;
        this.HP = 300;
        this.spriteClass = FungalCoreSprite.class;
        this.EXP = 20;
        this.state = this.PASSIVE;
        this.properties.add(Char.Property.IMMOVABLE);
        this.properties.add(Char.Property.BOSS);
    }

    @Override
    public boolean reset() {
        return true;
    }

    @Override
    public float spawningWeight() {
        return 0.0f;
    }

    @Override
    public void die(Object cause) {
        super.die(cause);
        Blacksmith.Quest.beatBoss();
    }
}

