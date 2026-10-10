/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.CounterBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.PowerOfMany;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.GuidingLight;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.LifeLinkSpell;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.TargetedClericSpell;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShaftParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class HallowedGround
extends TargetedClericSpell {
    public static final HallowedGround INSTANCE = new HallowedGround();

    @Override
    public int icon() {
        return 52;
    }

    @Override
    public float chargeUse(Hero hero) {
        return 2.0f;
    }

    @Override
    public int targetingFlags() {
        return 1;
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero) && hero.hasTalent(Talent.HALLOWED_GROUND);
    }

    @Override
    protected void onTargetSelected(HolyTome tome, Hero hero, Integer target) {
        if (target == null) {
            return;
        }
        if (Dungeon.level.solid[target] || !Dungeon.level.heroFOV[target]) {
            GLog.w(Messages.get(this, "invalid_target", new Object[0]), new Object[0]);
            return;
        }
        ArrayList<Char> affected = new ArrayList<Char>();
        PathFinder.buildDistanceMap((int)target, BArray.not(Dungeon.level.solid, null), hero.pointsInTalent(Talent.HALLOWED_GROUND));
        for (int i = 0; i < Dungeon.level.length(); ++i) {
            if (PathFinder.distance[i] == Integer.MAX_VALUE) continue;
            int c = Dungeon.level.map[i];
            if (c == 1 || c == 9 || c == 20) {
                Level.set(i, 2);
                GameScene.updateMap(i);
                CellEmitter.get(i).burst(LeafParticle.LEVEL_SPECIFIC, 2);
            }
            GameScene.add(Blob.seed(i, 20, HallowedTerrain.class));
            CellEmitter.get(i).burst(ShaftParticle.FACTORY, 2);
            Char ch = Actor.findChar(i);
            if (ch == null) continue;
            affected.add(ch);
        }
        Char ally = PowerOfMany.getPoweredAlly();
        if (ally != null && ally.buff(LifeLinkSpell.LifeLinkSpellBuff.class) != null) {
            if (affected.contains(hero) && !affected.contains(ally)) {
                affected.add(ally);
            } else if (!affected.contains(hero) && affected.contains(ally)) {
                affected.add(hero);
            }
        }
        for (Char ch : affected) {
            this.affectChar(ch);
        }
        Sample.INSTANCE.play("sounds/meld.mp3");
        hero.sprite.zap(target);
        hero.spendAndNext(1.0f);
        this.onSpellCast(tome, hero);
    }

    private void affectChar(Char ch) {
        if (ch.alignment == Char.Alignment.ALLY) {
            if (ch == Dungeon.hero || ch.HP == ch.HT) {
                int barrierToGive = Math.min(15, 30 - ch.shielding());
                Buff.affect(ch, Barrier.class).incShield(barrierToGive);
                ch.sprite.showStatusWithIcon(65280, Integer.toString(barrierToGive), FloatingText.SHIELDING, new Object[0]);
            } else {
                int barrier = 15 - (ch.HT - ch.HP);
                barrier = Math.max(barrier, 0);
                ch.HP += 15 - barrier;
                ch.sprite.showStatusWithIcon(65280, Integer.toString(15 - barrier), FloatingText.HEALING, new Object[0]);
                if (barrier > 0) {
                    Buff.affect(ch, Barrier.class).incShield(barrier);
                    ch.sprite.showStatusWithIcon(65280, Integer.toString(barrier), FloatingText.SHIELDING, new Object[0]);
                }
            }
        } else if (!ch.flying) {
            Buff.affect(ch, GuidingLight.Illuminated.class);
            Buff.affect(ch, Roots.class, 2.0f);
        }
    }

    @Override
    public String desc() {
        int area = 1 + 2 * Dungeon.hero.pointsInTalent(Talent.HALLOWED_GROUND);
        return Messages.get(this, "desc", area) + "\n\n" + Messages.get(this, "charge_cost", (int)this.chargeUse(Dungeon.hero));
    }

    public static class HallowedTerrain
    extends Blob {
        @Override
        protected void evolve() {
            Char ally;
            Fire fire = (Fire)Dungeon.level.blobs.get(Fire.class);
            ArrayList<Char> affected = new ArrayList<Char>();
            int chance = 10 + 10 * Dungeon.hero.pointsInTalent(Talent.HALLOWED_GROUND);
            for (int i = this.area.left - 1; i <= this.area.right; ++i) {
                for (int j = this.area.top - 1; j <= this.area.bottom; ++j) {
                    int cell = i + j * Dungeon.level.width();
                    if (this.cur[cell] > 0) {
                        Char ch;
                        if (fire != null && fire.volume > 0 && fire.cur[cell] > 0) {
                            this.cur[cell] = 0;
                            this.off[cell] = 0;
                            continue;
                        }
                        int c = Dungeon.level.map[cell];
                        if (c == 2 && Dungeon.level.plants.get(c) == null) {
                            if (Random.Int(chance) == 0) {
                                if (!Regeneration.regenOn() || Dungeon.hero.buff(HallowedFurrowTracker.class) != null && Dungeon.hero.buff(HallowedFurrowTracker.class).count() > 100.0f) {
                                    Level.set(cell, 30);
                                } else {
                                    Level.set(cell, 15);
                                }
                                GameScene.updateMap(cell);
                                CellEmitter.get(cell).burst(LeafParticle.LEVEL_SPECIFIC, 5);
                            }
                        } else if (c == 1 || c == 9 || c == 20) {
                            Level.set(cell, 2);
                            GameScene.updateMap(cell);
                            CellEmitter.get(cell).burst(LeafParticle.LEVEL_SPECIFIC, 2);
                        }
                        if ((ch = Actor.findChar(cell)) != null) {
                            affected.add(ch);
                        }
                        this.off[cell] = this.cur[cell] - 1;
                        this.volume += this.off[cell];
                        continue;
                    }
                    this.off[cell] = 0;
                }
            }
            if (this.volume > 0) {
                Buff.count(Dungeon.hero, HallowedFurrowTracker.class, 1.0f);
            }
            if ((ally = PowerOfMany.getPoweredAlly()) != null && ally.buff(LifeLinkSpell.LifeLinkSpellBuff.class) != null) {
                if (affected.contains(Dungeon.hero) && !affected.contains(ally)) {
                    affected.add(ally);
                } else if (!affected.contains(Dungeon.hero) && affected.contains(ally)) {
                    affected.add(Dungeon.hero);
                }
            }
            for (Char ch : affected) {
                this.affectChar(ch);
            }
        }

        private void affectChar(Char ch) {
            if (ch.alignment == Char.Alignment.ALLY) {
                if (ch == Dungeon.hero || ch.HP == ch.HT) {
                    Buff.affect(ch, Barrier.class).incShield(1);
                    ch.sprite.showStatusWithIcon(65280, "1", FloatingText.SHIELDING, new Object[0]);
                } else {
                    ++ch.HP;
                    ch.sprite.showStatusWithIcon(65280, "1", FloatingText.HEALING, new Object[0]);
                }
            } else if (!ch.flying && ch.buff(Roots.class) == null) {
                Buff.prolong(ch, Cripple.class, 1.0f);
            }
        }

        @Override
        public void use(BlobEmitter emitter) {
            super.use(emitter);
            emitter.pour(ShaftParticle.FACTORY, 1.0f);
        }

        @Override
        public String tileDesc() {
            return Messages.get(this, "desc", new Object[0]);
        }
    }

    public static class HallowedFurrowTracker
    extends CounterBuff {
        public HallowedFurrowTracker() {
            this.revivePersists = true;
        }
    }
}

