/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.potions.brews;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blizzard;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.Brew;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

public class BlizzardBrew
extends Brew {
    public BlizzardBrew() {
        this.image = ItemSpriteSheet.BREW_BLIZZARD;
    }

    @Override
    public void shatter(int cell) {
        this.splash(cell);
        if (Dungeon.level.heroFOV[cell]) {
            Sample.INSTANCE.play("sounds/shatter.mp3");
            Sample.INSTANCE.play("sounds/gas.mp3");
        }
        int centerVolume = 120;
        for (int i : PathFinder.NEIGHBOURS8) {
            if (!Dungeon.level.solid[cell + i]) {
                GameScene.add(Blob.seed(cell + i, 120, Blizzard.class));
                continue;
            }
            centerVolume += 120;
        }
        GameScene.add(Blob.seed(cell, centerVolume, Blizzard.class));
    }

    public static class Recipe
    extends Recipe.SimpleRecipe {
        public Recipe() {
            this.inputs = new Class[]{PotionOfFrost.class};
            this.inQuantity = new int[]{1};
            this.cost = 8;
            this.output = BlizzardBrew.class;
            this.outQuantity = 1;
        }
    }
}

