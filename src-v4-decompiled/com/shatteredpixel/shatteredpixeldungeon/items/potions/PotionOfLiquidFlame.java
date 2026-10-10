/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.potions;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

public class PotionOfLiquidFlame
extends Potion {
    public PotionOfLiquidFlame() {
        this.icon = ItemSpriteSheet.Icons.POTION_LIQFLAME;
    }

    @Override
    public void shatter(int cell) {
        this.splash(cell);
        if (Dungeon.level.heroFOV[cell]) {
            this.identify();
            Sample.INSTANCE.play("sounds/shatter.mp3");
            Sample.INSTANCE.play("sounds/burning.mp3");
        }
        for (int offset : PathFinder.NEIGHBOURS9) {
            if (Dungeon.level.solid[cell + offset]) continue;
            GameScene.add(Blob.seed(cell + offset, 2, Fire.class));
        }
    }

    @Override
    public int value() {
        return this.isKnown() ? 30 * this.quantity : super.value();
    }
}

