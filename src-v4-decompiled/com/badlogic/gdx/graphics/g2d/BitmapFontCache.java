/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FlushablePool;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.NumberUtils;
import java.util.Arrays;

public class BitmapFontCache {
    private static final Color tempColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);
    private final BitmapFont font;
    private boolean integer;
    private final Array<GlyphLayout> layouts = new Array(1);
    private final FlushablePool<GlyphLayout> pooledLayouts = new FlushablePool<GlyphLayout>(){

        @Override
        protected GlyphLayout newObject() {
            return new GlyphLayout();
        }
    };
    private int glyphCount;
    private float x;
    private float y;
    private final Color color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
    private float currentTint;
    private float[][] pageVertices;
    private int[] idx;
    private IntArray[] pageGlyphIndices;
    private int[] tempGlyphCount;

    public BitmapFontCache(BitmapFont font) {
        this(font, font.usesIntegerPositions());
    }

    public BitmapFontCache(BitmapFont font, boolean integer) {
        this.font = font;
        this.integer = integer;
        int pageCount = font.regions.size;
        if (pageCount == 0) {
            throw new IllegalArgumentException("The specified font must contain at least one texture page.");
        }
        this.pageVertices = new float[pageCount][];
        this.idx = new int[pageCount];
        if (pageCount > 1) {
            this.pageGlyphIndices = new IntArray[pageCount];
            int n = this.pageGlyphIndices.length;
            for (int i = 0; i < n; ++i) {
                this.pageGlyphIndices[i] = new IntArray();
            }
        }
        this.tempGlyphCount = new int[pageCount];
    }

    public void setPosition(float x, float y) {
        this.translate(x - this.x, y - this.y);
    }

    public void translate(float xAmount, float yAmount) {
        if (xAmount == 0.0f && yAmount == 0.0f) {
            return;
        }
        if (this.integer) {
            xAmount = Math.round(xAmount);
            yAmount = Math.round(yAmount);
        }
        this.x += xAmount;
        this.y += yAmount;
        float[][] pageVertices = this.pageVertices;
        int n = pageVertices.length;
        for (int i = 0; i < n; ++i) {
            float[] vertices = pageVertices[i];
            int nn = this.idx[i];
            for (int ii = 0; ii < nn; ii += 5) {
                int n2 = ii;
                vertices[n2] = vertices[n2] + xAmount;
                int n3 = ii + 1;
                vertices[n3] = vertices[n3] + yAmount;
            }
        }
    }

    public void tint(Color tint) {
        float newTint = tint.toFloatBits();
        if (this.currentTint == newTint) {
            return;
        }
        this.currentTint = newTint;
        float[][] pageVertices = this.pageVertices;
        Color tempColor = BitmapFontCache.tempColor;
        int[] tempGlyphCount = this.tempGlyphCount;
        Arrays.fill(tempGlyphCount, 0);
        int n = this.layouts.size;
        for (int i = 0; i < n; ++i) {
            GlyphLayout layout = this.layouts.get(i);
            IntArray colors = layout.colors;
            int colorsIndex = 0;
            int nextColorGlyphIndex = 0;
            int glyphIndex = 0;
            float lastColorFloatBits = 0.0f;
            int nn = layout.runs.size;
            for (int ii = 0; ii < nn; ++ii) {
                GlyphLayout.GlyphRun run = layout.runs.get(ii);
                T[] glyphs = run.glyphs.items;
                int nnn = run.glyphs.size;
                for (int iii = 0; iii < nnn; ++iii) {
                    if (glyphIndex++ == nextColorGlyphIndex) {
                        Color.abgr8888ToColor(tempColor, colors.get(++colorsIndex));
                        lastColorFloatBits = tempColor.mul(tint).toFloatBits();
                        nextColorGlyphIndex = ++colorsIndex < colors.size ? colors.get(colorsIndex) : -1;
                    }
                    int page = ((BitmapFont.Glyph)glyphs[iii]).page;
                    int offset = tempGlyphCount[page] * 20 + 2;
                    int n2 = page;
                    tempGlyphCount[n2] = tempGlyphCount[n2] + 1;
                    float[] vertices = pageVertices[page];
                    vertices[offset] = lastColorFloatBits;
                    vertices[offset + 5] = lastColorFloatBits;
                    vertices[offset + 10] = lastColorFloatBits;
                    vertices[offset + 15] = lastColorFloatBits;
                }
            }
        }
    }

    public void setAlphas(float alpha) {
        int alphaBits = (int)(254.0f * alpha) << 24;
        float prev = 0.0f;
        float newColor = 0.0f;
        int length = this.pageVertices.length;
        for (int j = 0; j < length; ++j) {
            float[] vertices = this.pageVertices[j];
            int n = this.idx[j];
            for (int i = 2; i < n; i += 5) {
                float c = vertices[i];
                if (c == prev && i != 2) {
                    vertices[i] = newColor;
                    continue;
                }
                prev = c;
                int rgba = NumberUtils.floatToIntColor(c);
                rgba = rgba & 0xFFFFFF | alphaBits;
                vertices[i] = newColor = NumberUtils.intToFloatColor(rgba);
            }
        }
    }

    public void setColors(float color) {
        int length = this.pageVertices.length;
        for (int j = 0; j < length; ++j) {
            float[] vertices = this.pageVertices[j];
            int n = this.idx[j];
            for (int i = 2; i < n; i += 5) {
                vertices[i] = color;
            }
        }
    }

    public void setColors(Color tint) {
        this.setColors(tint.toFloatBits());
    }

    public void setColors(float r, float g, float b, float a) {
        int intBits = (int)(255.0f * a) << 24 | (int)(255.0f * b) << 16 | (int)(255.0f * g) << 8 | (int)(255.0f * r);
        this.setColors(NumberUtils.intToFloatColor(intBits));
    }

    public void setColors(Color tint, int start, int end) {
        this.setColors(tint.toFloatBits(), start, end);
    }

    public void setColors(float color, int start, int end) {
        if (this.pageVertices.length == 1) {
            float[] vertices = this.pageVertices[0];
            int n = Math.min(end * 20, this.idx[0]);
            for (int i = start * 20 + 2; i < n; i += 5) {
                vertices[i] = color;
            }
            return;
        }
        int pageCount = this.pageVertices.length;
        for (int i = 0; i < pageCount; ++i) {
            int glyphIndex;
            float[] vertices = this.pageVertices[i];
            IntArray glyphIndices = this.pageGlyphIndices[i];
            int n = glyphIndices.size;
            for (int j = 0; j < n && (glyphIndex = glyphIndices.items[j]) < end; ++j) {
                if (glyphIndex < start) continue;
                int offset = j * 20 + 2;
                vertices[offset] = color;
                vertices[offset + 5] = color;
                vertices[offset + 10] = color;
                vertices[offset + 15] = color;
            }
        }
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color.set(color);
    }

    public void setColor(float r, float g, float b, float a) {
        this.color.set(r, g, b, a);
    }

    public void draw(Batch spriteBatch) {
        Array<TextureRegion> regions = this.font.getRegions();
        int n = this.pageVertices.length;
        for (int j = 0; j < n; ++j) {
            if (this.idx[j] <= 0) continue;
            float[] vertices = this.pageVertices[j];
            spriteBatch.draw(regions.get(j).getTexture(), vertices, 0, this.idx[j]);
        }
    }

    public void draw(Batch spriteBatch, int start, int end) {
        if (this.pageVertices.length == 1) {
            spriteBatch.draw(this.font.getRegion().getTexture(), this.pageVertices[0], start * 20, (end - start) * 20);
            return;
        }
        Array<TextureRegion> regions = this.font.getRegions();
        int pageCount = this.pageVertices.length;
        for (int i = 0; i < pageCount; ++i) {
            int glyphIndex;
            int offset = -1;
            int count = 0;
            IntArray glyphIndices = this.pageGlyphIndices[i];
            int n = glyphIndices.size;
            for (int ii = 0; ii < n && (glyphIndex = glyphIndices.get(ii)) < end; ++ii) {
                if (offset == -1 && glyphIndex >= start) {
                    offset = ii;
                }
                if (glyphIndex < start) continue;
                ++count;
            }
            if (offset == -1 || count == 0) continue;
            spriteBatch.draw(regions.get(i).getTexture(), this.pageVertices[i], offset * 20, count * 20);
        }
    }

    public void draw(Batch spriteBatch, float alphaModulation) {
        if (alphaModulation == 1.0f) {
            this.draw(spriteBatch);
            return;
        }
        Color color = this.getColor();
        float oldAlpha = color.a;
        color.a *= alphaModulation;
        this.setColors(color);
        this.draw(spriteBatch);
        color.a = oldAlpha;
        this.setColors(color);
    }

    public void clear() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.pooledLayouts.flush();
        this.layouts.clear();
        int n = this.idx.length;
        for (int i = 0; i < n; ++i) {
            if (this.pageGlyphIndices != null) {
                this.pageGlyphIndices[i].clear();
            }
            this.idx[i] = 0;
        }
    }

    private void requireGlyphs(GlyphLayout layout) {
        if (this.pageVertices.length == 1) {
            this.requirePageGlyphs(0, layout.glyphCount);
        } else {
            int i;
            int[] tempGlyphCount = this.tempGlyphCount;
            Arrays.fill(tempGlyphCount, 0);
            int n = layout.runs.size;
            for (i = 0; i < n; ++i) {
                Array<BitmapFont.Glyph> glyphs = layout.runs.get((int)i).glyphs;
                T[] glyphItems = glyphs.items;
                int nn = glyphs.size;
                for (int ii = 0; ii < nn; ++ii) {
                    int n2 = ((BitmapFont.Glyph)glyphItems[ii]).page;
                    tempGlyphCount[n2] = tempGlyphCount[n2] + 1;
                }
            }
            n = tempGlyphCount.length;
            for (i = 0; i < n; ++i) {
                this.requirePageGlyphs(i, tempGlyphCount[i]);
            }
        }
    }

    private void requirePageGlyphs(int page, int glyphCount) {
        if (this.pageGlyphIndices != null && glyphCount > this.pageGlyphIndices[page].items.length) {
            this.pageGlyphIndices[page].ensureCapacity(glyphCount - this.pageGlyphIndices[page].size);
        }
        int vertexCount = this.idx[page] + glyphCount * 20;
        float[] vertices = this.pageVertices[page];
        if (vertices == null) {
            this.pageVertices[page] = new float[vertexCount];
        } else if (vertices.length < vertexCount) {
            float[] newVertices = new float[vertexCount];
            System.arraycopy(vertices, 0, newVertices, 0, this.idx[page]);
            this.pageVertices[page] = newVertices;
        }
    }

    private void setPageCount(int pageCount) {
        float[][] newPageVertices = new float[pageCount][];
        System.arraycopy(this.pageVertices, 0, newPageVertices, 0, this.pageVertices.length);
        this.pageVertices = newPageVertices;
        int[] newIdx = new int[pageCount];
        System.arraycopy(this.idx, 0, newIdx, 0, this.idx.length);
        this.idx = newIdx;
        IntArray[] newPageGlyphIndices = new IntArray[pageCount];
        int pageGlyphIndicesLength = 0;
        if (this.pageGlyphIndices != null) {
            pageGlyphIndicesLength = this.pageGlyphIndices.length;
            System.arraycopy(this.pageGlyphIndices, 0, newPageGlyphIndices, 0, this.pageGlyphIndices.length);
        }
        for (int i = pageGlyphIndicesLength; i < pageCount; ++i) {
            newPageGlyphIndices[i] = new IntArray();
        }
        this.pageGlyphIndices = newPageGlyphIndices;
        this.tempGlyphCount = new int[pageCount];
    }

    private void addToCache(GlyphLayout layout, float x, float y) {
        int runCount = layout.runs.size;
        if (runCount == 0) {
            return;
        }
        if (this.pageVertices.length < this.font.regions.size) {
            this.setPageCount(this.font.regions.size);
        }
        this.layouts.add(layout);
        this.requireGlyphs(layout);
        IntArray colors = layout.colors;
        int colorsIndex = 0;
        int nextColorGlyphIndex = 0;
        int glyphIndex = 0;
        float lastColorFloatBits = 0.0f;
        for (int i = 0; i < runCount; ++i) {
            GlyphLayout.GlyphRun run = layout.runs.get(i);
            T[] glyphs = run.glyphs.items;
            float[] xAdvances = run.xAdvances.items;
            float gx = x + run.x;
            float gy = y + run.y;
            int nn = run.glyphs.size;
            for (int ii = 0; ii < nn; ++ii) {
                if (glyphIndex++ == nextColorGlyphIndex) {
                    lastColorFloatBits = NumberUtils.intToFloatColor(colors.get(++colorsIndex));
                    nextColorGlyphIndex = ++colorsIndex < colors.size ? colors.get(colorsIndex) : -1;
                }
                this.addGlyph((BitmapFont.Glyph)glyphs[ii], gx += xAdvances[ii], gy, lastColorFloatBits);
            }
        }
        this.currentTint = Color.WHITE_FLOAT_BITS;
    }

    private void addGlyph(BitmapFont.Glyph glyph, float x, float y, float color) {
        float scaleX = this.font.data.scaleX;
        float scaleY = this.font.data.scaleY;
        x += (float)glyph.xoffset * scaleX;
        y += (float)glyph.yoffset * scaleY;
        float width = (float)glyph.width * scaleX;
        float height = (float)glyph.height * scaleY;
        float u = glyph.u;
        float u2 = glyph.u2;
        float v = glyph.v;
        float v2 = glyph.v2;
        if (this.integer) {
            x = Math.round(x);
            y = Math.round(y);
            width = Math.round(width);
            height = Math.round(height);
        }
        float x2 = x + width;
        float y2 = y + height;
        int page = glyph.page;
        int idx = this.idx[page];
        int n = page;
        this.idx[n] = this.idx[n] + 20;
        if (this.pageGlyphIndices != null) {
            this.pageGlyphIndices[page].add(this.glyphCount++);
        }
        float[] vertices = this.pageVertices[page];
        vertices[idx++] = x;
        vertices[idx++] = y;
        vertices[idx++] = color;
        vertices[idx++] = u;
        vertices[idx++] = v;
        vertices[idx++] = x;
        vertices[idx++] = y2;
        vertices[idx++] = color;
        vertices[idx++] = u;
        vertices[idx++] = v2;
        vertices[idx++] = x2;
        vertices[idx++] = y2;
        vertices[idx++] = color;
        vertices[idx++] = u2;
        vertices[idx++] = v2;
        vertices[idx++] = x2;
        vertices[idx++] = y;
        vertices[idx++] = color;
        vertices[idx++] = u2;
        vertices[idx] = v;
    }

    public GlyphLayout setText(CharSequence str, float x, float y) {
        this.clear();
        return this.addText(str, x, y, 0, str.length(), 0.0f, 8, false);
    }

    public GlyphLayout setText(CharSequence str, float x, float y, float targetWidth, int halign, boolean wrap) {
        this.clear();
        return this.addText(str, x, y, 0, str.length(), targetWidth, halign, wrap);
    }

    public GlyphLayout setText(CharSequence str, float x, float y, int start, int end, float targetWidth, int halign, boolean wrap) {
        this.clear();
        return this.addText(str, x, y, start, end, targetWidth, halign, wrap);
    }

    public GlyphLayout setText(CharSequence str, float x, float y, int start, int end, float targetWidth, int halign, boolean wrap, String truncate) {
        this.clear();
        return this.addText(str, x, y, start, end, targetWidth, halign, wrap, truncate);
    }

    public void setText(GlyphLayout layout, float x, float y) {
        this.clear();
        this.addText(layout, x, y);
    }

    public GlyphLayout addText(CharSequence str, float x, float y) {
        return this.addText(str, x, y, 0, str.length(), 0.0f, 8, false, null);
    }

    public GlyphLayout addText(CharSequence str, float x, float y, float targetWidth, int halign, boolean wrap) {
        return this.addText(str, x, y, 0, str.length(), targetWidth, halign, wrap, null);
    }

    public GlyphLayout addText(CharSequence str, float x, float y, int start, int end, float targetWidth, int halign, boolean wrap) {
        return this.addText(str, x, y, start, end, targetWidth, halign, wrap, null);
    }

    public GlyphLayout addText(CharSequence str, float x, float y, int start, int end, float targetWidth, int halign, boolean wrap, String truncate) {
        GlyphLayout layout = this.pooledLayouts.obtain();
        layout.setText(this.font, str, start, end, this.color, targetWidth, halign, wrap, truncate);
        this.addText(layout, x, y);
        return layout;
    }

    public void addText(GlyphLayout layout, float x, float y) {
        this.addToCache(layout, x, y + this.font.data.ascent);
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public BitmapFont getFont() {
        return this.font;
    }

    public void setUseIntegerPositions(boolean use) {
        this.integer = use;
    }

    public boolean usesIntegerPositions() {
        return this.integer;
    }

    public int getPageCount() {
        return this.pageVertices.length;
    }

    public float[] getVertices() {
        return this.getVertices(0);
    }

    public float[] getVertices(int page) {
        return this.pageVertices[page];
    }

    public int getVertexCount(int page) {
        return this.idx[page];
    }

    public Array<GlyphLayout> getLayouts() {
        return this.layouts;
    }
}

