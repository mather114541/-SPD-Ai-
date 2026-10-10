/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.noosa;

import com.watabou.gltextures.SmartTexture;
import com.watabou.gltextures.TextureCache;
import com.watabou.glwrap.Quad;
import com.watabou.glwrap.Vertexbuffer;
import com.watabou.noosa.NoosaScript;
import com.watabou.noosa.Visual;
import com.watabou.utils.RectF;
import java.nio.Buffer;
import java.nio.FloatBuffer;

public class Image
extends Visual {
    public SmartTexture texture;
    protected RectF frame;
    public boolean flipHorizontal;
    public boolean flipVertical;
    protected float[] vertices = new float[16];
    protected FloatBuffer verticesBuffer = Quad.create();
    protected Vertexbuffer buffer;
    protected boolean dirty;

    public Image() {
        super(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public Image(Image src) {
        this();
        this.copy(src);
    }

    public Image(Object tx) {
        this();
        this.texture(tx);
    }

    public Image(Object tx, int left, int top, int width, int height) {
        this(tx);
        this.frame(this.texture.uvRect(left, top, left + width, top + height));
    }

    public void texture(Object tx) {
        this.texture = tx instanceof SmartTexture ? (SmartTexture)tx : TextureCache.get(tx);
        this.frame(new RectF(0.0f, 0.0f, 1.0f, 1.0f));
    }

    public void frame(RectF frame) {
        this.frame = frame;
        this.width = frame.width() * (float)this.texture.width;
        this.height = frame.height() * (float)this.texture.height;
        this.updateFrame();
        this.updateVertices();
    }

    public void frame(int left, int top, int width, int height) {
        this.frame(this.texture.uvRect(left, top, left + width, top + height));
    }

    public RectF frame() {
        return new RectF(this.frame);
    }

    public void copy(Image other) {
        this.texture = other.texture;
        this.frame = new RectF(other.frame);
        this.width = other.width;
        this.height = other.height;
        this.scale = other.scale;
        this.updateFrame();
        this.updateVertices();
        this.rm = other.rm;
        this.gm = other.gm;
        this.bm = other.bm;
        this.am = other.am;
        this.ra = other.ra;
        this.ga = other.ga;
        this.ba = other.ba;
        this.aa = other.aa;
    }

    protected void updateFrame() {
        if (this.flipHorizontal) {
            this.vertices[2] = this.frame.right;
            this.vertices[6] = this.frame.left;
            this.vertices[10] = this.frame.left;
            this.vertices[14] = this.frame.right;
        } else {
            this.vertices[2] = this.frame.left;
            this.vertices[6] = this.frame.right;
            this.vertices[10] = this.frame.right;
            this.vertices[14] = this.frame.left;
        }
        if (this.flipVertical) {
            this.vertices[3] = this.frame.bottom;
            this.vertices[7] = this.frame.bottom;
            this.vertices[11] = this.frame.top;
            this.vertices[15] = this.frame.top;
        } else {
            this.vertices[3] = this.frame.top;
            this.vertices[7] = this.frame.top;
            this.vertices[11] = this.frame.bottom;
            this.vertices[15] = this.frame.bottom;
        }
        this.dirty = true;
    }

    protected void updateVertices() {
        this.vertices[0] = 0.0f;
        this.vertices[1] = 0.0f;
        this.vertices[4] = this.width;
        this.vertices[5] = 0.0f;
        this.vertices[8] = this.width;
        this.vertices[9] = this.height;
        this.vertices[12] = 0.0f;
        this.vertices[13] = this.height;
        this.dirty = true;
    }

    @Override
    public void draw() {
        if (this.texture == null || !this.dirty && this.buffer == null) {
            return;
        }
        super.draw();
        if (this.dirty) {
            ((Buffer)this.verticesBuffer).position(0);
            this.verticesBuffer.put(this.vertices);
            if (this.buffer == null) {
                this.buffer = new Vertexbuffer(this.verticesBuffer);
            } else {
                this.buffer.updateVertices(this.verticesBuffer);
            }
            this.dirty = false;
        }
        NoosaScript script = this.script();
        this.texture.bind();
        script.camera(this.camera());
        script.uModel.valueM4(this.matrix);
        script.lighting(this.rm, this.gm, this.bm, this.am, this.ra, this.ga, this.ba, this.aa);
        script.drawQuad(this.buffer);
    }

    protected NoosaScript script() {
        return NoosaScript.get();
    }

    @Override
    public void destroy() {
        super.destroy();
        if (this.buffer != null) {
            this.buffer.delete();
        }
    }
}

