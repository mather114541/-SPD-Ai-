/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ChallengeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ShadowCaster;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import java.util.ArrayList;

public class ScrollOfChallenge
extends ExoticScroll {
    public ScrollOfChallenge() {
        this.icon = ItemSpriteSheet.Icons.SCROLL_CHALLENGE;
    }

    @Override
    public void doRead() {
        this.detach(ScrollOfChallenge.curUser.belongings.backpack);
        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
            mob.beckon(ScrollOfChallenge.curUser.pos);
        }
        Buff.affect(curUser, ChallengeArena.class).setup(ScrollOfChallenge.curUser.pos);
        this.identify();
        ScrollOfChallenge.curUser.sprite.centerEmitter().start(Speck.factory(5), 0.3f, 3);
        Sample.INSTANCE.play("sounds/challenge.mp3");
        this.readAnimation();
    }

    public static class ChallengeArena
    extends Buff {
        private ArrayList<Integer> arenaPositions = new ArrayList();
        private ArrayList<Emitter> arenaEmitters = new ArrayList();
        private static final float DURATION = 100.0f;
        int left = 0;
        private static final String ARENA_POSITIONS = "arena_positions";
        private static final String LEFT = "left";

        public ChallengeArena() {
            this.type = Buff.buffType.POSITIVE;
        }

        @Override
        public int icon() {
            return 20;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(1.0f, 0.0f, 0.0f);
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0.0f, (100.0f - (float)this.left) / 100.0f);
        }

        @Override
        public String iconTextDisplay() {
            return Integer.toString(this.left);
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", this.left);
        }

        public void setup(int pos) {
            int dist;
            if (Dungeon.depth == 5 || Dungeon.depth == 10 || Dungeon.depth == 20) {
                dist = 1;
            } else {
                boolean[] visibleCells = new boolean[Dungeon.level.length()];
                Point c = Dungeon.level.cellToPoint(pos);
                ShadowCaster.castShadow(c.x, c.y, Dungeon.level.width(), visibleCells, Dungeon.level.losBlocking, 8);
                int count = 0;
                for (boolean b : visibleCells) {
                    if (!b) continue;
                    ++count;
                }
                dist = count < 30 ? 1 : (count >= 100 ? 3 : 2);
            }
            PathFinder.buildDistanceMap(pos, BArray.or(Dungeon.level.passable, Dungeon.level.avoid, null), dist);
            for (int i = 0; i < PathFinder.distance.length; ++i) {
                if (PathFinder.distance[i] >= Integer.MAX_VALUE || this.arenaPositions.contains(i)) continue;
                this.arenaPositions.add(i);
            }
            if (this.target != null) {
                this.fx(false);
                this.fx(true);
            }
            this.left = 100;
        }

        public void extend(float duration) {
            this.left = (int)((float)this.left + duration);
        }

        @Override
        public boolean act() {
            if (!this.arenaPositions.contains(this.target.pos)) {
                this.detach();
            }
            --this.left;
            BuffIndicator.refreshHero();
            if (this.left <= 0) {
                this.detach();
            }
            this.spend(1.0f);
            return true;
        }

        @Override
        public void fx(boolean on) {
            if (on) {
                for (int i : this.arenaPositions) {
                    Emitter e = CellEmitter.get(i);
                    e.pour(ChallengeParticle.FACTORY, 0.05f);
                    this.arenaEmitters.add(e);
                }
            } else {
                for (Emitter e : this.arenaEmitters) {
                    e.on = false;
                }
                this.arenaEmitters.clear();
            }
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            int[] values = new int[this.arenaPositions.size()];
            for (int i = 0; i < values.length; ++i) {
                values[i] = this.arenaPositions.get(i);
            }
            bundle.put(ARENA_POSITIONS, values);
            bundle.put(LEFT, this.left);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            int[] values;
            super.restoreFromBundle(bundle);
            for (int value : values = bundle.getIntArray(ARENA_POSITIONS)) {
                this.arenaPositions.add(value);
            }
            this.left = bundle.getInt(LEFT);
        }
    }
}

