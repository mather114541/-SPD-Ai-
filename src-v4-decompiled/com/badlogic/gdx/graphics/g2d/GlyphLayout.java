/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DefaultPool;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Null;
import com.badlogic.gdx.utils.Pool;

public class GlyphLayout
implements Pool.Poolable {
    private static final Pool<GlyphRun> glyphRunPool = new DefaultPool<GlyphRun>(GlyphRun::new);
    private static final IntArray colorStack = new IntArray(4);
    private static final float epsilon = 1.0E-4f;
    public final Array<GlyphRun> runs = new Array(1);
    public final IntArray colors = new IntArray(2);
    public int glyphCount;
    public float width;
    public float height;

    public GlyphLayout() {
    }

    public GlyphLayout(BitmapFont font, CharSequence str) {
        this.setText(font, str);
    }

    public GlyphLayout(BitmapFont font, CharSequence str, Color color, float targetWidth, int halign, boolean wrap) {
        this.setText(font, str, color, targetWidth, halign, wrap);
    }

    public GlyphLayout(BitmapFont font, CharSequence str, int start, int end, Color color, float targetWidth, int halign, boolean wrap, String truncate) {
        this.setText(font, str, start, end, color, targetWidth, halign, wrap, truncate);
    }

    public void setText(BitmapFont font, CharSequence str) {
        this.setText(font, str, 0, str.length(), font.getColor(), 0.0f, 8, false, null);
    }

    public void setText(BitmapFont font, CharSequence str, Color color, float targetWidth, int halign, boolean wrap) {
        this.setText(font, str, 0, str.length(), color, targetWidth, halign, wrap, null);
    }

    public void setText(BitmapFont font, CharSequence str, int start, int end, Color color, float targetWidth, int halign, boolean wrap, @Null String truncate) {
        int currentColor;
        this.reset();
        BitmapFont.BitmapFontData fontData = font.data;
        if (start == end) {
            this.height = fontData.capHeight;
            return;
        }
        if (wrap) {
            targetWidth = Math.max(targetWidth, fontData.spaceXadvance * 3.0f);
        }
        boolean wrapOrTruncate = wrap || truncate != null;
        int nextColor = currentColor = color.toIntBits();
        this.colors.add(0, currentColor);
        boolean markupEnabled = fontData.markupEnabled;
        if (markupEnabled) {
            colorStack.add(currentColor);
        }
        boolean isLastRun = false;
        float y = 0.0f;
        float down = fontData.down;
        GlyphRun lineRun = null;
        BitmapFont.Glyph lastGlyph = null;
        int runStart = start;
        block4: while (true) {
            int runEnd;
            boolean newline;
            block30: {
                block29: {
                    GlyphRun run;
                    block28: {
                        newline = false;
                        if (start == end) {
                            if (runStart == end) break;
                            runEnd = end;
                            isLastRun = true;
                        } else {
                            switch (str.charAt(start++)) {
                                case '\n': {
                                    runEnd = start - 1;
                                    newline = true;
                                    break;
                                }
                                case '[': {
                                    if (markupEnabled) {
                                        int length = this.parseColorMarkup(str, start, end);
                                        if (length >= 0) {
                                            runEnd = start - 1;
                                            if ((start += length + 1) == end) {
                                                isLastRun = true;
                                                break;
                                            }
                                            nextColor = colorStack.peek();
                                            break;
                                        }
                                        if (length == -2) {
                                            ++start;
                                        }
                                    }
                                }
                                default: {
                                    continue block4;
                                }
                            }
                        }
                        run = glyphRunPool.obtain();
                        run.x = 0.0f;
                        run.y = y;
                        fontData.getGlyphs(run, str, runStart, runEnd, lastGlyph);
                        this.glyphCount += run.glyphs.size;
                        if (nextColor != currentColor) {
                            if (this.colors.get(this.colors.size - 2) == this.glyphCount) {
                                this.colors.set(this.colors.size - 1, nextColor);
                            } else {
                                this.colors.add(this.glyphCount);
                                this.colors.add(nextColor);
                            }
                            currentColor = nextColor;
                        }
                        if (run.glyphs.size != 0) break block28;
                        glyphRunPool.free(run);
                        if (lineRun != null) break block29;
                        break block30;
                    }
                    if (lineRun == null) {
                        lineRun = run;
                        this.runs.add(lineRun);
                    } else {
                        lineRun.appendRun(run);
                        glyphRunPool.free(run);
                    }
                }
                if (newline || isLastRun) {
                    this.setLastGlyphXAdvance(fontData, lineRun);
                    lastGlyph = null;
                } else {
                    lastGlyph = lineRun.glyphs.peek();
                }
                if (wrapOrTruncate && lineRun.glyphs.size != 0 && (newline || isLastRun)) {
                    float runWidth = lineRun.xAdvances.first() + lineRun.xAdvances.get(1);
                    for (int i = 2; i < lineRun.xAdvances.size; ++i) {
                        BitmapFont.Glyph glyph = lineRun.glyphs.get(i - 1);
                        float glyphWidth = this.getGlyphWidth(glyph, fontData);
                        if (runWidth + glyphWidth - 1.0E-4f <= targetWidth) {
                            runWidth += lineRun.xAdvances.items[i];
                            continue;
                        }
                        if (truncate != null) {
                            this.truncate(fontData, lineRun, targetWidth, truncate);
                            break block4;
                        }
                        int wrapIndex = fontData.getWrapIndex(lineRun.glyphs, i);
                        if (wrapIndex == 0 && lineRun.x == 0.0f || wrapIndex >= lineRun.glyphs.size) {
                            wrapIndex = i - 1;
                        }
                        if ((lineRun = this.wrap(fontData, lineRun, wrapIndex)) == null) break;
                        this.runs.add(lineRun);
                        lineRun.x = 0.0f;
                        lineRun.y = y += down;
                        runWidth = lineRun.xAdvances.first() + lineRun.xAdvances.get(1);
                        i = 1;
                    }
                }
            }
            if (newline) {
                lineRun = null;
                lastGlyph = null;
                y = runEnd == runStart ? (y += down * fontData.blankLineScale) : (y += down);
            }
            runStart = start;
        }
        this.height = fontData.capHeight + Math.abs(y);
        this.calculateWidths(fontData);
        this.alignRuns(targetWidth, halign);
        if (markupEnabled) {
            colorStack.clear();
        }
    }

    private void calculateWidths(BitmapFont.BitmapFontData fontData) {
        float width = 0.0f;
        T[] runsItems = this.runs.items;
        int n = this.runs.size;
        for (int i = 0; i < n; ++i) {
            GlyphRun run = (GlyphRun)runsItems[i];
            float[] xAdvances = run.xAdvances.items;
            float runWidth = run.x + xAdvances[0];
            float max = 0.0f;
            T[] glyphs = run.glyphs.items;
            int ii = 0;
            int nn = run.glyphs.size;
            while (ii < nn) {
                BitmapFont.Glyph glyph = (BitmapFont.Glyph)glyphs[ii];
                float glyphWidth = this.getGlyphWidth(glyph, fontData);
                max = Math.max(max, runWidth + glyphWidth);
                runWidth += xAdvances[++ii];
            }
            run.width = Math.max(runWidth, max) - run.x;
            width = Math.max(width, run.x + run.width);
        }
        this.width = width;
    }

    private void alignRuns(float targetWidth, int halign) {
        if ((halign & 8) == 0) {
            boolean center = (halign & 1) != 0;
            T[] runsItems = this.runs.items;
            int n = this.runs.size;
            for (int i = 0; i < n; ++i) {
                GlyphRun run = (GlyphRun)runsItems[i];
                run.x = run.x + (center ? 0.5f * (targetWidth - run.width) : targetWidth - run.width);
            }
        }
    }

    private void truncate(BitmapFont.BitmapFontData fontData, GlyphRun run, float targetWidth, String truncate) {
        int droppedGlyphCount;
        float xAdvance;
        int count;
        int glyphCount = run.glyphs.size;
        GlyphRun truncateRun = glyphRunPool.obtain();
        fontData.getGlyphs(truncateRun, truncate, 0, truncate.length(), null);
        float truncateWidth = 0.0f;
        if (truncateRun.xAdvances.size > 0) {
            this.setLastGlyphXAdvance(fontData, truncateRun);
            float[] xAdvances = truncateRun.xAdvances.items;
            int n = truncateRun.xAdvances.size;
            for (int i = 1; i < n; ++i) {
                truncateWidth += xAdvances[i];
            }
        }
        targetWidth -= truncateWidth;
        float width = run.x;
        float[] xAdvances = run.xAdvances.items;
        for (count = 0; count < run.xAdvances.size && !((width += (xAdvance = xAdvances[count])) > targetWidth); ++count) {
        }
        if (count > 1) {
            run.glyphs.truncate(count - 1);
            run.xAdvances.truncate(count);
            this.setLastGlyphXAdvance(fontData, run);
            if (truncateRun.xAdvances.size > 0) {
                run.xAdvances.addAll(truncateRun.xAdvances, 1, truncateRun.xAdvances.size - 1);
            }
        } else {
            run.glyphs.clear();
            run.xAdvances.clear();
            run.xAdvances.addAll(truncateRun.xAdvances);
        }
        if ((droppedGlyphCount = glyphCount - run.glyphs.size) > 0) {
            this.glyphCount -= droppedGlyphCount;
            if (fontData.markupEnabled) {
                while (this.colors.size > 2 && this.colors.get(this.colors.size - 2) >= this.glyphCount) {
                    this.colors.size -= 2;
                }
            }
        }
        run.glyphs.addAll(truncateRun.glyphs);
        this.glyphCount += truncate.length();
        glyphRunPool.free(truncateRun);
    }

    private GlyphRun wrap(BitmapFont.BitmapFontData fontData, GlyphRun first, int wrapIndex) {
        int secondStart;
        int firstEnd;
        Array<BitmapFont.Glyph> glyphs2 = first.glyphs;
        int glyphCount = first.glyphs.size;
        FloatArray xAdvances2 = first.xAdvances;
        for (firstEnd = wrapIndex; firstEnd > 0 && fontData.isWhitespace((char)glyphs2.get((int)(firstEnd - 1)).id); --firstEnd) {
        }
        for (secondStart = wrapIndex; secondStart < glyphCount && fontData.isWhitespace((char)glyphs2.get((int)secondStart).id); ++secondStart) {
        }
        GlyphRun second = null;
        if (secondStart < glyphCount) {
            second = glyphRunPool.obtain();
            Array<BitmapFont.Glyph> glyphs1 = second.glyphs;
            glyphs1.addAll(glyphs2, 0, firstEnd);
            glyphs2.removeRange(0, secondStart - 1);
            first.glyphs = glyphs1;
            second.glyphs = glyphs2;
            FloatArray xAdvances1 = second.xAdvances;
            xAdvances1.addAll(xAdvances2, 0, firstEnd + 1);
            xAdvances2.removeRange(1, secondStart);
            xAdvances2.items[0] = this.getLineOffset(glyphs2, fontData);
            first.xAdvances = xAdvances1;
            second.xAdvances = xAdvances2;
            int firstGlyphCount = first.glyphs.size;
            int secondGlyphCount = second.glyphs.size;
            int droppedGlyphCount = glyphCount - firstGlyphCount - secondGlyphCount;
            this.glyphCount -= droppedGlyphCount;
            if (fontData.markupEnabled && droppedGlyphCount > 0) {
                int colorChangeIndex;
                int reductionThreshold = this.glyphCount - secondGlyphCount;
                for (int i = this.colors.size - 2; i >= 2 && (colorChangeIndex = this.colors.get(i)) > reductionThreshold; i -= 2) {
                    this.colors.set(i, colorChangeIndex - droppedGlyphCount);
                }
            }
        } else {
            glyphs2.truncate(firstEnd);
            xAdvances2.truncate(firstEnd + 1);
            int droppedGlyphCount = secondStart - firstEnd;
            if (droppedGlyphCount > 0) {
                this.glyphCount -= droppedGlyphCount;
                if (fontData.markupEnabled && this.colors.get(this.colors.size - 2) > this.glyphCount) {
                    int lastColor = this.colors.peek();
                    while (this.colors.get(this.colors.size - 2) > this.glyphCount) {
                        this.colors.size -= 2;
                    }
                    this.colors.set(this.colors.size - 2, this.glyphCount);
                    this.colors.set(this.colors.size - 1, lastColor);
                }
            }
        }
        if (firstEnd == 0) {
            glyphRunPool.free(first);
            this.runs.pop();
        } else {
            this.setLastGlyphXAdvance(fontData, first);
        }
        return second;
    }

    private void setLastGlyphXAdvance(BitmapFont.BitmapFontData fontData, GlyphRun run) {
        BitmapFont.Glyph last = run.glyphs.peek();
        if (!last.fixedWidth) {
            run.xAdvances.items[run.xAdvances.size - 1] = this.getGlyphWidth(last, fontData);
        }
    }

    private float getGlyphWidth(BitmapFont.Glyph glyph, BitmapFont.BitmapFontData fontData) {
        return (float)(glyph.fixedWidth ? glyph.xadvance : glyph.width + glyph.xoffset) * fontData.scaleX - fontData.padRight;
    }

    private float getLineOffset(Array<BitmapFont.Glyph> glyphs, BitmapFont.BitmapFontData fontData) {
        BitmapFont.Glyph first = glyphs.first();
        return (first.fixedWidth ? 0.0f : (float)(-first.xoffset) * fontData.scaleX) - fontData.padLeft;
    }

    private int parseColorMarkup(CharSequence str, int start, int end) {
        if (start == end) {
            return -1;
        }
        switch (str.charAt(start)) {
            case '#': {
                int color = 0;
                for (int i = start + 1; i < end; ++i) {
                    char ch = str.charAt(i);
                    if (ch == ']') {
                        if (i < start + 2 || i > start + 9) break;
                        if (i - start < 8) {
                            color = color << (9 - (i - start) << 2) | 0xFF;
                        }
                        colorStack.add(Integer.reverseBytes(color));
                        return i - start;
                    }
                    color = (color << 4) + ch;
                    if (ch >= '0' && ch <= '9') {
                        color -= 48;
                        continue;
                    }
                    if (ch >= 'A' && ch <= 'F') {
                        color -= 55;
                        continue;
                    }
                    if (ch < 'a' || ch > 'f') break;
                    color -= 87;
                }
                return -1;
            }
            case '[': {
                return -2;
            }
            case ']': {
                if (GlyphLayout.colorStack.size > 1) {
                    colorStack.pop();
                }
                return 0;
            }
        }
        for (int i = start + 1; i < end; ++i) {
            char ch = str.charAt(i);
            if (ch != ']') continue;
            Color color = Colors.get(str.subSequence(start, i).toString());
            if (color == null) {
                return -1;
            }
            colorStack.add(color.toIntBits());
            return i - start;
        }
        return -1;
    }

    @Override
    public void reset() {
        glyphRunPool.freeAll(this.runs);
        this.runs.clear();
        this.colors.clear();
        this.glyphCount = 0;
        this.width = 0.0f;
        this.height = 0.0f;
    }

    public String toString() {
        if (this.runs.size == 0) {
            return "";
        }
        StringBuilder buffer = new StringBuilder(128);
        buffer.append(this.width);
        buffer.append('x');
        buffer.append(this.height);
        buffer.append('\n');
        int n = this.runs.size;
        for (int i = 0; i < n; ++i) {
            buffer.append(this.runs.get(i).toString());
            buffer.append('\n');
        }
        buffer.setLength(buffer.length() - 1);
        return buffer.toString();
    }

    public static class GlyphRun
    implements Pool.Poolable {
        public Array<BitmapFont.Glyph> glyphs = new Array();
        public FloatArray xAdvances = new FloatArray();
        public float x;
        public float y;
        public float width;

        void appendRun(GlyphRun run) {
            this.glyphs.addAll(run.glyphs);
            if (this.xAdvances.notEmpty()) {
                --this.xAdvances.size;
            }
            this.xAdvances.addAll(run.xAdvances);
        }

        @Override
        public void reset() {
            this.glyphs.clear();
            this.xAdvances.clear();
        }

        public String toString() {
            StringBuilder buffer = new StringBuilder(this.glyphs.size + 32);
            Array<BitmapFont.Glyph> glyphs = this.glyphs;
            int n = glyphs.size;
            for (int i = 0; i < n; ++i) {
                BitmapFont.Glyph g = glyphs.get(i);
                buffer.append((char)g.id);
            }
            buffer.append(", ");
            buffer.append(this.x);
            buffer.append(", ");
            buffer.append(this.y);
            buffer.append(", ");
            buffer.append(this.width);
            return buffer.toString();
        }
    }
}

