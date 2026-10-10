/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PurpleParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Dewdrop;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfDisintegration;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DisintegrationTrap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.EyeSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Eye
extends Mob {
    private Ballistica beam;
    private int beamTarget;
    private int beamCooldown;
    public boolean beamCharged;
    private static final String BEAM_TARGET = "beamTarget";
    private static final String BEAM_COOLDOWN = "beamCooldown";
    private static final String BEAM_CHARGED = "beamCharged";

    public Eye() {
        this.spriteClass = EyeSprite.class;
        this.HT = 100;
        this.HP = 100;
        this.defenseSkill = 20;
        this.viewDistance = 6;
        this.EXP = 13;
        this.maxLvl = 26;
        this.flying = true;
        this.HUNTING = new Hunting();
        this.loot = new Dewdrop();
        this.lootChance = 1.0f;
        this.properties.add(Char.Property.DEMONIC);
        this.beamTarget = -1;
        this.resistances.add(WandOfDisintegration.class);
        this.resistances.add(DeathGaze.class);
        this.resistances.add(DisintegrationTrap.class);
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(20, 30);
    }

    @Override
    public int attackSkill(Char target) {
        return 30;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 10);
    }

    @Override
    protected boolean canAttack(Char enemy) {
        if (this.beamCooldown == 0) {
            Ballistica aim = new Ballistica(this.pos, enemy.pos, 4);
            if (enemy.invisible == 0 && !this.isCharmedBy(enemy) && this.fieldOfView[enemy.pos] && (super.canAttack(enemy) || aim.subPath(1, aim.dist).contains(enemy.pos))) {
                this.beam = aim;
                this.beamTarget = enemy.pos;
                return true;
            }
            return this.beamCharged;
        }
        return super.canAttack(enemy);
    }

    @Override
    protected boolean act() {
        if (this.beamCharged && this.state != this.HUNTING) {
            this.beamCharged = false;
            this.sprite.idle();
        }
        if (this.beam == null && this.beamTarget != -1) {
            this.beam = new Ballistica(this.pos, this.beamTarget, 4);
            this.sprite.turnTo(this.pos, this.beamTarget);
        }
        if (this.beamCooldown > 0) {
            --this.beamCooldown;
        }
        return super.act();
    }

    @Override
    protected boolean doAttack(Char enemy) {
        this.beam = new Ballistica(this.pos, this.beamTarget, 4);
        if (this.beamCooldown > 0 || !this.beamCharged && !this.beam.subPath(1, this.beam.dist).contains(enemy.pos)) {
            return super.doAttack(enemy);
        }
        if (!this.beamCharged) {
            ((EyeSprite)this.sprite).charge(enemy.pos);
            this.spend(this.attackDelay() * 2.0f);
            this.beamCharged = true;
            return true;
        }
        this.spend(this.attackDelay());
        if (Dungeon.level.heroFOV[this.pos] || Dungeon.level.heroFOV[this.beam.collisionPos]) {
            this.sprite.zap(this.beam.collisionPos);
            return false;
        }
        this.sprite.idle();
        this.deathGaze();
        return true;
    }

    @Override
    public void damage(int dmg, Object src) {
        if (this.beamCharged) {
            dmg /= 4;
        }
        super.damage(dmg, src);
    }

    @Override
    public void die(Object cause) {
        this.flying = false;
        super.die(cause);
    }

    public void deathGaze() {
        if (!this.beamCharged || this.beamCooldown > 0 || this.beam == null) {
            return;
        }
        this.beamCharged = false;
        this.beamCooldown = Random.IntRange(4, 6);
        boolean terrainAffected = false;
        Invisibility.dispel(this);
        for (int pos : this.beam.subPath(1, this.beam.dist)) {
            Char ch;
            if (Dungeon.level.flamable[pos]) {
                Dungeon.level.destroy(pos);
                GameScene.updateMap(pos);
                terrainAffected = true;
            }
            if ((ch = Actor.findChar(pos)) == null) continue;
            if (Eye.hit(this, ch, true)) {
                int dmg = Random.NormalIntRange(30, 50);
                dmg = Math.round((float)dmg * AscensionChallenge.statModifier(this));
                ch.damage(dmg, new DeathGaze());
                if (Dungeon.level.heroFOV[pos]) {
                    ch.sprite.flash();
                    CellEmitter.center(pos).burst(PurpleParticle.BURST, Random.IntRange(1, 2));
                }
                if (ch.isAlive() || ch != Dungeon.hero) continue;
                Badges.validateDeathFromEnemyMagic();
                Dungeon.fail(this);
                GLog.n(Messages.get(this, "deathgaze_kill", new Object[0]), new Object[0]);
                continue;
            }
            ch.sprite.showStatus(0xFFFF00, ch.defenseVerb(), new Object[0]);
        }
        if (terrainAffected) {
            Dungeon.observe();
        }
        this.beam = null;
        this.beamTarget = -1;
    }

    @Override
    public Item createLoot() {
        Item loot;
        switch (Random.Int(4)) {
            default: {
                int ofs;
                loot = new Dewdrop();
                while (Dungeon.level.solid[this.pos + (ofs = PathFinder.NEIGHBOURS8[Random.Int(8)])] && !Dungeon.level.passable[this.pos + ofs]) {
                }
                if (Dungeon.level.heaps.get(this.pos + ofs) == null) {
                    Dungeon.level.drop((Item)new Dewdrop(), (int)(this.pos + ofs)).sprite.drop(this.pos);
                    break;
                }
                Dungeon.level.drop((Item)new Dewdrop(), (int)(this.pos + ofs)).sprite.drop(this.pos + ofs);
                break;
            }
            case 2: {
                loot = Generator.randomUsingDefaults(Generator.Category.SEED);
                break;
            }
            case 3: {
                loot = Generator.randomUsingDefaults(Generator.Category.STONE);
            }
        }
        return loot;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(BEAM_TARGET, this.beamTarget);
        bundle.put(BEAM_COOLDOWN, this.beamCooldown);
        bundle.put(BEAM_CHARGED, this.beamCharged);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if (bundle.contains(BEAM_TARGET)) {
            this.beamTarget = bundle.getInt(BEAM_TARGET);
        }
        this.beamCooldown = bundle.getInt(BEAM_COOLDOWN);
        this.beamCharged = bundle.getBoolean(BEAM_CHARGED);
    }

    private class Hunting
    extends Mob.Hunting {
        private Hunting() {
            super(Eye.this);
        }

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            if (Eye.this.beamCharged && Eye.this.enemy != null && Eye.this.canAttack(Eye.this.enemy)) {
                Eye.this.enemySeen = enemyInFOV;
                return Eye.this.doAttack(Eye.this.enemy);
            }
            return super.act(enemyInFOV, justAlerted);
        }
    }

    public static class DeathGaze {
    }
}

