/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.watabou.utils.Bundle;

public class SnipersMark
extends FlavourBuff
implements ActionIndicator.Action {
    public int object = 0;
    public float percentDmgBonus = 0.0f;
    private static final String OBJECT = "object";
    private static final String BONUS = "bonus";
    public static final float DURATION = 4.0f;

    public SnipersMark() {
        this.type = Buff.buffType.POSITIVE;
    }

    public void set(int object, float bonus) {
        this.object = object;
        this.percentDmgBonus = bonus;
        SpiritBow bow = Dungeon.hero.belongings.getItem(SpiritBow.class);
        if (bow != null) {
            ActionIndicator.setAction(this);
        }
    }

    @Override
    public void detach() {
        super.detach();
        ActionIndicator.clearAction(this);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(OBJECT, this.object);
        bundle.put(BONUS, this.percentDmgBonus);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.object = bundle.getInt(OBJECT);
        this.percentDmgBonus = bundle.getFloat(BONUS);
    }

    @Override
    public int icon() {
        return 27;
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0.0f, (4.0f - this.visualcooldown()) / 4.0f);
    }

    @Override
    public String actionName() {
        SpiritBow bow = Dungeon.hero.belongings.getItem(SpiritBow.class);
        if (bow == null) {
            return null;
        }
        switch (bow.augment) {
            default: {
                return Messages.get(this, "action_name_snapshot", new Object[0]);
            }
            case SPEED: {
                return Messages.get(this, "action_name_volley", new Object[0]);
            }
            case DAMAGE: 
        }
        return Messages.get(this, "action_name_sniper", new Object[0]);
    }

    @Override
    public int actionIcon() {
        return 108;
    }

    @Override
    public int indicatorColor() {
        return 0x444444;
    }

    @Override
    public void doAction() {
        Hero hero = Dungeon.hero;
        if (hero == null) {
            return;
        }
        SpiritBow bow = hero.belongings.getItem(SpiritBow.class);
        if (bow == null) {
            return;
        }
        SpiritBow.SpiritArrow arrow2 = bow.knockArrow();
        if (arrow2 == null) {
            return;
        }
        Char ch = (Char)Actor.findById(this.object);
        if (ch == null) {
            return;
        }
        int cell = QuickSlotButton.autoAim(ch, arrow2);
        if (cell == -1) {
            return;
        }
        bow.sniperSpecial = true;
        bow.sniperSpecialBonusDamage = this.percentDmgBonus;
        arrow2.cast(hero, cell);
        this.detach();
    }
}

