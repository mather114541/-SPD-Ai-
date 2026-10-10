/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Scorpio;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfExperience;
import com.shatteredpixel.shatteredpixeldungeon.sprites.AcidicSprite;

public class Acidic
extends Scorpio {
    public Acidic() {
        this.spriteClass = AcidicSprite.class;
        this.properties.add(Char.Property.ACIDIC);
        this.loot = PotionOfExperience.class;
        this.lootChance = 1.0f;
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        Buff.affect(enemy, Ooze.class).set(20.0f);
        return super.attackProc(enemy, damage);
    }

    @Override
    public int defenseProc(Char enemy, int damage) {
        if (Dungeon.level.adjacent(this.pos, enemy.pos)) {
            Buff.affect(enemy, Ooze.class).set(20.0f);
        }
        return super.defenseProc(enemy, damage);
    }

    @Override
    public Item createLoot() {
        return new PotionOfExperience();
    }
}

