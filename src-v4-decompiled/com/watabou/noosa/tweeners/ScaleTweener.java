/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.noosa.tweeners;

import com.watabou.noosa.Visual;
import com.watabou.noosa.tweeners.Tweener;
import com.watabou.utils.PointF;

public class ScaleTweener
extends Tweener {
    public Visual visual;
    public PointF start;
    public PointF end;

    public ScaleTweener(Visual visual, PointF scale, float time) {
        super(visual, time);
        this.visual = visual;
        this.start = visual.scale;
        this.end = scale;
    }

    @Override
    protected void updateValues(float progress) {
        this.visual.scale = PointF.inter(this.start, this.end, progress);
    }
}

