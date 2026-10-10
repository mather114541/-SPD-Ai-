/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.noosa.particles;

import com.watabou.glwrap.Blending;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Visual;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class Emitter
extends Group {
    protected boolean lightMode = false;
    public float x;
    public float y;
    public float width;
    public float height;
    protected Visual target;
    public boolean fillTarget = true;
    protected float interval;
    protected int quantity;
    public boolean on = false;
    private boolean started = false;
    public boolean autoKill = true;
    protected int count;
    protected float time;
    protected Factory factory;
    public static boolean freezeEmitters = false;

    public void pos(float x, float y) {
        this.pos(x, y, 0.0f, 0.0f);
    }

    public void pos(PointF p) {
        this.pos(p.x, p.y, 0.0f, 0.0f);
    }

    public void pos(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.target = null;
    }

    public void pos(Visual target) {
        this.target = target;
    }

    public void pos(Visual target, float x, float y, float width, float height) {
        this.pos(x, y, width, height);
        this.pos(target);
    }

    public void burst(Factory factory, int quantity) {
        this.start(factory, 0.0f, quantity);
    }

    public void pour(Factory factory, float interval) {
        this.start(factory, interval, 0);
    }

    public void start(Factory factory, float interval, int quantity) {
        this.startDelayed(factory, interval, quantity, Random.Float(interval));
    }

    public void startDelayed(Factory factory, float interval, int quantity, float delay) {
        this.factory = factory;
        this.lightMode = factory.lightMode();
        this.interval = interval;
        this.quantity = quantity;
        this.count = 0;
        this.time = interval - delay;
        this.on = true;
        this.started = true;
    }

    protected boolean isFrozen() {
        return Game.timeTotal > 1.0f && freezeEmitters;
    }

    @Override
    public void update() {
        if (this.isFrozen()) {
            return;
        }
        if (this.on) {
            this.time += Game.elapsed;
            while (this.time > this.interval) {
                this.time -= this.interval;
                this.emit(this.count++);
                if (this.quantity <= 0 || this.count < this.quantity) continue;
                this.on = false;
                break;
            }
        } else if (this.started && this.autoKill && this.countLiving() == 0) {
            this.kill();
        }
        super.update();
    }

    @Override
    public void revive() {
        this.started = false;
        this.visible = true;
        this.fillTarget = true;
        this.autoKill = true;
        super.revive();
    }

    protected void emit(int index) {
        if (this.target == null) {
            this.factory.emit(this, index, this.x + Random.Float(this.width), this.y + Random.Float(this.height));
        } else if (this.fillTarget) {
            this.factory.emit(this, index, this.target.x + Random.Float(this.target.width()), this.target.y + Random.Float(this.target.height()));
        } else {
            this.factory.emit(this, index, this.target.x + this.x + Random.Float(this.width), this.target.y + this.y + Random.Float(this.height));
        }
    }

    @Override
    public void draw() {
        if (this.lightMode) {
            Blending.setLightMode();
            super.draw();
            Blending.setNormalMode();
        } else {
            super.draw();
        }
    }

    public static abstract class Factory {
        public abstract void emit(Emitter var1, int var2, float var3, float var4);

        public boolean lightMode() {
            return false;
        }
    }
}

