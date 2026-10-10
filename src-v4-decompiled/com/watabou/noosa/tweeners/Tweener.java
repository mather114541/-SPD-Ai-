/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.noosa.tweeners;

import com.watabou.noosa.Game;
import com.watabou.noosa.Gizmo;

public abstract class Tweener
extends Gizmo {
    public Gizmo target;
    public float interval;
    public float elapsed;
    public Listener listener;

    public Tweener(Gizmo target, float interval) {
        this.target = target;
        this.interval = interval;
        this.elapsed = 0.0f;
    }

    @Override
    public void update() {
        if (this.elapsed < 0.0f) {
            this.onComplete();
            this.kill();
            return;
        }
        this.elapsed += Game.elapsed;
        if (this.interval - this.elapsed < Game.elapsed / 2.0f) {
            this.elapsed = this.interval;
        }
        if (this.elapsed >= this.interval) {
            this.updateValues(1.0f);
            this.onComplete();
            this.kill();
        } else {
            this.updateValues(this.elapsed / this.interval);
        }
    }

    public void stop(boolean complete) {
        this.elapsed = complete ? this.interval : -1.0f;
    }

    protected void onComplete() {
        if (this.listener != null) {
            this.listener.onComplete(this);
        }
    }

    protected abstract void updateValues(float var1);

    public static interface Listener {
        public void onComplete(Tweener var1);
    }
}

