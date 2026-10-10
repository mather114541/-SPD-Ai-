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
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;

public class HexagonalTiledMapRenderer
extends BatchTiledMapRenderer {
    private boolean staggerAxisX = true;
    private boolean staggerIndexEven = false;
    private float hexSideLength = 0.0f;

    public HexagonalTiledMapRenderer(TiledMap map) {
        super(map);
        this.init(map);
    }

    public HexagonalTiledMapRenderer(TiledMap map, float unitScale) {
        super(map, unitScale);
        this.init(map);
    }

    public HexagonalTiledMapRenderer(TiledMap map, Batch batch) {
        super(map, batch);
        this.init(map);
    }

    public HexagonalTiledMapRenderer(TiledMap map, float unitScale, Batch batch) {
        super(map, unitScale, batch);
        this.init(map);
    }

    private void init(TiledMap map) {
        Integer length;
        String index;
        String axis = map.getProperties().get("staggeraxis", String.class);
        if (axis != null) {
            this.staggerAxisX = axis.equals("x");
        }
        if ((index = map.getProperties().get("staggerindex", String.class)) != null) {
            this.staggerIndexEven = index.equals("even");
        }
        if (!this.staggerAxisX && map.getProperties().get("height", Integer.class) % 2 == 0) {
            boolean bl = this.staggerIndexEven = !this.staggerIndexEven;
        }
        if ((length = map.getProperties().get("hexsidelength", Integer.class)) != null) {
            this.hexSideLength = length.intValue();
        } else if (this.staggerAxisX) {
            length = map.getProperties().get("tilewidth", Integer.class);
            if (length != null) {
                this.hexSideLength = 0.5f * (float)length.intValue();
            } else if (map.getLayers().size() > 0) {
                TiledMapTileLayer tmtl = (TiledMapTileLayer)map.getLayers().get(0);
                this.hexSideLength = 0.5f * (float)tmtl.getTileWidth();
            } else {
                this.hexSideLength = 0.0f;
            }
        } else {
            length = map.getProperties().get("tileheight", Integer.class);
            if (length != null) {
                this.hexSideLength = 0.5f * (float)length.intValue();
            } else if (map.getLayers().size() > 0) {
                TiledMapTileLayer tmtl = (TiledMapTileLayer)map.getLayers().get(0);
                this.hexSideLength = 0.5f * (float)tmtl.getTileHeight();
            } else {
                this.hexSideLength = 0.0f;
            }
        }
    }

    @Override
    public void renderTileLayer(TiledMapTileLayer layer) {
        Color batchColor = this.batch.getColor();
        float color = this.getTileLayerColor(layer, batchColor);
        int layerWidth = layer.getWidth();
        int layerHeight = layer.getHeight();
        float layerTileWidth = (float)layer.getTileWidth() * this.unitScale;
        float layerTileHeight = (float)layer.getTileHeight() * this.unitScale;
        float layerOffsetX = layer.getRenderOffsetX() * this.unitScale - this.viewBounds.x * (layer.getParallaxX() - 1.0f);
        float layerOffsetY = -layer.getRenderOffsetY() * this.unitScale - this.viewBounds.y * (layer.getParallaxY() - 1.0f);
        float layerHexLength = this.hexSideLength * this.unitScale;
        if (this.staggerAxisX) {
            float tileWidthLowerCorner = (layerTileWidth - layerHexLength) / 2.0f;
            float tileWidthUpperCorner = (layerTileWidth + layerHexLength) / 2.0f;
            float layerTileHeight50 = layerTileHeight * 0.5f;
            int row1 = Math.max(0, (int)((this.viewBounds.y - layerTileHeight50 - layerOffsetY) / layerTileHeight));
            int row2 = Math.min(layerHeight, (int)((this.viewBounds.y + this.viewBounds.height + layerTileHeight - layerOffsetY) / layerTileHeight));
            int col1 = Math.max(0, (int)((this.viewBounds.x - tileWidthLowerCorner - layerOffsetX) / tileWidthUpperCorner));
            int col2 = Math.min(layerWidth, (int)((this.viewBounds.x + this.viewBounds.width + tileWidthUpperCorner - layerOffsetX) / tileWidthUpperCorner));
            int colA = this.staggerIndexEven == (col1 % 2 == 0) ? col1 + 1 : col1;
            int colB = this.staggerIndexEven == (col1 % 2 == 0) ? col1 : col1 + 1;
            for (int row = row2 - 1; row >= row1; --row) {
                int col;
                for (col = colA; col < col2; col += 2) {
                    this.renderCell(layer.getCell(col, row), tileWidthUpperCorner * (float)col + layerOffsetX, layerTileHeight50 + layerTileHeight * (float)row + layerOffsetY, color);
                }
                for (col = colB; col < col2; col += 2) {
                    this.renderCell(layer.getCell(col, row), tileWidthUpperCorner * (float)col + layerOffsetX, layerTileHeight * (float)row + layerOffsetY, color);
                }
            }
        } else {
            float tileHeightLowerCorner = (layerTileHeight - layerHexLength) / 2.0f;
            float tileHeightUpperCorner = (layerTileHeight + layerHexLength) / 2.0f;
            float layerTileWidth50 = layerTileWidth * 0.5f;
            int row1 = Math.max(0, (int)((this.viewBounds.y - tileHeightLowerCorner - layerOffsetY) / tileHeightUpperCorner));
            int row2 = Math.min(layerHeight, (int)((this.viewBounds.y + this.viewBounds.height + tileHeightUpperCorner - layerOffsetY) / tileHeightUpperCorner));
            int col1 = Math.max(0, (int)((this.viewBounds.x - layerTileWidth50 - layerOffsetX) / layerTileWidth));
            int col2 = Math.min(layerWidth, (int)((this.viewBounds.x + this.viewBounds.width + layerTileWidth - layerOffsetX) / layerTileWidth));
            float shiftX = 0.0f;
            for (int row = row2 - 1; row >= row1; --row) {
                shiftX = row % 2 == 0 == this.staggerIndexEven ? layerTileWidth50 : 0.0f;
                for (int col = col1; col < col2; ++col) {
                    this.renderCell(layer.getCell(col, row), layerTileWidth * (float)col + shiftX + layerOffsetX, tileHeightUpperCorner * (float)row + layerOffsetY, color);
                }
            }
        }
    }

    private void renderCell(TiledMapTileLayer.Cell cell, float x, float y, float color) {
        TiledMapTile tile;
        if (cell != null && (tile = cell.getTile()) != null) {
            float temp;
            if (tile instanceof AnimatedTiledMapTile) {
                return;
            }
            boolean flipX = cell.getFlipHorizontally();
            boolean flipY = cell.getFlipVertically();
            int rotations = cell.getRotation();
            TextureRegion region = tile.getTextureRegion();
            float x1 = x + tile.getOffsetX() * this.unitScale;
            float y1 = y + tile.getOffsetY() * this.unitScale;
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
            if (rotations == 2) {
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
            }
            this.batch.draw(region.getTexture(), this.vertices, 0, 20);
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
        float layerHexLength = this.hexSideLength;
        float totalHeightPixels = (float)(mapHeight * tileHeight) * this.unitScale;
        float hexMapHeightPixels = ((float)(mapHeight * tileHeight) * 0.75f + layerHexLength * 0.5f) * this.unitScale;
        float imageLayerYOffset = 0.0f;
        float layerTileHeight = (float)tileHeight * this.unitScale;
        float halfTileHeight = layerTileHeight * 0.5f;
        imageLayerYOffset = this.staggerAxisX ? halfTileHeight : -(totalHeightPixels - hexMapHeightPixels);
        float x = layer.getX();
        float y = layer.getY();
        float x1 = x * this.unitScale - this.viewBounds.x * (layer.getParallaxX() - 1.0f);
        float y1 = y * this.unitScale - this.viewBounds.y * (layer.getParallaxY() - 1.0f) + imageLayerYOffset;
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

    public boolean isStaggerAxisX() {
        return this.staggerAxisX;
    }

    public void setStaggerAxisX(boolean staggerAxisX) {
        this.staggerAxisX = staggerAxisX;
    }

    public boolean isStaggerIndexEven() {
        return this.staggerIndexEven;
    }

    public void setStaggerIndexEven(boolean staggerIndexEven) {
        this.staggerIndexEven = staggerIndexEven;
    }

    public float getHexSideLength() {
        return this.hexSideLength;
    }

    public void setHexSideLength(float hexSideLength) {
        this.hexSideLength = hexSideLength;
    }
}

