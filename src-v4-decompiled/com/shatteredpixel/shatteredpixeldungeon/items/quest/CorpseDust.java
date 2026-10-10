/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.quest;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Wraith;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class CorpseDust
extends Item {
    public CorpseDust() {
        this.image = ItemSpriteSheet.DUST;
        this.cursed = true;
        this.cursedKnown = true;
        this.unique = true;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        return new ArrayList<String>();
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public boolean doPickUp(Hero hero, int pos) {
        if (super.doPickUp(hero, pos)) {
            GLog.n(Messages.get(this, "chill", new Object[0]), new Object[0]);
            Buff.affect(hero, DustGhostSpawner.class);
            return true;
        }
        return false;
    }

    @Override
    protected void onDetach() {
        DustGhostSpawner spawner = Dungeon.hero.buff(DustGhostSpawner.class);
        if (spawner != null) {
            spawner.dispel();
        }
    }

    public static class DustGhostSpawner
    extends Buff {
        int spawnPower = 0;
        private static String SPAWNPOWER = "spawnpower";

        public DustGhostSpawner() {
            this.revivePersists = true;
        }

        @Override
        public boolean act() {
            if (this.target instanceof Hero && ((Hero)this.target).belongings.getItem(CorpseDust.class) == null) {
                this.spawnPower = 0;
                this.spend(1.0f);
                return true;
            }
            ++this.spawnPower;
            int wraiths = 1;
            for (Mob mob : Dungeon.level.mobs) {
                if (!(mob instanceof DustWraith)) continue;
                ++wraiths;
            }
            int powerNeeded = Math.min(49, wraiths * wraiths);
            if (powerNeeded <= this.spawnPower) {
                ArrayList<Integer> candidates = new ArrayList<Integer>();
                int minDist = Math.round((float)Dungeon.hero.viewDistance / 3.0f);
                for (int i = 0; i < Dungeon.level.length(); ++i) {
                    if (!Dungeon.level.heroFOV[i] || Dungeon.level.solid[i] || Actor.findChar(i) != null || Dungeon.level.distance(i, Dungeon.hero.pos) <= minDist) continue;
                    candidates.add(i);
                }
                if (!candidates.isEmpty()) {
                    Wraith.spawnAt((Integer)Random.element(candidates), DustWraith.class);
                    Sample.INSTANCE.play("sounds/cursed.mp3");
                    this.spawnPower -= powerNeeded;
                } else {
                    this.spawnPower = Math.min(this.spawnPower, 2 * wraiths);
                }
            }
            this.spend(1.0f);
            return true;
        }

        public void dispel() {
            this.detach();
            for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
                if (!(mob instanceof DustWraith)) continue;
                mob.die(null);
            }
            Game.runOnRenderThread(new Callback(){

                @Override
                public void call() {
                    Music.INSTANCE.fadeOut(1.0f, new Callback(){

                        @Override
                        public void call() {
                            if (Dungeon.level != null) {
                                Dungeon.level.playLevelMusic();
                            }
                        }
                    });
                }
            });
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(SPAWNPOWER, this.spawnPower);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.spawnPower = bundle.getInt(SPAWNPOWER);
        }
    }

    public static class DustWraith
    extends Wraith {
        private int atkCount = 0;
        private static final String ATK_COUNT = "atk_count";

        @Override
        public boolean attack(Char enemy, float dmgMulti, float dmgBonus, float accMulti) {
            if (enemy == Dungeon.hero) {
                ++this.atkCount;
                if (this.atkCount == 2 || this.atkCount == 3) {
                    Statistics.questScores[1] = Statistics.questScores[1] - 100;
                }
            }
            return super.attack(enemy, dmgMulti, dmgBonus, accMulti);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(ATK_COUNT, this.atkCount);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.atkCount = bundle.getInt(ATK_COUNT);
        }
    }
}

