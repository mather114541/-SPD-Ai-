/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.watabou.noosa.Game;
import com.watabou.noosa.RenderedText;
import com.watabou.noosa.ui.Component;
import java.util.ArrayList;

public class RenderedTextBlock
extends Component {
    private int maxWidth = Integer.MAX_VALUE;
    public int nLines;
    private static final RenderedText SPACE = new RenderedText();
    private static final RenderedText NEWLINE = new RenderedText();
    protected String text;
    protected String[] tokens = null;
    protected ArrayList<RenderedText> words = new ArrayList();
    protected boolean multiline = false;
    private int size;
    private float zoom;
    private int color = -1;
    private int hightlightColor = 0xFFFF44;
    private boolean highlightingEnabled = true;
    public static final int LEFT_ALIGN = 1;
    public static final int CENTER_ALIGN = 2;
    public static final int RIGHT_ALIGN = 3;
    private int alignment = 1;

    public RenderedTextBlock(int size) {
        this.size = size;
    }

    public RenderedTextBlock(String text, int size) {
        this.size = size;
        this.text(text);
    }

    public void text(String text) {
        this.text = text;
        if (text != null && !text.equals("")) {
            this.tokens = Game.platform.splitforTextBlock(text, this.multiline);
            this.build();
        }
    }

    public void tokens(String ... words) {
        StringBuilder fullText = new StringBuilder();
        for (String word : words) {
            fullText.append(word);
        }
        this.text = fullText.toString();
        this.tokens = words;
        this.build();
    }

    public void text(String text, int maxWidth) {
        this.maxWidth = maxWidth;
        this.multiline = true;
        this.text(text);
    }

    public String text() {
        return this.text;
    }

    public void maxWidth(int maxWidth) {
        if (this.maxWidth != maxWidth) {
            this.maxWidth = maxWidth;
            this.multiline = true;
            this.text(this.text);
        }
    }

    public int maxWidth() {
        return this.maxWidth;
    }

    private synchronized void build() {
        if (this.tokens == null) {
            return;
        }
        this.clear();
        this.words = new ArrayList();
        boolean highlighting = false;
        for (String str : this.tokens) {
            if ((str.equals("_") || str.equals("**")) && this.highlightingEnabled) {
                highlighting = !highlighting;
                continue;
            }
            if (str.equals("\n")) {
                this.words.add(NEWLINE);
                continue;
            }
            if (str.equals(" ")) {
                this.words.add(SPACE);
                continue;
            }
            RenderedText word = new RenderedText(str, this.size);
            if (highlighting) {
                word.hardlight(this.hightlightColor);
            } else if (this.color != -1) {
                word.hardlight(this.color);
            }
            word.scale.set(this.zoom);
            this.words.add(word);
            this.add(word);
            if (!(this.height < word.height())) continue;
            this.height = word.height();
        }
        this.layout();
    }

    public synchronized void zoom(float zoom) {
        this.zoom = zoom;
        for (RenderedText word : this.words) {
            if (word == null) continue;
            word.scale.set(zoom);
        }
        this.layout();
    }

    public synchronized void hardlight(int color) {
        this.color = color;
        for (RenderedText word : this.words) {
            if (word == null) continue;
            word.hardlight(color);
        }
    }

    public synchronized void resetColor() {
        this.color = -1;
        for (RenderedText word : this.words) {
            if (word == null) continue;
            word.resetColor();
        }
    }

    public synchronized void alpha(float value) {
        for (RenderedText word : this.words) {
            if (word == null) continue;
            word.alpha(value);
        }
    }

    public synchronized void setHightlighting(boolean enabled) {
        this.setHightlighting(enabled, 0xFFFF44);
    }

    public synchronized void setHightlighting(boolean enabled, int color) {
        if (enabled != this.highlightingEnabled || color != this.hightlightColor) {
            this.hightlightColor = color;
            this.highlightingEnabled = enabled;
            this.build();
        }
    }

    public synchronized void invert() {
        if (this.words != null) {
            for (RenderedText word : this.words) {
                if (word == null) continue;
                word.ra = 0.77f;
                word.ga = 0.73f;
                word.ba = 0.62f;
                word.rm = -0.77f;
                word.gm = -0.73f;
                word.bm = -0.62f;
            }
        }
    }

    public synchronized void align(int align) {
        this.alignment = align;
        this.layout();
    }

    @Override
    protected synchronized void layout() {
        super.layout();
        float x = this.x;
        float y = this.y;
        float height = 0.0f;
        this.nLines = 1;
        ArrayList lines = new ArrayList();
        ArrayList<RenderedText> curLine = new ArrayList<RenderedText>();
        lines.add(curLine);
        this.width = 0.0f;
        for (int i = 0; i < this.words.size(); ++i) {
            RenderedText renderedText = this.words.get(i);
            if (renderedText == SPACE) {
                x += 1.667f;
                continue;
            }
            if (renderedText == NEWLINE) {
                y += height + 2.0f;
                x = this.x;
                ++this.nLines;
                curLine = new ArrayList();
                lines.add(curLine);
                continue;
            }
            if (renderedText.height() > height) {
                height = renderedText.height();
            }
            float fullWidth = renderedText.width();
            for (int j = i + 1; Messages.lang() != Languages.CHI_SMPL && Messages.lang() != Languages.CHI_TRAD && Messages.lang() != Languages.JAPANESE && j < this.words.size() && this.words.get(j) != SPACE && this.words.get(j) != NEWLINE; ++j) {
                fullWidth += this.words.get(j).width() - 0.667f;
            }
            if (x - this.x + fullWidth - 0.001f > (float)this.maxWidth && !curLine.isEmpty()) {
                y += height + 2.0f;
                x = this.x;
                ++this.nLines;
                curLine = new ArrayList();
                lines.add(curLine);
            }
            renderedText.x = x;
            renderedText.y = y;
            PixelScene.align(renderedText);
            x += renderedText.width();
            curLine.add(renderedText);
            if (x - this.x > this.width) {
                this.width = x - this.x;
            }
            x -= 0.667f;
        }
        this.height = y - this.y + height;
        if (this.alignment != 1) {
            for (ArrayList arrayList : lines) {
                if (arrayList.size() == 0) continue;
                float lineWidth = ((RenderedText)arrayList.get(arrayList.size() - 1)).width() + ((RenderedText)arrayList.get((int)(arrayList.size() - 1))).x - this.x;
                if (this.alignment == 2) {
                    for (RenderedText text : arrayList) {
                        text.x += (this.width() - lineWidth) / 2.0f;
                        PixelScene.align(text);
                    }
                    continue;
                }
                if (this.alignment != 3) continue;
                for (RenderedText text : arrayList) {
                    text.x += this.width() - lineWidth;
                    PixelScene.align(text);
                }
            }
        }
    }
}

