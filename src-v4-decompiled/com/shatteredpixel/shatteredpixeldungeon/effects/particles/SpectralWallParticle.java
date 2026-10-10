/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects.particles;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.ColorMath;
import com.watabou.utils.GameMath;
import com.watabou.utils.Random;

public class SpectralWallParticle
extends PixelParticle {
    public static final Emitter.Factory FACTORY = new Emitter.Factory(){

        @Override
        public void emit(Emitter emitter, int index, float x, float y) {
            int type = 1 + (Dungeon.depth - 1) / 5;
            type = (int)GameMath.gate(1.0f, type, 5.0f);
            switch (type) {
                case 2: {
                    if (Random.Int(5) < 3) break;
                    return;
                }
                case 5: {
                    if (Random.Int(5) < 2) break;
                    return;
                }
            }
            ((SpectralWallParticle)emitter.recycle(SpectralWallParticle.class)).reset(x, y);
        }

        @Override
        public boolean lightMode() {
            return false;
        }
    };
    private int type = 0;
    private float xScale = 0.0f;
    private float yScale = 0.0f;

    public SpectralWallParticle() {
        this.lifespan = 3.0f;
        this.am = 0.6f;
    }

    public void reset(float x, float y) {
        this.revive();
        this.type = 1 + (Dungeon.depth - 1) / 5;
        this.type = (int)GameMath.gate(1.0f, this.type, 5.0f);
        this.x = x;
        this.y = y;
        this.left = this.lifespan;
        switch (this.type) {
            case 1: {
                this.x = Math.round(x / 7.0f) * 7;
                this.y = Math.round(y / 4.0f) * 4 - 6;
                this.x += (float)Math.round(this.y % 8.0f) / 4.0f - 1.0f;
                this.color(ColorMath.random(0xD4D4D4, 0xABABAB));
                this.xScale = 6.0f;
                this.yScale = 3.0f;
                break;
            }
            case 2: {
                this.x = Math.round(x / 7.0f) * 7;
                this.y = Math.round(y / 6.0f) * 6 - 6;
                this.x += (float)Math.round(this.y % 8.0f) / 4.0f - 1.0f;
                this.color(ColorMath.random(12893852, 10261117));
                this.xScale = 6.0f;
                this.yScale = 5.0f;
                break;
            }
            case 3: {
                this.y -= 6.0f;
                float colorScale = this.x % 16.0f / 16.0f + this.y % 16.0f / 16.0f;
                if (colorScale > 1.0f) {
                    colorScale = 2.0f - colorScale;
                }
                this.color(ColorMath.interpolate(12038309, 0x6A6662, colorScale));
                this.xScale = 5.0f;
                this.yScale = 5.0f;
                break;
            }
            case 4: {
                this.x = Math.round(x / 5.0f) * 5;
                this.y = Math.round(y / 5.0f) * 5 - 6;
                this.x += (float)Math.round(this.y % 16.0f) / 5.0f - 2.0f;
                this.color(ColorMath.interpolate(1.3679779E7f, 10718593));
                this.xScale = 4.0f;
                this.yScale = 4.0f;
                break;
            }
            case 5: {
                this.x = Math.round(x / 4.0f) * 4;
                this.y = Math.round((y + 8.0f) / 16.0f) * 16 - 14;
                this.color(ColorMath.interpolate(1.0654845E7f, 5851207));
                this.xScale = 4.0f;
                this.yScale = 20.0f;
            }
        }
    }

    @Override
    public void update() {
        super.update();
        this.scale.set(this.left / this.lifespan * this.xScale, this.left / this.lifespan * this.yScale);
    }
}

