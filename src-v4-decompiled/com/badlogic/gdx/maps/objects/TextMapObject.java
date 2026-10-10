/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.maps.objects;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Rectangle;

public class TextMapObject
extends MapObject {
    private float rotation = 0.0f;
    private String text = "";
    private int pixelSize = 16;
    private String fontFamily = "";
    private boolean bold = false;
    private boolean italic = false;
    private boolean underline = false;
    private boolean strikeout = false;
    private boolean kerning = true;
    private boolean wrap = true;
    private String horizontalAlign = "left";
    private String verticalAlign = "top";
    private Rectangle rectangle;

    public TextMapObject() {
        this(0.0f, 0.0f, 1.0f, 1.0f, "");
    }

    public TextMapObject(float x, float y, float width, float height, String text) {
        this.rectangle = new Rectangle(x, y, width, height);
        this.text = text;
    }

    public Rectangle getRectangle() {
        return this.rectangle;
    }

    public float getX() {
        return this.rectangle.getX();
    }

    public float getY() {
        return this.rectangle.getY();
    }

    public float getWidth() {
        return this.rectangle.getWidth();
    }

    public float getHeight() {
        return this.rectangle.getHeight();
    }

    public float getRotation() {
        return this.rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getHorizontalAlign() {
        return this.horizontalAlign;
    }

    public void setHorizontalAlign(String horizontalAlign) {
        this.horizontalAlign = horizontalAlign;
    }

    public String getVerticalAlign() {
        return this.verticalAlign;
    }

    public void setVerticalAlign(String verticalAlign) {
        this.verticalAlign = verticalAlign;
    }

    public int getPixelSize() {
        return this.pixelSize;
    }

    public void setPixelSize(int pixelSize) {
        this.pixelSize = pixelSize;
    }

    public String getFontFamily() {
        return this.fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public boolean isBold() {
        return this.bold;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }

    public boolean isItalic() {
        return this.italic;
    }

    public void setItalic(boolean italic) {
        this.italic = italic;
    }

    public boolean isUnderline() {
        return this.underline;
    }

    public void setUnderline(boolean underline) {
        this.underline = underline;
    }

    public boolean isStrikeout() {
        return this.strikeout;
    }

    public void setStrikeout(boolean strikeout) {
        this.strikeout = strikeout;
    }

    public boolean isKerning() {
        return this.kerning;
    }

    public void setKerning(boolean kerning) {
        this.kerning = kerning;
    }

    public boolean isWrap() {
        return this.wrap;
    }

    public void setWrap(boolean wrap) {
        this.wrap = wrap;
    }
}

