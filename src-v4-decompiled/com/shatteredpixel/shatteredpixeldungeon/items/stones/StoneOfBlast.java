/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.stones;

import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.Runestone;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class StoneOfBlast
extends Runestone {
    public StoneOfBlast() {
        this.image = ItemSpriteSheet.STONE_BLAST;
    }

    @Override
    protected void activate(int cell) {
        new Bomb.ConjuredBomb().explode(cell);
    }
}

