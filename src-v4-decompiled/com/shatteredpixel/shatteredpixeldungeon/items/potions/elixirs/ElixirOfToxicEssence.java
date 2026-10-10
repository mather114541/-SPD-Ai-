/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ToxicImbue;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PoisonParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.Elixir;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfCorrosiveGas;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class ElixirOfToxicEssence
extends Elixir {
    public ElixirOfToxicEssence() {
        this.image = ItemSpriteSheet.ELIXIR_TOXIC;
    }

    @Override
    public void apply(Hero hero) {
        Buff.affect(hero, ToxicImbue.class).set(50.0f);
        hero.sprite.emitter().burst(PoisonParticle.SPLASH, 10);
    }

    public static class Recipe
    extends Recipe.SimpleRecipe {
        public Recipe() {
            this.inputs = new Class[]{PotionOfCorrosiveGas.class};
            this.inQuantity = new int[]{1};
            this.cost = 8;
            this.output = ElixirOfToxicEssence.class;
            this.outQuantity = 1;
        }
    }
}

