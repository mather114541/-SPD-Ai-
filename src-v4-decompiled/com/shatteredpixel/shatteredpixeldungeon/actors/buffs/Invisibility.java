/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Preparation;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.RoundShield;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;

public class Invisibility
extends FlavourBuff {
    public static final float DURATION = 20.0f;

    public Invisibility() {
        this.type = Buff.buffType.POSITIVE;
        this.announced = true;
    }

    @Override
    public boolean attachTo(Char target) {
        if (super.attachTo(target)) {
            ++target.invisible;
            if (target instanceof Hero && ((Hero)target).subClass == HeroSubClass.ASSASSIN) {
                Buff.affect(target, Preparation.class);
            }
            if (target instanceof Hero && ((Hero)target).hasTalent(Talent.PROTECTIVE_SHADOWS)) {
                Buff.affect(target, Talent.ProtectiveShadowsTracker.class);
            }
            return true;
        }
        return false;
    }

    @Override
    public void detach() {
        if (this.target.invisible > 0) {
            --this.target.invisible;
        }
        super.detach();
    }

    @Override
    public int icon() {
        return 12;
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0.0f, (20.0f - this.visualcooldown()) / 20.0f);
    }

    @Override
    public void fx(boolean on) {
        if (on) {
            this.target.sprite.add(CharSprite.State.INVISIBLE);
        } else if (this.target.invisible == 0) {
            this.target.sprite.remove(CharSprite.State.INVISIBLE);
        }
    }

    public static void dispel() {
        if (Dungeon.hero == null) {
            return;
        }
        Invisibility.dispel(Dungeon.hero);
    }

    public static void dispel(Char ch) {
        RoundShield.GuardTracker guard;
        Swiftthistle.TimeBubble bubble;
        Preparation prep;
        TimekeepersHourglass.timeFreeze timeFreeze2;
        for (Buff buff : ch.buffs(Invisibility.class)) {
            buff.detach();
        }
        CloakOfShadows.cloakStealth cloakBuff = ch.buff(CloakOfShadows.cloakStealth.class);
        if (cloakBuff != null) {
            cloakBuff.dispel();
        }
        if ((timeFreeze2 = ch.buff(TimekeepersHourglass.timeFreeze.class)) != null) {
            timeFreeze2.detach();
        }
        if ((prep = ch.buff(Preparation.class)) != null) {
            prep.detach();
        }
        if ((bubble = ch.buff(Swiftthistle.TimeBubble.class)) != null) {
            bubble.detach();
        }
        if ((guard = ch.buff(RoundShield.GuardTracker.class)) != null && guard.hasBlocked) {
            guard.detach();
        }
    }
}

