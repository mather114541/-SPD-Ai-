/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Crab;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HermitCrabSprite;

public class HermitCrab
extends Crab {
    public HermitCrab() {
        this.spriteClass = HermitCrabSprite.class;
        this.HT = 25;
        this.HP = 25;
        this.baseSpeed = 1.0f;
        this.lootChance = 0.5f;
    }

    @Override
    public void rollToDropLoot() {
        super.rollToDropLoot();
        if (Dungeon.hero.lvl <= this.maxLvl + 2) {
            com.shatteredpixel.shatteredpixeldungeon.items.Heap h = Dungeon.level.drop((Item)Generator.randomArmor(), (int)this.pos);
            if (h.sprite != null) h.sprite.drop();
        }
    }

    @Override
    public int drRoll() {
        return super.drRoll() + 2;
    }
}

