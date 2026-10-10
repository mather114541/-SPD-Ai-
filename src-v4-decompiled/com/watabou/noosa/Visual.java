/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.noosa;

import com.watabou.glwrap.Matrix;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Gizmo;
import com.watabou.utils.Point;
import com.watabou.utils.PointF;

public class Visual
extends Gizmo {
    public float x;
    public float y;
    public float width;
    public float height;
    public PointF scale;
    public PointF origin;
    protected float[] matrix;
    public float rm;
    public float gm;
    public float bm;
    public float am;
    public float ra;
    public float ga;
    public float ba;
    public float aa;
    public PointF speed;
    public PointF acc;
    public float angle;
    public float angularSpeed;
    private float lastX;
    private float lastY;
    private float lastW;
    private float lastH;
    private float lastA;
    private PointF lastScale = new PointF();
    private PointF lastOrigin = new PointF();

    public Visual(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.scale = new PointF(1.0f, 1.0f);
        this.origin = new PointF();
        this.matrix = new float[16];
        this.resetColor();
        this.speed = new PointF();
        this.acc = new PointF();
    }

    @Override
    public void update() {
        this.updateMotion();
    }

    @Override
    public void draw() {
        if (this.lastX != this.x || this.lastY != this.y || this.lastW != this.width || this.lastH != this.height || this.lastA != this.angle || this.lastScale.x != this.scale.x || this.lastScale.y != this.scale.y || this.lastOrigin.x != this.origin.x || this.lastOrigin.y != this.origin.y) {
            this.lastX = this.x;
            this.lastY = this.y;
            this.lastW = this.width;
            this.lastH = this.height;
            this.lastA = this.angle;
            this.lastScale.x = this.scale.x;
            this.lastScale.y = this.scale.y;
            this.lastOrigin.x = this.origin.x;
            this.lastOrigin.y = this.origin.y;
            this.updateMatrix();
        }
    }

    protected void updateMatrix() {
        Matrix.setIdentity(this.matrix);
        Matrix.translate(this.matrix, this.x, this.y);
        if (this.origin.x != 0.0f || this.origin.y != 0.0f) {
            Matrix.translate(this.matrix, this.origin.x, this.origin.y);
        }
        if (this.angle != 0.0f) {
            Matrix.rotate(this.matrix, this.angle);
        }
        if (this.scale.x != 1.0f || this.scale.y != 1.0f) {
            Matrix.scale(this.matrix, this.scale.x, this.scale.y);
        }
        if (this.origin.x != 0.0f || this.origin.y != 0.0f) {
            Matrix.translate(this.matrix, -this.origin.x, -this.origin.y);
        }
    }

    public PointF point() {
        return new PointF(this.x, this.y);
    }

    public PointF point(PointF p) {
        this.x = p.x;
        this.y = p.y;
        return p;
    }

    public Point point(Point p) {
        this.x = p.x;
        this.y = p.y;
        return p;
    }

    public PointF center() {
        return new PointF(this.x + this.width() / 2.0f, this.y + this.height() / 2.0f);
    }

    public PointF center(PointF p) {
        this.x = p.x - this.width() / 2.0f;
        this.y = p.y - this.height() / 2.0f;
        return p;
    }

    public PointF center(Visual v) {
        return new PointF(this.x + (this.width() - v.width()) / 2.0f, this.y + (this.height() - v.height()) / 2.0f);
    }

    public void originToCenter() {
        this.origin.set(this.width / 2.0f, this.height / 2.0f);
    }

    public float width() {
        return this.width * this.scale.x;
    }

    public float height() {
        return this.height * this.scale.y;
    }

    protected void updateMotion() {
        if (this.acc.x != 0.0f) {
            this.speed.x += this.acc.x * Game.elapsed;
        }
        if (this.speed.x != 0.0f) {
            this.x += this.speed.x * Game.elapsed;
        }
        if (this.acc.y != 0.0f) {
            this.speed.y += this.acc.y * Game.elapsed;
        }
        if (this.speed.y != 0.0f) {
            this.y += this.speed.y * Game.elapsed;
        }
        if (this.angularSpeed != 0.0f) {
            this.angle += this.angularSpeed * Game.elapsed;
        }
    }

    public void alpha(float value) {
        this.am = value;
        this.aa = 0.0f;
    }

    public float alpha() {
        return this.am + this.aa;
    }

    public void invert() {
        this.bm = -1.0f;
        this.gm = -1.0f;
        this.rm = -1.0f;
        this.ba = 1.0f;
        this.ga = 1.0f;
        this.ra = 1.0f;
    }

    public void lightness(float value) {
        if (value < 0.5f) {
            this.gm = this.bm = value * 2.0f;
            this.rm = this.bm;
            this.ba = 0.0f;
            this.ga = 0.0f;
            this.ra = 0.0f;
        } else {
            this.gm = this.bm = 2.0f - value * 2.0f;
            this.rm = this.bm;
            this.ga = this.ba = value * 2.0f - 1.0f;
            this.ra = this.ba;
        }
    }

    public void brightness(float value) {
        this.gm = this.bm = value;
        this.rm = this.bm;
    }

    public void tint(float r, float g, float b, float strength) {
        this.gm = this.bm = 1.0f - strength;
        this.rm = this.bm;
        this.ra = r * strength;
        this.ga = g * strength;
        this.ba = b * strength;
    }

    public void tint(int color, float strength) {
        this.gm = this.bm = 1.0f - strength;
        this.rm = this.bm;
        this.ra = (float)(color >> 16 & 0xFF) / 255.0f * strength;
        this.ga = (float)(color >> 8 & 0xFF) / 255.0f * strength;
        this.ba = (float)(color & 0xFF) / 255.0f * strength;
    }

    public void tint(int color) {
        this.tint(color & 0xFFFFFF, (float)(color >> 24 & 0xFF) / 255.0f);
    }

    public void color(float r, float g, float b) {
        this.bm = 0.0f;
        this.gm = 0.0f;
        this.rm = 0.0f;
        this.ra = r;
        this.ga = g;
        this.ba = b;
    }

    public void color(int color) {
        this.color((float)(color >> 16 & 0xFF) / 255.0f, (float)(color >> 8 & 0xFF) / 255.0f, (float)(color & 0xFF) / 255.0f);
    }

    public void hardlight(float r, float g, float b) {
        this.ba = 0.0f;
        this.ga = 0.0f;
        this.ra = 0.0f;
        this.rm = r;
        this.gm = g;
        this.bm = b;
    }

    public void hardlight(int color) {
        this.hardlight((float)(color >> 16) / 255.0f, (float)(color >> 8 & 0xFF) / 255.0f, (float)(color & 0xFF) / 255.0f);
    }

    public void resetColor() {
        this.am = 1.0f;
        this.bm = 1.0f;
        this.gm = 1.0f;
        this.rm = 1.0f;
        this.aa = 0.0f;
        this.ba = 0.0f;
        this.ga = 0.0f;
        this.ra = 0.0f;
    }

    public boolean overlapsPoint(float x, float y) {
        return x >= this.x && x < this.x + this.width * this.scale.x && y >= this.y && y < this.y + this.height * this.scale.y;
    }

    public boolean overlapsScreenPoint(int x, int y) {
        Camera c = this.camera();
        if (c == null) {
            return false;
        }
        if (!c.hitTest(x, y)) {
            return false;
        }
        PointF p = c.screenToCamera(x, y);
        return this.overlapsPoint(p.x, p.y);
    }

    @Override
    public boolean isVisible() {
        Camera c = this.camera();
        if (c == null || !this.visible) {
            return false;
        }
        if (this.angle != 0.0f) {
            return true;
        }
        if (this.x > c.scroll.x + (float)c.width) {
            return false;
        }
        if (!(this.x >= c.scroll.x) && !(this.x + this.width() >= c.scroll.x)) {
            return false;
        }
        if (this.y > c.scroll.y + (float)c.height) {
            return false;
        }
        return this.y >= c.scroll.y || this.y + this.height() >= c.scroll.y;
    }
}

