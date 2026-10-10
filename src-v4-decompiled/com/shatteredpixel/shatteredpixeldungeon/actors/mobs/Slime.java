/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SlimeSprite;
import com.watabou.utils.Random;

public class Slime
extends Mob {
    public Slime() {
        this.spriteClass = SlimeSprite.class;
        this.HT = 20;
        this.HP = 20;
        this.defenseSkill = 5;
        this.EXP = 4;
        this.maxLvl = 9;
        this.lootChance = 0.2f;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(2, 5);
    }

    @Override
    public int attackSkill(Char target) {
        return 12;
    }

    @Override
    public void damage(int dmg, Object src) {
        float scaleFactor = AscensionChallenge.statModifier(this);
        float scaledDmg = (float)dmg / scaleFactor;
        if (scaledDmg >= 5.0f) {
            scaledDmg = 4.0f + (float)(Math.sqrt(8.0f * (scaledDmg - 4.0f) + 1.0f) - 1.0) / 2.0f;
        }
        dmg = (int)(scaledDmg * AscensionChallenge.statModifier(this));
        super.damage(dmg, src);
    }

    @Override
    public float lootChance() {
        return super.lootChance() * (float)Math.pow(0.25, Dungeon.LimitedDrops.SLIME_WEP.count);
    }

    @Override
    public Item createLoot() {
        ++Dungeon.LimitedDrops.SLIME_WEP.count;
        Generator.Category c = Generator.Category.WEP_T2;
        MeleeWeapon w = (MeleeWeapon)Generator.randomUsingDefaults(Generator.Category.WEP_T2);
        w.level(0);
        return w;
    }
}

