/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.potions;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

public class PotionOfFrost
extends Potion {
    public PotionOfFrost() {
        this.icon = ItemSpriteSheet.Icons.POTION_FROST;
    }

    @Override
    public void shatter(int cell) {
        this.splash(cell);
        if (Dungeon.level.heroFOV[cell]) {
            this.identify();
            Sample.INSTANCE.play("sounds/shatter.mp3");
        }
        for (int offset : PathFinder.NEIGHBOURS9) {
            if (Dungeon.level.solid[cell + offset]) continue;
            GameScene.add(Blob.seed(cell + offset, 10, Freezing.class));
        }
    }

    @Override
    public int value() {
        return this.isKnown() ? 30 * this.quantity : super.value();
    }
}

