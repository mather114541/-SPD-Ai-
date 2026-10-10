/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ScorpioSprite;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

public class Scorpio
extends Mob {
    public Scorpio() {
        this.spriteClass = ScorpioSprite.class;
        this.HT = 110;
        this.HP = 110;
        this.defenseSkill = 24;
        this.viewDistance = 6;
        this.EXP = 14;
        this.maxLvl = 27;
        this.loot = Generator.Category.POTION;
        this.lootChance = 0.5f;
        this.properties.add(Char.Property.DEMONIC);
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(30, 40);
    }

    @Override
    public int attackSkill(Char target) {
        return 36;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 16);
    }

    @Override
    protected boolean canAttack(Char enemy) {
        return !Dungeon.level.adjacent(this.pos, enemy.pos) && (super.canAttack(enemy) || new Ballistica((int)this.pos, (int)enemy.pos, (int)7).collisionPos == enemy.pos);
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        damage = super.attackProc(enemy, damage);
        if (Random.Int(2) == 0) {
            Buff.prolong(enemy, Cripple.class, 10.0f);
        }
        return damage;
    }

    @Override
    protected boolean getCloser(int target) {
        if (this.state == this.HUNTING) {
            return this.enemySeen && this.getFurther(target);
        }
        return super.getCloser(target);
    }

    @Override
    public void aggro(Char ch) {
        if (ch == null || this.fieldOfView == null || this.fieldOfView.length != Dungeon.level.length() || this.fieldOfView[ch.pos]) {
            super.aggro(ch);
        }
    }

    @Override
    public Item createLoot() {
        Class<?> loot;
        while ((loot = Random.oneOf(Generator.Category.POTION.classes)) == PotionOfHealing.class || loot == PotionOfStrength.class) {
        }
        return (Item)Reflection.newInstance(loot);
    }
}

