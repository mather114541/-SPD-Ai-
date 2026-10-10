/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.noosa.tweeners;

import com.watabou.noosa.Visual;
import com.watabou.noosa.tweeners.Tweener;

public class AlphaTweener
extends Tweener {
    public Visual image;
    public float start;
    public float delta;

    public AlphaTweener(Visual image, float alpha, float time) {
        super(image, time);
        this.image = image;
        this.start = image.alpha();
        this.delta = alpha - this.start;
    }

    @Override
    protected void updateValues(float progress) {
        this.image.alpha(this.start + this.delta * progress);
    }
}

