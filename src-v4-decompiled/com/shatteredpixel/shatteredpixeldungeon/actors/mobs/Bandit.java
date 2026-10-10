/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Thief;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BanditSprite;
import com.watabou.utils.Random;

public class Bandit
extends Thief {
    public Item item;

    public Bandit() {
        this.spriteClass = BanditSprite.class;
        this.lootChance = 1.0f;
    }

    @Override
    protected boolean steal(Hero hero) {
        if (super.steal(hero)) {
            Buff.prolong(hero, Blindness.class, 5.0f);
            Buff.affect(hero, Poison.class).set(Random.IntRange(5, 6));
            Buff.prolong(hero, Cripple.class, 5.0f);
            Dungeon.observe();
            return true;
        }
        return false;
    }
}

