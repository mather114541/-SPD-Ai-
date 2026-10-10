/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.spells;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArtifactRecharge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.MetalShard;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.TargetedSpell;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.CursedWand;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import java.util.ArrayList;

public class WildEnergy
extends TargetedSpell {
    public WildEnergy() {
        this.image = ItemSpriteSheet.WILD_ENERGY;
        this.usesTargeting = true;
        this.talentChance = 0.2f;
    }

    @Override
    protected void fx(Ballistica bolt, Callback callback) {
        CursedWand.cursedZap(this, curUser, bolt, callback);
    }

    @Override
    protected void affectTarget(Ballistica bolt, Hero hero) {
        Sample.INSTANCE.play("sounds/lightning.mp3");
        Sample.INSTANCE.play("sounds/chargeup.mp3");
        ScrollOfRecharging.charge(hero);
        SpellSprite.show(hero, 2);
        hero.belongings.charge(1.0f);
        ArtifactRecharge.chargeArtifacts(hero, 4.0f);
        Buff.affect(hero, Recharging.class, 8.0f);
        Buff.affect((Char)hero, ArtifactRecharge.class).extend((float)8.0f).ignoreHornOfPlenty = false;
        this.onSpellused();
    }

    @Override
    public int value() {
        return (int)(60.0f * ((float)this.quantity / 5.0f));
    }

    @Override
    public int energyVal() {
        return (int)(12.0f * ((float)this.quantity / 5.0f));
    }

    public static class Recipe
    extends Recipe.SimpleRecipe {
        private static final int OUT_QUANTITY = 5;

        public Recipe() {
            this.inputs = new Class[]{ScrollOfRecharging.class, MetalShard.class};
            this.inQuantity = new int[]{1, 1};
            this.cost = 4;
            this.output = WildEnergy.class;
            this.outQuantity = 5;
        }

        @Override
        public Item brew(ArrayList<Item> ingredients) {
            Catalog.countUse(MetalShard.class);
            return super.brew(ingredients);
        }
    }
}

