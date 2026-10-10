/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.Dewdrop;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.VialOfBlood;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;
import java.util.ArrayList;

public class Waterskin
extends Item {
    private static final int MAX_VOLUME = 20;
    private static final String AC_DRINK = "DRINK";
    private static final float TIME_TO_DRINK = 1.0f;
    private static final String TXT_STATUS = "%d/%d";
    private int volume;
    private static final String VOLUME = "volume";

    public Waterskin() {
        this.image = ItemSpriteSheet.WATERSKIN;
        this.defaultAction = AC_DRINK;
        this.unique = true;
        this.volume = 0;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(VOLUME, this.volume);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.volume = bundle.getInt(VOLUME);
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (this.volume > 0) {
            actions.add(AC_DRINK);
        }
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_DRINK)) {
            if (this.volume > 0) {
                float missingHealthPercent = 1.0f - (float)hero.HP / (float)hero.HT;
                float dropsNeeded = missingHealthPercent / 0.05f;
                if (dropsNeeded > 1.01f && VialOfBlood.delayBurstHealing()) {
                    dropsNeeded /= VialOfBlood.totalHealMultiplier();
                }
                int curShield = 0;
                if (hero.buff(Barrier.class) != null) {
                    curShield = hero.buff(Barrier.class).shielding();
                }
                int maxShield = Math.round((float)hero.HT * 0.2f * (float)hero.pointsInTalent(Talent.SHIELDING_DEW));
                if (hero.hasTalent(Talent.SHIELDING_DEW)) {
                    float missingShieldPercent = 1.0f - (float)curShield / (float)maxShield;
                    if ((missingShieldPercent *= 0.2f * (float)hero.pointsInTalent(Talent.SHIELDING_DEW)) > 0.0f) {
                        dropsNeeded += missingShieldPercent / 0.05f;
                    }
                }
                int dropsToConsume = (int)Math.ceil(dropsNeeded - 0.01f);
                if (Dewdrop.consumeDew(dropsToConsume = (int)GameMath.gate(1.0f, dropsToConsume, this.volume), hero, true)) {
                    this.volume -= dropsToConsume;
                    Catalog.countUses(Dewdrop.class, dropsToConsume);
                    hero.spend(1.0f);
                    hero.busy();
                    Sample.INSTANCE.play("sounds/drink.mp3");
                    hero.sprite.operate(hero.pos);
                    Waterskin.updateQuickslot();
                }
            } else {
                GLog.w(Messages.get(this, "empty", new Object[0]), new Object[0]);
            }
        }
    }

    @Override
    public String info() {
        Object info = super.info();
        info = this.volume == 0 ? (String)info + "\n\n" + Messages.get(this, "desc_water", new Object[0]) : (String)info + "\n\n" + Messages.get(this, "desc_heal", new Object[0]);
        if (this.isFull()) {
            info = (String)info + "\n\n" + Messages.get(this, "desc_full", new Object[0]);
        }
        return info;
    }

    public void empty() {
        this.volume = 0;
        Waterskin.updateQuickslot();
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    public boolean isFull() {
        return this.volume >= 20;
    }

    public void collectDew(Dewdrop dew) {
        GLog.i(Messages.get(this, "collected", new Object[0]), new Object[0]);
        this.volume += dew.quantity;
        if (this.volume >= 20) {
            this.volume = 20;
            GLog.p(Messages.get(this, "full", new Object[0]), new Object[0]);
        }
        Waterskin.updateQuickslot();
    }

    public void fill() {
        this.volume = 20;
        Waterskin.updateQuickslot();
    }

    @Override
    public String status() {
        return Messages.format(TXT_STATUS, this.volume, 20);
    }
}

