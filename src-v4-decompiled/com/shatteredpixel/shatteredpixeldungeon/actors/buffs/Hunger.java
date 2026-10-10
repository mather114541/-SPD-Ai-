/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Shadows;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.WellFed;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfChallenge;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.SaltCube;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

public class Hunger
extends Buff
implements Hero.Doom {
    public static final float HUNGRY = 300.0f;
    public static final float STARVING = 450.0f;
    private float level;
    private float partialDamage;
    private static final String LEVEL = "level";
    private static final String PARTIALDAMAGE = "partialDamage";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LEVEL, this.level);
        bundle.put(PARTIALDAMAGE, this.partialDamage);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.level = bundle.getFloat(LEVEL);
        this.partialDamage = bundle.getFloat(PARTIALDAMAGE);
    }

    @Override
    public boolean act() {
        if (Dungeon.level.locked || this.target.buff(WellFed.class) != null || SPDSettings.intro() || this.target.buff(ScrollOfChallenge.ChallengeArena.class) != null) {
            this.spend(1.0f);
            return true;
        }
        if (this.target.isAlive() && this.target instanceof Hero) {
            Hero hero = (Hero)this.target;
            if (this.isStarving()) {
                this.partialDamage += (float)this.target.HT / 1000.0f;
                if (this.partialDamage > 1.0f) {
                    this.target.damage((int)this.partialDamage, this);
                    this.partialDamage -= (float)((int)this.partialDamage);
                }
            } else {
                float newLevel;
                float hungerDelay = 1.0f;
                if (this.target.buff(Shadows.class) != null) {
                    hungerDelay *= 1.5f;
                }
                if ((newLevel = this.level + 1.0f / (hungerDelay /= SaltCube.hungerGainMultiplier())) >= 450.0f) {
                    GLog.n(Messages.get(this, "onstarving", new Object[0]), new Object[0]);
                    hero.damage(1, this);
                    hero.interrupt();
                    newLevel = 450.0f;
                } else if (newLevel >= 300.0f && this.level < 300.0f) {
                    GLog.w(Messages.get(this, "onhungry", new Object[0]), new Object[0]);
                    if (!Document.ADVENTURERS_GUIDE.isPageRead("Food")) {
                        GameScene.flashForDocument(Document.ADVENTURERS_GUIDE, "Food");
                    }
                }
                this.level = newLevel;
            }
            this.spend(1.0f);
        } else {
            this.diactivate();
        }
        return true;
    }

    public void satisfy(float energy) {
        this.affectHunger(energy, false);
    }

    public void affectHunger(float energy) {
        this.affectHunger(energy, false);
    }

    public void affectHunger(float energy, boolean overrideLimits) {
        if (energy < 0.0f && this.target.buff(WellFed.class) != null) {
            this.target.buff(WellFed.class).left = (int)((float)this.target.buff(WellFed.class).left + energy);
            BuffIndicator.refreshHero();
            return;
        }
        float oldLevel = this.level;
        this.level -= energy;
        if (this.level < 0.0f && !overrideLimits) {
            this.level = 0.0f;
        } else if (this.level > 450.0f) {
            float excess = this.level - 450.0f;
            this.level = 450.0f;
            this.partialDamage += excess * ((float)this.target.HT / 1000.0f);
            if (this.partialDamage > 1.0f) {
                this.target.damage((int)this.partialDamage, this);
                this.partialDamage -= (float)((int)this.partialDamage);
            }
        }
        if (oldLevel < 300.0f && this.level >= 300.0f) {
            GLog.w(Messages.get(this, "onhungry", new Object[0]), new Object[0]);
        } else if (oldLevel < 450.0f && this.level >= 450.0f) {
            GLog.n(Messages.get(this, "onstarving", new Object[0]), new Object[0]);
            this.target.damage(1, this);
        }
        BuffIndicator.refreshHero();
    }

    public boolean isStarving() {
        return this.level >= 450.0f;
    }

    public int hunger() {
        return (int)Math.ceil(this.level);
    }

    @Override
    public int icon() {
        if (this.level < 300.0f) {
            return 127;
        }
        if (this.level < 450.0f) {
            return 5;
        }
        return 6;
    }

    @Override
    public String name() {
        if (this.level < 450.0f) {
            return Messages.get(this, "hungry", new Object[0]);
        }
        return Messages.get(this, "starving", new Object[0]);
    }

    @Override
    public String desc() {
        Object result = this.level < 450.0f ? Messages.get(this, "desc_intro_hungry", new Object[0]) : Messages.get(this, "desc_intro_starving", new Object[0]);
        result = (String)result + Messages.get(this, "desc", new Object[0]);
        return result;
    }

    @Override
    public void onDeath() {
        Badges.validateDeathFromHunger();
        Dungeon.fail(this);
        GLog.n(Messages.get(this, "ondeath", new Object[0]), new Object[0]);
    }
}

