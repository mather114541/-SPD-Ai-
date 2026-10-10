/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FrostImbue;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SnowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.Elixir;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfSnapFreeze;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class ElixirOfIcyTouch
extends Elixir {
    public ElixirOfIcyTouch() {
        this.image = ItemSpriteSheet.ELIXIR_ICY;
    }

    @Override
    public void apply(Hero hero) {
        Buff.prolong(hero, FrostImbue.class, 50.0f);
        hero.sprite.emitter().burst(SnowParticle.FACTORY, 5);
    }

    public static class Recipe
    extends Recipe.SimpleRecipe {
        public Recipe() {
            this.inputs = new Class[]{PotionOfSnapFreeze.class};
            this.inQuantity = new int[]{1};
            this.cost = 6;
            this.output = ElixirOfIcyTouch.class;
            this.outQuantity = 1;
        }
    }
}

