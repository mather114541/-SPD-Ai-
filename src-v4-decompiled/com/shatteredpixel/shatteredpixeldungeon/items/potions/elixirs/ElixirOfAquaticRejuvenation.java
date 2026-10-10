/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.Elixir;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.GooBlob;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class ElixirOfAquaticRejuvenation
extends Elixir {
    public ElixirOfAquaticRejuvenation() {
        this.image = ItemSpriteSheet.ELIXIR_AQUA;
    }

    @Override
    public void apply(Hero hero) {
        if (Dungeon.isChallenged(4)) {
            PotionOfHealing.pharmacophobiaProc(hero);
        } else {
            Buff.affect(hero, AquaHealing.class).set(Math.round((float)hero.HT * 1.5f));
        }
    }

    public static class AquaHealing
    extends Buff {
        private int left;
        private static final String LEFT = "left";

        public AquaHealing() {
            this.type = Buff.buffType.POSITIVE;
            this.announced = true;
        }

        public void set(int amount) {
            if (amount > this.left) {
                this.left = amount;
            }
        }

        public void extend(float duration) {
            this.left = (int)((float)this.left + duration);
        }

        @Override
        public boolean act() {
            if (!this.target.flying && Dungeon.level.water[this.target.pos] && this.target.HP < this.target.HT) {
                float healAmt = GameMath.gate(1.0f, (float)this.target.HT / 50.0f, this.left);
                healAmt = Math.min(healAmt, (float)(this.target.HT - this.target.HP));
                healAmt = Random.Float() < healAmt % 1.0f ? (float)Math.ceil(healAmt) : (float)Math.floor(healAmt);
                this.target.HP += (int)healAmt;
                this.left -= (int)healAmt;
                this.target.sprite.showStatusWithIcon(65280, Integer.toString((int)healAmt), FloatingText.HEALING, new Object[0]);
                if (this.target.HP >= this.target.HT) {
                    this.target.HP = this.target.HT;
                    if (this.target instanceof Hero) {
                        ((Hero)this.target).resting = false;
                    }
                }
            }
            if (this.left <= 0) {
                this.detach();
                if (this.target instanceof Hero) {
                    ((Hero)this.target).resting = false;
                }
            } else {
                this.spend(1.0f);
            }
            return true;
        }

        @Override
        public int icon() {
            return 44;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0.0f, 0.75f, 0.75f);
        }

        @Override
        public float iconFadePercent() {
            float max = Math.round((float)this.target.HT * 1.5f);
            return Math.max(0.0f, (max - (float)this.left) / max);
        }

        @Override
        public String iconTextDisplay() {
            return Integer.toString(this.left);
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", this.left);
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
            this.inputs = new Class[]{PotionOfHealing.class, GooBlob.class};
            this.inQuantity = new int[]{1, 1};
            this.cost = 6;
            this.output = ElixirOfAquaticRejuvenation.class;
            this.outQuantity = 1;
        }

        @Override
        public Item brew(ArrayList<Item> ingredients) {
            Catalog.countUse(GooBlob.class);
            return super.brew(ingredients);
        }
    }
}

