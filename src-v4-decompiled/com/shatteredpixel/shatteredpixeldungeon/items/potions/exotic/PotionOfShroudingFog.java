/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SmokeScreen;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

public class PotionOfShroudingFog
extends ExoticPotion {
    public PotionOfShroudingFog() {
        this.icon = ItemSpriteSheet.Icons.POTION_SHROUDFOG;
    }

    @Override
    public void shatter(int cell) {
        this.splash(cell);
        if (Dungeon.level.heroFOV[cell]) {
            this.identify();
            Sample.INSTANCE.play("sounds/shatter.mp3");
            Sample.INSTANCE.play("sounds/gas.mp3");
        }
        int centerVolume = 180;
        for (int i : PathFinder.NEIGHBOURS8) {
            if (!Dungeon.level.solid[cell + i]) {
                GameScene.add(Blob.seed(cell + i, 180, SmokeScreen.class));
                continue;
            }
            centerVolume += 180;
        }
        GameScene.add(Blob.seed(cell, centerVolume, SmokeScreen.class));
    }
}

