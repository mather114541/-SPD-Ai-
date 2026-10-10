/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.noosa;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Matrix4;
import com.watabou.glwrap.Matrix;
import com.watabou.glwrap.Quad;
import com.watabou.glwrap.Texture;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.NoosaScript;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;

public class RenderedText
extends Image {
    private BitmapFont font = null;
    private int size;
    private String text;
    private static final ArrayList<Character> alreadyReported = new ArrayList();
    private float renderedHeight = 0.0f;
    private static TextRenderBatch textRenderer = new TextRenderBatch();

    public RenderedText() {
        this.text = null;
    }

    public RenderedText(int size) {
        this.text = null;
        this.size = size;
    }

    public RenderedText(String text, int size) {
        this.text = text;
        this.size = size;
        this.measure();
    }

    public void text(String text) {
        this.text = text;
        this.measure();
    }

    public String text() {
        return this.text;
    }

    public void size(int size) {
        this.size = size;
        this.measure();
    }

    private synchronized void measure() {
        if (Thread.currentThread().getName().equals("SHPD Actor Thread")) {
            throw new RuntimeException("Text measured from the actor thread!");
        }
        if (this.text == null || this.text.equals("")) {
            this.text = "";
            this.height = 0.0f;
            this.width = 0.0f;
            this.visible = false;
            return;
        }
        this.visible = true;
        this.font = Game.platform.getFont(this.size, this.text, true, true);
        if (this.font != null) {
            GlyphLayout glyphs = new GlyphLayout(this.font, this.text);
            for (char c : this.text.toCharArray()) {
                BitmapFont.Glyph g = this.font.getData().getGlyph(c);
                if (g != null && g.id == c) continue;
                Object toException = this.text;
                if (((String)toException).length() > 30) {
                    toException = ((String)toException).substring(0, 30) + "...";
                }
                if (alreadyReported.contains(Character.valueOf(c))) continue;
                Game.reportException(new Throwable("font file " + this.font.toString() + " could not render " + c + " from string: " + (String)toException));
                alreadyReported.add(Character.valueOf(c));
            }
            BitmapFont.Glyph lastGlyph = this.font.getData().getGlyph(this.text.charAt(this.text.length() - 1));
            this.width = lastGlyph != null && (float)lastGlyph.xadvance > (float)lastGlyph.width * 1.5f ? glyphs.width - (float)lastGlyph.width + (float)lastGlyph.xadvance : glyphs.width;
            this.height = Math.round((float)this.size * 0.75f);
            this.renderedHeight = glyphs.height;
        }
    }

    @Override
    protected void updateMatrix() {
        super.updateMatrix();
        if (this.renderedHeight != this.height) {
            Matrix.translate(this.matrix, 0.0f, Math.round(this.height - this.renderedHeight));
        }
    }

    @Override
    public synchronized void draw() {
        if (this.font != null) {
            this.updateMatrix();
            TextRenderBatch.textBeingRendered = this;
            this.font.draw((Batch)textRenderer, this.text, 0.0f, 0.0f);
        }
    }

    private static class TextRenderBatch
    implements Batch {
        private static RenderedText textBeingRendered = null;
        private static float[] vertices = new float[16];
        private static HashMap<Integer, FloatBuffer> buffers = new HashMap();

        private TextRenderBatch() {
        }

        @Override
        public void draw(com.badlogic.gdx.graphics.Texture texture, float[] spriteVertices, int offset, int count) {
            FloatBuffer toOpenGL;
            RenderedText v = textBeingRendered;
            if (buffers.containsKey(count / 20)) {
                toOpenGL = buffers.get(count / 20);
                ((Buffer)toOpenGL).position(0);
            } else {
                toOpenGL = Quad.createSet(count / 20);
                buffers.put(count / 20, toOpenGL);
            }
            for (int i = 0; i < count; i += 20) {
                TextRenderBatch.vertices[0] = spriteVertices[i + 0];
                TextRenderBatch.vertices[1] = spriteVertices[i + 1];
                TextRenderBatch.vertices[2] = spriteVertices[i + 3];
                TextRenderBatch.vertices[3] = spriteVertices[i + 4];
                TextRenderBatch.vertices[4] = spriteVertices[i + 5];
                TextRenderBatch.vertices[5] = spriteVertices[i + 6];
                TextRenderBatch.vertices[6] = spriteVertices[i + 8];
                TextRenderBatch.vertices[7] = spriteVertices[i + 9];
                TextRenderBatch.vertices[8] = spriteVertices[i + 10];
                TextRenderBatch.vertices[9] = spriteVertices[i + 11];
                TextRenderBatch.vertices[10] = spriteVertices[i + 13];
                TextRenderBatch.vertices[11] = spriteVertices[i + 14];
                TextRenderBatch.vertices[12] = spriteVertices[i + 15];
                TextRenderBatch.vertices[13] = spriteVertices[i + 16];
                TextRenderBatch.vertices[14] = spriteVertices[i + 18];
                TextRenderBatch.vertices[15] = spriteVertices[i + 19];
                toOpenGL.put(vertices);
            }
            ((Buffer)toOpenGL).position(0);
            NoosaScript script = NoosaScript.get();
            texture.bind();
            Texture.clear();
            script.camera(v.camera());
            script.uModel.valueM4(v.matrix);
            script.lighting(v.rm, v.gm, v.bm, v.am, v.ra, v.ga, v.ba, v.aa);
            script.drawQuadSet(toOpenGL, count / 20);
        }

        @Override
        public void begin() {
        }

        @Override
        public void end() {
        }

        @Override
        public void setColor(Color tint) {
        }

        @Override
        public void setColor(float r, float g, float b, float a) {
        }

        @Override
        public Color getColor() {
            return null;
        }

        @Override
        public void setPackedColor(float packedColor) {
        }

        @Override
        public float getPackedColor() {
            return 0.0f;
        }

        @Override
        public void draw(com.badlogic.gdx.graphics.Texture texture, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation, int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY) {
        }

        @Override
        public void draw(com.badlogic.gdx.graphics.Texture texture, float x, float y, float width, float height, int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY) {
        }

        @Override
        public void draw(com.badlogic.gdx.graphics.Texture texture, float x, float y, int srcX, int srcY, int srcWidth, int srcHeight) {
        }

        @Override
        public void draw(com.badlogic.gdx.graphics.Texture texture, float x, float y, float width, float height, float u, float v, float u2, float v2) {
        }

        @Override
        public void draw(com.badlogic.gdx.graphics.Texture texture, float x, float y) {
        }

        @Override
        public void draw(com.badlogic.gdx.graphics.Texture texture, float x, float y, float width, float height) {
        }

        @Override
        public void draw(TextureRegion region, float x, float y) {
        }

        @Override
        public void draw(TextureRegion region, float x, float y, float width, float height) {
        }

        @Override
        public void draw(TextureRegion region, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation) {
        }

        @Override
        public void draw(TextureRegion region, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation, boolean clockwise) {
        }

        @Override
        public void draw(TextureRegion region, float width, float height, Affine2 transform) {
        }

        @Override
        public void flush() {
        }

        @Override
        public void disableBlending() {
        }

        @Override
        public void enableBlending() {
        }

        @Override
        public void setBlendFunction(int srcFunc, int dstFunc) {
        }

        @Override
        public void setBlendFunctionSeparate(int srcFuncColor, int dstFuncColor, int srcFuncAlpha, int dstFuncAlpha) {
        }

        @Override
        public int getBlendSrcFunc() {
            return 0;
        }

        @Override
        public int getBlendDstFunc() {
            return 0;
        }

        @Override
        public int getBlendSrcFuncAlpha() {
            return 0;
        }

        @Override
        public int getBlendDstFuncAlpha() {
            return 0;
        }

        @Override
        public Matrix4 getProjectionMatrix() {
            return null;
        }

        @Override
        public Matrix4 getTransformMatrix() {
            return null;
        }

        @Override
        public void setProjectionMatrix(Matrix4 projection) {
        }

        @Override
        public void setTransformMatrix(Matrix4 transform) {
        }

        @Override
        public void setShader(ShaderProgram shader) {
        }

        @Override
        public ShaderProgram getShader() {
            return null;
        }

        @Override
        public boolean isBlendingEnabled() {
            return false;
        }

        @Override
        public boolean isDrawing() {
            return false;
        }

        @Override
        public void dispose() {
        }
    }
}

