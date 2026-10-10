/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class Crossbow
extends MeleeWeapon {
    public Crossbow() {
        this.image = ItemSpriteSheet.CROSSBOW;
        this.hitSound = "sounds/hit.mp3";
        this.hitSoundPitch = 1.0f;
        this.tier = 4;
    }

    @Override
    public boolean doUnequip(Hero hero, boolean collect, boolean single) {
        if (super.doUnequip(hero, collect, single)) {
            if (hero.buff(ChargedShot.class) != null && !(hero.belongings.weapon() instanceof Crossbow) && !(hero.belongings.secondWep() instanceof Crossbow)) {
                hero.buff(ChargedShot.class).detach();
            }
            return true;
        }
        return false;
    }

    @Override
    public float accuracyFactor(final Char owner, final Char target) {
        if (owner.buff(ChargedShot.class) != null) {
            Actor.add(new Actor(){
                {
                    this.actPriority = 100;
                }

                @Override
                protected boolean act() {
                    if (owner instanceof Hero && !target.isAlive()) {
                        MeleeWeapon.onAbilityKill((Hero)owner, target);
                    }
                    Actor.remove(this);
                    return true;
                }
            });
            return Float.POSITIVE_INFINITY;
        }
        return super.accuracyFactor(owner, target);
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        int dmg = super.proc(attacker, defender, damage);
        if (attacker == Dungeon.hero && Dungeon.hero.buff(ChargedShot.class) != null && Dungeon.hero.belongings.attackingWeapon() == this) {
            Ballistica trajectory = new Ballistica(attacker.pos, defender.pos, 1);
            trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size() - 1), 7);
            WandOfBlastWave.throwChar(defender, trajectory, 4, true, true, this);
            attacker.buff(ChargedShot.class).detach();
        }
        return dmg;
    }

    @Override
    public int max(int lvl) {
        return 4 * (this.tier + 1) + lvl * this.tier;
    }

    public int dartMin() {
        return this.dartMin(this.buffedLvl());
    }

    public int dartMin(int lvl) {
        return 4 + lvl;
    }

    public int dartMax() {
        return this.dartMax(this.buffedLvl());
    }

    public int dartMax(int lvl) {
        return 12 + 3 * lvl;
    }

    @Override
    public String statsInfo() {
        if (this.isIdentified()) {
            return Messages.get(this, "stats_desc", this.dartMin(), this.dartMax());
        }
        return Messages.get(this, "typical_stats_desc", this.dartMin(0), this.dartMax(0));
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        if (hero.buff(ChargedShot.class) != null) {
            GLog.w(Messages.get(this, "ability_cant_use", new Object[0]), new Object[0]);
            return;
        }
        this.beforeAbilityUsed(hero, null);
        Buff.affect(hero, ChargedShot.class);
        hero.sprite.operate(hero.pos);
        hero.next();
        this.afterAbilityUsed(hero);
    }

    @Override
    public String abilityInfo() {
        if (this.levelKnown) {
            return Messages.get(this, "ability_desc", 3 + this.buffedLvl(), 3 + this.buffedLvl());
        }
        return Messages.get(this, "typical_ability_desc", 3, 3);
    }

    @Override
    public String upgradeAbilityStat(int level) {
        return Integer.toString(3 + level);
    }

    public static class ChargedShot
    extends Buff {
        public ChargedShot() {
            this.announced = true;
            this.type = Buff.buffType.POSITIVE;
        }

        @Override
        public int icon() {
            return 66;
        }
    }
}

