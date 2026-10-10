/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.food;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.CounterBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Berry
extends Food {
    public Berry() {
        this.image = ItemSpriteSheet.BERRY;
        this.energy = 100.0f;
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
        SeedCounter counter = Buff.count(hero, SeedCounter.class, 1.0f);
        if (counter.count() >= 2.0f) {
            Dungeon.level.drop((Item)Generator.randomUsingDefaults((Generator.Category)Generator.Category.SEED), (int)hero.pos).sprite.drop();
            counter.detach();
        }
    }

    @Override
    public int value() {
        return 5 * this.quantity;
    }

    public static class SeedCounter
    extends CounterBuff {
        public SeedCounter() {
            this.revivePersists = true;
        }
    }
}

