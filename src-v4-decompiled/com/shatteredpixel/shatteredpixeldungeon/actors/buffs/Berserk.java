/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.HoldFast;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShieldBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Image;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;
import com.watabou.utils.Random;

public class Berserk
extends ShieldBuff
implements ActionIndicator.Action {
    private State state;
    private static final float LEVEL_RECOVER_START = 4.0f;
    private float levelRecovery;
    private static final int TURN_RECOVERY_START = 100;
    private int turnRecovery;
    public int powerLossBuffer;
    private float power;
    private static final String STATE = "state";
    private static final String LEVEL_RECOVERY = "levelrecovery";
    private static final String TURN_RECOVERY = "turn_recovery";
    private static final String POWER = "power";
    private static final String POWER_BUFFER = "power_buffer";

    public Berserk() {
        this.type = Buff.buffType.POSITIVE;
        this.detachesAtZero = false;
        this.shieldUsePriority = -1;
        this.state = State.NORMAL;
        this.powerLossBuffer = 0;
        this.power = 0.0f;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(STATE, this.state);
        bundle.put(POWER, this.power);
        bundle.put(POWER_BUFFER, this.powerLossBuffer);
        bundle.put(LEVEL_RECOVERY, this.levelRecovery);
        bundle.put(TURN_RECOVERY, this.turnRecovery);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.state = bundle.getEnum(STATE, State.class);
        this.power = bundle.getFloat(POWER);
        this.powerLossBuffer = bundle.getInt(POWER_BUFFER);
        this.levelRecovery = bundle.getFloat(LEVEL_RECOVERY);
        this.turnRecovery = bundle.getInt(TURN_RECOVERY);
        if (this.power >= 1.0f && this.state == State.NORMAL) {
            ActionIndicator.setAction(this);
        }
    }

    @Override
    public boolean act() {
        if (this.state == State.BERSERK) {
            if (this.target.shielding() > 0) {
                float dmg = (float)Math.ceil((float)this.target.shielding() * 0.025f) * HoldFast.buffDecayFactor(this.target);
                if (Random.Float() < dmg % 1.0f) {
                    dmg += 1.0f;
                }
                ShieldBuff.processDamage(this.target, (int)dmg, this);
                if (this.target.shielding() <= 0) {
                    this.state = State.RECOVERING;
                    this.power = 0.0f;
                    BuffIndicator.refreshHero();
                    if (!this.target.isAlive()) {
                        this.target.die(this);
                        if (!this.target.isAlive()) {
                            Dungeon.fail(this);
                        }
                    }
                }
            } else {
                this.state = State.RECOVERING;
                this.power = 0.0f;
                if (!this.target.isAlive()) {
                    this.target.die(this);
                    if (!this.target.isAlive()) {
                        Dungeon.fail(this);
                    }
                }
            }
        } else if (this.state == State.NORMAL) {
            if (this.powerLossBuffer > 0) {
                --this.powerLossBuffer;
            } else {
                this.power = (float)((double)this.power - (double)(GameMath.gate(0.1f, this.power, 1.0f) * 0.05f) * Math.pow((float)this.target.HP / (float)this.target.HT, 2.0));
                if (this.power < 1.0f) {
                    ActionIndicator.clearAction(this);
                } else {
                    ActionIndicator.refresh();
                }
                if (this.power <= 0.0f) {
                    this.detach();
                }
            }
        } else if (this.state == State.RECOVERING && this.levelRecovery == 0.0f && Regeneration.regenOn()) {
            --this.turnRecovery;
            if (this.turnRecovery <= 0) {
                this.turnRecovery = 0;
                this.state = State.NORMAL;
            }
        }
        this.spend(1.0f);
        return true;
    }

    @Override
    public void detach() {
        super.detach();
        if (this.state == State.BERSERK) {
            this.state = State.RECOVERING;
        }
        ActionIndicator.clearAction(this);
    }

    public float enchantFactor(float chance) {
        return chance + Math.min(1.0f, this.power) * 0.15f * (float)((Hero)this.target).pointsInTalent(Talent.ENRAGED_CATALYST);
    }

    public float damageFactor(float dmg) {
        return dmg * Math.min(1.5f, 1.0f + this.power / 2.0f);
    }

    public boolean berserking() {
        if (this.target.HP == 0 && this.state == State.NORMAL && this.power >= 1.0f && ((Hero)this.target).hasTalent(Talent.DEATHLESS_FURY)) {
            this.startBerserking();
            ActionIndicator.clearAction(this);
        }
        return this.state == State.BERSERK && this.target.shielding() > 0;
    }

    private void startBerserking() {
        this.state = State.BERSERK;
        SpellSprite.show(this.target, 3);
        Sample.INSTANCE.play("sounds/challenge.mp3");
        GameScene.flash(0xFF0000);
        if (this.target.HP > 0) {
            this.turnRecovery = 100;
            this.levelRecovery = 0.0f;
        } else {
            this.levelRecovery = 4.0f - (float)((Hero)this.target).pointsInTalent(Talent.DEATHLESS_FURY);
            this.turnRecovery = 0;
        }
        int shieldAmount = this.currentShieldBoost();
        this.setShield(shieldAmount);
        this.target.sprite.showStatusWithIcon(65280, Integer.toString(shieldAmount), FloatingText.SHIELDING, new Object[0]);
        BuffIndicator.refreshHero();
    }

    public int currentShieldBoost() {
        float shieldMultiplier = 1.0f + 2.0f * (float)Math.pow(1.0f - (float)this.target.HP / (float)this.target.HT, 3.0);
        if (this.power > 1.0f) {
            shieldMultiplier *= this.power;
            this.levelRecovery *= 2.0f - this.power;
            this.turnRecovery = (int)((float)this.turnRecovery * (2.0f - this.power));
        }
        int baseShield = 8;
        if (this.target instanceof Hero && ((Hero)this.target).belongings.armor() != null) {
            baseShield += 2 * ((Hero)this.target).belongings.armor().buffedLvl();
        }
        return Math.round((float)baseShield * shieldMultiplier);
    }

    public int maxShieldBoost() {
        int baseShield = 8;
        if (this.target instanceof Hero && ((Hero)this.target).belongings.armor() != null) {
            baseShield += 2 * ((Hero)this.target).belongings.armor().buffedLvl();
        }
        return baseShield * 3;
    }

    public void damage(int damage) {
        if (this.state != State.NORMAL) {
            return;
        }
        float maxPower = 1.0f + 0.1667f * (float)((Hero)this.target).pointsInTalent(Talent.ENDLESS_RAGE);
        this.power = Math.min(maxPower, this.power + (float)damage / (float)this.target.HT / 4.0f);
        BuffIndicator.refreshHero();
        this.powerLossBuffer = 3;
        if (this.power >= 1.0f) {
            ActionIndicator.setAction(this);
        }
    }

    public void recover(float percent) {
        if (this.state == State.RECOVERING && this.levelRecovery > 0.0f) {
            this.levelRecovery -= percent;
            if (this.levelRecovery <= 0.0f) {
                this.levelRecovery = 0.0f;
                if (this.turnRecovery == 0) {
                    this.state = State.NORMAL;
                }
            }
        }
    }

    @Override
    public String actionName() {
        return Messages.get(this, "action_name", new Object[0]);
    }

    @Override
    public int actionIcon() {
        return 104;
    }

    @Override
    public Visual secondaryVisual() {
        BitmapText txt = new BitmapText(PixelScene.pixelFont);
        txt.text((int)(this.power * 100.0f) + "%");
        txt.hardlight(65280);
        txt.measure();
        return txt;
    }

    @Override
    public int indicatorColor() {
        return 0x660000;
    }

    @Override
    public void doAction() {
        BrokenSeal.WarriorShield shield = this.target.buff(BrokenSeal.WarriorShield.class);
        if (shield != null && shield.maxShield() > 0) {
            this.startBerserking();
            ActionIndicator.clearAction(this);
        } else {
            GLog.w(Messages.get(this, "no_seal", new Object[0]), new Object[0]);
        }
    }

    @Override
    public int icon() {
        return 40;
    }

    @Override
    public void tintIcon(Image icon) {
        switch (this.state.ordinal()) {
            default: {
                if (this.power < 1.0f) {
                    icon.hardlight(1.0f, 0.5f, 0.0f);
                    break;
                }
                icon.hardlight(1.0f, 0.0f, 0.0f);
                break;
            }
            case 1: {
                icon.hardlight(1.0f, 0.0f, 0.0f);
                break;
            }
            case 2: {
                icon.hardlight(0.0f, 0.0f, 1.0f);
            }
        }
    }

    @Override
    public float iconFadePercent() {
        switch (this.state.ordinal()) {
            default: {
                float maxPower = 1.0f + 0.1667f * (float)((Hero)this.target).pointsInTalent(Talent.ENDLESS_RAGE);
                return (maxPower - this.power) / maxPower;
            }
            case 1: {
                return 1.0f - (float)this.shielding() / (float)this.maxShieldBoost();
            }
            case 2: 
        }
        if (this.levelRecovery > 0.0f) {
            return this.levelRecovery / (4.0f - (float)Dungeon.hero.pointsInTalent(Talent.DEATHLESS_FURY));
        }
        return (float)this.turnRecovery / 100.0f;
    }

    @Override
    public String iconTextDisplay() {
        switch (this.state.ordinal()) {
            default: {
                return (int)(this.power * 100.0f) + "%";
            }
            case 1: {
                return Integer.toString(this.shielding());
            }
            case 2: 
        }
        if (this.levelRecovery > 0.0f) {
            return Messages.decimalFormat("#.##", this.levelRecovery);
        }
        return Integer.toString(this.turnRecovery);
    }

    @Override
    public String name() {
        switch (this.state.ordinal()) {
            default: {
                return Messages.get(this, "angered", new Object[0]);
            }
            case 1: {
                return Messages.get(this, "berserk", new Object[0]);
            }
            case 2: 
        }
        return Messages.get(this, "recovering", new Object[0]);
    }

    @Override
    public String desc() {
        float dispDamage = (float)((int)this.damageFactor(10000.0f)) / 100.0f - 100.0f;
        switch (this.state.ordinal()) {
            default: {
                return Messages.get(this, "angered_desc", Math.floor(this.power * 100.0f), Float.valueOf(dispDamage), this.currentShieldBoost());
            }
            case 1: {
                return Messages.get(this, "berserk_desc", this.shielding());
            }
            case 2: 
        }
        if (this.levelRecovery > 0.0f) {
            return Messages.get(this, "recovering_desc", new Object[0]) + "\n\n" + Messages.get(this, "recovering_desc_levels", Float.valueOf(this.levelRecovery));
        }
        return Messages.get(this, "recovering_desc", new Object[0]) + "\n\n" + Messages.get(this, "recovering_desc_turns", this.turnRecovery);
    }

    private static enum State {
        NORMAL,
        BERSERK,
        RECOVERING;

    }
}

