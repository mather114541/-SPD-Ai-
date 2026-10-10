/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.bombs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;

public class Firebomb
extends Bomb {
    public Firebomb() {
        this.image = ItemSpriteSheet.FIRE_BOMB;
    }

    @Override
    protected int explosionRange() {
        return 2;
    }

    @Override
    public void explode(int cell) {
        super.explode(cell);
        PathFinder.buildDistanceMap(cell, BArray.not(Dungeon.level.solid, null), this.explosionRange());
        for (int i = 0; i < PathFinder.distance.length; ++i) {
            if (PathFinder.distance[i] >= Integer.MAX_VALUE) continue;
            if (Dungeon.level.pit[i]) {
                GameScene.add(Blob.seed(i, 2, Fire.class));
            } else {
                GameScene.add(Blob.seed(i, 10, Fire.class));
            }
            CellEmitter.get(i).burst(FlameParticle.FACTORY, 5);
        }
        Sample.INSTANCE.play("sounds/burning.mp3");
    }

    @Override
    public int value() {
        return this.quantity * 50;
    }
}

