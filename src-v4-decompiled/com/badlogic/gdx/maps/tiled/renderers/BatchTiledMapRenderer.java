/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.maps.tiled.renderers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapGroupLayer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapImageLayer;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;

public abstract class BatchTiledMapRenderer
implements TiledMapRenderer,
Disposable {
    protected static final int NUM_VERTICES = 20;
    protected TiledMap map;
    protected float unitScale;
    protected Batch batch;
    protected Rectangle viewBounds;
    protected Rectangle imageBounds = new Rectangle();
    protected Rectangle repeatedImageBounds = new Rectangle();
    protected boolean ownsBatch;
    protected float[] vertices = new float[20];

    public TiledMap getMap() {
        return this.map;
    }

    public void setMap(TiledMap map) {
        this.map = map;
    }

    public float getUnitScale() {
        return this.unitScale;
    }

    public Batch getBatch() {
        return this.batch;
    }

    public Rectangle getViewBounds() {
        return this.viewBounds;
    }

    public BatchTiledMapRenderer(TiledMap map) {
        this(map, 1.0f);
    }

    public BatchTiledMapRenderer(TiledMap map, float unitScale) {
        this.map = map;
        this.unitScale = unitScale;
        this.viewBounds = new Rectangle();
        this.batch = new SpriteBatch();
        this.ownsBatch = true;
    }

    public BatchTiledMapRenderer(TiledMap map, Batch batch) {
        this(map, 1.0f, batch);
    }

    public BatchTiledMapRenderer(TiledMap map, float unitScale, Batch batch) {
        this.map = map;
        this.unitScale = unitScale;
        this.viewBounds = new Rectangle();
        this.batch = batch;
        this.ownsBatch = false;
    }

    @Override
    public void setView(OrthographicCamera camera) {
        this.batch.setProjectionMatrix(camera.combined);
        float width = camera.viewportWidth * camera.zoom;
        float height = camera.viewportHeight * camera.zoom;
        float w = width * Math.abs(camera.up.y) + height * Math.abs(camera.up.x);
        float h = height * Math.abs(camera.up.y) + width * Math.abs(camera.up.x);
        this.viewBounds.set(camera.position.x - w / 2.0f, camera.position.y - h / 2.0f, w, h);
    }

    @Override
    public void setView(Matrix4 projection, float x, float y, float width, float height) {
        this.batch.setProjectionMatrix(projection);
        this.viewBounds.set(x, y, width, height);
    }

    @Override
    public void render() {
        this.beginRender();
        for (MapLayer layer : this.map.getLayers()) {
            this.renderMapLayer(layer);
        }
        this.endRender();
    }

    @Override
    public void render(int[] layers) {
        this.beginRender();
        for (int layerIdx : layers) {
            MapLayer layer = this.map.getLayers().get(layerIdx);
            this.renderMapLayer(layer);
        }
        this.endRender();
    }

    public void renderMapLayer(MapLayer layer) {
        if (!layer.isVisible()) {
            return;
        }
        if (layer instanceof MapGroupLayer) {
            MapLayers childLayers = ((MapGroupLayer)layer).getLayers();
            for (int i = 0; i < childLayers.size(); ++i) {
                MapLayer childLayer = childLayers.get(i);
                if (!childLayer.isVisible()) continue;
                this.renderMapLayer(childLayer);
            }
        } else if (layer instanceof TiledMapTileLayer) {
            this.renderTileLayer((TiledMapTileLayer)layer);
        } else if (layer instanceof TiledMapImageLayer) {
            this.renderImageLayer((TiledMapImageLayer)layer);
        } else {
            this.renderObjects(layer);
        }
    }

    @Override
    public void renderObjects(MapLayer layer) {
        for (MapObject object : layer.getObjects()) {
            this.renderObject(object);
        }
    }

    @Override
    public void renderObject(MapObject object) {
    }

    @Override
    public void renderImageLayer(TiledMapImageLayer layer) {
        Color batchColor = this.batch.getColor();
        float color = this.getImageLayerColor(layer, batchColor);
        float[] vertices = this.vertices;
        TextureRegion region = layer.getTextureRegion();
        if (region == null) {
            return;
        }
        float x = layer.getX();
        float y = layer.getY();
        float x1 = x * this.unitScale - this.viewBounds.x * (layer.getParallaxX() - 1.0f);
        float y1 = y * this.unitScale - this.viewBounds.y * (layer.getParallaxY() - 1.0f);
        float x2 = x1 + (float)region.getRegionWidth() * this.unitScale;
        float y2 = y1 + (float)region.getRegionHeight() * this.unitScale;
        this.imageBounds.set(x1, y1, x2 - x1, y2 - y1);
        if (!layer.isRepeatX() && !layer.isRepeatY()) {
            if (this.viewBounds.contains(this.imageBounds) || this.viewBounds.overlaps(this.imageBounds)) {
                float u1 = region.getU();
                float v1 = region.getV2();
                float u2 = region.getU2();
                float v2 = region.getV();
                vertices[0] = x1;
                vertices[1] = y1;
                vertices[2] = color;
                vertices[3] = u1;
                vertices[4] = v1;
                vertices[5] = x1;
                vertices[6] = y2;
                vertices[7] = color;
                vertices[8] = u1;
                vertices[9] = v2;
                vertices[10] = x2;
                vertices[11] = y2;
                vertices[12] = color;
                vertices[13] = u2;
                vertices[14] = v2;
                vertices[15] = x2;
                vertices[16] = y1;
                vertices[17] = color;
                vertices[18] = u2;
                vertices[19] = v1;
                this.batch.draw(region.getTexture(), vertices, 0, 20);
            }
        } else {
            int repeatX = layer.isRepeatX() ? (int)Math.ceil(this.viewBounds.width / this.imageBounds.width + 4.0f) : 0;
            int repeatY = layer.isRepeatY() ? (int)Math.ceil(this.viewBounds.height / this.imageBounds.height + 4.0f) : 0;
            float startX = this.viewBounds.x;
            float startY = this.viewBounds.y;
            startX -= startX % this.imageBounds.width;
            startY -= startY % this.imageBounds.height;
            for (int i = 0; i <= repeatX; ++i) {
                for (int j = 0; j <= repeatY; ++j) {
                    float rx1 = x1;
                    float ry1 = y1;
                    float rx2 = x2;
                    float ry2 = y2;
                    if (layer.isRepeatX()) {
                        rx1 = startX + (float)(i - 2) * this.imageBounds.width + x1 % this.imageBounds.width;
                        rx2 = rx1 + this.imageBounds.width;
                    }
                    if (layer.isRepeatY()) {
                        ry1 = startY + (float)(j - 2) * this.imageBounds.height + y1 % this.imageBounds.height;
                        ry2 = ry1 + this.imageBounds.height;
                    }
                    this.repeatedImageBounds.set(rx1, ry1, rx2 - rx1, ry2 - ry1);
                    if (!this.viewBounds.contains(this.repeatedImageBounds) && !this.viewBounds.overlaps(this.repeatedImageBounds)) continue;
                    float ru1 = region.getU();
                    float rv1 = region.getV2();
                    float ru2 = region.getU2();
                    float rv2 = region.getV();
                    vertices[0] = rx1;
                    vertices[1] = ry1;
                    vertices[2] = color;
                    vertices[3] = ru1;
                    vertices[4] = rv1;
                    vertices[5] = rx1;
                    vertices[6] = ry2;
                    vertices[7] = color;
                    vertices[8] = ru1;
                    vertices[9] = rv2;
                    vertices[10] = rx2;
                    vertices[11] = ry2;
                    vertices[12] = color;
                    vertices[13] = ru2;
                    vertices[14] = rv2;
                    vertices[15] = rx2;
                    vertices[16] = ry1;
                    vertices[17] = color;
                    vertices[18] = ru2;
                    vertices[19] = rv1;
                    this.batch.draw(region.getTexture(), vertices, 0, 20);
                }
            }
        }
    }

    protected float getImageLayerColor(TiledMapImageLayer layer, Color batchColor) {
        Color combinedTint = layer.getCombinedTintColor();
        boolean supportsTransparency = layer.supportsTransparency();
        float alphaMultiplier = supportsTransparency ? 1.0f : combinedTint.a;
        float opacityMultiplier = supportsTransparency ? combinedTint.a : 1.0f;
        return Color.toFloatBits(batchColor.r * (combinedTint.r * alphaMultiplier), batchColor.g * (combinedTint.g * alphaMultiplier), batchColor.b * (combinedTint.b * alphaMultiplier), batchColor.a * (layer.getOpacity() * opacityMultiplier));
    }

    protected float getTileLayerColor(TiledMapTileLayer layer, Color batchColor) {
        return Color.toFloatBits(batchColor.r * layer.getCombinedTintColor().r, batchColor.g * layer.getCombinedTintColor().g, batchColor.b * layer.getCombinedTintColor().b, batchColor.a * layer.getCombinedTintColor().a * layer.getOpacity());
    }

    protected void beginRender() {
        AnimatedTiledMapTile.updateAnimationBaseTime();
        this.batch.begin();
    }

    protected void endRender() {
        this.batch.end();
    }

    @Override
    public void dispose() {
        if (this.ownsBatch) {
            this.batch.dispose();
        }
    }
}

