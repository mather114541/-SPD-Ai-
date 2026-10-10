/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Degrade;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WarlockSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class Warlock
extends Mob {
    private static final float TIME_TO_ZAP = 1.0f;

    public Warlock() {
        this.spriteClass = WarlockSprite.class;
        this.HT = 70;
        this.HP = 70;
        this.defenseSkill = 18;
        this.EXP = 11;
        this.maxLvl = 21;
        this.loot = Generator.Category.POTION;
        this.lootChance = 0.5f;
        this.properties.add(Char.Property.UNDEAD);
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(12, 18);
    }

    @Override
    public int attackSkill(Char target) {
        return 25;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 8);
    }

    @Override
    protected boolean canAttack(Char enemy) {
        return super.canAttack(enemy) || new Ballistica((int)this.pos, (int)enemy.pos, (int)6).collisionPos == enemy.pos;
    }

    @Override
    protected boolean doAttack(Char enemy) {
        if (Dungeon.level.adjacent(this.pos, enemy.pos) || new Ballistica((int)this.pos, (int)enemy.pos, (int)6).collisionPos != enemy.pos) {
            return super.doAttack(enemy);
        }
        if (this.sprite != null && (this.sprite.visible || enemy.sprite.visible)) {
            this.sprite.zap(enemy.pos);
            return false;
        }
        this.zap();
        return true;
    }

    protected void zap() {
        this.spend(1.0f);
        Invisibility.dispel(this);
        Char enemy = this.enemy;
        if (Warlock.hit(this, enemy, true)) {
            if (enemy == Dungeon.hero && Random.Int(2) == 0) {
                Buff.prolong(enemy, Degrade.class, 30.0f);
                Sample.INSTANCE.play("sounds/degrade.mp3");
            }
            int dmg = Random.NormalIntRange(12, 18);
            dmg = Math.round((float)dmg * AscensionChallenge.statModifier(this));
            enemy.damage(dmg, new DarkBolt());
            if (enemy == Dungeon.hero && !enemy.isAlive()) {
                Badges.validateDeathFromEnemyMagic();
                Dungeon.fail(this);
                GLog.n(Messages.get(this, "bolt_kill", new Object[0]), new Object[0]);
            }
        } else {
            enemy.sprite.showStatus(0xFFFF00, enemy.defenseVerb(), new Object[0]);
        }
    }

    public void onZapComplete() {
        this.zap();
        this.next();
    }

    @Override
    public Item createLoot() {
        Item i;
        if (Random.Int(3) == 0 && Random.Int(8) > Dungeon.LimitedDrops.WARLOCK_HP.count) {
            ++Dungeon.LimitedDrops.WARLOCK_HP.count;
            return new PotionOfHealing();
        }
        while ((i = Generator.randomUsingDefaults(Generator.Category.POTION)) instanceof PotionOfHealing) {
        }
        return i;
    }

    public static class DarkBolt {
    }
}

