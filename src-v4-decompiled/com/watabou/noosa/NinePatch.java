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

public class NinePatch
extends Visual {
    public SmartTexture texture;
    protected float[] vertices;
    protected FloatBuffer quads;
    protected Vertexbuffer buffer;
    protected RectF outterF;
    protected RectF innerF;
    protected int marginLeft;
    protected int marginRight;
    protected int marginTop;
    protected int marginBottom;
    protected float nWidth;
    protected float nHeight;
    protected boolean flipHorizontal;
    protected boolean flipVertical;
    protected boolean dirty;

    public NinePatch(Object tx, int margin) {
        this(tx, margin, margin, margin, margin);
    }

    public NinePatch(Object tx, int left, int top, int right, int bottom) {
        this(tx, 0, 0, 0, 0, left, top, right, bottom);
    }

    public NinePatch(Object tx, int x, int y, int w, int h, int margin) {
        this(tx, x, y, w, h, margin, margin, margin, margin);
    }

    public NinePatch(Object tx, int x, int y, int w, int h, int left, int top, int right, int bottom) {
        super(0.0f, 0.0f, 0.0f, 0.0f);
        this.texture = TextureCache.get(tx);
        w = w == 0 ? this.texture.width : w;
        h = h == 0 ? this.texture.height : h;
        this.nWidth = this.width = (float)w;
        this.nHeight = this.height = (float)h;
        this.vertices = new float[16];
        this.quads = Quad.createSet(9);
        this.marginLeft = left;
        this.marginRight = right;
        this.marginTop = top;
        this.marginBottom = bottom;
        this.outterF = this.texture.uvRect(x, y, x + w, y + h);
        this.innerF = this.texture.uvRect(x + left, y + top, x + w - right, y + h - bottom);
        this.updateVertices();
    }

    protected void updateVertices() {
        ((Buffer)this.quads).position(0);
        float right = this.width - (float)this.marginRight;
        float bottom = this.height - (float)this.marginBottom;
        float outleft = this.flipHorizontal ? this.outterF.right : this.outterF.left;
        float outright = this.flipHorizontal ? this.outterF.left : this.outterF.right;
        float outtop = this.flipVertical ? this.outterF.bottom : this.outterF.top;
        float outbottom = this.flipVertical ? this.outterF.top : this.outterF.bottom;
        float inleft = this.flipHorizontal ? this.innerF.right : this.innerF.left;
        float inright = this.flipHorizontal ? this.innerF.left : this.innerF.right;
        float intop = this.flipVertical ? this.innerF.bottom : this.innerF.top;
        float inbottom = this.flipVertical ? this.innerF.top : this.innerF.bottom;
        Quad.fill(this.vertices, 0.0f, this.marginLeft, 0.0f, this.marginTop, outleft, inleft, outtop, intop);
        this.quads.put(this.vertices);
        Quad.fill(this.vertices, this.marginLeft, right, 0.0f, this.marginTop, inleft, inright, outtop, intop);
        this.quads.put(this.vertices);
        Quad.fill(this.vertices, right, this.width, 0.0f, this.marginTop, inright, outright, outtop, intop);
        this.quads.put(this.vertices);
        Quad.fill(this.vertices, 0.0f, this.marginLeft, this.marginTop, bottom, outleft, inleft, intop, inbottom);
        this.quads.put(this.vertices);
        Quad.fill(this.vertices, this.marginLeft, right, this.marginTop, bottom, inleft, inright, intop, inbottom);
        this.quads.put(this.vertices);
        Quad.fill(this.vertices, right, this.width, this.marginTop, bottom, inright, outright, intop, inbottom);
        this.quads.put(this.vertices);
        Quad.fill(this.vertices, 0.0f, this.marginLeft, bottom, this.height, outleft, inleft, inbottom, outbottom);
        this.quads.put(this.vertices);
        Quad.fill(this.vertices, this.marginLeft, right, bottom, this.height, inleft, inright, inbottom, outbottom);
        this.quads.put(this.vertices);
        Quad.fill(this.vertices, right, this.width, bottom, this.height, inright, outright, inbottom, outbottom);
        this.quads.put(this.vertices);
        this.dirty = true;
    }

    public int marginLeft() {
        return this.marginLeft;
    }

    public int marginRight() {
        return this.marginRight;
    }

    public int marginTop() {
        return this.marginTop;
    }

    public int marginBottom() {
        return this.marginBottom;
    }

    public int marginHor() {
        return this.marginLeft + this.marginRight;
    }

    public int marginVer() {
        return this.marginTop + this.marginBottom;
    }

    public float innerWidth() {
        return this.width - (float)this.marginLeft - (float)this.marginRight;
    }

    public float innerHeight() {
        return this.height - (float)this.marginTop - (float)this.marginBottom;
    }

    public float innerRight() {
        return this.width - (float)this.marginRight;
    }

    public float innerBottom() {
        return this.height - (float)this.marginBottom;
    }

    public void flipHorizontal(boolean value) {
        if (this.flipHorizontal != value) {
            this.flipHorizontal = value;
            this.updateVertices();
        }
    }

    public void flipVertical(boolean value) {
        if (this.flipVertical != value) {
            this.flipVertical = value;
            this.updateVertices();
        }
    }

    public void size(float width, float height) {
        this.width = width;
        this.height = height;
        this.updateVertices();
    }

    @Override
    public void draw() {
        super.draw();
        if (this.dirty) {
            if (this.buffer == null) {
                this.buffer = new Vertexbuffer(this.quads);
            } else {
                this.buffer.updateVertices(this.quads);
            }
            this.dirty = false;
        }
        NoosaScript script = NoosaScript.get();
        this.texture.bind();
        script.camera(this.camera());
        script.uModel.valueM4(this.matrix);
        script.lighting(this.rm, this.gm, this.bm, this.am, this.ra, this.ga, this.ba, this.aa);
        script.drawQuadSet(this.buffer, 9, 0);
    }

    @Override
    public void destroy() {
        super.destroy();
        if (this.buffer != null) {
            this.buffer.delete();
        }
    }
}

