/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.effects.Effects;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.glwrap.Blending;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;
import java.util.Arrays;
import java.util.List;

public class Lightning
extends Group {
    private static final float DURATION = 0.3f;
    private float life;
    private List<Arc> arcs;
    private Callback callback;
    private static final double A = 57.29577951308232;

    public Lightning(int from, int to, Callback callback) {
        this(Arrays.asList(new Arc(from, to)), callback);
    }

    public Lightning(PointF from, int to, Callback callback) {
        this(Arrays.asList(new Arc(from, to)), callback);
    }

    public Lightning(int from, PointF to, Callback callback) {
        this(Arrays.asList(new Arc(from, to)), callback);
    }

    public Lightning(PointF from, PointF to, Callback callback) {
        this(Arrays.asList(new Arc(from, to)), callback);
    }

    public Lightning(List<Arc> arcs, Callback callback) {
        this.arcs = arcs;
        for (Arc arc : this.arcs) {
            this.add(arc);
        }
        this.callback = callback;
        this.life = 0.3f;
    }

    @Override
    public void update() {
        float f;
        this.life -= Game.elapsed;
        if (f < 0.0f) {
            this.killAndErase();
            if (this.callback != null) {
                this.callback.call();
            }
        } else {
            float alpha = this.life / 0.3f;
            for (Arc arc : this.arcs) {
                arc.alpha(alpha);
            }
            super.update();
        }
    }

    @Override
    public void draw() {
        Blending.setLightMode();
        super.draw();
        Blending.setNormalMode();
    }

    public static class Arc
    extends Group {
        private Image arc1;
        private Image arc2;
        private PointF start;
        private PointF end;

        public Arc(int from, int to) {
            this(DungeonTilemap.tileCenterToWorld(from), DungeonTilemap.tileCenterToWorld(to));
        }

        public Arc(PointF from, int to) {
            this(from, DungeonTilemap.tileCenterToWorld(to));
        }

        public Arc(int from, PointF to) {
            this(DungeonTilemap.tileCenterToWorld(from), to);
        }

        public Arc(PointF from, PointF to) {
            this.start = from;
            this.end = to;
            this.arc1 = new Image(Effects.get(Effects.Type.LIGHTNING));
            this.arc1.x = this.start.x - this.arc1.origin.x;
            this.arc1.y = this.start.y - this.arc1.origin.y;
            this.arc1.origin.set(0.0f, this.arc1.height() / 2.0f);
            this.add(this.arc1);
            this.arc2 = new Image(Effects.get(Effects.Type.LIGHTNING));
            this.arc2.origin.set(0.0f, this.arc2.height() / 2.0f);
            this.add(this.arc2);
            this.update();
        }

        public void alpha(float alpha) {
            this.arc1.am = this.arc2.am = alpha;
        }

        @Override
        public void update() {
            float x2 = (this.start.x + this.end.x) / 2.0f + Random.Float(-4.0f, 4.0f);
            float y2 = (this.start.y + this.end.y) / 2.0f + Random.Float(-4.0f, 4.0f);
            float dx = x2 - this.start.x;
            float dy = y2 - this.start.y;
            this.arc1.angle = (float)(Math.atan2(dy, dx) * 57.29577951308232);
            this.arc1.scale.x = (float)Math.sqrt(dx * dx + dy * dy) / this.arc1.width;
            dx = this.end.x - x2;
            dy = this.end.y - y2;
            this.arc2.angle = (float)(Math.atan2(dy, dx) * 57.29577951308232);
            this.arc2.scale.x = (float)Math.sqrt(dx * dx + dy * dy) / this.arc2.width;
            this.arc2.x = x2 - this.arc2.origin.x;
            this.arc2.y = y2 - this.arc2.origin.x;
        }
    }
}

