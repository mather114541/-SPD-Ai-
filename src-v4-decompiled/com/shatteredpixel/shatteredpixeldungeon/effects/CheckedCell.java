/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;

public class CheckedCell
extends Image {
    private float alpha;
    private float delay;

    public CheckedCell() {
        super(TextureCache.createSolid(-11162881));
    }

    public void reset(int pos, int visSource) {
        this.revive();
        this.origin.set(0.5f);
        this.point(DungeonTilemap.tileToWorld(pos).offset(8.0f, 8.0f));
        this.alpha = 0.8f;
        this.scale.set(16.0f * this.alpha);
        this.delay = Dungeon.level.trueDistance(pos, visSource) - 1.0f;
        if (this.delay > 0.0f) {
            this.delay = (float)Math.pow(this.delay, 0.67f) / 10.0f;
            this.alpha(0.0f);
        }
    }

    @Override
    public void update() {
        float f;
        this.delay -= Game.elapsed;
        if (f > 0.0f) {
            this.alpha(0.0f);
        } else {
            float f2;
            this.alpha -= Game.elapsed;
            if (f2 > 0.0f) {
                this.alpha(this.alpha);
                this.scale.set(16.0f * this.alpha);
            } else {
                this.kill();
            }
        }
    }
}

