/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLevitation;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.Elixir;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;

public class ElixirOfFeatherFall
extends Elixir {
    public ElixirOfFeatherFall() {
        this.image = ItemSpriteSheet.ELIXIR_FEATHER;
        this.talentChance = 1.0f;
    }

    @Override
    public void apply(Hero hero) {
        Buff.append(hero, FeatherBuff.class, 50.0f);
        hero.sprite.emitter().burst(Speck.factory(106), 20);
        GLog.p(Messages.get(this, "light", new Object[0]), new Object[0]);
    }

    public static class Recipe
    extends Recipe.SimpleRecipe {
        private static final int OUT_QUANTITY = 1;

        public Recipe() {
            this.inputs = new Class[]{PotionOfLevitation.class};
            this.inQuantity = new int[]{1};
            this.cost = 10;
            this.output = ElixirOfFeatherFall.class;
            this.outQuantity = 1;
        }
    }

    public static class FeatherBuff
    extends FlavourBuff {
        public static final float DURATION = 50.0f;

        public FeatherBuff() {
            this.type = Buff.buffType.POSITIVE;
        }

        public void processFall() {
            this.spend(-10.0f);
            if (this.cooldown() <= 0.0f) {
                this.detach();
            }
        }

        @Override
        public int icon() {
            return 1;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(1.0f, 2.0f, 1.25f);
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0.0f, (50.0f - this.visualcooldown()) / 50.0f);
        }
    }
}

