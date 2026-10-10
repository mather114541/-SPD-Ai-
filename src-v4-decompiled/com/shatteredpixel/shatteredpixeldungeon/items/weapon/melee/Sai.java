/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Combo;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Gauntlet;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Gloves;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;

public class Sai
extends MeleeWeapon {
    public Sai() {
        this.image = ItemSpriteSheet.SAI;
        this.hitSound = "sounds/hit_stab.mp3";
        this.hitSoundPitch = 1.3f;
        this.tier = 3;
        this.DLY = 0.5f;
    }

    @Override
    public int max(int lvl) {
        return Math.round(2.5f * (float)(this.tier + 1)) + lvl * Math.round(0.5f * (float)(this.tier + 1));
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt", new Object[0]);
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        int dmgBoost = this.augment.damageFactor(4 + this.buffedLvl());
        Sai.comboStrikeAbility(hero, target, 0.0f, dmgBoost, this);
    }

    @Override
    public String abilityInfo() {
        int dmgBoost;
        int n = dmgBoost = this.levelKnown ? 4 + this.buffedLvl() : 4;
        if (this.levelKnown) {
            return Messages.get(this, "ability_desc", this.augment.damageFactor(dmgBoost));
        }
        return Messages.get(this, "typical_ability_desc", this.augment.damageFactor(dmgBoost));
    }

    @Override
    public String upgradeAbilityStat(int level) {
        return "+" + this.augment.damageFactor(4 + level);
    }

    public static void comboStrikeAbility(final Hero hero, Integer target, final float multiPerHit, final int boostPerHit, final MeleeWeapon wep) {
        if (target == null) {
            return;
        }
        final Char enemy = Actor.findChar(target);
        if (enemy == null || enemy == hero || hero.isCharmedBy(enemy) || !Dungeon.level.heroFOV[target]) {
            GLog.w(Messages.get(wep, "ability_no_target", new Object[0]), new Object[0]);
            return;
        }
        hero.belongings.abilityWeapon = wep;
        if (!hero.canAttack(enemy)) {
            GLog.w(Messages.get(wep, "ability_target_range", new Object[0]), new Object[0]);
            hero.belongings.abilityWeapon = null;
            return;
        }
        hero.belongings.abilityWeapon = null;
        hero.sprite.attack(enemy.pos, new Callback(){

            @Override
            public void call() {
                boolean hit;
                wep.beforeAbilityUsed(hero, enemy);
                AttackIndicator.target(enemy);
                int recentHits = 0;
                ComboStrikeTracker buff = hero.buff(ComboStrikeTracker.class);
                if (buff != null) {
                    recentHits = buff.hits;
                    buff.detach();
                }
                if ((hit = hero.attack(enemy, 1.0f + multiPerHit * (float)recentHits, boostPerHit * recentHits, Char.INFINITE_ACCURACY)) && !enemy.isAlive()) {
                    MeleeWeapon.onAbilityKill(hero, enemy);
                }
                Invisibility.dispel();
                hero.spendAndNext(hero.attackDelay());
                if (recentHits >= 2 && hit) {
                    Sample.INSTANCE.play("sounds/hit_strong.mp3");
                }
                wep.afterAbilityUsed(hero);
            }
        });
    }

    public static class ComboStrikeTracker
    extends Buff {
        public static int DURATION = 5;
        private float comboTime;
        private float initialTime;
        public int hits;
        private static final String TIME = "combo_time";
        private static final String INITIAL_TIME = "initial_time";
        public static String RECENT_HITS = "recent_hits";

        public ComboStrikeTracker() {
            this.type = Buff.buffType.POSITIVE;
            this.comboTime = 0.0f;
            this.initialTime = 5.0f;
            this.hits = 0;
        }

        @Override
        public int icon() {
            if (Dungeon.hero.belongings.weapon() instanceof Gloves || Dungeon.hero.belongings.weapon() instanceof Sai || Dungeon.hero.belongings.weapon() instanceof Gauntlet || Dungeon.hero.belongings.secondWep() instanceof Gloves || Dungeon.hero.belongings.secondWep() instanceof Sai || Dungeon.hero.belongings.secondWep() instanceof Gauntlet) {
                return 69;
            }
            return 127;
        }

        @Override
        public boolean act() {
            this.comboTime -= 1.0f;
            this.spend(1.0f);
            if (this.comboTime <= 0.0f) {
                this.detach();
            }
            return true;
        }

        public void addHit(Char enemy) {
            ++this.hits;
            if (this.comboTime <= 5.0f) {
                this.comboTime = 5.0f;
                this.initialTime = 5.0f;
            }
            if (!enemy.isAlive() || enemy.buff(Corruption.class) != null && enemy.HP == enemy.HT) {
                this.comboTime = 15.0f;
                this.initialTime = 15.0f;
            }
            if (this.hits >= 2 && this.icon() != 127) {
                GLog.p(Messages.get(Combo.class, "combo", this.hits), new Object[0]);
            }
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0.0f, (this.initialTime - this.comboTime) / this.initialTime);
        }

        @Override
        public String iconTextDisplay() {
            return Integer.toString((int)this.comboTime);
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", this.hits, this.dispTurns(this.comboTime));
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(TIME, this.comboTime);
            bundle.put(INITIAL_TIME, this.initialTime);
            bundle.put(RECENT_HITS, this.hits);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.comboTime = bundle.getInt(TIME);
            this.initialTime = bundle.getInt(INITIAL_TIME);
            this.hits = bundle.getInt(RECENT_HITS);
        }
    }
}

