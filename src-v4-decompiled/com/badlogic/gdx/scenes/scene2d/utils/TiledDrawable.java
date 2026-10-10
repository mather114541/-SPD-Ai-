/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

public class TiledDrawable
extends TextureRegionDrawable {
    private final Color color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
    private float scale = 1.0f;
    private int align = 12;

    public TiledDrawable() {
    }

    public TiledDrawable(TextureRegion region) {
        super(region);
    }

    public TiledDrawable(TextureRegionDrawable drawable) {
        super(drawable);
    }

    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        float oldColor = batch.getPackedColor();
        batch.setColor(batch.getColor().mul(this.color));
        TiledDrawable.draw(batch, this.getRegion(), x, y, width, height, this.scale, this.align);
        batch.setPackedColor(oldColor);
    }

    public static void draw(Batch batch, TextureRegion textureRegion, float x, float y, float width, float height, float scale, int align) {
        float bottomPartialHeight;
        float topPartialHeight;
        float rightPartialWidth;
        float leftPartialWidth;
        float regionWidth = (float)textureRegion.getRegionWidth() * scale;
        float regionHeight = (float)textureRegion.getRegionHeight() * scale;
        Texture texture = textureRegion.getTexture();
        float textureWidth = (float)texture.getWidth() * scale;
        float textureHeight = (float)texture.getHeight() * scale;
        float u = textureRegion.getU();
        float v = textureRegion.getV();
        float u2 = textureRegion.getU2();
        float v2 = textureRegion.getV2();
        int fullX = (int)(width / regionWidth);
        if (Align.isLeft(align)) {
            leftPartialWidth = 0.0f;
            rightPartialWidth = width - regionWidth * (float)fullX;
        } else if (Align.isRight(align)) {
            leftPartialWidth = width - regionWidth * (float)fullX;
            rightPartialWidth = 0.0f;
        } else if (fullX != 0) {
            float leftRight;
            fullX = fullX % 2 == 1 ? fullX : fullX - 1;
            leftPartialWidth = leftRight = 0.5f * (width - regionWidth * (float)fullX);
            rightPartialWidth = leftRight;
        } else {
            leftPartialWidth = 0.0f;
            rightPartialWidth = 0.0f;
        }
        int fullY = (int)(height / regionHeight);
        if (Align.isTop(align)) {
            topPartialHeight = 0.0f;
            bottomPartialHeight = height - regionHeight * (float)fullY;
        } else if (Align.isBottom(align)) {
            topPartialHeight = height - regionHeight * (float)fullY;
            bottomPartialHeight = 0.0f;
        } else if (fullY != 0) {
            float topBottom;
            fullY = fullY % 2 == 1 ? fullY : fullY - 1;
            topPartialHeight = topBottom = 0.5f * (height - regionHeight * (float)fullY);
            bottomPartialHeight = topBottom;
        } else {
            topPartialHeight = 0.0f;
            bottomPartialHeight = 0.0f;
        }
        float drawX = x;
        float drawY = y;
        if (leftPartialWidth > 0.0f) {
            float leftEdgeU = u2 - leftPartialWidth / textureWidth;
            if (bottomPartialHeight > 0.0f) {
                float leftBottomV = v + bottomPartialHeight / textureHeight;
                batch.draw(texture, drawX, drawY, leftPartialWidth, bottomPartialHeight, leftEdgeU, leftBottomV, u2, v);
                drawY += bottomPartialHeight;
            }
            if (fullY == 0 && Align.isCenterVertical(align)) {
                float vOffset = 0.5f * (v2 - v) * (1.0f - height / regionHeight);
                float leftCenterV = v2 - vOffset;
                float leftCenterV2 = v + vOffset;
                batch.draw(texture, drawX, drawY, leftPartialWidth, height, leftEdgeU, leftCenterV, u2, leftCenterV2);
                drawY += height;
            } else {
                for (int i = 0; i < fullY; ++i) {
                    batch.draw(texture, drawX, drawY, leftPartialWidth, regionHeight, leftEdgeU, v2, u2, v);
                    drawY += regionHeight;
                }
            }
            if (topPartialHeight > 0.0f) {
                float leftTopV = v2 - topPartialHeight / textureHeight;
                batch.draw(texture, drawX, drawY, leftPartialWidth, topPartialHeight, leftEdgeU, v2, u2, leftTopV);
            }
        }
        if (bottomPartialHeight > 0.0f) {
            drawX = x + leftPartialWidth;
            drawY = y;
            float centerBottomV = v + bottomPartialHeight / textureHeight;
            if (fullX == 0 && Align.isCenterHorizontal(align)) {
                float uOffset = 0.5f * (u2 - u) * (1.0f - width / regionWidth);
                float centerBottomU = u + uOffset;
                float centerBottomU2 = u2 - uOffset;
                batch.draw(texture, drawX, drawY, width, bottomPartialHeight, centerBottomU, centerBottomV, centerBottomU2, v);
                drawX += width;
            } else {
                for (int i = 0; i < fullX; ++i) {
                    batch.draw(texture, drawX, drawY, regionWidth, bottomPartialHeight, u, centerBottomV, u2, v);
                    drawX += regionWidth;
                }
            }
        }
        drawX = x + leftPartialWidth;
        int originalFullX = fullX;
        int originalFullY = fullY;
        float centerCenterDrawWidth = regionWidth;
        float centerCenterDrawHeight = regionHeight;
        float centerCenterU = u;
        float centerCenterU2 = u2;
        float centerCenterV = v2;
        float centerCenterV2 = v;
        if (fullX == 0 && Align.isCenterHorizontal(align)) {
            fullX = 1;
            centerCenterDrawWidth = width;
            float uOffset = 0.5f * (u2 - u) * (1.0f - width / regionWidth);
            centerCenterU = u + uOffset;
            centerCenterU2 = u2 - uOffset;
        }
        if (fullY == 0 && Align.isCenterVertical(align)) {
            fullY = 1;
            centerCenterDrawHeight = height;
            float vOffset = 0.5f * (v2 - v) * (1.0f - height / regionHeight);
            centerCenterV = v2 - vOffset;
            centerCenterV2 = v + vOffset;
        }
        for (int i = 0; i < fullX; ++i) {
            drawY = y + bottomPartialHeight;
            for (int ii = 0; ii < fullY; ++ii) {
                batch.draw(texture, drawX, drawY, centerCenterDrawWidth, centerCenterDrawHeight, centerCenterU, centerCenterV, centerCenterU2, centerCenterV2);
                drawY += centerCenterDrawHeight;
            }
            drawX += centerCenterDrawWidth;
        }
        fullX = originalFullX;
        fullY = originalFullY;
        if (topPartialHeight > 0.0f) {
            drawX = x + leftPartialWidth;
            float centerTopV = v2 - topPartialHeight / textureHeight;
            if (fullX == 0 && Align.isCenterHorizontal(align)) {
                float uOffset = 0.5f * (u2 - u) * (1.0f - width / regionWidth);
                float centerTopU = u + uOffset;
                float centerTopU2 = u2 - uOffset;
                batch.draw(texture, drawX, drawY, width, topPartialHeight, centerTopU, v2, centerTopU2, centerTopV);
                drawX += width;
            } else {
                for (int i = 0; i < fullX; ++i) {
                    batch.draw(texture, drawX, drawY, regionWidth, topPartialHeight, u, v2, u2, centerTopV);
                    drawX += regionWidth;
                }
            }
        }
        if (rightPartialWidth > 0.0f) {
            drawY = y;
            float rightEdgeU2 = u + rightPartialWidth / textureWidth;
            if (bottomPartialHeight > 0.0f) {
                float rightBottomV = v + bottomPartialHeight / textureHeight;
                batch.draw(texture, drawX, drawY, rightPartialWidth, bottomPartialHeight, u, rightBottomV, rightEdgeU2, v);
                drawY += bottomPartialHeight;
            }
            if (fullY == 0 && Align.isCenterVertical(align)) {
                float vOffset = 0.5f * (v2 - v) * (1.0f - height / regionHeight);
                float rightCenterV = v2 - vOffset;
                float rightCenterV2 = v + vOffset;
                batch.draw(texture, drawX, drawY, rightPartialWidth, height, u, rightCenterV, rightEdgeU2, rightCenterV2);
                drawY += height;
            } else {
                for (int i = 0; i < fullY; ++i) {
                    batch.draw(texture, drawX, drawY, rightPartialWidth, regionHeight, u, v2, rightEdgeU2, v);
                    drawY += regionHeight;
                }
            }
            if (topPartialHeight > 0.0f) {
                float rightTopV = v2 - topPartialHeight / textureHeight;
                batch.draw(texture, drawX, drawY, rightPartialWidth, topPartialHeight, u, v2, rightEdgeU2, rightTopV);
            }
        }
    }

    @Override
    public void draw(Batch batch, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation) {
        throw new UnsupportedOperationException();
    }

    public Color getColor() {
        return this.color;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getScale() {
        return this.scale;
    }

    public int getAlign() {
        return this.align;
    }

    public void setAlign(int align) {
        this.align = align;
    }

    @Override
    public TiledDrawable tint(Color tint) {
        TiledDrawable drawable = new TiledDrawable(this);
        drawable.color.set(tint);
        drawable.setLeftWidth(this.getLeftWidth());
        drawable.setRightWidth(this.getRightWidth());
        drawable.setTopHeight(this.getTopHeight());
        drawable.setBottomHeight(this.getBottomHeight());
        return drawable;
    }
}

