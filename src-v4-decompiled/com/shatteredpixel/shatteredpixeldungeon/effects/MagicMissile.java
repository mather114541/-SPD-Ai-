/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BloodParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.CorrosionParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PoisonParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PurpleParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.RainbowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Visual;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.Callback;
import com.watabou.utils.ColorMath;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class MagicMissile
extends Emitter {
    private static final float SPEED = 200.0f;
    private Callback callback;
    private PointF to;
    private float sx;
    private float sy;
    private float time;
    public static final int MAGIC_MISSILE = 0;
    public static final int FROST = 1;
    public static final int FIRE = 2;
    public static final int CORROSION = 3;
    public static final int FOLIAGE = 4;
    public static final int FORCE = 5;
    public static final int BEACON = 6;
    public static final int SHADOW = 7;
    public static final int RAINBOW = 8;
    public static final int EARTH = 9;
    public static final int WARD = 10;
    public static final int SHAMAN_RED = 11;
    public static final int SHAMAN_BLUE = 12;
    public static final int SHAMAN_PURPLE = 13;
    public static final int SHAMAN_ELMO = 14;
    public static final int ELMO = 15;
    public static final int POISON = 16;
    public static final int LIGHT_MISSILE = 17;
    public static final int MAGIC_MISS_CONE = 100;
    public static final int FROST_CONE = 101;
    public static final int FIRE_CONE = 102;
    public static final int CORROSION_CONE = 103;
    public static final int FOLIAGE_CONE = 104;
    public static final int FORCE_CONE = 105;
    public static final int SHADOW_CONE = 107;
    public static final int RAINBOW_CONE = 108;
    public static final int EARTH_CONE = 109;
    public static final int WARD_CONE = 110;
    public static final int PURPLE_CONE = 111;
    public static final int SPARK_CONE = 112;
    public static final int BLOOD_CONE = 113;
    public static final int POISON_CONE = 114;
    public static final int SPECK = 1000;

    public void reset(int type, int from, int to, Callback callback) {
        this.reset(type, DungeonTilemap.raisedTileCenterToWorld(from), DungeonTilemap.raisedTileCenterToWorld(to), callback);
    }

    public void reset(int type, Visual from, Visual to, Callback callback) {
        this.reset(type, from.center(), to.center(), callback);
    }

    public void reset(int type, Visual from, int to, Callback callback) {
        this.reset(type, from.center(), DungeonTilemap.raisedTileCenterToWorld(to), callback);
    }

    public void reset(int type, PointF from, PointF to, Callback callback) {
        this.callback = callback;
        this.to = to;
        this.x = from.x;
        this.y = from.y;
        this.width = 0.0f;
        this.height = 0.0f;
        PointF d = PointF.diff(to, from);
        PointF speed = new PointF(d).normalize().scale(200.0f);
        this.sx = speed.x;
        this.sy = speed.y;
        this.time = d.length() / 200.0f;
        if (type >= 1000) {
            this.size(10.0f);
            this.pour(Speck.factory(type - 1000), 0.02f);
            this.revive();
            return;
        }
        switch (type) {
            default: {
                this.size(4.0f);
                this.pour(WhiteParticle.FACTORY, 0.01f);
                break;
            }
            case 1: {
                this.pour(MagicParticle.FACTORY, 0.01f);
                break;
            }
            case 2: {
                this.size(4.0f);
                this.pour(FlameParticle.FACTORY, 0.01f);
                break;
            }
            case 3: {
                this.size(3.0f);
                this.pour(CorrosionParticle.MISSILE, 0.01f);
                break;
            }
            case 4: {
                this.size(4.0f);
                this.pour(LeafParticle.GENERAL, 0.01f);
                break;
            }
            case 5: {
                this.pour(SlowParticle.FACTORY, 0.01f);
                break;
            }
            case 6: {
                this.pour(ForceParticle.FACTORY, 0.01f);
                break;
            }
            case 7: {
                this.size(4.0f);
                this.pour(ShadowParticle.MISSILE, 0.01f);
                break;
            }
            case 8: {
                this.size(4.0f);
                this.pour(RainbowParticle.BURST, 0.01f);
                break;
            }
            case 9: {
                this.size(4.0f);
                this.pour(EarthParticle.FACTORY, 0.01f);
                break;
            }
            case 10: {
                this.size(4.0f);
                this.pour(WardParticle.FACTORY, 0.01f);
                break;
            }
            case 11: {
                this.size(2.0f);
                this.pour(ShamanParticle.RED, 0.01f);
                break;
            }
            case 12: {
                this.size(2.0f);
                this.pour(ShamanParticle.BLUE, 0.01f);
                break;
            }
            case 13: {
                this.size(2.0f);
                this.pour(ShamanParticle.PURPLE, 0.01f);
                break;
            }
            case 14: {
                this.size(2.0f);
                this.pour(ShamanParticle.ELMO, 0.01f);
                break;
            }
            case 15: {
                this.size(5.0f);
                this.pour(ElmoParticle.FACTORY, 0.01f);
                break;
            }
            case 16: {
                this.size(3.0f);
                this.pour(PoisonParticle.MISSILE, 0.01f);
                break;
            }
            case 17: {
                this.size(4.0f);
                this.pour(WhiteParticle.YELLOW, 0.01f);
                break;
            }
            case 100: {
                this.size(10.0f);
                this.pour(WhiteParticle.FACTORY, 0.03f);
                break;
            }
            case 101: {
                this.size(10.0f);
                this.pour(MagicParticle.FACTORY, 0.03f);
                break;
            }
            case 102: {
                this.size(10.0f);
                this.pour(FlameParticle.FACTORY, 0.03f);
                break;
            }
            case 103: {
                this.size(10.0f);
                this.pour(CorrosionParticle.MISSILE, 0.03f);
                break;
            }
            case 104: {
                this.size(10.0f);
                this.pour(LeafParticle.GENERAL, 0.03f);
                break;
            }
            case 105: {
                this.size(10.0f);
                this.pour(SlowParticle.FACTORY, 0.02f);
                break;
            }
            case 107: {
                this.size(10.0f);
                this.pour(ShadowParticle.MISSILE, 0.03f);
                break;
            }
            case 108: {
                this.size(10.0f);
                this.pour(RainbowParticle.BURST, 0.03f);
                break;
            }
            case 109: {
                this.size(10.0f);
                this.pour(EarthParticle.FACTORY, 0.03f);
                break;
            }
            case 110: {
                this.size(10.0f);
                this.pour(WardParticle.FACTORY, 0.03f);
                break;
            }
            case 111: {
                this.size(10.0f);
                this.pour(PurpleParticle.MISSILE, 0.03f);
                break;
            }
            case 112: {
                this.size(10.0f);
                this.pour(SparkParticle.FACTORY, 0.03f);
                break;
            }
            case 113: {
                this.size(10.0f);
                this.pour(BloodParticle.FACTORY, 0.03f);
                break;
            }
            case 114: {
                this.size(10.0f);
                this.pour(PoisonParticle.MISSILE, 0.03f);
            }
        }
        this.revive();
    }

    public void size(float size) {
        this.x -= size / 2.0f;
        this.y -= size / 2.0f;
        this.width = this.height = size;
    }

    public void setSpeed(float newSpeed) {
        PointF d = PointF.diff(this.to, new PointF(this.x, this.y));
        PointF speed = new PointF(d).normalize().scale(newSpeed);
        this.sx = speed.x;
        this.sy = speed.y;
        this.time = d.length() / newSpeed;
    }

    public static MagicMissile boltFromChar(Group group, int type, Visual sprite, int to, Callback callback) {
        MagicMissile missile = (MagicMissile)group.recycle(MagicMissile.class);
        if (Actor.findChar(to) != null) {
            missile.reset(type, sprite.center(), Actor.findChar((int)to).sprite.destinationCenter(), callback);
        } else {
            missile.reset(type, sprite, to, callback);
        }
        return missile;
    }

    @Override
    protected boolean isFrozen() {
        return false;
    }

    @Override
    public void update() {
        super.update();
        if (this.on) {
            float f;
            float d = Game.elapsed;
            this.x += this.sx * d;
            this.y += this.sy * d;
            this.time -= d;
            if (f <= 0.0f) {
                this.on = false;
                if (this.callback != null) {
                    this.callback.call();
                }
            }
        }
    }

    public static class WhiteParticle
    extends PixelParticle {
        public static final Emitter.Factory FACTORY = new Emitter.Factory(){

            @Override
            public void emit(Emitter emitter, int index, float x, float y) {
                ((WhiteParticle)emitter.recycle(WhiteParticle.class)).reset(x, y);
            }

            @Override
            public boolean lightMode() {
                return true;
            }
        };
        public static final Emitter.Factory YELLOW = new Emitter.Factory(){

            @Override
            public void emit(Emitter emitter, int index, float x, float y) {
                ((WhiteParticle)emitter.recycle(WhiteParticle.class)).reset(x, y, 1.0f, 1.0f, 0.25f);
            }

            @Override
            public boolean lightMode() {
                return true;
            }
        };
        public static final Emitter.Factory WALL = new Emitter.Factory(){

            @Override
            public void emit(Emitter emitter, int index, float x, float y) {
                ((WhiteParticle)emitter.recycle(WhiteParticle.class)).resetWall(x, y);
            }

            @Override
            public boolean lightMode() {
                return true;
            }
        };

        public WhiteParticle() {
            this.lifespan = 0.4f;
            this.am = 0.5f;
        }

        public void reset(float x, float y) {
            this.revive();
            this.x = x;
            this.y = y;
            this.left = this.lifespan;
            this.hardlight(1.0f, 1.0f, 1.0f);
        }

        public void reset(float x, float y, float r, float g, float b) {
            this.reset(x, y);
            this.hardlight(r, g, b);
        }

        public void resetWall(float x, float y) {
            this.reset(x, y);
            this.lifespan = 2.0f;
            this.left = 2.0f;
            this.x = Math.round(x / 4.0f) * 4;
            this.y = Math.round(y / 4.0f) * 4 - 6;
            this.x += (float)Math.round(this.y % 16.0f) / 4.0f - 2.0f;
        }

        @Override
        public void update() {
            super.update();
            this.size(this.left / this.lifespan * 3.0f);
        }
    }

    public static class MagicParticle
    extends PixelParticle {
        public static final Emitter.Factory FACTORY = new Emitter.Factory(){

            @Override
            public void emit(Emitter emitter, int index, float x, float y) {
                ((MagicParticle)emitter.recycle(MagicParticle.class)).reset(x, y);
            }

            @Override
            public boolean lightMode() {
                return true;
            }
        };
        public static final Emitter.Factory ATTRACTING = new Emitter.Factory(){

            @Override
            public void emit(Emitter emitter, int index, float x, float y) {
                ((MagicParticle)emitter.recycle(MagicParticle.class)).resetAttract(x, y);
            }

            @Override
            public boolean lightMode() {
                return true;
            }
        };

        public MagicParticle() {
            this.color(0x88CCFF);
            this.lifespan = 0.5f;
            this.speed.set(Random.Float(-10.0f, 10.0f), Random.Float(-10.0f, 10.0f));
        }

        public void reset(float x, float y) {
            this.revive();
            this.x = x;
            this.y = y;
            this.left = this.lifespan;
        }

        public void resetAttract(float x, float y) {
            this.revive();
            this.left = this.lifespan;
            this.speed.polar(Random.Float(6.283185f), Random.Float(16.0f, 32.0f));
            this.x = x - this.speed.x * this.lifespan;
            this.y = y - this.speed.y * this.lifespan;
        }

        @Override
        public void update() {
            super.update();
            this.am = this.left / this.lifespan;
            this.size(4.0f - this.am * 3.0f);
        }
    }

    public static class SlowParticle
    extends PixelParticle {
        private Emitter emitter;
        public static final Emitter.Factory FACTORY = new Emitter.Factory(){

            @Override
            public void emit(Emitter emitter, int index, float x, float y) {
                ((SlowParticle)emitter.recycle(SlowParticle.class)).reset(x, y, emitter);
            }

            @Override
            public boolean lightMode() {
                return true;
            }
        };

        public SlowParticle() {
            this.lifespan = 0.6f;
            this.color(0x664422);
            this.size(2.0f);
        }

        public void reset(float x, float y, Emitter emitter) {
            this.revive();
            this.x = x;
            this.y = y;
            this.emitter = emitter;
            this.left = this.lifespan;
            this.acc.set(0.0f);
            this.speed.set(Random.Float(-20.0f, 20.0f), Random.Float(-20.0f, 20.0f));
        }

        @Override
        public void update() {
            super.update();
            this.am = this.left / this.lifespan;
            this.acc.set((this.emitter.x - this.x) * 10.0f, (this.emitter.y - this.y) * 10.0f);
        }
    }

    public static class ForceParticle
    extends PixelParticle.Shrinking {
        public static final Emitter.Factory FACTORY = new Emitter.Factory(){

            @Override
            public void emit(Emitter emitter, int index, float x, float y) {
                ((ForceParticle)emitter.recycle(ForceParticle.class)).reset(index, x, y);
            }

            @Override
            public boolean lightMode() {
                return true;
            }
        };

        public void reset(int index, float x, float y) {
            super.reset(x, y, ColorMath.interpolate(0xFFCC99, 0xBB4411, Random.Float()), 8.0f, 0.5f);
            this.speed.polar(0.7853981f * (float)index, 12.0f);
            this.x -= this.speed.x * this.lifespan;
            this.y -= this.speed.y * this.lifespan;
        }

        @Override
        public void update() {
            super.update();
            this.am = (1.0f - this.left / this.lifespan) / 2.0f;
        }
    }

    public static class EarthParticle
    extends PixelParticle.Shrinking {
        public static final Emitter.Factory FACTORY = new Emitter.Factory(){

            @Override
            public void emit(Emitter emitter, int index, float x, float y) {
                ((EarthParticle)emitter.recycle(EarthParticle.class)).reset(x, y);
            }
        };
        public static final Emitter.Factory BURST = new Emitter.Factory(){

            @Override
            public void emit(Emitter emitter, int index, float x, float y) {
                ((EarthParticle)emitter.recycle(EarthParticle.class)).resetBurst(x, y);
            }
        };
        public static final Emitter.Factory ATTRACT = new Emitter.Factory(){

            @Override
            public void emit(Emitter emitter, int index, float x, float y) {
                ((EarthParticle)emitter.recycle(EarthParticle.class)).resetAttract(x, y);
            }
        };

        public EarthParticle() {
            this.lifespan = 0.5f;
            this.acc.set(0.0f, 40.0f);
        }

        public void reset(float x, float y) {
            this.revive();
            this.x = x;
            this.y = y;
            this.left = this.lifespan;
            this.size = 4.0f;
            if (Random.Int(10) == 0) {
                this.color(ColorMath.random(0xFFF266, 8419098));
            } else {
                this.color(ColorMath.random(0x805500, 3351808));
            }
            this.speed.set(Random.Float(-10.0f, 10.0f), Random.Float(-10.0f, 10.0f));
        }

        public void resetBurst(float x, float y) {
            this.reset(x, y);
            this.speed.polar(Random.Float(6.283185f), Random.Float(40.0f, 60.0f));
        }

        public void resetAttract(float x, float y) {
            this.reset(x, y);
            this.speed.polar(Random.Float(6.283185f), Random.Float(24.0f, 32.0f));
            this.x = x - this.speed.x * this.lifespan;
            this.y = y - this.speed.y * this.lifespan;
            this.acc.set(0.0f, 0.0f);
        }
    }

    public static class WardParticle
    extends PixelParticle.Shrinking {
        public static final Emitter.Factory FACTORY = new Emitter.Factory(){

            @Override
            public void emit(Emitter emitter, int index, float x, float y) {
                ((WardParticle)emitter.recycle(WardParticle.class)).reset(x, y);
            }

            @Override
            public boolean lightMode() {
                return true;
            }
        };
        public static final Emitter.Factory UP = new Emitter.Factory(){

            @Override
            public void emit(Emitter emitter, int index, float x, float y) {
                ((WardParticle)emitter.recycle(WardParticle.class)).resetUp(x, y);
            }

            @Override
            public boolean lightMode() {
                return true;
            }
        };

        public WardParticle() {
            this.lifespan = 0.6f;
            this.color(0x8822FF);
        }

        public void reset(float x, float y) {
            this.revive();
            this.x = x;
            this.y = y;
            this.left = this.lifespan;
            this.size = 8.0f;
        }

        public void resetUp(float x, float y) {
            this.reset(x, y);
            this.speed.set(Random.Float(-8.0f, 8.0f), Random.Float(-32.0f, -48.0f));
        }

        @Override
        public void update() {
            super.update();
            this.am = 1.0f - this.left / this.lifespan;
        }
    }

    public static class ShamanParticle
    extends EarthParticle {
        public static final Emitter.Factory RED = new Emitter.Factory(){

            @Override
            public void emit(Emitter emitter, int index, float x, float y) {
                ((ShamanParticle)emitter.recycle(ShamanParticle.class)).reset(x, y, ColorMath.random(0xFF4D4D, 8395290));
            }
        };
        public static final Emitter.Factory BLUE = new Emitter.Factory(){

            @Override
            public void emit(Emitter emitter, int index, float x, float y) {
                ((ShamanParticle)emitter.recycle(ShamanParticle.class)).reset(x, y, ColorMath.random(0x6699FF, 1719424));
            }
        };
        public static final Emitter.Factory PURPLE = new Emitter.Factory(){

            @Override
            public void emit(Emitter emitter, int index, float x, float y) {
                ((ShamanParticle)emitter.recycle(ShamanParticle.class)).reset(x, y, ColorMath.random(0xBB33FF, 6167168));
            }
        };
        public static final Emitter.Factory ELMO = new Emitter.Factory(){

            @Override
            public void emit(Emitter emitter, int index, float x, float y) {
                ((ShamanParticle)emitter.recycle(ShamanParticle.class)).reset(x, y, ColorMath.random(0x66EE99, 0x22EE66));
            }

            @Override
            public boolean lightMode() {
                return true;
            }
        };
        int startColor;
        int endColor;

        public ShamanParticle() {
            this.lifespan = 0.6f;
            this.acc.set(0.0f, 0.0f);
        }

        public void reset(float x, float y, int endColor) {
            super.reset(x, y);
            this.size(1.0f);
            this.endColor = endColor;
            this.startColor = ColorMath.random(0x805500, 3351808);
            this.speed.set(Random.Float(-10.0f, 10.0f), Random.Float(-10.0f, 10.0f));
        }

        @Override
        public void update() {
            super.update();
            this.color(ColorMath.interpolate(this.endColor, this.startColor, this.left / this.lifespan));
        }
    }
}

