/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.noosa.tweeners;

import com.watabou.noosa.Visual;
import com.watabou.noosa.tweeners.Tweener;
import com.watabou.utils.PointF;

public class PosTweener
extends Tweener {
    public Visual visual;
    public PointF start;
    public PointF end;

    public PosTweener(Visual visual, PointF pos, float time) {
        super(visual, time);
        this.visual = visual;
        this.start = visual.point();
        this.end = pos;
    }

    @Override
    protected void updateValues(float progress) {
        this.visual.point(PointF.inter(this.start, this.end, progress));
    }
}

