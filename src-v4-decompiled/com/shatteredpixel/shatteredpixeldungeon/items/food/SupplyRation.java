/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.food;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class SupplyRation
extends Food {
    public SupplyRation() {
        this.image = ItemSpriteSheet.SUPPLY_RATION;
        this.energy = 200.0f;
        this.bones = false;
    }

    @Override
    protected float eatingTime() {
        if (Dungeon.hero.hasTalent(Talent.IRON_STOMACH) || Dungeon.hero.hasTalent(Talent.ENERGIZING_MEAL) || Dungeon.hero.hasTalent(Talent.MYSTICAL_MEAL) || Dungeon.hero.hasTalent(Talent.INVIGORATING_MEAL) || Dungeon.hero.hasTalent(Talent.FOCUSED_MEAL) || Dungeon.hero.hasTalent(Talent.ENLIGHTENING_MEAL)) {
            return 0.0f;
        }
        return 1.0f;
    }

    @Override
    protected void satisfy(Hero hero) {
        super.satisfy(hero);
        hero.HP = Math.min(hero.HP + 5, hero.HT);
        hero.sprite.showStatusWithIcon(65280, "5", FloatingText.HEALING, new Object[0]);
        CloakOfShadows cloak = hero.belongings.getItem(CloakOfShadows.class);
        if (cloak != null) {
            cloak.directCharge(1);
            ScrollOfRecharging.charge(hero);
        }
    }

    @Override
    public int value() {
        return 10 * this.quantity;
    }
}

