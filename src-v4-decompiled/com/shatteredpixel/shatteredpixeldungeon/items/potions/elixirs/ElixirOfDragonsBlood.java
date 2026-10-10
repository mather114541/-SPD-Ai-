/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FireImbue;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.Elixir;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfDragonsBreath;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;

public class ElixirOfDragonsBlood
extends Elixir {
    public ElixirOfDragonsBlood() {
        this.image = ItemSpriteSheet.ELIXIR_DRAGON;
    }

    @Override
    public void apply(Hero hero) {
        Buff.affect(hero, FireImbue.class).set(50.0f);
        Sample.INSTANCE.play("sounds/burning.mp3");
        hero.sprite.emitter().burst(FlameParticle.FACTORY, 10);
    }

    public static class Recipe
    extends Recipe.SimpleRecipe {
        public Recipe() {
            this.inputs = new Class[]{PotionOfDragonsBreath.class};
            this.inQuantity = new int[]{1};
            this.cost = 10;
            this.output = ElixirOfDragonsBlood.class;
            this.outQuantity = 1;
        }
    }
}

