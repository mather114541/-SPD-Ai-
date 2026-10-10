/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.DM100Sprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Random;

public class DM100
extends Mob {
    private static final float TIME_TO_ZAP = 1.0f;

    public DM100() {
        this.spriteClass = DM100Sprite.class;
        this.HT = 20;
        this.HP = 20;
        this.defenseSkill = 8;
        this.EXP = 6;
        this.maxLvl = 13;
        this.loot = Generator.Category.SCROLL;
        this.lootChance = 0.25f;
        this.properties.add(Char.Property.ELECTRIC);
        this.properties.add(Char.Property.INORGANIC);
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(2, 8);
    }

    @Override
    public int attackSkill(Char target) {
        return 11;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 4);
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
        this.spend(1.0f);
        Invisibility.dispel(this);
        if (DM100.hit(this, enemy, true)) {
            int dmg = Random.NormalIntRange(3, 10);
            dmg = Math.round((float)dmg * AscensionChallenge.statModifier(this));
            enemy.damage(dmg, new LightningBolt());
            if (enemy.sprite.visible) {
                enemy.sprite.centerEmitter().burst(SparkParticle.FACTORY, 3);
                enemy.sprite.flash();
            }
            if (enemy == Dungeon.hero) {
                PixelScene.shake(2.0f, 0.3f);
                if (!enemy.isAlive()) {
                    Badges.validateDeathFromEnemyMagic();
                    Dungeon.fail(this);
                    GLog.n(Messages.get(this, "zap_kill", new Object[0]), new Object[0]);
                }
            }
        } else {
            enemy.sprite.showStatus(0xFFFF00, enemy.defenseVerb(), new Object[0]);
        }
        if (this.sprite != null && (this.sprite.visible || enemy.sprite.visible)) {
            this.sprite.zap(enemy.pos);
            return false;
        }
        return true;
    }

    public static class LightningBolt {
    }
}

