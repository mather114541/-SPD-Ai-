/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Healing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.Ratmogrify;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Bat;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Brute;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Crab;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM100;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM200;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Elemental;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Eye;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Ghoul;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Gnoll;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Golem;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Guard;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Monk;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Necromancer;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Rat;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.RipperDemon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Scorpio;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Shaman;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Skeleton;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Slime;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Snake;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Succubus;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Swarm;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Thief;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Warlock;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Shopkeeper;
import com.shatteredpixel.shatteredpixeldungeon.items.Amulet;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import java.util.HashMap;

public class AscensionChallenge
extends Buff {
    private static HashMap<Class<? extends Mob>, Float> modifiers = new HashMap();
    private float stacks;
    private float damageInc;
    private boolean stacksLowered;
    private boolean justAscended;
    public static final String STACKS = "enemy_stacks";
    public static final String DAMAGE = "damage_inc";
    public static final String STACKS_LOWERED = "stacks_lowered";

    public AscensionChallenge() {
        this.revivePersists = true;
        this.stacks = 0.0f;
        this.damageInc = 0.0f;
        this.stacksLowered = false;
        this.justAscended = false;
    }

    public static float statModifier(Char ch) {
        if (Dungeon.hero == null || Dungeon.hero.buff(AscensionChallenge.class) == null) {
            return 1.0f;
        }
        if (ch instanceof Ratmogrify.TransmogRat) {
            ch = ((Ratmogrify.TransmogRat)ch).getOriginal();
        }
        if (ch.buff(AscensionBuffBlocker.class) != null) {
            return 1.0f;
        }
        for (Class<? extends Mob> cls : modifiers.keySet()) {
            if (!cls.isAssignableFrom(ch.getClass())) continue;
            return modifiers.get(cls).floatValue();
        }
        return 1.0f;
    }

    public static void beckonEnemies() {
        if (Dungeon.hero.buff(AscensionChallenge.class) != null && Dungeon.hero.buff(AscensionChallenge.class).stacks >= 2.0f) {
            for (Mob m : Dungeon.level.mobs) {
                if (m.alignment != Char.Alignment.ENEMY || m.distance(Dungeon.hero) <= 8) continue;
                m.beckon(Dungeon.hero.pos);
            }
        }
    }

    public static float enemySpeedModifier(Mob m) {
        if (Dungeon.hero.buff(AscensionChallenge.class) != null && m.alignment == Char.Alignment.ENEMY && Dungeon.hero.buff(AscensionChallenge.class).stacks >= 4.0f && m.state != m.HUNTING && m.state != m.FLEEING) {
            return 2.0f;
        }
        return 1.0f;
    }

    public static float modifyHeroSpeed(float speed) {
        if (Dungeon.hero.buff(AscensionChallenge.class) != null && Dungeon.hero.buff(AscensionChallenge.class).stacks >= 6.0f) {
            return Math.min(speed / 2.0f, 1.0f);
        }
        return speed;
    }

    public static boolean qualifiedForPacifist() {
        if (Dungeon.hero.buff(AscensionChallenge.class) != null) {
            return !Dungeon.hero.buff(AscensionChallenge.class).stacksLowered;
        }
        return false;
    }

    public static void processEnemyKill(Char enemy) {
        AscensionChallenge chal = Dungeon.hero.buff(AscensionChallenge.class);
        if (chal == null) {
            return;
        }
        if (enemy instanceof Ratmogrify.TransmogRat && (enemy = ((Ratmogrify.TransmogRat)enemy).getOriginal()) == null) {
            return;
        }
        if (enemy.buff(AscensionBuffBlocker.class) != null) {
            return;
        }
        boolean found = false;
        for (Class<? extends Mob> cls : modifiers.keySet()) {
            if (!cls.isAssignableFrom(enemy.getClass())) continue;
            found = true;
            break;
        }
        if (!found) {
            return;
        }
        float oldStacks = chal.stacks;
        chal.stacks = enemy instanceof Ghoul || enemy instanceof RipperDemon ? (chal.stacks -= 0.5f) : (chal.stacks -= 1.0f);
        chal.stacks = Math.max(0.0f, chal.stacks);
        if (!chal.stacksLowered) {
            chal.stacksLowered = true;
            GLog.p(Messages.get(AscensionChallenge.class, "weaken", new Object[0]), new Object[0]);
        } else if (chal.stacks < 8.0f && (int)(chal.stacks / 2.0f) != (int)(oldStacks / 2.0f)) {
            GLog.p(Messages.get(AscensionChallenge.class, "weaken", new Object[0]), new Object[0]);
        }
        if (oldStacks > chal.stacks && Dungeon.hero.lvl == 30) {
            Dungeon.hero.earnExp(Math.round(10.0f * (oldStacks - chal.stacks)), chal.getClass());
        }
        BuffIndicator.refreshHero();
    }

    public static int AscensionCorruptResist(Mob m) {
        if (Dungeon.hero.buff(AscensionChallenge.class) == null) {
            return m.EXP;
        }
        if (m instanceof Ratmogrify.TransmogRat) {
            m = ((Ratmogrify.TransmogRat)m).getOriginal();
        }
        if (m.buff(AscensionBuffBlocker.class) != null) {
            return m.EXP;
        }
        if (m instanceof RipperDemon) {
            return 10;
        }
        if (m instanceof Ghoul) {
            return 7;
        }
        for (Class<? extends Mob> cls : modifiers.keySet()) {
            if (!cls.isAssignableFrom(m.getClass())) continue;
            return Math.max(13, m.EXP);
        }
        return m.EXP;
    }

    public void onLevelSwitch() {
        if (Dungeon.depth < Statistics.highestAscent) {
            Statistics.highestAscent = Dungeon.depth;
            this.justAscended = true;
            if (Dungeon.bossLevel()) {
                Dungeon.hero.buff(Hunger.class).satisfy(450.0f);
                Buff.affect(Dungeon.hero, Healing.class).setHeal(Dungeon.hero.HT, 0.0f, 20);
            } else {
                this.stacks += 2.0f;
                for (int i = 0; i < Dungeon.level.length(); ++i) {
                    if (Dungeon.level.map[i] != 38) continue;
                    Level.set(i, 5, Dungeon.level);
                }
                for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
                    if (mob.reset()) continue;
                    Dungeon.level.mobs.remove(mob);
                }
                Dungeon.level.spawnMob(12);
            }
        }
        if (Statistics.highestAscent < 20) {
            for (Mob m : Dungeon.level.mobs.toArray(new Mob[0])) {
                if (!(m instanceof Shopkeeper)) continue;
                ((Shopkeeper)m).flee();
            }
        }
    }

    public void saySwitch() {
        if (Dungeon.bossLevel()) {
            if (this.justAscended) {
                GLog.p(Messages.get(this, "break", new Object[0]), new Object[0]);
                for (Char ch : Actor.chars()) {
                    if (!(ch instanceof DriedRose.GhostHero)) continue;
                    ((DriedRose.GhostHero)ch).sayAppeared();
                }
            }
        } else {
            if (Dungeon.depth == 1) {
                GLog.n(Messages.get(this, "almost", new Object[0]), new Object[0]);
            } else if (this.stacks >= 8.0f) {
                GLog.n(Messages.get(this, "damage", new Object[0]), new Object[0]);
            } else if (this.stacks >= 6.0f) {
                GLog.n(Messages.get(this, "slow", new Object[0]), new Object[0]);
            } else if (this.stacks >= 4.0f) {
                GLog.n(Messages.get(this, "haste", new Object[0]), new Object[0]);
            } else if (this.stacks >= 2.0f) {
                GLog.n(Messages.get(this, "beckon", new Object[0]), new Object[0]);
            }
            if (this.stacks > 4.0f && !this.stacksLowered) {
                GLog.h(Messages.get(this, "weaken_info_no_kills", new Object[0]), new Object[0]);
            } else if (this.stacks > 8.0f) {
                GLog.h(Messages.get(this, "weaken_info", new Object[0]), new Object[0]);
            }
        }
        this.justAscended = false;
    }

    @Override
    public boolean act() {
        AscensionChallenge.beckonEnemies();
        if (this.stacks >= 8.0f && !Dungeon.bossLevel()) {
            this.damageInc += (this.stacks - 4.0f) / 4.0f;
            if (this.damageInc >= 1.0f) {
                this.target.damage((int)this.damageInc, this);
                this.damageInc -= (float)((int)this.damageInc);
                if (this.target == Dungeon.hero && !this.target.isAlive()) {
                    Badges.validateDeathFromFriendlyMagic();
                    GLog.n(Messages.get(this, "on_kill", new Object[0]), new Object[0]);
                    Dungeon.fail(Amulet.class);
                }
            }
        } else {
            this.damageInc = 0.0f;
        }
        this.spend(1.0f);
        return true;
    }

    @Override
    public int icon() {
        return 59;
    }

    @Override
    public void tintIcon(Image icon) {
        if (this.stacks < 2.0f) {
            icon.hardlight(0.5f, 1.0f, 0.0f);
        } else if (this.stacks < 4.0f) {
            icon.hardlight(1.0f, 1.0f, 0.0f);
        } else if (this.stacks < 6.0f) {
            icon.hardlight(1.0f, 0.67f, 0.0f);
        } else if (this.stacks < 8.0f) {
            icon.hardlight(1.0f, 0.33f, 0.0f);
        } else {
            icon.hardlight(1.0f, 0.0f, 0.0f);
        }
    }

    @Override
    public String desc() {
        Object desc = Messages.get(this, "desc", new Object[0]);
        desc = (String)desc + "\n";
        if (this.stacks < 2.0f) {
            desc = (String)desc + "\n" + Messages.get(this, "desc_clear", new Object[0]);
        } else {
            if (this.stacks >= 2.0f) {
                desc = (String)desc + "\n" + Messages.get(this, "desc_beckon", new Object[0]);
            }
            if (this.stacks >= 4.0f) {
                desc = (String)desc + "\n" + Messages.get(this, "desc_haste", new Object[0]);
            }
            if (this.stacks >= 6.0f) {
                desc = (String)desc + "\n" + Messages.get(this, "desc_slow", new Object[0]);
            }
            if (this.stacks >= 8.0f) {
                desc = (String)desc + "\n" + Messages.get(this, "desc_damage", new Object[0]);
            }
        }
        return desc;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(STACKS, this.stacks);
        bundle.put(DAMAGE, this.damageInc);
        bundle.put(STACKS_LOWERED, this.stacksLowered);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.stacks = bundle.getFloat(STACKS);
        this.damageInc = bundle.getFloat(DAMAGE);
        this.stacksLowered = bundle.getBoolean(STACKS_LOWERED);
    }

    static {
        modifiers.put(Rat.class, Float.valueOf(10.0f));
        modifiers.put(Snake.class, Float.valueOf(9.0f));
        modifiers.put(Gnoll.class, Float.valueOf(9.0f));
        modifiers.put(Swarm.class, Float.valueOf(8.5f));
        modifiers.put(Crab.class, Float.valueOf(8.0f));
        modifiers.put(Slime.class, Float.valueOf(8.0f));
        modifiers.put(Skeleton.class, Float.valueOf(5.0f));
        modifiers.put(Thief.class, Float.valueOf(5.0f));
        modifiers.put(DM100.class, Float.valueOf(4.5f));
        modifiers.put(Guard.class, Float.valueOf(4.0f));
        modifiers.put(Necromancer.class, Float.valueOf(4.0f));
        modifiers.put(Bat.class, Float.valueOf(2.5f));
        modifiers.put(Brute.class, Float.valueOf(2.25f));
        modifiers.put(Shaman.class, Float.valueOf(2.25f));
        modifiers.put(Spinner.class, Float.valueOf(2.0f));
        modifiers.put(DM200.class, Float.valueOf(2.0f));
        modifiers.put(Ghoul.class, Float.valueOf(1.67f));
        modifiers.put(Elemental.class, Float.valueOf(1.67f));
        modifiers.put(Warlock.class, Float.valueOf(1.5f));
        modifiers.put(Monk.class, Float.valueOf(1.5f));
        modifiers.put(Golem.class, Float.valueOf(1.33f));
        modifiers.put(RipperDemon.class, Float.valueOf(1.2f));
        modifiers.put(Succubus.class, Float.valueOf(1.2f));
        modifiers.put(Eye.class, Float.valueOf(1.1f));
        modifiers.put(Scorpio.class, Float.valueOf(1.1f));
    }

    public static class AscensionBuffBlocker
    extends Buff {
    }
}

