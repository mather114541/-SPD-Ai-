/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BatSprite;
import com.watabou.utils.Random;

public class Bat
extends Mob {
    public Bat() {
        this.spriteClass = BatSprite.class;
        this.HT = 30;
        this.HP = 30;
        this.defenseSkill = 15;
        this.baseSpeed = 2.0f;
        this.EXP = 7;
        this.maxLvl = 15;
        this.flying = true;
        this.loot = PotionOfHealing.class;
        this.lootChance = 0.1667f;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(5, 18);
    }

    @Override
    public int attackSkill(Char target) {
        return 16;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 4);
    }

    @Override
    public void die(Object cause) {
        this.flying = false;
        super.die(cause);
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        int reg = Math.min((damage = super.attackProc(enemy, damage)) - 4, this.HT - this.HP);
        if (reg > 0) {
            this.HP += reg;
            this.sprite.showStatusWithIcon(65280, Integer.toString(reg), FloatingText.HEALING, new Object[0]);
        }
        return damage;
    }

    @Override
    public float lootChance() {
        return super.lootChance() * ((7.0f - (float)Dungeon.LimitedDrops.BAT_HP.count) / 7.0f);
    }

    @Override
    public Item createLoot() {
        ++Dungeon.LimitedDrops.BAT_HP.count;
        return super.createLoot();
    }
}

