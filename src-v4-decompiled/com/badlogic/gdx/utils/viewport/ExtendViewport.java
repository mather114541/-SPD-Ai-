/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.utils.viewport;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.Viewport;

public class ExtendViewport
extends Viewport {
    private float minWorldWidth;
    private float minWorldHeight;
    private float maxWorldWidth;
    private float maxWorldHeight;
    private Scaling scaling = Scaling.fit;

    public ExtendViewport(float minWorldWidth, float minWorldHeight) {
        this(minWorldWidth, minWorldHeight, 0.0f, 0.0f, new OrthographicCamera());
    }

    public ExtendViewport(float minWorldWidth, float minWorldHeight, Camera camera) {
        this(minWorldWidth, minWorldHeight, 0.0f, 0.0f, camera);
    }

    public ExtendViewport(float minWorldWidth, float minWorldHeight, float maxWorldWidth, float maxWorldHeight) {
        this(minWorldWidth, minWorldHeight, maxWorldWidth, maxWorldHeight, new OrthographicCamera());
    }

    public ExtendViewport(float minWorldWidth, float minWorldHeight, float maxWorldWidth, float maxWorldHeight, Camera camera) {
        this.minWorldWidth = minWorldWidth;
        this.minWorldHeight = minWorldHeight;
        this.maxWorldWidth = maxWorldWidth;
        this.maxWorldHeight = maxWorldHeight;
        this.setCamera(camera);
    }

    @Override
    public void update(int screenWidth, int screenHeight, boolean centerCamera) {
        float lengthen;
        float toWorldSpace;
        float toViewportSpace;
        float worldWidth = this.minWorldWidth;
        float worldHeight = this.minWorldHeight;
        Vector2 scaled = this.scaling.apply(worldWidth, worldHeight, screenWidth, screenHeight);
        int viewportWidth = Math.round(scaled.x);
        int viewportHeight = Math.round(scaled.y);
        if (viewportWidth < screenWidth) {
            toViewportSpace = (float)viewportHeight / worldHeight;
            toWorldSpace = worldHeight / (float)viewportHeight;
            lengthen = (float)(screenWidth - viewportWidth) * toWorldSpace;
            if (this.maxWorldWidth > 0.0f) {
                lengthen = Math.min(lengthen, this.maxWorldWidth - this.minWorldWidth);
            }
            worldWidth += lengthen;
            viewportWidth += Math.round(lengthen * toViewportSpace);
        }
        if (viewportHeight < screenHeight) {
            toViewportSpace = (float)viewportWidth / worldWidth;
            toWorldSpace = worldWidth / (float)viewportWidth;
            lengthen = (float)(screenHeight - viewportHeight) * toWorldSpace;
            if (this.maxWorldHeight > 0.0f) {
                lengthen = Math.min(lengthen, this.maxWorldHeight - this.minWorldHeight);
            }
            worldHeight += lengthen;
            viewportHeight += Math.round(lengthen * toViewportSpace);
        }
        this.setWorldSize(worldWidth, worldHeight);
        this.setScreenBounds((screenWidth - viewportWidth) / 2, (screenHeight - viewportHeight) / 2, viewportWidth, viewportHeight);
        this.apply(centerCamera);
    }

    public float getMinWorldWidth() {
        return this.minWorldWidth;
    }

    public void setMinWorldWidth(float minWorldWidth) {
        this.minWorldWidth = minWorldWidth;
    }

    public float getMinWorldHeight() {
        return this.minWorldHeight;
    }

    public void setMinWorldHeight(float minWorldHeight) {
        this.minWorldHeight = minWorldHeight;
    }

    public float getMaxWorldWidth() {
        return this.maxWorldWidth;
    }

    public void setMaxWorldWidth(float maxWorldWidth) {
        this.maxWorldWidth = maxWorldWidth;
    }

    public float getMaxWorldHeight() {
        return this.maxWorldHeight;
    }

    public void setMaxWorldHeight(float maxWorldHeight) {
        this.maxWorldHeight = maxWorldHeight;
    }

    public void setScaling(Scaling scaling) {
        this.scaling = scaling;
    }
}

