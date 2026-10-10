/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.TormentedSpirit;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ChallengeParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.RatSkull;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WraithSprite;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import java.util.ArrayList;

public class Wraith
extends Mob {
    private static final float SPAWN_DELAY = 2.0f;
    protected int level;
    private static final String LEVEL = "level";

    public Wraith() {
        this.spriteClass = WraithSprite.class;
        this.HT = 1;
        this.HP = 1;
        this.EXP = 0;
        this.maxLvl = -2;
        this.flying = true;
        this.properties.add(Char.Property.UNDEAD);
        this.properties.add(Char.Property.INORGANIC);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LEVEL, this.level);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.level = bundle.getInt(LEVEL);
        this.adjustStats(this.level);
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(1 + this.level / 2, 2 + this.level);
    }

    @Override
    public int attackSkill(Char target) {
        return 10 + this.level;
    }

    public void adjustStats(int level) {
        this.level = level;
        this.defenseSkill = this.attackSkill(null) * 5;
        this.enemySeen = true;
    }

    @Override
    public float spawningWeight() {
        return 0.0f;
    }

    @Override
    public boolean reset() {
        this.state = this.WANDERING;
        return true;
    }

    public static void spawnAround(int pos) {
        Wraith.spawnAround(pos, null);
    }

    public static void spawnAround(int pos, Class<? extends Wraith> wraithClass) {
        for (int n : PathFinder.NEIGHBOURS4) {
            Wraith.spawnAt(pos + n, wraithClass, false);
        }
    }

    public static Wraith spawnAt(int pos) {
        return Wraith.spawnAt(pos, null);
    }

    public static Wraith spawnAt(int pos, Class<? extends Wraith> wraithClass) {
        return Wraith.spawnAt(pos, wraithClass, true);
    }

    private static Wraith spawnAt(int pos, Class<? extends Wraith> wraithClass, boolean allowAdjacent) {
        if (Dungeon.level.solid[pos] || Actor.findChar(pos) != null) {
            ArrayList<Integer> candidates = new ArrayList<Integer>();
            for (int i : PathFinder.NEIGHBOURS8) {
                if (Dungeon.level.solid[pos + i] || Actor.findChar(pos + i) != null) continue;
                candidates.add(pos + i);
            }
            pos = allowAdjacent && !candidates.isEmpty() ? (Integer)Random.element(candidates) : -1;
        }
        if (pos != -1) {
            Wraith w;
            if (wraithClass == null) {
                float altChance = 0.01f * RatSkull.exoticChanceMultiplier();
                w = Random.Float() < altChance ? new TormentedSpirit() : new Wraith();
            } else {
                w = Reflection.newInstance(wraithClass);
            }
            w.adjustStats(Dungeon.scalingDepth());
            w.pos = pos;
            w.state = w.HUNTING;
            GameScene.add(w, 2.0f);
            Dungeon.level.occupyCell(w);
            w.sprite.alpha(0.0f);
            w.sprite.parent.add(new AlphaTweener(w.sprite, 1.0f, 0.5f));
            if (w instanceof TormentedSpirit) {
                w.sprite.emitter().burst(ChallengeParticle.FACTORY, 10);
            } else {
                w.sprite.emitter().burst(ShadowParticle.CURSE, 5);
            }
            return w;
        }
        return null;
    }
}

