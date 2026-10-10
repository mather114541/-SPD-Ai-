/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.InventoryClericSpell;
import com.shatteredpixel.shatteredpixeldungeon.effects.Identification;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class HolyIntuition
extends InventoryClericSpell {
    public static final HolyIntuition INSTANCE = new HolyIntuition();

    @Override
    public int icon() {
        return 43;
    }

    @Override
    protected boolean usableOnItem(Item item) {
        return (item instanceof EquipableItem || item instanceof Wand) && !item.isIdentified() && !item.cursedKnown;
    }

    @Override
    public float chargeUse(Hero hero) {
        return 4 - hero.pointsInTalent(Talent.HOLY_INTUITION);
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero) && hero.hasTalent(Talent.HOLY_INTUITION);
    }

    @Override
    protected void onItemSelected(HolyTome tome, Hero hero, Item item) {
        if (item == null) {
            return;
        }
        item.cursedKnown = true;
        if (item.cursed) {
            GLog.w(Messages.get(this, "cursed", new Object[0]), new Object[0]);
        } else {
            GLog.i(Messages.get(this, "uncursed", new Object[0]), new Object[0]);
        }
        hero.spend(1.0f);
        hero.busy();
        hero.sprite.operate(hero.pos);
        hero.sprite.parent.add(new Identification(hero.sprite.center().offset(0.0f, -16.0f)));
        Sample.INSTANCE.play("sounds/read.mp3");
        this.onSpellCast(tome, hero);
    }
}

