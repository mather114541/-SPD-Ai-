/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.Elixir;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class ElixirOfMight
extends Elixir {
    public ElixirOfMight() {
        this.image = ItemSpriteSheet.ELIXIR_MIGHT;
        this.unique = true;
        this.talentFactor = 2.0f;
    }

    @Override
    public void apply(Hero hero) {
        this.identify();
        ++hero.STR;
        hero.sprite.showStatusWithIcon(65280, "1", FloatingText.STRENGTH, new Object[0]);
        Buff.affect(hero, HTBoost.class).reset();
        HTBoost boost = Buff.affect(hero, HTBoost.class);
        boost.reset();
        hero.updateHT(true);
        GLog.p(Messages.get(this, "msg", hero.STR()), new Object[0]);
        Badges.validateStrengthAttained();
        Badges.validateDuelistUnlock();
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", HTBoost.boost(Dungeon.hero != null ? Dungeon.hero.HT : 20));
    }

    public static class HTBoost
    extends Buff {
        private int left;
        private static String LEFT = "left";

        public HTBoost() {
            this.type = Buff.buffType.POSITIVE;
        }

        public void reset() {
            this.left = 5;
        }

        public int boost() {
            return Math.round((float)(this.left * HTBoost.boost(15 + 5 * ((Hero)this.target).lvl)) / 5.0f);
        }

        public static int boost(int HT) {
            return Math.round(4.0f + (float)HT / 20.0f);
        }

        public void onLevelUp() {
            --this.left;
            if (this.left <= 0) {
                this.detach();
            }
        }

        @Override
        public int icon() {
            return 44;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(1.0f, 0.5f, 0.0f);
        }

        @Override
        public float iconFadePercent() {
            return (5.0f - (float)this.left) / 5.0f;
        }

        @Override
        public String iconTextDisplay() {
            return Integer.toString(this.left);
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", this.boost(), this.left);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(LEFT, this.left);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.left = bundle.getInt(LEFT);
        }
    }

    public static class Recipe
    extends Recipe.SimpleRecipe {
        public Recipe() {
            this.inputs = new Class[]{PotionOfStrength.class};
            this.inQuantity = new int[]{1};
            this.cost = 16;
            this.output = ElixirOfMight.class;
            this.outQuantity = 1;
        }
    }
}

