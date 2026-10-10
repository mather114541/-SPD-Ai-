/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Random;
import com.watabou.utils.RectF;

public class BlobEmitter
extends Emitter {
    private Blob blob;
    public RectF bound = new RectF(0.0f, 0.0f, 1.0f, 1.0f);

    public BlobEmitter(Blob blob) {
        this.blob = blob;
        // headless: no render loop, so don't hand the blob back to the emitter
        // (that path would leave blob.emitter pointing at an unrendered emitter and,
        // more importantly, subclass fields like SacrificialFire.curEmitter set only here)
        if (!rl.RLMode.headless) blob.use(this);
    }

    @Override
    protected void emit(int index) {
        if (this.blob.volume <= 0) {
            return;
        }
        if (this.blob.area.isEmpty()) {
            this.blob.setupArea();
        }
        int[] map = this.blob.cur;
        float size = 16.0f;
        for (int i = this.blob.area.left; i < this.blob.area.right; ++i) {
            for (int j = this.blob.area.top; j < this.blob.area.bottom; ++j) {
                boolean visible;
                int cell = i + j * Dungeon.level.width();
                boolean bl = visible = cell < Dungeon.level.heroFOV.length && Dungeon.level.heroFOV[cell];
                if (this.blob.alwaysVisible && cell < Dungeon.level.length()) {
                    boolean bl2 = visible = visible || Dungeon.level.mapped[cell] || Dungeon.level.visited[cell];
                }
                if (!visible || map[cell] <= 0) continue;
                float x = ((float)i + Random.Float(this.bound.left, this.bound.right)) * size;
                float y = ((float)j + Random.Float(this.bound.top, this.bound.bottom)) * size;
                this.factory.emit(this, index, x, y);
            }
        }
    }
}

