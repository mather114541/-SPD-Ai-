/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Styleable;
import com.badlogic.gdx.scenes.scene2d.ui.Tooltip;
import com.badlogic.gdx.scenes.scene2d.ui.TooltipManager;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Null;

public class TextTooltip
extends Tooltip<Label>
implements Styleable<TextTooltipStyle> {
    private TextTooltipStyle style;

    public TextTooltip(@Null String text, Skin skin) {
        this(text, TooltipManager.getInstance(), skin.get(TextTooltipStyle.class));
    }

    public TextTooltip(@Null String text, Skin skin, String styleName) {
        this(text, TooltipManager.getInstance(), skin.get(styleName, TextTooltipStyle.class));
    }

    public TextTooltip(@Null String text, TextTooltipStyle style) {
        this(text, TooltipManager.getInstance(), style);
    }

    public TextTooltip(@Null String text, TooltipManager manager, Skin skin) {
        this(text, manager, skin.get(TextTooltipStyle.class));
    }

    public TextTooltip(@Null String text, TooltipManager manager, Skin skin, String styleName) {
        this(text, manager, skin.get(styleName, TextTooltipStyle.class));
    }

    public TextTooltip(@Null String text, TooltipManager manager, TextTooltipStyle style) {
        super(null, manager);
        this.container.setActor(this.newLabel(text, style.label));
        this.setStyle(style);
    }

    protected Label newLabel(String text, Label.LabelStyle style) {
        return new Label((CharSequence)text, style);
    }

    @Override
    public void setStyle(TextTooltipStyle style) {
        if (style == null) {
            throw new NullPointerException("style cannot be null");
        }
        this.style = style;
        this.container.setBackground(style.background);
        this.container.maxWidth(style.wrapWidth);
        boolean wrap = style.wrapWidth != 0.0f;
        this.container.fill(wrap);
        Label label = (Label)this.container.getActor();
        label.setStyle(style.label);
        label.setWrap(wrap);
    }

    @Override
    public TextTooltipStyle getStyle() {
        return this.style;
    }

    public static class TextTooltipStyle {
        public Label.LabelStyle label;
        @Null
        public Drawable background;
        public float wrapWidth;

        public TextTooltipStyle() {
        }

        public TextTooltipStyle(Label.LabelStyle label, @Null Drawable background) {
            this.label = label;
            this.background = background;
        }

        public TextTooltipStyle(TextTooltipStyle style) {
            this.label = new Label.LabelStyle(style.label);
            this.background = style.background;
            this.wrapWidth = style.wrapWidth;
        }
    }
}

