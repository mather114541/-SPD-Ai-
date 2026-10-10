/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import java.util.Arrays;

public class VaultFlameTraps
extends Blob {
    public int[] afterTriggerCooldowns;
    public int[] curCooldowns;
    public int[] triggersAfterCooldown;
    private static long SFXLastPlayed = 0L;
    private static final String AFTER_TRIGGER_CDS = "after_trigger_cds";
    private static final String CUR_COOLDOWNS = "cur_cooldowns";
    private static final String TRIGGERS = "triggers";

    @Override
    public boolean act() {
        super.act();
        if (this.afterTriggerCooldowns != null) {
            for (int i = 0; i < this.afterTriggerCooldowns.length; ++i) {
                if (this.afterTriggerCooldowns[i] <= -1) continue;
                if (this.curCooldowns[i] <= 0) {
                    this.curCooldowns[i] = this.afterTriggerCooldowns[i];
                }
                int n = i;
                this.curCooldowns[n] = this.curCooldowns[n] - 1;
                if (this.curCooldowns[i] > 0) continue;
                this.seed(Dungeon.level, i, this.triggersAfterCooldown[i]);
            }
        }
        return true;
    }

    @Override
    protected void evolve() {
        boolean playSfx = false;
        for (int i = this.area.left; i < this.area.right; ++i) {
            for (int j = this.area.top; j < this.area.bottom; ++j) {
                int cell = i + j * Dungeon.level.width();
                if (this.cur[cell] > 0) {
                    Char ch = Actor.findChar(cell);
                    if (ch != null && !ch.isImmune(this.getClass())) {
                        Plant plant;
                        Heap heap;
                        if (ch == Dungeon.hero) {
                            Sample.INSTANCE.play("sounds/burning.mp3");
                            SFXLastPlayed = ShatteredPixelDungeon.realTime;
                            if (Imp.Quest.hazardFreebies > 0) {
                                --Imp.Quest.hazardFreebies;
                            } else {
                                Statistics.questScores[3] = Statistics.questScores[3] - 100;
                            }
                        }
                        if (!ch.isImmune(Fire.class)) {
                            Buff.affect(ch, Burning.class).reignite(ch, 4.0f);
                        }
                        if ((heap = (Heap)Dungeon.level.heaps.get(cell)) != null) {
                            heap.burn();
                        }
                        if ((plant = (Plant)Dungeon.level.plants.get(cell)) != null) {
                            plant.wither();
                        }
                    }
                    if (Dungeon.level.heroFOV[cell]) {
                        CellEmitter.get(cell).start(ElmoParticle.FACTORY, 0.02f, 10);
                        playSfx = true;
                    }
                    this.off[cell] = this.cur[cell] - 1;
                    this.volume += this.off[cell];
                    continue;
                }
                this.off[cell] = 0;
            }
        }
        if (playSfx && SFXLastPlayed + 80L < ShatteredPixelDungeon.realTime) {
            Sample.INSTANCE.play("sounds/burning.mp3", 0.5f);
            SFXLastPlayed = ShatteredPixelDungeon.realTime;
        }
    }

    @Override
    public void seed(Level level, int cell, int amount) {
        super.seed(level, cell, amount);
        if (this.afterTriggerCooldowns == null) {
            this.afterTriggerCooldowns = new int[level.length()];
            Arrays.fill(this.afterTriggerCooldowns, -1);
        }
        if (this.curCooldowns == null) {
            this.curCooldowns = new int[level.length()];
        }
        if (this.triggersAfterCooldown == null) {
            this.triggersAfterCooldown = new int[level.length()];
        }
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        if (this.afterTriggerCooldowns != null) {
            bundle.put(AFTER_TRIGGER_CDS, this.afterTriggerCooldowns);
            bundle.put(CUR_COOLDOWNS, this.curCooldowns);
            bundle.put(TRIGGERS, this.triggersAfterCooldown);
        }
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if (bundle.contains(AFTER_TRIGGER_CDS)) {
            this.afterTriggerCooldowns = bundle.getIntArray(AFTER_TRIGGER_CDS);
            this.curCooldowns = bundle.getIntArray(CUR_COOLDOWNS);
            this.triggersAfterCooldown = bundle.getIntArray(TRIGGERS);
        }
    }

    @Override
    public void use(BlobEmitter emitter) {
        super.use(emitter);
        emitter.bound.set(0.4f, 0.4f, 0.6f, 0.6f);
        emitter.pour(ElmoParticle.FACTORY, 0.3f);
    }

    @Override
    public String tileDesc() {
        return Messages.get(this, "desc", new Object[0]);
    }
}

