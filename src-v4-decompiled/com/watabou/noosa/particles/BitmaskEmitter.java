/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.noosa.particles;

import com.watabou.gltextures.SmartTexture;
import com.watabou.noosa.Image;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Random;
import com.watabou.utils.RectF;

public class BitmaskEmitter
extends Emitter {
    private SmartTexture map;
    private int mapW;
    private int mapH;

    public BitmaskEmitter(Image target) {
        this.target = target;
        this.map = target.texture;
        this.mapW = this.map.bitmap.getWidth();
        this.mapH = this.map.bitmap.getHeight();
    }

    @Override
    protected void emit(int index) {
        float y;
        float x;
        RectF frame = ((Image)this.target).frame();
        float ofsX = frame.left * (float)this.mapW;
        float ofsY = frame.top * (float)this.mapH;
        while ((this.map.bitmap.getPixel((int)((x = Random.Float(frame.width()) * (float)this.mapW) + ofsX), (int)((y = Random.Float(frame.height()) * (float)this.mapH) + ofsY)) & 0xFF) == 0) {
        }
        this.factory.emit(this, index, this.target.x + x * this.target.scale.x, this.target.y + y * this.target.scale.y);
    }
}

