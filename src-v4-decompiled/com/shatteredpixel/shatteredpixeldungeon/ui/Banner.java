/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.watabou.noosa.Game;
import com.watabou.noosa.Image;

public class Banner
extends Image {
    private State state;
    private float time;
    private int color;
    private float fadeTime;
    private float showTime;

    public Banner(Image sample) {
        this.copy(sample);
        this.alpha(0.0f);
    }

    public Banner(Object tx) {
        super(tx);
        this.alpha(0.0f);
    }

    public void show(int color, float fadeTime, float showTime) {
        this.color = color;
        this.fadeTime = fadeTime;
        this.showTime = showTime;
        this.state = State.FADE_IN;
        this.time = fadeTime;
    }

    public void show(int color, float fadeTime) {
        this.show(color, fadeTime, Float.MAX_VALUE);
    }

    @Override
    public void update() {
        super.update();
        this.time -= Game.elapsed;
        if (this.time >= 0.0f) {
            float p = this.time / this.fadeTime;
            switch (this.state.ordinal()) {
                case 0: {
                    this.tint(this.color, p);
                    this.alpha(1.0f - p);
                    break;
                }
                case 1: {
                    this.resetColor();
                    break;
                }
                case 2: {
                    this.resetColor();
                    this.alpha(p);
                }
            }
        } else {
            switch (this.state.ordinal()) {
                case 0: {
                    this.time = this.showTime;
                    this.state = State.STATIC;
                    break;
                }
                case 1: {
                    this.time = this.fadeTime;
                    this.state = State.FADE_OUT;
                    break;
                }
                case 2: {
                    this.killAndErase();
                }
            }
        }
    }

    private static enum State {
        FADE_IN,
        STATIC,
        FADE_OUT;

    }
}

