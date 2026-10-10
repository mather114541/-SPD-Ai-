/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Acidic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Albino;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.ArmoredBrute;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Bandit;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Bat;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Brute;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.CausticSlime;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Crab;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM100;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM200;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM201;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Elemental;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Eye;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Ghoul;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Gnoll;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GnollExile;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Golem;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Guard;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.HermitCrab;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Monk;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Necromancer;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Rat;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Scorpio;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Senior;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Shaman;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Skeleton;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Slime;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Snake;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.SpectralNecromancer;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Succubus;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Swarm;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Thief;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Warlock;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.RatSkull;
import com.watabou.utils.Random;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MobSpawner
extends Actor {
    public static final HashMap<Class<? extends Mob>, Class<? extends Mob>> RARE_ALTS = new HashMap();

    public MobSpawner() {
        this.actPriority = -30;
    }

    @Override
    protected boolean act() {
        if (Dungeon.level.mobCount() < Dungeon.level.mobLimit()) {
            if (Dungeon.level.spawnMob(12)) {
                this.spend(Dungeon.level.respawnCooldown());
            } else {
                this.spend(1.0f);
            }
        } else {
            this.spend(Dungeon.level.respawnCooldown());
        }
        return true;
    }

    public void resetCooldown() {
        this.spend(-this.cooldown());
        this.spend(Dungeon.level.respawnCooldown());
    }

    public static ArrayList<Class<? extends Mob>> getMobRotation(int depth) {
        ArrayList<Class<? extends Mob>> mobs = MobSpawner.standardMobRotation(depth);
        MobSpawner.addRareMobs(depth, mobs);
        MobSpawner.swapMobAlts(mobs);
        Random.shuffle(mobs);
        return mobs;
    }

    private static ArrayList<Class<? extends Mob>> standardMobRotation(int depth) {
        switch (depth) {
            default: {
                return new ArrayList<Class<? extends Mob>>(Arrays.asList(Rat.class, Rat.class, Rat.class, Snake.class));
            }
            case 2: {
                return new ArrayList<Class<? extends Mob>>(Arrays.asList(Rat.class, Rat.class, Snake.class, Gnoll.class, Gnoll.class));
            }
            case 3: {
                return new ArrayList<Class<? extends Mob>>(Arrays.asList(Rat.class, Snake.class, Gnoll.class, Gnoll.class, Gnoll.class, Swarm.class, Crab.class));
            }
            case 4: 
            case 5: {
                return new ArrayList<Class<? extends Mob>>(Arrays.asList(Gnoll.class, Swarm.class, Crab.class, Crab.class, Slime.class, Slime.class));
            }
            case 6: {
                return new ArrayList<Class<? extends Mob>>(Arrays.asList(Skeleton.class, Skeleton.class, Skeleton.class, Thief.class, Swarm.class));
            }
            case 7: {
                return new ArrayList<Class<? extends Mob>>(Arrays.asList(Skeleton.class, Skeleton.class, Skeleton.class, Thief.class, DM100.class, Guard.class));
            }
            case 8: {
                return new ArrayList<Class<? extends Mob>>(Arrays.asList(Skeleton.class, Skeleton.class, Thief.class, DM100.class, DM100.class, Guard.class, Guard.class, Necromancer.class));
            }
            case 9: 
            case 10: {
                return new ArrayList<Class<? extends Mob>>(Arrays.asList(Skeleton.class, Thief.class, DM100.class, DM100.class, Guard.class, Guard.class, Necromancer.class, Necromancer.class));
            }
            case 11: {
                return new ArrayList<Class<? extends Mob>>(Arrays.asList(Bat.class, Bat.class, Bat.class, Brute.class, Shaman.random()));
            }
            case 12: {
                return new ArrayList<Class<? extends Mob>>(Arrays.asList(Bat.class, Bat.class, Brute.class, Brute.class, Shaman.random(), Spinner.class));
            }
            case 13: {
                return new ArrayList<Class<? extends Mob>>(Arrays.asList(Bat.class, Brute.class, Brute.class, Shaman.random(), Shaman.random(), Spinner.class, Spinner.class, DM200.class));
            }
            case 14: 
            case 15: {
                return new ArrayList<Class<? extends Mob>>(Arrays.asList(Bat.class, Brute.class, Shaman.random(), Shaman.random(), Spinner.class, Spinner.class, DM200.class, DM200.class));
            }
            case 16: {
                return new ArrayList<Class<? extends Mob>>(Arrays.asList(Ghoul.class, Ghoul.class, Ghoul.class, Elemental.random(), Warlock.class));
            }
            case 17: {
                return new ArrayList<Class<? extends Mob>>(Arrays.asList(Ghoul.class, Elemental.random(), Elemental.random(), Warlock.class, Monk.class));
            }
            case 18: {
                return new ArrayList<Class<? extends Mob>>(Arrays.asList(Ghoul.class, Elemental.random(), Warlock.class, Warlock.class, Monk.class, Monk.class, Golem.class));
            }
            case 19: 
            case 20: {
                return new ArrayList<Class<? extends Mob>>(Arrays.asList(Elemental.random(), Warlock.class, Warlock.class, Monk.class, Monk.class, Golem.class, Golem.class, Golem.class));
            }
            case 21: {
                return new ArrayList<Class<? extends Mob>>(Arrays.asList(Succubus.class, Succubus.class, Eye.class));
            }
            case 22: {
                return new ArrayList<Class<? extends Mob>>(Arrays.asList(Succubus.class, Eye.class));
            }
            case 23: {
                return new ArrayList<Class<? extends Mob>>(Arrays.asList(Succubus.class, Eye.class, Eye.class, Scorpio.class));
            }
            case 24: 
            case 25: 
            case 26: 
        }
        return new ArrayList<Class<? extends Mob>>(Arrays.asList(Succubus.class, Eye.class, Eye.class, Scorpio.class, Scorpio.class, Scorpio.class));
    }

    public static void addRareMobs(int depth, ArrayList<Class<? extends Mob>> rotation) {
        switch (depth) {
            default: {
                return;
            }
            case 4: {
                if (Random.Float() < 0.025f) {
                    rotation.add(Thief.class);
                }
                return;
            }
            case 9: {
                if (Random.Float() < 0.025f) {
                    rotation.add(Bat.class);
                }
                return;
            }
            case 14: {
                if (Random.Float() < 0.025f) {
                    rotation.add(Ghoul.class);
                }
                return;
            }
            case 19: 
        }
        if (Random.Float() < 0.025f) {
            rotation.add(Succubus.class);
        }
    }

    private static void swapMobAlts(ArrayList<Class<? extends Mob>> rotation) {
        float altChance = 0.02f * RatSkull.exoticChanceMultiplier();
        for (int i = 0; i < rotation.size(); ++i) {
            Class<? extends Mob> cl;
            Class<? extends Mob> alt;
            if (!(Random.Float() < altChance) || (alt = RARE_ALTS.get(cl = rotation.get(i))) == null) continue;
            rotation.set(i, alt);
        }
    }

    static {
        RARE_ALTS.put(Rat.class, Albino.class);
        RARE_ALTS.put(Gnoll.class, GnollExile.class);
        RARE_ALTS.put(Crab.class, HermitCrab.class);
        RARE_ALTS.put(Slime.class, CausticSlime.class);
        RARE_ALTS.put(Thief.class, Bandit.class);
        RARE_ALTS.put(Necromancer.class, SpectralNecromancer.class);
        RARE_ALTS.put(Brute.class, ArmoredBrute.class);
        RARE_ALTS.put(DM200.class, DM201.class);
        RARE_ALTS.put(Monk.class, Senior.class);
        RARE_ALTS.put(Elemental.class, Elemental.ChaosElemental.class);
        RARE_ALTS.put(Scorpio.class, Acidic.class);
    }
}

