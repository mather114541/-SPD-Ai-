/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.noosa;

import com.watabou.glwrap.Matrix;
import com.watabou.noosa.Game;
import com.watabou.noosa.Gizmo;
import com.watabou.noosa.Visual;
import com.watabou.utils.Point;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class Camera
extends Gizmo {
    private static ArrayList<Camera> all = new ArrayList();
    protected static float invW2;
    protected static float invH2;
    public static Camera main;
    public boolean fullScreen;
    public float zoom;
    public int x;
    public int y;
    public int width;
    public int height;
    int screenWidth;
    int screenHeight;
    public float[] matrix;
    public PointF edgeScroll;
    public PointF scroll;
    public PointF centerOffset;
    private float shakeMagX = 10.0f;
    private float shakeMagY = 10.0f;
    private float shakeTime = 0.0f;
    private float shakeDuration = 1.0f;
    protected float shakeX;
    protected float shakeY;
    Visual followTarget = null;
    PointF panTarget = new PointF();
    float panIntensity = 0.0f;
    float followDeadzone = 0.0f;

    public static Camera reset() {
        return Camera.reset(Camera.createFullscreen(1.0f));
    }

    public static synchronized Camera reset(Camera newCamera) {
        invW2 = 2.0f / (float)Game.width;
        invH2 = 2.0f / (float)Game.height;
        int length = all.size();
        for (int i = 0; i < length; ++i) {
            all.get(i).destroy();
        }
        all.clear();
        main = Camera.add(newCamera);
        return main;
    }

    public static synchronized Camera add(Camera camera) {
        all.add(camera);
        return camera;
    }

    public static synchronized Camera remove(Camera camera) {
        all.remove(camera);
        return camera;
    }

    public static synchronized void updateAll() {
        int length = all.size();
        for (int i = 0; i < length; ++i) {
            Camera c = all.get(i);
            if (c == null || !c.exists || !c.active) continue;
            c.update();
        }
    }

    public static Camera createFullscreen(float zoom) {
        int w = (int)Math.ceil((float)Game.width / zoom);
        int h = (int)Math.ceil((float)Game.height / zoom);
        Camera c = new Camera((int)((float)Game.width - (float)w * zoom) / 2, (int)((float)Game.height - (float)h * zoom) / 2, w, h, zoom);
        c.fullScreen = true;
        return c;
    }

    public Camera(int x, int y, int width, int height, float zoom) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.zoom = zoom;
        this.screenWidth = (int)((float)width * zoom);
        this.screenHeight = (int)((float)height * zoom);
        this.edgeScroll = new PointF();
        this.scroll = new PointF();
        this.centerOffset = new PointF();
        this.matrix = new float[16];
        Matrix.setIdentity(this.matrix);
    }

    @Override
    public void destroy() {
        this.panIntensity = 0.0f;
    }

    public synchronized void zoom(float value) {
        this.zoom(value, this.scroll.x + (float)this.width / 2.0f, this.scroll.y + (float)this.height / 2.0f);
    }

    public synchronized void zoom(float value, float fx, float fy) {
        PointF offsetAdjust = this.centerOffset.clone();
        this.centerOffset.scale(this.zoom).invScale(value);
        this.zoom = value;
        this.width = (int)((float)this.screenWidth / this.zoom);
        this.height = (int)((float)this.screenHeight / this.zoom);
        this.snapTo(fx - offsetAdjust.x, fy - offsetAdjust.y);
    }

    public synchronized void resize(int width, int height) {
        this.width = width;
        this.height = height;
        this.screenWidth = (int)((float)width * this.zoom);
        this.screenHeight = (int)((float)height * this.zoom);
    }

    @Override
    public synchronized void update() {
        float f;
        super.update();
        float deadX = 0.0f;
        float deadY = 0.0f;
        if (this.followTarget != null && this.followTarget.visible) {
            this.panTarget.x = this.followTarget.x + this.followTarget.width() / 2.0f;
            this.panTarget.y = this.followTarget.y + this.followTarget.height() / 2.0f;
            this.panTarget.offset(this.centerOffset);
            deadX = (float)this.width * this.followDeadzone / 2.0f;
            deadY = (float)this.height * this.followDeadzone / 2.0f;
        }
        if (this.panIntensity > 0.0f) {
            float panX = this.panTarget.x - (this.scroll.x + (float)this.width / 2.0f);
            float panY = this.panTarget.y - (this.scroll.y + (float)this.height / 2.0f);
            panX = panX > deadX ? (panX -= deadX) : (panX < -deadX ? (panX += deadX) : 0.0f);
            panY = panY > deadY ? (panY -= deadY) : (panY < -deadY ? (panY += deadY) : 0.0f);
            this.scroll.offset(panX *= Math.min(1.0f, Game.elapsed * this.panIntensity), panY *= Math.min(1.0f, Game.elapsed * this.panIntensity));
        }
        this.shakeTime -= Game.elapsed;
        if (f > 0.0f) {
            float damping = this.shakeTime / this.shakeDuration;
            this.shakeX = Random.Float(-this.shakeMagX, this.shakeMagX) * damping;
            this.shakeY = Random.Float(-this.shakeMagY, this.shakeMagY) * damping;
        } else {
            this.shakeX = 0.0f;
            this.shakeY = 0.0f;
        }
        this.updateMatrix();
    }

    public PointF center() {
        return new PointF(this.width / 2, this.height / 2);
    }

    public boolean hitTest(float x, float y) {
        return x >= (float)this.x && y >= (float)this.y && x < (float)(this.x + this.screenWidth) && y < (float)(this.y + this.screenHeight);
    }

    public synchronized void shift(PointF point) {
        this.scroll.offset(point);
        this.panIntensity = 0.0f;
    }

    public synchronized void setCenterOffset(float x, float y) {
        this.scroll.x += x - this.centerOffset.x;
        this.scroll.y += y - this.centerOffset.y;
        if (this.panTarget != null) {
            this.panTarget.x += x - this.centerOffset.x;
            this.panTarget.y += y - this.centerOffset.y;
        }
        this.centerOffset.set(x, y);
    }

    public synchronized void snapTo(float x, float y) {
        this.scroll.set(x - (float)this.width / 2.0f, y - (float)this.height / 2.0f).offset(this.centerOffset);
        this.panIntensity = 0.0f;
        this.followTarget = null;
    }

    public void snapTo(PointF point) {
        this.snapTo(point.x, point.y);
    }

    public synchronized void panTo(PointF dst, float intensity) {
        this.panTarget = dst.offset(this.centerOffset);
        this.panIntensity = intensity;
        this.followTarget = null;
    }

    public synchronized void panFollow(Visual target, float intensity) {
        this.followTarget = target;
        this.panIntensity = intensity;
    }

    public synchronized Visual followTarget() {
        return this.followTarget;
    }

    public synchronized void setFollowDeadzone(float deadzone) {
        this.followDeadzone = deadzone;
    }

    public PointF screenToCamera(int x, int y) {
        return new PointF((float)(x - this.x) / this.zoom + this.scroll.x, (float)(y - this.y) / this.zoom + this.scroll.y);
    }

    public Point cameraToScreen(float x, float y) {
        return new Point((int)((x - this.scroll.x) * this.zoom + (float)this.x), (int)((y - this.scroll.y) * this.zoom + (float)this.y));
    }

    public float screenWidth() {
        return (float)this.width * this.zoom;
    }

    public float screenHeight() {
        return (float)this.height * this.zoom;
    }

    protected void updateMatrix() {
        this.matrix[0] = this.zoom * invW2;
        this.matrix[5] = -this.zoom * invH2;
        this.matrix[12] = -1.0f + (float)this.x * invW2 - (this.scroll.x + this.shakeX) * this.matrix[0];
        this.matrix[13] = 1.0f - (float)this.y * invH2 - (this.scroll.y + this.shakeY) * this.matrix[5];
    }

    public synchronized void shake(float magnitude, float duration) {
        this.shakeMagX = this.shakeMagY = magnitude;
        this.shakeTime = this.shakeDuration = duration;
    }
}

