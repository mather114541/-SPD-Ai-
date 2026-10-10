/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Null;
import com.badlogic.gdx.utils.Scaling;

public class CheckBox
extends TextButton {
    private Image image;
    private Cell imageCell;
    private CheckBoxStyle style;

    public CheckBox(@Null String text, Skin skin) {
        this(text, skin.get(CheckBoxStyle.class));
    }

    public CheckBox(@Null String text, Skin skin, String styleName) {
        this(text, skin.get(styleName, CheckBoxStyle.class));
    }

    public CheckBox(@Null String text, CheckBoxStyle style) {
        super(text, style);
        Label label = this.getLabel();
        label.setAlignment(8);
        this.image = this.newImage();
        this.image.setDrawable(style.checkboxOff);
        this.clearChildren();
        this.imageCell = this.add(this.image);
        this.add(label);
        this.setSize(this.getPrefWidth(), this.getPrefHeight());
    }

    protected Image newImage() {
        return new Image((Drawable)null, Scaling.none);
    }

    @Override
    public void setStyle(Button.ButtonStyle style) {
        if (!(style instanceof CheckBoxStyle)) {
            throw new IllegalArgumentException("style must be a CheckBoxStyle.");
        }
        this.style = (CheckBoxStyle)style;
        super.setStyle(style);
    }

    @Override
    public CheckBoxStyle getStyle() {
        return this.style;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        this.image.setDrawable(this.getImageDrawable());
        super.draw(batch, parentAlpha);
    }

    @Null
    protected Drawable getImageDrawable() {
        boolean over;
        if (this.isDisabled()) {
            if (this.isChecked && this.style.checkboxOnDisabled != null) {
                return this.style.checkboxOnDisabled;
            }
            return this.style.checkboxOffDisabled;
        }
        boolean bl = over = this.isOver() && !this.isDisabled();
        if (this.isChecked && this.style.checkboxOn != null) {
            return over && this.style.checkboxOnOver != null ? this.style.checkboxOnOver : this.style.checkboxOn;
        }
        if (over && this.style.checkboxOver != null) {
            return this.style.checkboxOver;
        }
        return this.style.checkboxOff;
    }

    public Image getImage() {
        return this.image;
    }

    public Cell getImageCell() {
        return this.imageCell;
    }

    public static class CheckBoxStyle
    extends TextButton.TextButtonStyle {
        public Drawable checkboxOn;
        public Drawable checkboxOff;
        @Null
        public Drawable checkboxOnOver;
        @Null
        public Drawable checkboxOver;
        @Null
        public Drawable checkboxOnDisabled;
        @Null
        public Drawable checkboxOffDisabled;

        public CheckBoxStyle() {
        }

        public CheckBoxStyle(Drawable checkboxOff, Drawable checkboxOn, BitmapFont font, @Null Color fontColor) {
            this.checkboxOff = checkboxOff;
            this.checkboxOn = checkboxOn;
            this.font = font;
            this.fontColor = fontColor;
        }

        public CheckBoxStyle(CheckBoxStyle style) {
            super(style);
            this.checkboxOff = style.checkboxOff;
            this.checkboxOn = style.checkboxOn;
            this.checkboxOnOver = style.checkboxOnOver;
            this.checkboxOver = style.checkboxOver;
            this.checkboxOnDisabled = style.checkboxOnDisabled;
            this.checkboxOffDisabled = style.checkboxOffDisabled;
        }
    }
}

