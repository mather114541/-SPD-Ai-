/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.noosa.tweeners;

import com.watabou.noosa.Camera;
import com.watabou.noosa.tweeners.Tweener;
import com.watabou.utils.PointF;

public class CameraScrollTweener
extends Tweener {
    public Camera camera;
    public PointF start;
    public PointF end;

    public CameraScrollTweener(Camera camera, PointF pos, float time) {
        super(camera, time);
        this.camera = camera;
        this.start = camera.scroll;
        this.end = pos;
    }

    @Override
    protected void updateValues(float progress) {
        this.camera.scroll = PointF.inter(this.start, this.end, progress);
    }
}

