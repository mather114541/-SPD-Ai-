/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Monk;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Pasty;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SeniorSprite;
import com.watabou.utils.Random;

public class Senior
extends Monk {
    public Senior() {
        this.spriteClass = SeniorSprite.class;
        this.loot = Pasty.class;
        this.lootChance = 1.0f;
    }

    @Override
    public void move(int step, boolean travelling) {
        if (travelling) {
            this.focusCooldown -= 1.66f;
        }
        super.move(step, travelling);
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(16, 25);
    }
}

