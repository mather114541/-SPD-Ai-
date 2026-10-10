/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.HolyWard;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.ShieldOfLight;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLivingEarth;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Earthroot;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SkeletonSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.TargetHealthIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Skeleton
extends Mob {
    public Skeleton() {
        this.spriteClass = SkeletonSprite.class;
        this.HT = 25;
        this.HP = 25;
        this.defenseSkill = 9;
        this.EXP = 5;
        this.maxLvl = 10;
        this.loot = Generator.Category.WEAPON;
        this.lootChance = 0.1667f;
        this.properties.add(Char.Property.UNDEAD);
        this.properties.add(Char.Property.INORGANIC);
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(2, 10);
    }

    @Override
    public void die(Object cause) {
        super.die(cause);
        if (cause == Chasm.class) {
            return;
        }
        boolean heroKilled = false;
        for (int i = 0; i < PathFinder.NEIGHBOURS8.length; ++i) {
            Char ch = Skeleton.findChar(this.pos + PathFinder.NEIGHBOURS8[i]);
            if (ch == null || !ch.isAlive()) continue;
            int damage = Math.round(Random.NormalIntRange(6, 12));
            damage = Math.round((float)damage * AscensionChallenge.statModifier(this));
            WandOfLivingEarth.RockArmor rockArmor = ch.buff(WandOfLivingEarth.RockArmor.class);
            if (rockArmor != null) {
                int preDmg = damage;
                damage = rockArmor.absorb(damage);
                damage *= Math.round((float)damage / (float)preDmg);
            }
            Earthroot.Armor armor = ch.buff(Earthroot.Armor.class);
            if (damage > 0 && armor != null) {
                int preDmg = damage;
                damage = armor.absorb(damage);
                damage -= preDmg - damage;
            }
            if (ch.buff(MagicImmune.class) == null) {
                ShieldOfLight.ShieldOfLightTracker shield = ch.buff(ShieldOfLight.ShieldOfLightTracker.class);
                if (shield != null && shield.object == this.id()) {
                    int min = 1 + Dungeon.hero.pointsInTalent(Talent.SHIELD_OF_LIGHT);
                    damage -= Random.NormalIntRange(min, 2 * min);
                    damage -= Random.NormalIntRange(min, 2 * min);
                    damage = Math.max(damage, 0);
                } else if (ch == Dungeon.hero && Dungeon.hero.heroClass != HeroClass.CLERIC && Dungeon.hero.hasTalent(Talent.SHIELD_OF_LIGHT) && TargetHealthIndicator.instance.target() == this && Random.Int(6) < 1 + Dungeon.hero.pointsInTalent(Talent.SHIELD_OF_LIGHT)) {
                    damage -= 2;
                }
                if (ch.buff(HolyWard.HolyArmBuff.class) != null) {
                    damage -= Dungeon.hero.subClass == HeroSubClass.PALADIN ? 6 : 2;
                }
            }
            damage = Math.max(0, damage - (ch.drRoll() + ch.drRoll()));
            ch.damage(damage, this);
            if (ch != Dungeon.hero || ch.isAlive()) continue;
            heroKilled = true;
        }
        if (Dungeon.level.heroFOV[this.pos]) {
            Sample.INSTANCE.play("sounds/bones.mp3");
        }
        if (heroKilled) {
            Dungeon.fail(this);
            GLog.n(Messages.get(this, "explo_kill", new Object[0]), new Object[0]);
        }
    }

    @Override
    public float lootChance() {
        return super.lootChance() * (float)Math.pow(0.3333333432674408, Dungeon.LimitedDrops.SKELE_WEP.count);
    }

    @Override
    public Item createLoot() {
        ++Dungeon.LimitedDrops.SKELE_WEP.count;
        return super.createLoot();
    }

    @Override
    public int attackSkill(Char target) {
        return 12;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 5);
    }
}

