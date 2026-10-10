/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Eye;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PurpleParticle;
import com.shatteredpixel.shatteredpixeldungeon.journal.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SentrySprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class VaultLaser
extends NPC {
    public int[] laserDirs;
    public int laserDirIdx;
    public int curCooldown;
    public int afterShotCooldown;
    public int shotsAfterCooldown;
    private int shotsFired;
    public boolean giveWarning;
    private static long SFXLastPlayed = 0L;
    private boolean seen;
    private static final String LASER_DIRS = "laser_dirs";
    private static final String LASER_DIR_IDX = "laser_dir_idx";
    private static final String AFTER_SHOT_COOLDOWN = "after_shot_cooldown";
    private static final String CUR_COOLDOWN = "cur_cooldown";
    private static final String SHOTS = "shots";
    private static final String SHOTS_FIRED = "shots_fired";
    private static final String WARNING = "warning";

    public VaultLaser() {
        this.spriteClass = SentrySprite.VaultLaser.class;
        this.properties.add(Char.Property.IMMOVABLE);
        this.shotsAfterCooldown = 1;
        this.shotsFired = 0;
        this.giveWarning = true;
        this.seen = false;
    }

    @Override
    protected boolean act() {
        boolean visible;
        if (!this.seen && Dungeon.level.heroFOV[this.pos]) {
            Bestiary.setSeen(this.getClass());
            this.seen = true;
        }
        --this.curCooldown;
        if (this.curCooldown <= 0) {
            Ballistica beam = new Ballistica(this.pos, this.laserDirs[this.laserDirIdx], 4);
            visible = false;
            boolean observe = false;
            for (int cell : beam.subPath(1, beam.dist)) {
                Char ch;
                if (Dungeon.level.heroFOV[cell]) {
                    visible = true;
                }
                if (Dungeon.level.flamable[cell]) {
                    Dungeon.level.destroy(cell);
                    observe = true;
                    GameScene.updateMap(cell);
                }
                if ((ch = Actor.findChar(cell)) == null || ch.alignment != Char.Alignment.ALLY) continue;
                ch.damage(Random.NormalIntRange(10, 20), new Eye.DeathGaze());
                if (ch.sprite.visible) {
                    ch.sprite.flash();
                    CellEmitter.center(this.pos).burst(PurpleParticle.BURST, Random.IntRange(1, 2));
                }
                if (ch != Dungeon.hero) continue;
                Sample.INSTANCE.play("sounds/ray.mp3");
                SFXLastPlayed = ShatteredPixelDungeon.realTime;
                if (Imp.Quest.hazardFreebies > 0) {
                    --Imp.Quest.hazardFreebies;
                } else {
                    Statistics.questScores[3] = Statistics.questScores[3] - 100;
                }
                if (ch.isAlive()) continue;
                Badges.validateDeathFromEnemyMagic();
                Dungeon.fail(this);
                GLog.n(Messages.get(this, "ondeath", new Object[0]), new Object[0]);
            }
            if (visible) {
                this.sprite.parent.add(new Beam.DeathRay(this.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(beam.collisionPos)));
                if (SFXLastPlayed + 80L < ShatteredPixelDungeon.realTime) {
                    Sample.INSTANCE.play("sounds/ray.mp3", 0.5f);
                    SFXLastPlayed = ShatteredPixelDungeon.realTime;
                }
            }
            if (observe) {
                Dungeon.observe();
            }
            ++this.laserDirIdx;
            if (this.laserDirIdx >= this.laserDirs.length) {
                this.laserDirIdx = 0;
            }
            ++this.shotsFired;
            if (this.shotsFired < this.shotsAfterCooldown) {
                this.curCooldown = 1;
            } else {
                this.shotsFired = 0;
                this.curCooldown = this.afterShotCooldown;
            }
        }
        if (this.curCooldown == 1 && this.giveWarning) {
            Ballistica nextBeam = new Ballistica(this.pos, this.laserDirs[this.laserDirIdx], 4);
            visible = false;
            for (int cell : nextBeam.subPath(1, nextBeam.dist)) {
                if (!Dungeon.level.heroFOV[cell]) continue;
                visible = true;
                break;
            }
            if (visible) {
                for (int cell : nextBeam.subPath(1, nextBeam.dist)) {
                    GameScene.targetedCell(cell, 1.0f);
                    if (Actor.findChar(cell) != Dungeon.hero) continue;
                    Dungeon.hero.interrupt();
                }
            }
        }
        this.throwItems();
        this.spend(1.0f);
        return true;
    }

    @Override
    public boolean isImmune(Class effect) {
        return true;
    }

    @Override
    public boolean isInvulnerable(Class effect) {
        return true;
    }

    @Override
    public boolean reset() {
        return true;
    }

    @Override
    public boolean interact(Char c) {
        return true;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LASER_DIRS, this.laserDirs);
        bundle.put(LASER_DIR_IDX, this.laserDirIdx);
        bundle.put(AFTER_SHOT_COOLDOWN, this.afterShotCooldown);
        bundle.put(CUR_COOLDOWN, this.curCooldown);
        bundle.put(SHOTS, this.shotsAfterCooldown);
        bundle.put(SHOTS_FIRED, this.shotsFired);
        bundle.put(WARNING, this.giveWarning);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.laserDirs = bundle.getIntArray(LASER_DIRS);
        this.laserDirIdx = bundle.getInt(LASER_DIR_IDX);
        if (bundle.contains(AFTER_SHOT_COOLDOWN)) {
            this.afterShotCooldown = bundle.getInt(AFTER_SHOT_COOLDOWN);
            this.curCooldown = bundle.getInt(CUR_COOLDOWN);
            this.shotsAfterCooldown = bundle.getInt(SHOTS);
            this.shotsFired = bundle.getInt(SHOTS_FIRED);
            this.giveWarning = bundle.getBoolean(WARNING);
        } else {
            this.afterShotCooldown = Random.IntRange(3, 7);
            this.curCooldown = Random.IntRange(1, this.afterShotCooldown);
        }
    }
}

