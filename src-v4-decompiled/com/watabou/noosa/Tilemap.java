/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.noosa;

import com.watabou.gltextures.SmartTexture;
import com.watabou.gltextures.TextureCache;
import com.watabou.glwrap.Quad;
import com.watabou.glwrap.Vertexbuffer;
import com.watabou.noosa.Image;
import com.watabou.noosa.NoosaScript;
import com.watabou.noosa.NoosaScriptNoLighting;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.Visual;
import com.watabou.utils.Rect;
import com.watabou.utils.RectF;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.Arrays;

public class Tilemap
extends Visual {
    protected SmartTexture texture;
    protected TextureFilm tileset;
    protected int[] data;
    protected int mapWidth;
    protected int mapHeight;
    protected int size;
    private float cellW;
    private float cellH;
    protected float[] vertices;
    protected FloatBuffer quads;
    protected Vertexbuffer buffer;
    private volatile Rect updated;
    private boolean fullUpdate;
    private Rect updating;
    private int topLeftUpdating;
    private int bottomRightUpdating;

    public Tilemap(Object tx, TextureFilm tileset) {
        super(0.0f, 0.0f, 0.0f, 0.0f);
        this.texture = TextureCache.get(tx);
        this.tileset = tileset;
        RectF r = tileset.get(0);
        this.cellW = tileset.width(r);
        this.cellH = tileset.height(r);
        this.vertices = new float[16];
        this.updated = new Rect();
    }

    public void map(int[] data, int cols) {
        this.data = data;
        this.mapWidth = cols;
        this.mapHeight = data.length / cols;
        this.size = this.mapWidth * this.mapHeight;
        this.width = this.cellW * (float)this.mapWidth;
        this.height = this.cellH * (float)this.mapHeight;
        this.quads = Quad.createSet(this.size);
        this.updateMap();
    }

    public Image image(int x, int y) {
        if (!this.needsRender(x + this.mapWidth * y)) {
            return null;
        }
        Image img = new Image(this.texture);
        img.frame(this.tileset.get(this.data[x + this.mapWidth * y]));
        return img;
    }

    public synchronized void updateMap() {
        this.updated.set(0, 0, this.mapWidth, this.mapHeight);
        this.fullUpdate = true;
    }

    public synchronized void updateMapCell(int cell) {
        this.updated.union(cell % this.mapWidth, cell / this.mapWidth);
    }

    private synchronized void moveToUpdating() {
        this.updating = new Rect(this.updated);
        this.updated.setEmpty();
    }

    protected void updateVertices() {
        this.moveToUpdating();
        float y1 = this.cellH * (float)this.updating.top;
        float y2 = y1 + this.cellH;
        for (int i = this.updating.top; i < this.updating.bottom; ++i) {
            float x1 = this.cellW * (float)this.updating.left;
            float x2 = x1 + this.cellW;
            int pos = i * this.mapWidth + this.updating.left;
            for (int j = this.updating.left; j < this.updating.right; ++j) {
                if (this.topLeftUpdating == -1) {
                    this.topLeftUpdating = pos;
                }
                this.bottomRightUpdating = pos + 1;
                ((Buffer)this.quads).position(pos * 16);
                RectF uv = this.tileset.get(this.data[pos]);
                if (this.needsRender(pos) && uv != null) {
                    this.vertices[0] = x1;
                    this.vertices[1] = y1;
                    this.vertices[2] = uv.left;
                    this.vertices[3] = uv.top;
                    this.vertices[4] = x2;
                    this.vertices[5] = y1;
                    this.vertices[6] = uv.right;
                    this.vertices[7] = uv.top;
                    this.vertices[8] = x2;
                    this.vertices[9] = y2;
                    this.vertices[10] = uv.right;
                    this.vertices[11] = uv.bottom;
                    this.vertices[12] = x1;
                    this.vertices[13] = y2;
                    this.vertices[14] = uv.left;
                    this.vertices[15] = uv.bottom;
                } else {
                    Arrays.fill(this.vertices, 0.0f);
                }
                this.quads.put(this.vertices);
                ++pos;
                x1 = x2;
                x2 += this.cellW;
            }
            y1 = y2;
            y2 += this.cellH;
        }
    }

    @Override
    public void draw() {
        super.draw();
        if (!this.updated.isEmpty()) {
            this.updateVertices();
            if (this.buffer == null) {
                this.buffer = new Vertexbuffer(this.quads);
            } else if (this.fullUpdate) {
                this.buffer.updateVertices(this.quads);
                this.fullUpdate = false;
            } else {
                this.buffer.updateVertices(this.quads, this.topLeftUpdating * 16, this.bottomRightUpdating * 16);
            }
            this.topLeftUpdating = -1;
            this.updating.setEmpty();
        }
        NoosaScript script = this.script();
        this.texture.bind();
        script.uModel.valueM4(this.matrix);
        script.lighting(this.rm, this.gm, this.bm, this.am, this.ra, this.ga, this.ba, this.aa);
        script.camera(this.camera);
        script.drawQuadSet(this.buffer, this.size, 0);
    }

    protected NoosaScript script() {
        return NoosaScriptNoLighting.get();
    }

    @Override
    public void destroy() {
        super.destroy();
        if (this.buffer != null) {
            this.buffer.delete();
        }
    }

    protected boolean needsRender(int pos) {
        return this.data[pos] >= 0;
    }
}

