/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class Flail
extends MeleeWeapon {
    private static int spinBoost = 0;

    public Flail() {
        this.image = ItemSpriteSheet.FLAIL;
        this.hitSound = "sounds/hit_crush.mp3";
        this.hitSoundPitch = 0.8f;
        this.tier = 4;
        this.ACC = 0.8f;
    }

    @Override
    public int max(int lvl) {
        return Math.round(7 * (this.tier + 1)) + lvl * Math.round(1.6f * (float)(this.tier + 1));
    }

    @Override
    public int damageRoll(Char owner) {
        int dmg = super.damageRoll(owner) + spinBoost;
        if (spinBoost > 0) {
            Sample.INSTANCE.play("sounds/hit_strong.mp3");
        }
        spinBoost = 0;
        return dmg;
    }

    @Override
    public float accuracyFactor(final Char owner, final Char target) {
        SpinAbilityTracker spin = owner.buff(SpinAbilityTracker.class);
        if (spin != null && spinBoost == 0) {
            Actor.add(new Actor(){
                {
                    this.actPriority = 100;
                }

                @Override
                protected boolean act() {
                    spinBoost = 0;
                    if (owner instanceof Hero && !target.isAlive()) {
                        MeleeWeapon.onAbilityKill((Hero)owner, target);
                    }
                    Actor.remove(this);
                    return true;
                }
            });
            spin.detach();
            spinBoost = spin.spins * this.augment.damageFactor(8 + 2 * this.buffedLvl());
            return Float.POSITIVE_INFINITY;
        }
        if (spinBoost != 0) {
            return Float.POSITIVE_INFINITY;
        }
        return super.accuracyFactor(owner, target);
    }

    @Override
    protected int baseChargeUse(Hero hero, Char target) {
        if (Dungeon.hero.buff(SpinAbilityTracker.class) != null) {
            return 0;
        }
        return 1;
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        SpinAbilityTracker spin = hero.buff(SpinAbilityTracker.class);
        if (spin != null && spin.spins >= 3) {
            GLog.w(Messages.get(this, "spin_warn", new Object[0]), new Object[0]);
            return;
        }
        this.beforeAbilityUsed(hero, null);
        if (spin == null) {
            spin = Buff.affect(hero, SpinAbilityTracker.class, 3.0f);
        }
        ++spin.spins;
        Buff.prolong(hero, SpinAbilityTracker.class, 3.0f);
        Sample.INSTANCE.play("sounds/chains.mp3", 1.0f, 1.0f, 0.9f + 0.1f * (float)spin.spins);
        hero.sprite.operate(hero.pos);
        hero.spendAndNext(1.0f);
        BuffIndicator.refreshHero();
        this.afterAbilityUsed(hero);
    }

    @Override
    public String abilityInfo() {
        int dmgBoost;
        int n = dmgBoost = this.levelKnown ? 8 + 2 * this.buffedLvl() : 8;
        if (this.levelKnown) {
            return Messages.get(this, "ability_desc", this.augment.damageFactor(dmgBoost));
        }
        return Messages.get(this, "typical_ability_desc", this.augment.damageFactor(dmgBoost));
    }

    @Override
    public String upgradeAbilityStat(int level) {
        return "+" + this.augment.damageFactor(8 + 2 * level);
    }

    public static class SpinAbilityTracker
    extends FlavourBuff {
        public int spins;
        public static String SPINS = "spins";

        public SpinAbilityTracker() {
            this.type = Buff.buffType.POSITIVE;
            this.spins = 0;
        }

        @Override
        public int icon() {
            return 62;
        }

        @Override
        public void tintIcon(Image icon) {
            switch (this.spins) {
                default: {
                    icon.hardlight(0.0f, 1.0f, 0.0f);
                    break;
                }
                case 2: {
                    icon.hardlight(1.0f, 1.0f, 0.0f);
                    break;
                }
                case 3: {
                    icon.hardlight(1.0f, 0.0f, 0.0f);
                }
            }
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0.0f, (3.0f - this.visualcooldown()) / 3.0f);
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", Math.round((float)this.spins / 3.0f * 100.0f), this.dispTurns());
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(SPINS, this.spins);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.spins = bundle.getInt(SPINS);
        }
    }
}

