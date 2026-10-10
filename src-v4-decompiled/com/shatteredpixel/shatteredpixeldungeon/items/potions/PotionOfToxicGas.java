/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.potions;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;

public class PotionOfToxicGas
extends Potion {
    public PotionOfToxicGas() {
        this.icon = ItemSpriteSheet.Icons.POTION_TOXICGAS;
    }

    @Override
    public void shatter(int cell) {
        this.splash(cell);
        if (Dungeon.level.heroFOV[cell]) {
            this.identify();
            Sample.INSTANCE.play("sounds/shatter.mp3");
            Sample.INSTANCE.play("sounds/gas.mp3");
        }
        GameScene.add(Blob.seed(cell, 1000, ToxicGas.class));
    }

    @Override
    public int value() {
        return this.isKnown() ? 30 * this.quantity : super.value();
    }
}

