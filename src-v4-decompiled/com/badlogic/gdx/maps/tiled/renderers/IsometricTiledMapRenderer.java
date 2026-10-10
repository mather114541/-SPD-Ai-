/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.maps.tiled.renderers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapImageLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.BatchTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class IsometricTiledMapRenderer
extends BatchTiledMapRenderer {
    private Matrix4 isoTransform;
    private Matrix4 invIsotransform;
    private Vector3 screenPos = new Vector3();
    private Vector2 topRight = new Vector2();
    private Vector2 bottomLeft = new Vector2();
    private Vector2 topLeft = new Vector2();
    private Vector2 bottomRight = new Vector2();

    public IsometricTiledMapRenderer(TiledMap map) {
        super(map);
        this.init();
    }

    public IsometricTiledMapRenderer(TiledMap map, Batch batch) {
        super(map, batch);
        this.init();
    }

    public IsometricTiledMapRenderer(TiledMap map, float unitScale) {
        super(map, unitScale);
        this.init();
    }

    public IsometricTiledMapRenderer(TiledMap map, float unitScale, Batch batch) {
        super(map, unitScale, batch);
        this.init();
    }

    private void init() {
        this.isoTransform = new Matrix4();
        this.isoTransform.idt();
        this.isoTransform.scale((float)(Math.sqrt(2.0) / 2.0), (float)(Math.sqrt(2.0) / 4.0), 1.0f);
        this.isoTransform.rotate(0.0f, 0.0f, 1.0f, -45.0f);
        this.invIsotransform = new Matrix4(this.isoTransform);
        this.invIsotransform.inv();
    }

    private Vector3 translateScreenToIso(Vector2 vec) {
        this.screenPos.set(vec.x, vec.y, 0.0f);
        this.screenPos.mul(this.invIsotransform);
        return this.screenPos;
    }

    @Override
    public void renderTileLayer(TiledMapTileLayer layer) {
        Color batchColor = this.batch.getColor();
        float color = this.getTileLayerColor(layer, batchColor);
        float tileWidth = (float)layer.getTileWidth() * this.unitScale;
        float tileHeight = (float)layer.getTileHeight() * this.unitScale;
        float layerOffsetX = layer.getRenderOffsetX() * this.unitScale - this.viewBounds.x * (layer.getParallaxX() - 1.0f);
        float layerOffsetY = -layer.getRenderOffsetY() * this.unitScale - this.viewBounds.y * (layer.getParallaxY() - 1.0f);
        float halfTileWidth = tileWidth * 0.5f;
        float halfTileHeight = tileHeight * 0.5f;
        this.topRight.set(this.viewBounds.x + this.viewBounds.width - layerOffsetX, this.viewBounds.y - layerOffsetY);
        this.bottomLeft.set(this.viewBounds.x - layerOffsetX, this.viewBounds.y + this.viewBounds.height - layerOffsetY);
        this.topLeft.set(this.viewBounds.x - layerOffsetX, this.viewBounds.y - layerOffsetY);
        this.bottomRight.set(this.viewBounds.x + this.viewBounds.width - layerOffsetX, this.viewBounds.y + this.viewBounds.height - layerOffsetY);
        int row1 = (int)(this.translateScreenToIso((Vector2)this.topLeft).y / tileWidth) - 2;
        int row2 = (int)(this.translateScreenToIso((Vector2)this.bottomRight).y / tileWidth) + 2;
        int col1 = (int)(this.translateScreenToIso((Vector2)this.bottomLeft).x / tileWidth) - 2;
        int col2 = (int)(this.translateScreenToIso((Vector2)this.topRight).x / tileWidth) + 2;
        for (int row = row2; row >= row1; --row) {
            for (int col = col1; col <= col2; ++col) {
                float temp;
                TiledMapTile tile;
                float x = (float)col * halfTileWidth + (float)row * halfTileWidth;
                float y = (float)row * halfTileHeight - (float)col * halfTileHeight;
                TiledMapTileLayer.Cell cell = layer.getCell(col, row);
                if (cell == null || (tile = cell.getTile()) == null) continue;
                boolean flipX = cell.getFlipHorizontally();
                boolean flipY = cell.getFlipVertically();
                int rotations = cell.getRotation();
                TextureRegion region = tile.getTextureRegion();
                float x1 = x + tile.getOffsetX() * this.unitScale + layerOffsetX;
                float y1 = y + tile.getOffsetY() * this.unitScale + layerOffsetY;
                float x2 = x1 + (float)region.getRegionWidth() * this.unitScale;
                float y2 = y1 + (float)region.getRegionHeight() * this.unitScale;
                float u1 = region.getU();
                float v1 = region.getV2();
                float u2 = region.getU2();
                float v2 = region.getV();
                this.vertices[0] = x1;
                this.vertices[1] = y1;
                this.vertices[2] = color;
                this.vertices[3] = u1;
                this.vertices[4] = v1;
                this.vertices[5] = x1;
                this.vertices[6] = y2;
                this.vertices[7] = color;
                this.vertices[8] = u1;
                this.vertices[9] = v2;
                this.vertices[10] = x2;
                this.vertices[11] = y2;
                this.vertices[12] = color;
                this.vertices[13] = u2;
                this.vertices[14] = v2;
                this.vertices[15] = x2;
                this.vertices[16] = y1;
                this.vertices[17] = color;
                this.vertices[18] = u2;
                this.vertices[19] = v1;
                if (flipX) {
                    temp = this.vertices[3];
                    this.vertices[3] = this.vertices[13];
                    this.vertices[13] = temp;
                    temp = this.vertices[8];
                    this.vertices[8] = this.vertices[18];
                    this.vertices[18] = temp;
                }
                if (flipY) {
                    temp = this.vertices[4];
                    this.vertices[4] = this.vertices[14];
                    this.vertices[14] = temp;
                    temp = this.vertices[9];
                    this.vertices[9] = this.vertices[19];
                    this.vertices[19] = temp;
                }
                if (rotations != 0) {
                    switch (rotations) {
                        case 1: {
                            float tempV = this.vertices[4];
                            this.vertices[4] = this.vertices[9];
                            this.vertices[9] = this.vertices[14];
                            this.vertices[14] = this.vertices[19];
                            this.vertices[19] = tempV;
                            float tempU = this.vertices[3];
                            this.vertices[3] = this.vertices[8];
                            this.vertices[8] = this.vertices[13];
                            this.vertices[13] = this.vertices[18];
                            this.vertices[18] = tempU;
                            break;
                        }
                        case 2: {
                            float tempU = this.vertices[3];
                            this.vertices[3] = this.vertices[13];
                            this.vertices[13] = tempU;
                            tempU = this.vertices[8];
                            this.vertices[8] = this.vertices[18];
                            this.vertices[18] = tempU;
                            float tempV = this.vertices[4];
                            this.vertices[4] = this.vertices[14];
                            this.vertices[14] = tempV;
                            tempV = this.vertices[9];
                            this.vertices[9] = this.vertices[19];
                            this.vertices[19] = tempV;
                            break;
                        }
                        case 3: {
                            float tempV = this.vertices[4];
                            this.vertices[4] = this.vertices[19];
                            this.vertices[19] = this.vertices[14];
                            this.vertices[14] = this.vertices[9];
                            this.vertices[9] = tempV;
                            float tempU = this.vertices[3];
                            this.vertices[3] = this.vertices[18];
                            this.vertices[18] = this.vertices[13];
                            this.vertices[13] = this.vertices[8];
                            this.vertices[8] = tempU;
                            break;
                        }
                    }
                }
                this.batch.draw(region.getTexture(), this.vertices, 0, 20);
            }
        }
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
        int tileHeight = this.getMap().getProperties().get("tileheight", Integer.class);
        int mapHeight = this.getMap().getProperties().get("height", Integer.class);
        float mapHeightPixels = (float)(mapHeight * tileHeight) * this.unitScale;
        float halfTileHeight = (float)tileHeight * 0.5f * this.unitScale;
        float x = layer.getX();
        float y = layer.getY();
        float x1 = x * this.unitScale - this.viewBounds.x * (layer.getParallaxX() - 1.0f);
        float y1 = y * this.unitScale - this.viewBounds.y * (layer.getParallaxY() - 1.0f) - mapHeightPixels * 0.5f + halfTileHeight;
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
}

