/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorrosiveGas;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

public class PotionOfCorrosiveGas
extends ExoticPotion {
    public PotionOfCorrosiveGas() {
        this.icon = ItemSpriteSheet.Icons.POTION_CORROGAS;
    }

    @Override
    public void shatter(int cell) {
        this.splash(cell);
        if (Dungeon.level.heroFOV[cell]) {
            this.identify();
            Sample.INSTANCE.play("sounds/shatter.mp3");
            Sample.INSTANCE.play("sounds/gas.mp3");
        }
        int centerVolume = 25;
        for (int i : PathFinder.NEIGHBOURS8) {
            if (!Dungeon.level.solid[cell + i]) {
                GameScene.add(Blob.seed(cell + i, 25, CorrosiveGas.class).setStrength(2 + Dungeon.scalingDepth() / 5));
                continue;
            }
            centerVolume += 25;
        }
        GameScene.add(Blob.seed(cell, centerVolume, CorrosiveGas.class).setStrength(2 + Dungeon.scalingDepth() / 5));
    }
}

