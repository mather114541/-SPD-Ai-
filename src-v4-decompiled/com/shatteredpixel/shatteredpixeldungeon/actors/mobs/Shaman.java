/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hex;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ShamanSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public abstract class Shaman
extends Mob {
    public Shaman() {
        this.HT = 35;
        this.HP = 35;
        this.defenseSkill = 15;
        this.EXP = 8;
        this.maxLvl = 16;
        this.loot = Generator.Category.WAND;
        this.lootChance = 0.03f;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(5, 10);
    }

    @Override
    public int attackSkill(Char target) {
        return 18;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 6);
    }

    @Override
    protected boolean canAttack(Char enemy) {
        return super.canAttack(enemy) || new Ballistica((int)this.pos, (int)enemy.pos, (int)6).collisionPos == enemy.pos;
    }

    @Override
    public float lootChance() {
        return super.lootChance() * (float)Math.pow(0.3333333432674408, Dungeon.LimitedDrops.SHAMAN_WAND.count);
    }

    @Override
    public Item createLoot() {
        ++Dungeon.LimitedDrops.SHAMAN_WAND.count;
        return super.createLoot();
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

    private void zap() {
        this.spend(1.0f);
        Invisibility.dispel(this);
        Char enemy = this.enemy;
        if (Shaman.hit(this, enemy, true)) {
            if (Random.Int(2) == 0) {
                this.debuff(enemy);
                if (enemy == Dungeon.hero) {
                    Sample.INSTANCE.play("sounds/debuff.mp3");
                }
            }
            int dmg = Random.NormalIntRange(6, 15);
            dmg = Math.round((float)dmg * AscensionChallenge.statModifier(this));
            enemy.damage(dmg, new EarthenBolt());
            if (!enemy.isAlive() && enemy == Dungeon.hero) {
                Badges.validateDeathFromEnemyMagic();
                Dungeon.fail(this);
                GLog.n(Messages.get(this, "bolt_kill", new Object[0]), new Object[0]);
            }
        } else {
            enemy.sprite.showStatus(0xFFFF00, enemy.defenseVerb(), new Object[0]);
        }
    }

    protected abstract void debuff(Char var1);

    public void onZapComplete() {
        this.zap();
        this.next();
    }

    @Override
    public String description() {
        return super.description() + "\n\n" + Messages.get(this, "spell_desc", new Object[0]);
    }

    public static Class<? extends Shaman> random() {
        float roll = Random.Float();
        if (roll < 0.4f) {
            return RedShaman.class;
        }
        if (roll < 0.8f) {
            return BlueShaman.class;
        }
        return PurpleShaman.class;
    }

    public static class EarthenBolt {
    }

    public static class RedShaman
    extends Shaman {
        public RedShaman() {
            this.spriteClass = ShamanSprite.Red.class;
        }

        @Override
        protected void debuff(Char enemy) {
            Buff.prolong(enemy, Weakness.class, 20.0f);
        }
    }

    public static class BlueShaman
    extends Shaman {
        public BlueShaman() {
            this.spriteClass = ShamanSprite.Blue.class;
        }

        @Override
        protected void debuff(Char enemy) {
            Buff.prolong(enemy, Vulnerable.class, 20.0f);
        }
    }

    public static class PurpleShaman
    extends Shaman {
        public PurpleShaman() {
            this.spriteClass = ShamanSprite.Purple.class;
        }

        @Override
        protected void debuff(Char enemy) {
            Buff.prolong(enemy, Hex.class, 30.0f);
        }
    }
}

