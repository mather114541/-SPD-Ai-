/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.graphics.g3d.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntIntMap;

public class FirstPersonCameraController
extends InputAdapter {
    protected final Camera camera;
    protected final IntIntMap keys = new IntIntMap();
    public int strafeLeftKey = 29;
    public int strafeRightKey = 32;
    public int forwardKey = 51;
    public int backwardKey = 47;
    public int upKey = 45;
    public int downKey = 33;
    public boolean autoUpdate = true;
    protected float velocity = 5.0f;
    protected float degreesPerPixel = 0.5f;
    protected final Vector3 tmp = new Vector3();

    public FirstPersonCameraController(Camera camera) {
        this.camera = camera;
    }

    @Override
    public boolean keyDown(int keycode) {
        this.keys.put(keycode, keycode);
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        this.keys.remove(keycode, 0);
        return true;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    public void setDegreesPerPixel(float degreesPerPixel) {
        this.degreesPerPixel = degreesPerPixel;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        float deltaX = (float)(-Gdx.input.getDeltaX()) * this.degreesPerPixel;
        float deltaY = (float)(-Gdx.input.getDeltaY()) * this.degreesPerPixel;
        this.camera.direction.rotate(this.camera.up, deltaX);
        this.tmp.set(this.camera.direction).crs(this.camera.up).nor();
        this.camera.direction.rotate(this.tmp, deltaY);
        return true;
    }

    public void update() {
        this.update(Gdx.graphics.getDeltaTime());
    }

    public void update(float deltaTime) {
        if (this.keys.containsKey(this.forwardKey)) {
            this.tmp.set(this.camera.direction).nor().scl(deltaTime * this.velocity);
            this.camera.position.add(this.tmp);
        }
        if (this.keys.containsKey(this.backwardKey)) {
            this.tmp.set(this.camera.direction).nor().scl(-deltaTime * this.velocity);
            this.camera.position.add(this.tmp);
        }
        if (this.keys.containsKey(this.strafeLeftKey)) {
            this.tmp.set(this.camera.direction).crs(this.camera.up).nor().scl(-deltaTime * this.velocity);
            this.camera.position.add(this.tmp);
        }
        if (this.keys.containsKey(this.strafeRightKey)) {
            this.tmp.set(this.camera.direction).crs(this.camera.up).nor().scl(deltaTime * this.velocity);
            this.camera.position.add(this.tmp);
        }
        if (this.keys.containsKey(this.upKey)) {
            this.tmp.set(this.camera.up).nor().scl(deltaTime * this.velocity);
            this.camera.position.add(this.tmp);
        }
        if (this.keys.containsKey(this.downKey)) {
            this.tmp.set(this.camera.up).nor().scl(-deltaTime * this.velocity);
            this.camera.position.add(this.tmp);
        }
        if (this.autoUpdate) {
            this.camera.update(true);
        }
    }
}

