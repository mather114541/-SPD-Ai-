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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM100;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.journal.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SentrySprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class VaultSentry
extends NPC {
    public float scanWidth;
    public float scanLength;
    public int[][] scanDirs;
    public int scanDirIdx;
    public int curCooldown;
    public int afterScanCooldown;
    public int scansAfterCooldown;
    private int scansMade;
    public boolean giveWarning;
    private static long SFXLastPlayed = 0L;
    private boolean seen;
    private ArrayList<Integer> recentZaps;
    private static final String SCAN_WIDTH = "scan_width";
    private static final String SCAN_LENGTH = "scan_length";
    private static final String SCAN_DIR_IDX = "scan_dir_idx";
    private static final String SCAN_DIRS = "scan_dirs_";
    private static final String SCAN_DIRS_LEN = "scan_dirs_len";
    private static final String AFTER_SCAN_COOLDOWN = "after_shot_cooldown";
    private static final String CUR_COOLDOWN = "cur_cooldown";
    private static final String SCANS = "shots";
    private static final String SCANS_MADE = "shots_fired";
    private static final String WARNING = "warning";
    private static final String RECENT_ZAPS = "recent_zaps";

    public VaultSentry() {
        this.spriteClass = SentrySprite.VaultScan.class;
        this.properties.add(Char.Property.IMMOVABLE);
        this.curCooldown = 1;
        this.afterScanCooldown = 1;
        this.scansAfterCooldown = 1;
        this.scansMade = 0;
        this.giveWarning = false;
        this.seen = false;
        this.recentZaps = new ArrayList();
    }

    @Override
    protected boolean act() {
        if (!this.seen && Dungeon.level.heroFOV[this.pos]) {
            Bestiary.setSeen(this.getClass());
            this.seen = true;
        }
        if (this.fieldOfView == null || this.fieldOfView.length != Dungeon.level.length()) {
            this.fieldOfView = new boolean[Dungeon.level.length()];
        }
        Dungeon.level.updateFieldOfView(this, this.fieldOfView);
        --this.curCooldown;
        ArrayList<Integer> curZaps = new ArrayList<Integer>();
        if (this.curCooldown <= 0) {
            int[] scanDirsThisTurn = this.scanDirs[this.scanDirIdx];
            boolean visible = false;
            int[] nArray = scanDirsThisTurn;
            int n = nArray.length;
            for (int i = 0; i < n; ++i) {
                int scanDir = nArray[i];
                Ballistica aim = new Ballistica(this.pos, scanDir, 0);
                ConeAOE scan = new ConeAOE(aim, this.scanLength, this.scanWidth, 5);
                if (scan.cells.isEmpty() && aim.path.size() >= 2) {
                    scan.cells.add(aim.path.get(1));
                }
                for (int cell : scan.cells) {
                    if (!this.fieldOfView[cell] || !Dungeon.level.heroFOV[cell]) continue;
                    visible = true;
                    break;
                }
                if (!visible) continue;
                for (int cell : scan.cells) {
                    if (!this.fieldOfView[cell]) continue;
                    Char ch = Actor.findChar(cell);
                    if (ch != null && ch.alignment == Char.Alignment.ALLY && ch.invisible == 0) {
                        if (this.recentZaps.contains(ch.id())) {
                            ch.damage(Random.NormalIntRange(3, 6), new DM100.LightningBolt());
                        } else {
                            ch.damage(Random.NormalIntRange(6, 12), new DM100.LightningBolt());
                        }
                        curZaps.add(ch.id());
                        if (ch.sprite.visible || this.sprite.visible) {
                            Sample.INSTANCE.play("sounds/lightning.mp3");
                            this.sprite.parent.add(new Lightning(this.sprite.center(), ch.sprite.destinationCenter(), null));
                            if (ch.sprite.visible) {
                                Emitter e = GameScene.emitter();
                                if (e != null) {
                                    e.pos(ch.sprite.destinationCenter());
                                    e.burst(SparkParticle.FACTORY, 3);
                                }
                                ch.sprite.flash();
                            }
                        }
                        if (ch == Dungeon.hero) {
                            if (Imp.Quest.hazardFreebies > 0) {
                                --Imp.Quest.hazardFreebies;
                            } else {
                                Statistics.questScores[3] = Statistics.questScores[3] - 100;
                            }
                            if (!ch.isAlive()) {
                                Badges.validateDeathFromEnemyMagic();
                                Dungeon.fail(this);
                                GLog.n(Messages.get(this, "ondeath", new Object[0]), new Object[0]);
                            }
                        }
                    }
                    GameScene.checkedCell(cell, this.pos);
                }
            }
            if (visible && SFXLastPlayed + 80L < ShatteredPixelDungeon.realTime) {
                Sample.INSTANCE.play("sounds/zap.mp3", 0.5f);
                SFXLastPlayed = ShatteredPixelDungeon.realTime;
            }
            ++this.scanDirIdx;
            if (this.scanDirIdx >= this.scanDirs.length) {
                this.scanDirIdx = 0;
            }
            ++this.scansMade;
            if (this.scansMade < this.scansAfterCooldown) {
                this.curCooldown = 1;
            } else {
                this.scansMade = 0;
                this.curCooldown = this.afterScanCooldown;
            }
        }
        this.recentZaps.clear();
        this.recentZaps.addAll(curZaps);
        if (this.curCooldown == 1 && this.giveWarning) {
            int[] scanDirsNextTurn;
            for (int scanDir : scanDirsNextTurn = this.scanDirs[this.scanDirIdx]) {
                Ballistica aim = new Ballistica(this.pos, scanDir, 0);
                ConeAOE scan = new ConeAOE(aim, this.scanLength, this.scanWidth, 5);
                if (scan.cells.isEmpty() && aim.path.size() >= 2) {
                    scan.cells.add(aim.path.get(1));
                }
                boolean visible = false;
                for (int cell : scan.cells) {
                    if (!Dungeon.level.heroFOV[cell] || !this.fieldOfView[cell]) continue;
                    visible = true;
                }
                if (!visible) continue;
                for (int cell : scan.cells) {
                    if (!this.fieldOfView[cell]) continue;
                    Dungeon.hero.interrupt();
                    GameScene.targetedCell(cell, 0xFF0000, 1.0f);
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
        bundle.put(SCAN_WIDTH, this.scanWidth);
        bundle.put(SCAN_LENGTH, this.scanLength);
        bundle.put(SCAN_DIR_IDX, this.scanDirIdx);
        bundle.put(SCAN_DIRS_LEN, this.scanDirs.length);
        for (int i = 0; i < this.scanDirs.length; ++i) {
            bundle.put(SCAN_DIRS + i, this.scanDirs[i]);
        }
        bundle.put(AFTER_SCAN_COOLDOWN, this.afterScanCooldown);
        bundle.put(CUR_COOLDOWN, this.curCooldown);
        bundle.put(SCANS, this.scansAfterCooldown);
        bundle.put(SCANS_MADE, this.scansMade);
        bundle.put(WARNING, this.giveWarning);
        int[] recent = new int[this.recentZaps.size()];
        for (int i = 0; i < recent.length; ++i) {
            recent[i] = this.recentZaps.get(i);
        }
        bundle.put(RECENT_ZAPS, recent);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.scanWidth = bundle.getFloat(SCAN_WIDTH);
        this.scanLength = bundle.getFloat(SCAN_LENGTH);
        this.scanDirIdx = bundle.getInt(SCAN_DIR_IDX);
        this.scanDirs = new int[bundle.getInt(SCAN_DIRS_LEN)][];
        for (int i = 0; i < this.scanDirs.length; ++i) {
            this.scanDirs[i] = bundle.getIntArray(SCAN_DIRS + i);
        }
        if (bundle.contains(AFTER_SCAN_COOLDOWN)) {
            this.afterScanCooldown = bundle.getInt(AFTER_SCAN_COOLDOWN);
            this.curCooldown = bundle.getInt(CUR_COOLDOWN);
            this.scansAfterCooldown = bundle.getInt(SCANS);
            this.scansMade = bundle.getInt(SCANS_MADE);
            this.giveWarning = bundle.getBoolean(WARNING);
        }
        if (bundle.contains(RECENT_ZAPS)) {
            this.recentZaps.clear();
            for (int i : bundle.getIntArray(RECENT_ZAPS)) {
                this.recentZaps.add(i);
            }
        }
    }
}

