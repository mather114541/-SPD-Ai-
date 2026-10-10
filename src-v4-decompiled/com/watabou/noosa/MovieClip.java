/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.noosa;

import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.RectF;

public class MovieClip
extends Image {
    protected Animation curAnim;
    protected int curFrame;
    protected float frameTimer;
    protected boolean finished;
    public boolean paused = false;
    public Listener listener;

    public MovieClip() {
    }

    public MovieClip(Object tx) {
        super(tx);
    }

    @Override
    public void update() {
        super.update();
        if (!this.paused) {
            this.updateAnimation();
        }
    }

    public boolean looping() {
        return this.curAnim != null && this.curAnim.looped;
    }

    protected synchronized void updateAnimation() {
        if (this.curAnim != null && this.curAnim.delay > 0.0f && (this.curAnim.looped || !this.finished)) {
            int lastFrame = this.curFrame;
            this.frameTimer += Game.elapsed;
            while (this.frameTimer > this.curAnim.delay) {
                this.frameTimer -= this.curAnim.delay;
                if (this.curFrame >= this.curAnim.frames.length - 1) {
                    if (this.curAnim.looped) {
                        this.curFrame = 0;
                    } else {
                        this.curFrame = this.curAnim.frames.length - 1;
                        this.frameTimer = 0.0f;
                    }
                    this.finished = true;
                    if (this.listener == null) continue;
                    this.listener.onComplete(this.curAnim);
                    if (this.curAnim != null) continue;
                    return;
                }
                ++this.curFrame;
            }
            if (this.curFrame != lastFrame) {
                this.frame(this.curAnim.frames[this.curFrame]);
            }
        }
    }

    public void play(Animation anim) {
        this.play(anim, false);
    }

    public synchronized void play(Animation anim, boolean force) {
        if (!(force || this.curAnim == null || this.curAnim != anim || !this.curAnim.looped && this.finished)) {
            return;
        }
        this.curAnim = anim;
        this.curFrame = 0;
        this.finished = false;
        this.frameTimer = 0.0f;
        if (anim != null) {
            this.frame(anim.frames[this.curFrame]);
        }
    }

    public static class Animation {
        public float delay;
        public RectF[] frames;
        public boolean looped;

        public Animation(int fps, boolean looped) {
            this.delay = 1.0f / (float)fps;
            this.looped = looped;
        }

        public Animation frames(RectF ... frames) {
            this.frames = frames;
            return this;
        }

        public Animation frames(TextureFilm film, Object ... frames) {
            this.frames = new RectF[frames.length];
            for (int i = 0; i < frames.length; ++i) {
                this.frames[i] = film.get(frames[i]);
            }
            return this;
        }

        public Animation clone() {
            return new Animation(Math.round(1.0f / this.delay), this.looped).frames(this.frames);
        }
    }

    public static interface Listener {
        public void onComplete(Animation var1);
    }
}

