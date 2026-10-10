/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.noosa;

import com.badlogic.gdx.graphics.Pixmap;
import com.watabou.gltextures.SmartTexture;
import com.watabou.glwrap.Matrix;
import com.watabou.glwrap.Quad;
import com.watabou.glwrap.Vertexbuffer;
import com.watabou.noosa.NoosaScript;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.Visual;
import com.watabou.utils.RectF;
import java.nio.Buffer;
import java.nio.FloatBuffer;

public class BitmapText
extends Visual {
    protected String text;
    protected Font font;
    protected float[] vertices = new float[16];
    protected FloatBuffer quads;
    protected Vertexbuffer buffer;
    public int realLength;
    protected boolean dirty = true;

    public BitmapText() {
        this("", null);
    }

    public BitmapText(Font font) {
        this("", font);
    }

    public BitmapText(String text, Font font) {
        super(0.0f, 0.0f, 0.0f, 0.0f);
        this.text = text;
        this.font = font;
    }

    @Override
    protected void updateMatrix() {
        Matrix.setIdentity(this.matrix);
        Matrix.translate(this.matrix, this.x, this.y);
        Matrix.scale(this.matrix, this.scale.x, this.scale.y);
        Matrix.rotate(this.matrix, this.angle);
    }

    @Override
    public void draw() {
        super.draw();
        if (this.dirty) {
            this.updateVertices();
            ((Buffer)this.quads).limit(this.quads.position());
            if (this.buffer == null) {
                this.buffer = new Vertexbuffer(this.quads);
            } else {
                this.buffer.updateVertices(this.quads);
            }
        }
        NoosaScript script = NoosaScript.get();
        this.font.texture.bind();
        script.camera(this.camera());
        script.uModel.valueM4(this.matrix);
        script.lighting(this.rm, this.gm, this.bm, this.am, this.ra, this.ga, this.ba, this.aa);
        script.drawQuadSet(this.buffer, this.realLength, 0);
    }

    @Override
    public void destroy() {
        super.destroy();
        if (this.buffer != null) {
            this.buffer.delete();
        }
    }

    protected synchronized void updateVertices() {
        this.width = 0.0f;
        this.height = 0.0f;
        if (this.text == null) {
            this.text = "";
        }
        this.quads = Quad.createSet(this.text.length());
        this.realLength = 0;
        int length = this.text.length();
        for (int i = 0; i < length; ++i) {
            RectF rect = this.font.get(this.text.charAt(i));
            if (rect == null) {
                rect = null;
            }
            float w = this.font.width(rect);
            float h = this.font.height(rect);
            this.vertices[0] = this.width;
            this.vertices[1] = 0.0f;
            this.vertices[2] = rect.left;
            this.vertices[3] = rect.top;
            this.vertices[4] = this.width + w;
            this.vertices[5] = 0.0f;
            this.vertices[6] = rect.right;
            this.vertices[7] = rect.top;
            this.vertices[8] = this.width + w;
            this.vertices[9] = h;
            this.vertices[10] = rect.right;
            this.vertices[11] = rect.bottom;
            this.vertices[12] = this.width;
            this.vertices[13] = h;
            this.vertices[14] = rect.left;
            this.vertices[15] = rect.bottom;
            this.quads.put(this.vertices);
            ++this.realLength;
            this.width += w + this.font.tracking;
            if (!(h > this.height)) continue;
            this.height = h;
        }
        if (length > 0) {
            this.width -= this.font.tracking;
        }
        this.dirty = false;
    }

    public synchronized void measure() {
        this.width = 0.0f;
        this.height = 0.0f;
        if (this.text == null) {
            this.text = "";
        }
        int length = this.text.length();
        for (int i = 0; i < length; ++i) {
            RectF rect = this.font.get(this.text.charAt(i));
            float w = this.font.width(rect);
            float h = this.font.height(rect);
            this.width += w + this.font.tracking;
            if (!(h > this.height)) continue;
            this.height = h;
        }
        if (length > 0) {
            this.width -= this.font.tracking;
        }
    }

    public float baseLine() {
        return this.font.baseLine * this.scale.y;
    }

    public Font font() {
        return this.font;
    }

    public synchronized void font(Font value) {
        this.font = value;
    }

    public String text() {
        return this.text;
    }

    public synchronized void text(String str) {
        if (str == null || !str.equals(this.text)) {
            this.text = str;
            this.dirty = true;
        }
    }

    public static class Font
    extends TextureFilm {
        public static final String LATIN_FULL = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f";
        public SmartTexture texture;
        public float tracking = 0.0f;
        public float baseLine;
        public float lineHeight;

        protected Font(SmartTexture tx) {
            super(tx);
            this.texture = tx;
        }

        public Font(SmartTexture tx, int width, String chars) {
            this(tx, width, tx.height, chars);
        }

        public Font(SmartTexture tx, int width, int height, String chars) {
            super(tx);
            this.texture = tx;
            int length = chars.length();
            float uw = (float)width / (float)tx.width;
            float vh = (float)height / (float)tx.height;
            float left = 0.0f;
            float top = 0.0f;
            float bottom = vh;
            for (int i = 0; i < length; ++i) {
                RectF rect = new RectF(left, top, left += uw, bottom);
                this.add(Character.valueOf(chars.charAt(i)), rect);
                if (!(left >= 1.0f)) continue;
                left = 0.0f;
                top = bottom;
                bottom += vh;
            }
            this.lineHeight = this.baseLine = (float)height;
        }

        protected void splitBy(Pixmap bitmap, int height, int color, String chars) {
            int pos;
            int length = chars.length();
            int width = bitmap.getWidth();
            float vHeight = (float)height / (float)bitmap.getHeight();
            int line = 0;
            block0: for (pos = 0; pos < width; ++pos) {
                for (int j = 0; j < height; ++j) {
                    if (bitmap.getPixel(pos, j) != color) break block0;
                }
            }
            this.add(Character.valueOf(' '), new RectF(0.0f, 0.0f, (float)pos / (float)width, vHeight - 0.01f));
            int separator = pos;
            for (int i = 0; i < length; ++i) {
                boolean found;
                char ch = chars.charAt(i);
                if (ch == ' ') continue;
                do {
                    if (separator >= width) {
                        line += height;
                        separator = 0;
                    }
                    found = false;
                    for (int j = line; j < line + height; ++j) {
                        if (!this.colorNotMatch(bitmap, separator, j, color)) continue;
                        found = true;
                        break;
                    }
                    if (found) continue;
                    ++separator;
                } while (!found);
                int start = separator;
                block5: do {
                    if (++separator >= width) {
                        start = 0;
                        separator = 0;
                        if ((line += height) + height >= bitmap.getHeight()) break;
                    }
                    found = true;
                    for (int j = line; j < line + height; ++j) {
                        if (!this.colorNotMatch(bitmap, separator, j, color)) continue;
                        found = false;
                        continue block5;
                    }
                } while (!found);
                this.add(Character.valueOf(ch), new RectF((float)start / (float)width, (float)line / (float)bitmap.getHeight(), (float)separator / (float)width, (float)line / (float)bitmap.getHeight() + vHeight));
                ++separator;
            }
            this.lineHeight = this.baseLine = this.height((RectF)this.frames.get(Character.valueOf(chars.charAt(0))));
        }

        private boolean colorNotMatch(Pixmap pixmap, int x, int y, int color) {
            int pixel = pixmap.getPixel(x, y);
            if ((pixel & 0xFF) == 0) {
                return color != 0;
            }
            return pixel != color;
        }

        public static Font colorMarked(SmartTexture tex, int color, String chars) {
            Font font = new Font(tex);
            font.splitBy(tex.bitmap, tex.height, color, chars);
            return font;
        }

        public static Font colorMarked(SmartTexture tex, int height, int color, String chars) {
            Font font = new Font(tex);
            font.splitBy(tex.bitmap, height, color, chars);
            return font;
        }

        public RectF get(char ch) {
            if (this.frames.containsKey(Character.valueOf(ch))) {
                return super.get(Character.valueOf(ch));
            }
            return super.get(Character.valueOf('?'));
        }
    }
}

