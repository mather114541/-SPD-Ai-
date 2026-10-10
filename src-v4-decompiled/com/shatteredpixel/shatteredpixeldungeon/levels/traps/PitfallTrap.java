/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PitfallParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import java.util.ArrayList;

public class PitfallTrap
extends Trap {
    public PitfallTrap() {
        this.color = 0;
        this.shape = 4;
    }

    @Override
    public void activate() {
        if (Dungeon.bossLevel() || Dungeon.depth > 25 || Dungeon.branch != 0) {
            GLog.w(Messages.get(this, "no_pit", new Object[0]), new Object[0]);
            return;
        }
        DelayedPit p = Buff.append(Dungeon.hero, DelayedPit.class, 1.0f);
        p.depth = Dungeon.depth;
        p.branch = Dungeon.branch;
        ArrayList<Integer> positions = new ArrayList<Integer>();
        for (int i : PathFinder.NEIGHBOURS9) {
            if (Dungeon.level.solid[this.pos + i] && !Dungeon.level.passable[this.pos + i]) continue;
            CellEmitter.floor(this.pos + i).burst(PitfallParticle.FACTORY4, 8);
            positions.add(this.pos + i);
        }
        p.setPositions(positions);
        if (this.pos == Dungeon.hero.pos) {
            GLog.n(Messages.get(this, "triggered_hero", new Object[0]), new Object[0]);
        } else if (Dungeon.level.heroFOV[this.pos]) {
            GLog.n(Messages.get(this, "triggered", new Object[0]), new Object[0]);
        }
    }

    public static class DelayedPit
    extends FlavourBuff {
        public int[] positions;
        public int depth;
        public int branch;
        public boolean ignoreAllies;
        private static final String POSITIONS = "positions";
        private static final String DEPTH = "depth";
        private static final String BRANCH = "branch";
        private static final String IGNORE_ALLIES = "ignore_allies";

        public DelayedPit() {
            this.revivePersists = true;
            this.positions = new int[0];
            this.ignoreAllies = false;
        }

        @Override
        public boolean act() {
            boolean herofell = false;
            if (this.depth == Dungeon.depth && this.branch == Dungeon.branch && this.positions != null) {
                for (int cell : this.positions) {
                    Heap heap;
                    if (!Dungeon.level.insideMap(cell) || Dungeon.level.solid[cell] && !Dungeon.level.passable[cell]) continue;
                    CellEmitter.floor(cell).burst(PitfallParticle.FACTORY8, 12);
                    Char ch = Actor.findChar(cell);
                    if (!(ch == null || ch.flying || ch.alignment == Char.Alignment.NEUTRAL && Char.hasProp(ch, Char.Property.IMMOVABLE) || ch.alignment == Char.Alignment.ALLY && this.ignoreAllies)) {
                        if (ch == Dungeon.hero) {
                            herofell = true;
                        } else {
                            Chasm.mobFall((Mob)ch);
                        }
                    }
                    if ((heap = (Heap)Dungeon.level.heaps.get(cell)) == null || this.ignoreAllies || heap.type == Heap.Type.FOR_SALE || heap.type == Heap.Type.LOCKED_CHEST || heap.type == Heap.Type.CRYSTAL_CHEST) continue;
                    for (Item item : heap.items) {
                        Dungeon.dropToChasm(item);
                    }
                    heap.sprite.kill();
                    GameScene.discard(heap);
                    heap.sprite.drop();
                    Dungeon.level.heaps.remove(cell);
                }
            }
            if (herofell) {
                Chasm.heroFall(Dungeon.hero.pos);
            }
            this.detach();
            return !herofell;
        }

        public void setPositions(ArrayList<Integer> positions) {
            this.positions = new int[positions.size()];
            for (int i = 0; i < this.positions.length; ++i) {
                this.positions[i] = positions.get(i);
            }
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(POSITIONS, this.positions);
            bundle.put(DEPTH, this.depth);
            bundle.put(BRANCH, this.branch);
            bundle.put(IGNORE_ALLIES, this.ignoreAllies);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.positions = bundle.getIntArray(POSITIONS);
            this.depth = bundle.getInt(DEPTH);
            this.branch = bundle.getInt(BRANCH);
            this.ignoreAllies = bundle.getBoolean(IGNORE_ALLIES);
        }
    }
}

