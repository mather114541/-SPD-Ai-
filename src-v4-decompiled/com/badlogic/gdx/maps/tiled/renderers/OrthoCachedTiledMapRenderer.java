/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.maps.tiled.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapImageLayer;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;

public class OrthoCachedTiledMapRenderer
implements TiledMapRenderer,
Disposable {
    private static final float tolerance = 1.0E-5f;
    protected static final int NUM_VERTICES = 20;
    protected final TiledMap map;
    protected final SpriteCache spriteCache;
    protected final float[] vertices = new float[20];
    protected boolean blending;
    protected float unitScale;
    protected final Rectangle viewBounds = new Rectangle();
    protected final Rectangle cacheBounds = new Rectangle();
    protected float overCache = 0.5f;
    protected float maxTileWidth;
    protected float maxTileHeight;
    protected boolean cached;
    protected int count;
    protected boolean canCacheMoreN;
    protected boolean canCacheMoreE;
    protected boolean canCacheMoreW;
    protected boolean canCacheMoreS;
    protected Rectangle imageBounds = new Rectangle();

    public OrthoCachedTiledMapRenderer(TiledMap map) {
        this(map, 1.0f, 2000);
    }

    public OrthoCachedTiledMapRenderer(TiledMap map, float unitScale) {
        this(map, unitScale, 2000);
    }

    public OrthoCachedTiledMapRenderer(TiledMap map, float unitScale, int cacheSize) {
        this.map = map;
        this.unitScale = unitScale;
        this.spriteCache = new SpriteCache(cacheSize, true);
    }

    @Override
    public void setView(OrthographicCamera camera) {
        this.spriteCache.setProjectionMatrix(camera.combined);
        float width = camera.viewportWidth * camera.zoom + this.maxTileWidth * 2.0f * this.unitScale;
        float height = camera.viewportHeight * camera.zoom + this.maxTileHeight * 2.0f * this.unitScale;
        this.viewBounds.set(camera.position.x - width / 2.0f, camera.position.y - height / 2.0f, width, height);
        if (this.canCacheMoreW && this.viewBounds.x < this.cacheBounds.x - 1.0E-5f || this.canCacheMoreS && this.viewBounds.y < this.cacheBounds.y - 1.0E-5f || this.canCacheMoreE && this.viewBounds.x + this.viewBounds.width > this.cacheBounds.x + this.cacheBounds.width + 1.0E-5f || this.canCacheMoreN && this.viewBounds.y + this.viewBounds.height > this.cacheBounds.y + this.cacheBounds.height + 1.0E-5f) {
            this.cached = false;
        }
    }

    @Override
    public void setView(Matrix4 projection, float x, float y, float width, float height) {
        this.spriteCache.setProjectionMatrix(projection);
        this.viewBounds.set(x -= this.maxTileWidth * this.unitScale, y -= this.maxTileHeight * this.unitScale, width += this.maxTileWidth * 2.0f * this.unitScale, height += this.maxTileHeight * 2.0f * this.unitScale);
        if (this.canCacheMoreW && this.viewBounds.x < this.cacheBounds.x - 1.0E-5f || this.canCacheMoreS && this.viewBounds.y < this.cacheBounds.y - 1.0E-5f || this.canCacheMoreE && this.viewBounds.x + this.viewBounds.width > this.cacheBounds.x + this.cacheBounds.width + 1.0E-5f || this.canCacheMoreN && this.viewBounds.y + this.viewBounds.height > this.cacheBounds.y + this.cacheBounds.height + 1.0E-5f) {
            this.cached = false;
        }
    }

    @Override
    public void render() {
        if (!this.cached) {
            this.cached = true;
            this.count = 0;
            this.spriteCache.clear();
            float extraWidth = this.viewBounds.width * this.overCache;
            float extraHeight = this.viewBounds.height * this.overCache;
            this.cacheBounds.x = this.viewBounds.x - extraWidth;
            this.cacheBounds.y = this.viewBounds.y - extraHeight;
            this.cacheBounds.width = this.viewBounds.width + extraWidth * 2.0f;
            this.cacheBounds.height = this.viewBounds.height + extraHeight * 2.0f;
            for (MapLayer layer : this.map.getLayers()) {
                this.spriteCache.beginCache();
                if (layer instanceof TiledMapTileLayer) {
                    this.renderTileLayer((TiledMapTileLayer)layer);
                } else if (layer instanceof TiledMapImageLayer) {
                    this.renderImageLayer((TiledMapImageLayer)layer);
                }
                this.spriteCache.endCache();
            }
        }
        if (this.blending) {
            Gdx.gl.glEnable(3042);
            Gdx.gl.glBlendFunc(770, 771);
        }
        this.spriteCache.begin();
        MapLayers mapLayers = this.map.getLayers();
        int j = mapLayers.getCount();
        for (int i = 0; i < j; ++i) {
            MapLayer layer;
            layer = mapLayers.get(i);
            if (!layer.isVisible()) continue;
            this.spriteCache.draw(i);
            this.renderObjects(layer);
        }
        this.spriteCache.end();
        if (this.blending) {
            Gdx.gl.glDisable(3042);
        }
    }

    @Override
    public void render(int[] layers) {
        if (!this.cached) {
            this.cached = true;
            this.count = 0;
            this.spriteCache.clear();
            float extraWidth = this.viewBounds.width * this.overCache;
            float extraHeight = this.viewBounds.height * this.overCache;
            this.cacheBounds.x = this.viewBounds.x - extraWidth;
            this.cacheBounds.y = this.viewBounds.y - extraHeight;
            this.cacheBounds.width = this.viewBounds.width + extraWidth * 2.0f;
            this.cacheBounds.height = this.viewBounds.height + extraHeight * 2.0f;
            for (MapLayer layer : this.map.getLayers()) {
                this.spriteCache.beginCache();
                if (layer instanceof TiledMapTileLayer) {
                    this.renderTileLayer((TiledMapTileLayer)layer);
                } else if (layer instanceof TiledMapImageLayer) {
                    this.renderImageLayer((TiledMapImageLayer)layer);
                }
                this.spriteCache.endCache();
            }
        }
        if (this.blending) {
            Gdx.gl.glEnable(3042);
            Gdx.gl.glBlendFunc(770, 771);
        }
        this.spriteCache.begin();
        MapLayers mapLayers = this.map.getLayers();
        for (int i : layers) {
            MapLayer layer = mapLayers.get(i);
            if (!layer.isVisible()) continue;
            this.spriteCache.draw(i);
            this.renderObjects(layer);
        }
        this.spriteCache.end();
        if (this.blending) {
            Gdx.gl.glDisable(3042);
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
    public void renderTileLayer(TiledMapTileLayer layer) {
        float color = Color.toFloatBits(layer.getCombinedTintColor().r, layer.getCombinedTintColor().g, layer.getCombinedTintColor().b, layer.getOpacity() * layer.getCombinedTintColor().a);
        int layerWidth = layer.getWidth();
        int layerHeight = layer.getHeight();
        float layerTileWidth = (float)layer.getTileWidth() * this.unitScale;
        float layerTileHeight = (float)layer.getTileHeight() * this.unitScale;
        float layerOffsetX = layer.getRenderOffsetX() * this.unitScale - this.viewBounds.x * (layer.getParallaxX() - 1.0f);
        float layerOffsetY = -layer.getRenderOffsetY() * this.unitScale - this.viewBounds.y * (layer.getParallaxY() - 1.0f);
        int col1 = Math.max(0, (int)((this.cacheBounds.x - layerOffsetX) / layerTileWidth));
        int col2 = Math.min(layerWidth, (int)((this.cacheBounds.x + this.cacheBounds.width + layerTileWidth - layerOffsetX) / layerTileWidth));
        int row1 = Math.max(0, (int)((this.cacheBounds.y - layerOffsetY) / layerTileHeight));
        int row2 = Math.min(layerHeight, (int)((this.cacheBounds.y + this.cacheBounds.height + layerTileHeight - layerOffsetY) / layerTileHeight));
        this.canCacheMoreN = row2 < layerHeight;
        this.canCacheMoreE = col2 < layerWidth;
        this.canCacheMoreW = col1 > 0;
        this.canCacheMoreS = row1 > 0;
        float[] vertices = this.vertices;
        for (int row = row2; row >= row1; --row) {
            for (int col = col1; col < col2; ++col) {
                float temp;
                TiledMapTile tile;
                TiledMapTileLayer.Cell cell = layer.getCell(col, row);
                if (cell == null || (tile = cell.getTile()) == null) continue;
                ++this.count;
                boolean flipX = cell.getFlipHorizontally();
                boolean flipY = cell.getFlipVertically();
                int rotations = cell.getRotation();
                TextureRegion region = tile.getTextureRegion();
                Texture texture = region.getTexture();
                float x1 = (float)col * layerTileWidth + tile.getOffsetX() * this.unitScale + layerOffsetX;
                float y1 = (float)row * layerTileHeight + tile.getOffsetY() * this.unitScale + layerOffsetY;
                float x2 = x1 + (float)region.getRegionWidth() * this.unitScale;
                float y2 = y1 + (float)region.getRegionHeight() * this.unitScale;
                float adjustX = 0.5f / (float)texture.getWidth();
                float adjustY = 0.5f / (float)texture.getHeight();
                float u1 = region.getU() + adjustX;
                float v1 = region.getV2() - adjustY;
                float u2 = region.getU2() - adjustX;
                float v2 = region.getV() + adjustY;
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
                if (flipX) {
                    temp = vertices[3];
                    vertices[3] = vertices[13];
                    vertices[13] = temp;
                    temp = vertices[8];
                    vertices[8] = vertices[18];
                    vertices[18] = temp;
                }
                if (flipY) {
                    temp = vertices[4];
                    vertices[4] = vertices[14];
                    vertices[14] = temp;
                    temp = vertices[9];
                    vertices[9] = vertices[19];
                    vertices[19] = temp;
                }
                if (rotations != 0) {
                    switch (rotations) {
                        case 1: {
                            float tempV = vertices[4];
                            vertices[4] = vertices[9];
                            vertices[9] = vertices[14];
                            vertices[14] = vertices[19];
                            vertices[19] = tempV;
                            float tempU = vertices[3];
                            vertices[3] = vertices[8];
                            vertices[8] = vertices[13];
                            vertices[13] = vertices[18];
                            vertices[18] = tempU;
                            break;
                        }
                        case 2: {
                            float tempU = vertices[3];
                            vertices[3] = vertices[13];
                            vertices[13] = tempU;
                            tempU = vertices[8];
                            vertices[8] = vertices[18];
                            vertices[18] = tempU;
                            float tempV = vertices[4];
                            vertices[4] = vertices[14];
                            vertices[14] = tempV;
                            tempV = vertices[9];
                            vertices[9] = vertices[19];
                            vertices[19] = tempV;
                            break;
                        }
                        case 3: {
                            float tempV = vertices[4];
                            vertices[4] = vertices[19];
                            vertices[19] = vertices[14];
                            vertices[14] = vertices[9];
                            vertices[9] = tempV;
                            float tempU = vertices[3];
                            vertices[3] = vertices[18];
                            vertices[18] = vertices[13];
                            vertices[13] = vertices[8];
                            vertices[8] = tempU;
                            break;
                        }
                    }
                }
                this.spriteCache.add(texture, vertices, 0, 20);
            }
        }
    }

    @Override
    public void renderImageLayer(TiledMapImageLayer layer) {
        Color combinedTint = layer.getCombinedTintColor();
        boolean supportsTransparency = layer.supportsTransparency();
        float alphaMultiplier = supportsTransparency ? 1.0f : combinedTint.a;
        float opacityMultiplier = supportsTransparency ? combinedTint.a : 1.0f;
        float color = Color.toFloatBits(combinedTint.r * alphaMultiplier, combinedTint.g * alphaMultiplier, combinedTint.b * alphaMultiplier, layer.getOpacity() * opacityMultiplier);
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
            this.spriteCache.add(region.getTexture(), vertices, 0, 20);
        } else {
            int repeatX = layer.isRepeatX() ? (int)Math.ceil(this.cacheBounds.width / this.imageBounds.width + 4.0f) : 0;
            int repeatY = layer.isRepeatY() ? (int)Math.ceil(this.cacheBounds.height / this.imageBounds.height + 4.0f) : 0;
            float startX = this.cacheBounds.x;
            float startY = this.cacheBounds.y;
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
                    this.spriteCache.add(region.getTexture(), vertices, 0, 20);
                }
            }
        }
    }

    public void invalidateCache() {
        this.cached = false;
    }

    public boolean isCached() {
        return this.cached;
    }

    public void setOverCache(float overCache) {
        this.overCache = overCache;
    }

    public void setMaxTileSize(float maxPixelWidth, float maxPixelHeight) {
        this.maxTileWidth = maxPixelWidth;
        this.maxTileHeight = maxPixelHeight;
    }

    public void setBlending(boolean blending) {
        this.blending = blending;
    }

    public SpriteCache getSpriteCache() {
        return this.spriteCache;
    }

    @Override
    public void dispose() {
        this.spriteCache.dispose();
    }
}

