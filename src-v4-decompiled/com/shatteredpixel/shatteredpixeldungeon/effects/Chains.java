/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.effects.Effects;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;

public class Chains
extends Group {
    private static final double A = 57.29577951308232;
    private float spent = 0.0f;
    private float duration;
    private Callback callback;
    private Image[] chains;
    private int numChains;
    private float distance;
    private float rotation = 0.0f;
    private PointF from;
    private PointF to;

    public Chains(int from, int to, Effects.Type type, Callback callback) {
        this(DungeonTilemap.tileCenterToWorld(from), DungeonTilemap.tileCenterToWorld(to), type, callback);
    }

    public Chains(PointF from, PointF to, Effects.Type type, Callback callback) {
        this.callback = callback;
        this.from = from;
        this.to = to;
        float dx = to.x - from.x;
        float dy = to.y - from.y;
        this.distance = (float)Math.hypot(dx, dy);
        this.duration = this.distance / 320.0f + 0.2f;
        this.rotation = (float)(Math.atan2(dy, dx) * 57.29577951308232) + 90.0f;
        this.numChains = Math.round(this.distance / 6.0f) + 1;
        this.chains = new Image[this.numChains];
        for (int i = 0; i < this.chains.length; ++i) {
            this.chains[i] = new Image(Effects.get(type));
            this.chains[i].angle = this.rotation;
            this.chains[i].origin.set(this.chains[i].width() / 2.0f, this.chains[i].height());
            this.add(this.chains[i]);
        }
    }

    @Override
    public void update() {
        float f;
        this.spent += Game.elapsed;
        if (f > this.duration) {
            this.killAndErase();
            if (this.callback != null) {
                this.callback.call();
            }
        } else {
            float dx = this.to.x - this.from.x;
            float dy = this.to.y - this.from.y;
            for (int i = 0; i < this.chains.length; ++i) {
                this.chains[i].center(new PointF(this.from.x + dx * ((float)i / (float)this.chains.length) * (this.spent / this.duration), this.from.y + dy * ((float)i / (float)this.chains.length) * (this.spent / this.duration)));
            }
        }
    }
}

