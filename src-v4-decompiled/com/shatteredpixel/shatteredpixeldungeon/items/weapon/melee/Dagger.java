/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;

public class Dagger
extends MeleeWeapon {
    public Dagger() {
        this.image = ItemSpriteSheet.DAGGER;
        this.hitSound = "sounds/hit_stab.mp3";
        this.hitSoundPitch = 1.1f;
        this.tier = 1;
        this.bones = false;
    }

    @Override
    public int max(int lvl) {
        return 4 * (this.tier + 1) + lvl * (this.tier + 1);
    }

    @Override
    public int damageRoll(Char owner) {
        Hero hero;
        Char enemy;
        if (owner instanceof Hero && (enemy = (hero = (Hero)owner).attackTarget()) instanceof Mob && ((Mob)enemy).surprisedBy(hero)) {
            int diff = this.max() - this.min();
            int damage = this.augment.damageFactor(Hero.heroDamageIntRange(this.min() + Math.round((float)diff * 0.75f), this.max()));
            int exStr = hero.STR() - this.STRReq();
            if (exStr > 0) {
                damage += Hero.heroDamageIntRange(0, exStr);
            }
            return damage;
        }
        return super.damageRoll(owner);
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt", new Object[0]);
    }

    @Override
    public boolean useTargeting() {
        return false;
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        Dagger.sneakAbility(hero, target, 5, 2 + this.buffedLvl(), this);
    }

    @Override
    public String abilityInfo() {
        if (this.levelKnown) {
            return Messages.get(this, "ability_desc", 2 + this.buffedLvl());
        }
        return Messages.get(this, "typical_ability_desc", 2);
    }

    @Override
    public String upgradeAbilityStat(int level) {
        return Integer.toString(2 + level);
    }

    public static void sneakAbility(Hero hero, Integer target, int maxDist, int invisTurns, MeleeWeapon wep) {
        if (target == null) {
            return;
        }
        PathFinder.buildDistanceMap(Dungeon.hero.pos, BArray.or(Dungeon.level.passable, Dungeon.level.avoid, null), maxDist);
        if (PathFinder.distance[target] == Integer.MAX_VALUE || !Dungeon.level.heroFOV[target] || hero.rooted) {
            GLog.w(Messages.get(wep, "ability_target_range", new Object[0]), new Object[0]);
            if (Dungeon.hero.rooted) {
                PixelScene.shake(1.0f, 1.0f);
            }
            return;
        }
        if (Actor.findChar(target) != null) {
            GLog.w(Messages.get(wep, "ability_occupied", new Object[0]), new Object[0]);
            return;
        }
        wep.beforeAbilityUsed(hero, null);
        Buff.prolong(hero, Invisibility.class, invisTurns - 1);
        Dungeon.hero.sprite.turnTo(Dungeon.hero.pos, target);
        Dungeon.hero.pos = target;
        Dungeon.level.occupyCell(Dungeon.hero);
        Dungeon.observe();
        GameScene.updateFog();
        Dungeon.hero.checkVisibleMobs();
        Dungeon.hero.sprite.place(Dungeon.hero.pos);
        CellEmitter.get(Dungeon.hero.pos).burst(Speck.factory(7), 6);
        Sample.INSTANCE.play("sounds/puff.mp3");
        hero.next();
        wep.afterAbilityUsed(hero);
    }
}

