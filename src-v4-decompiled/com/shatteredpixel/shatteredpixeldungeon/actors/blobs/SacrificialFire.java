/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Bee;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Piranha;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Statue;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Swarm;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Wraith;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SacrificialParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SacrificeRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class SacrificialFire
extends Blob {
    BlobEmitter curEmitter;
    private int bonusSpawns;
    private Item prize;
    private static final String BONUS_SPAWNS = "bonus_spawns";
    private static final String PRIZE = "prize";

    public SacrificialFire() {
        this.actPriority = -21;
        this.bonusSpawns = 3;
        // headless: use() is never called (no render loop), so back curEmitter with
        // a no-op emitter to keep evolve()'s pour() safe
        if (rl.RLMode.headless) {
            this.curEmitter = new rl.RLNoopEmitter(this);
        }
    }

    @Override
    public Notes.Landmark landmark() {
        return Notes.Landmark.SACRIFICIAL_FIRE;
    }

    @Override
    protected void evolve() {
        for (int i = this.area.top - 1; i <= this.area.bottom; ++i) {
            for (int j = this.area.left - 1; j <= this.area.right; ++j) {
                int cell = j + i * Dungeon.level.width();
                if (!Dungeon.level.insideMap(cell)) continue;
                this.off[cell] = this.cur[cell];
                this.volume += this.off[cell];
                if (this.off[cell] <= 0) continue;
                for (int k : PathFinder.NEIGHBOURS9) {
                    Char ch = Actor.findChar(cell + k);
                    if (ch == null) continue;
                    if (Dungeon.level.heroFOV[cell + k] && ch.buff(Marked.class) == null) {
                        CellEmitter.get(cell + k).burst(SacrificialParticle.FACTORY, 5);
                    }
                    Buff.prolong(ch, Marked.class, 2.0f);
                }
                if (this.off[cell] <= 0 || !Dungeon.level.visited[cell] || Dungeon.level.mobCount() != 0 || this.bonusSpawns <= 0 || !Dungeon.level.spawnMob(4)) continue;
                --this.bonusSpawns;
            }
        }
        int max = 6 + Dungeon.depth * 4;
        this.curEmitter.pour(SacrificialParticle.FACTORY, 0.01f + (float)this.volume / (float)max * 0.09f);
    }

    @Override
    public void use(BlobEmitter emitter) {
        super.use(emitter);
        this.curEmitter = emitter;
        int max = 6 + Dungeon.depth * 4;
        this.curEmitter.pour(SacrificialParticle.FACTORY, 0.01f + (float)this.volume / (float)max * 0.09f);
    }

    @Override
    public String tileDesc() {
        return Messages.get(this, "desc", new Object[0]);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(BONUS_SPAWNS, this.bonusSpawns);
        bundle.put(PRIZE, this.prize);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.bonusSpawns = bundle.getInt(BONUS_SPAWNS);
        if (bundle.contains(PRIZE)) {
            this.prize = (Item)bundle.get(PRIZE);
        }
    }

    public void setPrize(Item prize) {
        this.prize = prize;
    }

    public void sacrifice(Char ch) {
        int firePos = -1;
        for (int i : PathFinder.NEIGHBOURS9) {
            if (this.volume <= 0 || this.cur[ch.pos + i] <= 0) continue;
            firePos = ch.pos + i;
            break;
        }
        if (firePos != -1) {
            int exp = 0;
            if (ch instanceof Mob) {
                if (ch instanceof Statue || ch instanceof Mimic) {
                    exp = 1 + Dungeon.depth;
                } else if (ch instanceof Piranha || ch instanceof Bee) {
                    exp = 1 + Dungeon.depth / 2;
                } else if (ch instanceof Wraith) {
                    exp = 1 + Dungeon.depth / 3;
                } else if (ch instanceof Swarm && ((Swarm)ch).EXP == 0) {
                    exp = 1;
                } else if (((Mob)ch).EXP > 0) {
                    exp = 1 + ((Mob)ch).EXP;
                }
                exp *= Random.IntRange(2, 3);
            } else if (ch instanceof Hero) {
                exp = 1000000;
                Badges.validateDeathFromSacrifice();
            }
            if (exp > 0) {
                int volumeLeft = this.cur[firePos] - exp;
                if (volumeLeft > 0) {
                    int n = firePos;
                    this.cur[n] = this.cur[n] - exp;
                    this.volume -= exp;
                    ++this.bonusSpawns;
                    CellEmitter.get(firePos).burst(SacrificialParticle.FACTORY, 20);
                    Sample.INSTANCE.play("sounds/burning.mp3");
                    GLog.w(Messages.get(SacrificialFire.class, "worthy", new Object[0]), new Object[0]);
                } else {
                    this.clear(firePos);
                    if (this.volume <= 0) {
                        Notes.remove(this.landmark());
                    }
                    for (int i : PathFinder.NEIGHBOURS9) {
                        CellEmitter.get(firePos + i).burst(SacrificialParticle.FACTORY, 20);
                    }
                    Sample.INSTANCE.play("sounds/burning.mp3");
                    Sample.INSTANCE.play("sounds/burning.mp3");
                    Sample.INSTANCE.play("sounds/burning.mp3");
                    GLog.w(Messages.get(SacrificialFire.class, "reward", new Object[0]), new Object[0]);
                    if (this.prize != null) {
                        Dungeon.level.drop((Item)this.prize, (int)firePos).sprite.drop();
                    } else {
                        Dungeon.level.drop((Item)SacrificeRoom.prize((Level)Dungeon.level), (int)firePos).sprite.drop();
                    }
                }
            } else {
                GLog.w(Messages.get(SacrificialFire.class, "unworthy", new Object[0]), new Object[0]);
            }
        }
    }

    public static class Marked
    extends FlavourBuff {
        public static final float DURATION = 2.0f;

        @Override
        public void detach() {
            SacrificialFire fire;
            if (!this.target.isAlive() && (fire = (SacrificialFire)Dungeon.level.blobs.get(SacrificialFire.class)) != null) {
                fire.sacrifice(this.target);
            }
            super.detach();
        }
    }
}

