/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Bee;
import com.shatteredpixel.shatteredpixeldungeon.items.Honeypot;
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.Elixir;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;

public class ElixirOfHoneyedHealing
extends Elixir {
    public ElixirOfHoneyedHealing() {
        this.image = ItemSpriteSheet.ELIXIR_HONEY;
    }

    @Override
    public void apply(Hero hero) {
        PotionOfHealing.cure(hero);
        PotionOfHealing.heal(hero);
        Buff.affect(hero, Hunger.class).satisfy(150.0f);
        Talent.onFoodEaten(hero, 150.0f, this);
    }

    @Override
    public void shatter(int cell) {
        Char ch;
        this.splash(cell);
        if (Dungeon.level.heroFOV[cell]) {
            Sample.INSTANCE.play("sounds/shatter.mp3");
        }
        if ((ch = Actor.findChar(cell)) != null) {
            PotionOfHealing.cure(ch);
            PotionOfHealing.heal(ch);
            if (ch instanceof Bee && ch.alignment != ElixirOfHoneyedHealing.curUser.alignment) {
                ch.alignment = Char.Alignment.ALLY;
                ((Bee)ch).setPotInfo(-1, null);
            }
        }
    }

    @Override
    public int value() {
        return this.quantity * 40;
    }

    @Override
    public int energyVal() {
        return this.quantity * 8;
    }

    public static class Recipe
    extends Recipe.SimpleRecipe {
        public Recipe() {
            this.inputs = new Class[]{PotionOfHealing.class, Honeypot.ShatteredPot.class};
            this.inQuantity = new int[]{1, 1};
            this.cost = 2;
            this.output = ElixirOfHoneyedHealing.class;
            this.outQuantity = 1;
        }
    }
}

