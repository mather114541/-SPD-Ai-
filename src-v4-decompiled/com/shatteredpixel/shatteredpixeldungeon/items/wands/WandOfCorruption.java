/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corrosion;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Daze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Doom;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dread;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Drowsy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hex;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicalSleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SoulMark;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Bee;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DwarfKing;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Piranha;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Statue;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Swarm;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Wraith;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;
import java.util.HashMap;

public class WandOfCorruption
extends Wand {
    private static final float MINOR_DEBUFF_WEAKEN = 0.25f;
    private static final HashMap<Class<? extends Buff>, Float> MINOR_DEBUFFS = new HashMap();
    private static final float MAJOR_DEBUFF_WEAKEN = 0.5f;
    private static final HashMap<Class<? extends Buff>, Float> MAJOR_DEBUFFS;

    public WandOfCorruption() {
        this.image = ItemSpriteSheet.WAND_CORRUPTION;
    }

    @Override
    public void onZap(Ballistica bolt) {
        Char ch = Actor.findChar(bolt.collisionPos);
        if (ch != null) {
            float enemyResist;
            if (!(ch instanceof Mob)) {
                return;
            }
            Mob enemy = (Mob)ch;
            if (enemy instanceof DwarfKing) {
                Statistics.qualifiedForBossChallengeBadge = false;
            }
            float corruptingPower = 3.0f + (float)this.buffedLvl() / 3.0f;
            if (ch instanceof Mimic || ch instanceof Statue) {
                enemyResist = 1 + Dungeon.depth;
            } else if (ch instanceof Piranha || ch instanceof Bee) {
                enemyResist = 1.0f + (float)Dungeon.depth / 2.0f;
            } else if (ch instanceof Wraith) {
                enemyResist = (1.0f + (float)Dungeon.scalingDepth() / 4.0f) / 5.0f;
            } else if (ch instanceof Swarm) {
                enemyResist = 1 + AscensionChallenge.AscensionCorruptResist(enemy);
                if (enemyResist == 1.0f) {
                    enemyResist = 4.0f;
                }
            } else {
                enemyResist = 1 + AscensionChallenge.AscensionCorruptResist(enemy);
            }
            enemyResist = (float)((double)enemyResist * (1.0 + 4.0 * Math.pow((float)enemy.HP / (float)enemy.HT, 2.0)));
            for (Buff buff : enemy.buffs()) {
                if (MAJOR_DEBUFFS.containsKey(buff.getClass())) {
                    enemyResist *= 0.5f;
                    continue;
                }
                if (MINOR_DEBUFFS.containsKey(buff.getClass())) {
                    enemyResist *= 0.75f;
                    continue;
                }
                if (buff.type != Buff.buffType.NEGATIVE) continue;
                enemyResist *= 0.75f;
            }
            if (enemy.buff(Corruption.class) != null || enemy.buff(Doom.class) != null) {
                corruptingPower = enemyResist - 0.001f;
            }
            if (corruptingPower > enemyResist) {
                this.corruptEnemy(enemy);
            } else {
                float debuffChance = corruptingPower / enemyResist;
                if (Random.Float() < debuffChance) {
                    this.debuffEnemy(enemy, MAJOR_DEBUFFS);
                } else {
                    this.debuffEnemy(enemy, MINOR_DEBUFFS);
                }
            }
            this.wandProc(ch, this.chargesPerCast());
            Sample.INSTANCE.play("sounds/hit_magic.mp3", 1.0f, 0.8f * Random.Float(0.87f, 1.15f));
        } else {
            Dungeon.level.pressCell(bolt.collisionPos);
        }
    }

    private void debuffEnemy(Mob enemy, HashMap<Class<? extends Buff>, Float> category) {
        HashMap<Class<? extends Buff>, Float> debuffs = new HashMap<Class<? extends Buff>, Float>(category);
        for (Buff buff : enemy.buffs()) {
            if (!debuffs.containsKey(buff.getClass())) continue;
            debuffs.put(buff.getClass(), Float.valueOf(0.0f));
        }
        for (Class clazz : debuffs.keySet()) {
            if (!(debuffs.get(clazz).floatValue() > 0.0f) || !enemy.isImmune(clazz)) continue;
            debuffs.put(clazz, Float.valueOf(0.0f));
        }
        Class<? extends Buff> debuffCls = Random.chances(debuffs);
        if (debuffCls != null) {
            Buff.append(enemy, debuffCls, 6 + this.buffedLvl() * 3);
        } else if (category == MINOR_DEBUFFS) {
            this.debuffEnemy(enemy, MAJOR_DEBUFFS);
        } else if (category == MAJOR_DEBUFFS) {
            this.corruptEnemy(enemy);
        }
    }

    private void corruptEnemy(Mob enemy) {
        if (enemy.buff(Corruption.class) != null || enemy.buff(Doom.class) != null) {
            GLog.w(Messages.get(this, "already_corrupted", new Object[0]), new Object[0]);
            return;
        }
        if (!enemy.isImmune(Corruption.class)) {
            Corruption.corruptionHeal(enemy);
            AllyBuff.affectAndLoot(enemy, curUser, Corruption.class);
        } else {
            Buff.affect(enemy, Doom.class);
        }
    }

    @Override
    public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
        int level = Math.max(0, this.buffedLvl());
        float procChance = ((float)level + 1.0f) / ((float)level + 6.0f) * WandOfCorruption.procChanceMultiplier(attacker);
        if (Random.Float() < procChance) {
            float powerMulti = Math.max(1.0f, procChance);
            Buff.prolong(defender, Amok.class, Math.round((float)(4 + level * 2) * powerMulti));
        }
    }

    @Override
    public String upgradeStat1(int level) {
        return Messages.decimalFormat("#.##", 3.0f + (float)level / 3.0f);
    }

    @Override
    public String upgradeStat2(int level) {
        return Integer.toString(6 + 3 * level);
    }

    @Override
    public void fx(Ballistica bolt, Callback callback) {
        MagicMissile.boltFromChar(WandOfCorruption.curUser.sprite.parent, 7, WandOfCorruption.curUser.sprite, bolt.collisionPos, callback);
        Sample.INSTANCE.play("sounds/zap.mp3");
    }

    @Override
    public void staffFx(MagesStaff.StaffParticle particle) {
        particle.color(0);
        particle.am = 0.6f;
        particle.setLifespan(2.0f);
        particle.speed.set(0.0f, 5.0f);
        particle.setSize(0.5f, 2.0f);
        particle.shuffleXY(1.0f);
    }

    static {
        MINOR_DEBUFFS.put(Weakness.class, Float.valueOf(2.0f));
        MINOR_DEBUFFS.put(Vulnerable.class, Float.valueOf(2.0f));
        MINOR_DEBUFFS.put(Cripple.class, Float.valueOf(1.0f));
        MINOR_DEBUFFS.put(Blindness.class, Float.valueOf(1.0f));
        MINOR_DEBUFFS.put(Terror.class, Float.valueOf(1.0f));
        MINOR_DEBUFFS.put(Chill.class, Float.valueOf(0.0f));
        MINOR_DEBUFFS.put(Ooze.class, Float.valueOf(0.0f));
        MINOR_DEBUFFS.put(Roots.class, Float.valueOf(0.0f));
        MINOR_DEBUFFS.put(Vertigo.class, Float.valueOf(0.0f));
        MINOR_DEBUFFS.put(Drowsy.class, Float.valueOf(0.0f));
        MINOR_DEBUFFS.put(Bleeding.class, Float.valueOf(0.0f));
        MINOR_DEBUFFS.put(Burning.class, Float.valueOf(0.0f));
        MINOR_DEBUFFS.put(Poison.class, Float.valueOf(0.0f));
        MAJOR_DEBUFFS = new HashMap();
        MAJOR_DEBUFFS.put(Amok.class, Float.valueOf(3.0f));
        MAJOR_DEBUFFS.put(Slow.class, Float.valueOf(2.0f));
        MAJOR_DEBUFFS.put(Hex.class, Float.valueOf(2.0f));
        MAJOR_DEBUFFS.put(Paralysis.class, Float.valueOf(1.0f));
        MAJOR_DEBUFFS.put(Daze.class, Float.valueOf(0.0f));
        MAJOR_DEBUFFS.put(Dread.class, Float.valueOf(0.0f));
        MAJOR_DEBUFFS.put(Charm.class, Float.valueOf(0.0f));
        MAJOR_DEBUFFS.put(MagicalSleep.class, Float.valueOf(0.0f));
        MAJOR_DEBUFFS.put(SoulMark.class, Float.valueOf(0.0f));
        MAJOR_DEBUFFS.put(Corrosion.class, Float.valueOf(0.0f));
        MAJOR_DEBUFFS.put(Frost.class, Float.valueOf(0.0f));
        MAJOR_DEBUFFS.put(Doom.class, Float.valueOf(0.0f));
    }
}

