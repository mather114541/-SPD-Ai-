/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.ColorMath;
import com.watabou.utils.Random;
import com.watabou.utils.SparseArray;

public class Speck
extends Image {
    public static final int HEALING = 0;
    public static final int STAR = 1;
    public static final int LIGHT = 2;
    public static final int QUESTION = 3;
    public static final int UP = 4;
    public static final int SCREAM = 5;
    public static final int BONE = 6;
    public static final int WOOL = 7;
    public static final int ROCK = 8;
    public static final int NOTE = 9;
    public static final int CHANGE = 10;
    public static final int HEART = 11;
    public static final int BUBBLE = 12;
    public static final int STEAM = 13;
    public static final int COIN = 14;
    public static final int DOWN = 15;
    public static final int DISCOVER = 101;
    public static final int EVOKE = 102;
    public static final int MASK = 103;
    public static final int CROWN = 104;
    public static final int RATTLE = 105;
    public static final int JET = 106;
    public static final int TOXIC = 107;
    public static final int CORROSION = 108;
    public static final int PARALYSIS = 109;
    public static final int DUST = 110;
    public static final int STENCH = 111;
    public static final int FORGE = 112;
    public static final int CONFUSION = 113;
    public static final int RED_LIGHT = 114;
    public static final int CALM = 115;
    public static final int SMOKE = 116;
    public static final int STORM = 117;
    public static final int INFERNO = 118;
    public static final int BLIZZARD = 119;
    public static final int YELLOW_LIGHT = 120;
    public static final int BLUE_LIGHT = 121;
    private static final int SIZE = 7;
    private int type;
    private float lifespan;
    private float left;
    private static TextureFilm film;
    private static SparseArray<Emitter.Factory> factories;

    public Speck() {
        this.texture("effects/specks.png");
        if (film == null) {
            film = new TextureFilm(this.texture, 7, 7);
        }
        this.origin.set(3.5f);
    }

    public Speck image(int type) {
        this.reset(0, 0.0f, 0.0f, type);
        this.lifespan = Float.POSITIVE_INFINITY;
        this.left = Float.POSITIVE_INFINITY;
        this.type = -1;
        this.resetColor();
        this.scale.set(1.0f);
        this.speed.set(0.0f);
        this.acc.set(0.0f);
        this.angle = 0.0f;
        this.angularSpeed = 0.0f;
        return this;
    }

    public void reset(int index, float x, float y, int type) {
        this.revive();
        this.type = type;
        switch (type) {
            case 101: 
            case 114: 
            case 120: 
            case 121: {
                this.frame(film.get(2));
                break;
            }
            case 102: 
            case 103: 
            case 104: 
            case 112: {
                this.frame(film.get(1));
                break;
            }
            case 105: {
                this.frame(film.get(6));
                break;
            }
            case 106: 
            case 107: 
            case 108: 
            case 109: 
            case 110: 
            case 111: 
            case 113: 
            case 116: 
            case 117: 
            case 118: 
            case 119: {
                this.frame(film.get(13));
                break;
            }
            case 115: {
                this.frame(film.get(5));
                break;
            }
            default: {
                this.frame(film.get(type));
            }
        }
        this.x = x - this.origin.x;
        this.y = y - this.origin.y;
        this.resetColor();
        this.scale.set(1.0f);
        this.speed.set(0.0f);
        this.acc.set(0.0f);
        this.angle = 0.0f;
        this.angularSpeed = 0.0f;
        switch (type) {
            case 0: {
                this.speed.set(0.0f, -20.0f);
                this.lifespan = 1.0f;
                break;
            }
            case 1: {
                this.speed.polar(Random.Float(6.283185f), Random.Float(128.0f));
                this.acc.set(0.0f, 128.0f);
                this.angle = Random.Float(360.0f);
                this.angularSpeed = Random.Float(-360.0f, 360.0f);
                this.lifespan = 1.0f;
                break;
            }
            case 112: {
                this.speed.polar(Random.Float(-3.1415925f), Random.Float(64.0f));
                this.acc.set(0.0f, 128.0f);
                this.angle = Random.Float(360.0f);
                this.angularSpeed = Random.Float(-360.0f, 360.0f);
                this.lifespan = 0.51f;
                break;
            }
            case 102: {
                this.speed.polar(Random.Float(-3.1415925f), 50.0f);
                this.acc.set(0.0f, 50.0f);
                this.angle = Random.Float(360.0f);
                this.angularSpeed = Random.Float(-180.0f, 180.0f);
                this.lifespan = 1.0f;
                break;
            }
            case 103: {
                this.speed.polar((float)index * 3.1415925f / 5.0f, 50.0f);
                this.acc.set(-this.speed.x, -this.speed.y);
                this.angle = index * 36;
                this.angularSpeed = 360.0f;
                this.lifespan = 1.0f;
                break;
            }
            case 104: {
                this.acc.set(index % 2 == 0 ? Random.Float(-512.0f, -256.0f) : Random.Float(256.0f, 512.0f), 0.0f);
                this.angularSpeed = this.acc.x < 0.0f ? -180.0f : 180.0f;
                this.lifespan = 0.5f;
                break;
            }
            case 114: {
                this.tint(-3407872);
                this.angle = Random.Float(360.0f);
                this.angularSpeed = 90.0f;
                this.lifespan = 1.0f;
                break;
            }
            case 2: {
                this.angle = Random.Float(360.0f);
                this.angularSpeed = 90.0f;
                this.lifespan = 1.0f;
                break;
            }
            case 120: {
                this.tint(-2237184);
                this.angle = Random.Float(360.0f);
                this.angularSpeed = 90.0f;
                this.lifespan = 1.0f;
                break;
            }
            case 121: {
                this.tint(-16724737);
                this.angle = Random.Float(360.0f);
                this.angularSpeed = 90.0f;
                this.lifespan = 1.0f;
                break;
            }
            case 101: {
                this.angle = Random.Float(360.0f);
                this.angularSpeed = 90.0f;
                this.lifespan = 0.5f;
                this.am = 0.0f;
                break;
            }
            case 3: {
                this.lifespan = 0.8f;
                break;
            }
            case 4: {
                this.speed.set(0.0f, -20.0f);
                this.lifespan = 1.0f;
                break;
            }
            case 15: {
                this.speed.set(0.0f, 20.0f);
                this.lifespan = 1.0f;
                break;
            }
            case 115: {
                this.color(0.0f, 1.0f, 1.0f);
            }
            case 5: {
                this.lifespan = 0.9f;
                break;
            }
            case 6: {
                this.lifespan = 0.2f;
                this.speed.polar(Random.Float(6.283185f), 24.0f / this.lifespan);
                this.acc.set(0.0f, 128.0f);
                this.angle = Random.Float(360.0f);
                this.angularSpeed = 360.0f;
                break;
            }
            case 105: {
                this.lifespan = 0.5f;
                this.speed.set(0.0f, -100.0f);
                this.acc.set(0.0f, -2.0f * this.speed.y / this.lifespan);
                this.angle = Random.Float(360.0f);
                this.angularSpeed = 360.0f;
                break;
            }
            case 7: {
                this.lifespan = 0.5f;
                this.speed.set(0.0f, -50.0f);
                this.angle = Random.Float(360.0f);
                this.angularSpeed = Random.Float(-360.0f, 360.0f);
                break;
            }
            case 8: {
                this.angle = Random.Float(360.0f);
                this.angularSpeed = Random.Float(-360.0f, 360.0f);
                this.scale.set(Random.Float(1.0f, 2.0f));
                this.speed.set(0.0f, 64.0f);
                this.lifespan = 0.2f;
                this.y -= this.speed.y * this.lifespan;
                break;
            }
            case 9: {
                this.angularSpeed = Random.Float(-30.0f, 30.0f);
                this.speed.polar((this.angularSpeed - 90.0f) * ((float)Math.PI / 180), 30.0f);
                this.lifespan = 1.0f;
                break;
            }
            case 10: {
                this.angle = Random.Float(360.0f);
                this.speed.polar((this.angle - 90.0f) * ((float)Math.PI / 180), Random.Float(4.0f, 12.0f));
                this.lifespan = 1.5f;
                break;
            }
            case 11: {
                this.speed.set(Random.IntRange(-10, 10), -40.0f);
                this.angularSpeed = Random.Float(-45.0f, 45.0f);
                this.lifespan = 1.0f;
                break;
            }
            case 12: {
                this.speed.set(0.0f, -15.0f);
                this.scale.set(PixelScene.align(Random.Float(0.8f, 1.0f)));
                this.lifespan = Random.Float(0.8f, 1.5f);
                break;
            }
            case 13: {
                this.speed.y = -Random.Float(10.0f, 15.0f);
                this.angularSpeed = Random.Float(180.0f);
                this.angle = Random.Float(360.0f);
                this.lifespan = 1.0f;
                break;
            }
            case 106: {
                this.speed.y = 32.0f;
                this.acc.y = -64.0f;
                this.angularSpeed = Random.Float(180.0f, 360.0f);
                this.angle = Random.Float(360.0f);
                this.lifespan = 0.5f;
                break;
            }
            case 107: {
                this.hardlight(5308256);
                this.angularSpeed = 30.0f;
                this.angle = Random.Float(360.0f);
                this.lifespan = Random.Float(1.0f, 3.0f);
                break;
            }
            case 108: {
                this.hardlight(0xAAAAAA);
                this.angularSpeed = 30.0f;
                this.angle = Random.Float(360.0f);
                this.lifespan = Random.Float(1.0f, 3.0f);
                break;
            }
            case 109: {
                this.hardlight(0xFFFF66);
                this.angularSpeed = -30.0f;
                this.angle = Random.Float(360.0f);
                this.lifespan = Random.Float(1.0f, 3.0f);
                break;
            }
            case 111: {
                this.hardlight(13056);
                this.angularSpeed = -30.0f;
                this.angle = Random.Float(360.0f);
                this.lifespan = Random.Float(1.0f, 3.0f);
                break;
            }
            case 113: {
                this.hardlight(Random.Int(0x1000000) | 0x80);
                this.angularSpeed = Random.Float(-20.0f, 20.0f);
                this.angle = Random.Float(360.0f);
                this.lifespan = Random.Float(1.0f, 3.0f);
                break;
            }
            case 117: {
                this.hardlight(0x8AD8D8);
                this.angularSpeed = Random.Float(-20.0f, 20.0f);
                this.angle = Random.Float(360.0f);
                this.lifespan = Random.Float(1.0f, 3.0f);
                break;
            }
            case 118: {
                this.hardlight(0xEE7722);
                this.angularSpeed = Random.Float(200.0f, 300.0f) * (float)(Random.Int(2) == 0 ? -1 : 1);
                this.angle = Random.Float(360.0f);
                this.lifespan = Random.Float(1.0f, 3.0f);
                break;
            }
            case 119: {
                this.hardlight(0xFFFFFF);
                this.angularSpeed = Random.Float(200.0f, 300.0f) * (float)(Random.Int(2) == 0 ? -1 : 1);
                this.angle = Random.Float(360.0f);
                this.lifespan = Random.Float(1.0f, 3.0f);
                break;
            }
            case 116: {
                this.hardlight(0);
                this.angularSpeed = 30.0f;
                this.angle = Random.Float(360.0f);
                this.lifespan = Random.Float(1.0f, 1.5f);
                break;
            }
            case 110: {
                this.hardlight(0xFFFF66);
                this.angle = Random.Float(360.0f);
                this.speed.polar(Random.Float(6.283185f), Random.Float(16.0f, 48.0f));
                this.lifespan = 0.5f;
                break;
            }
            case 14: {
                this.speed.polar(-3.1415925f * Random.Float(0.3f, 0.7f), Random.Float(48.0f, 96.0f));
                this.acc.y = 256.0f;
                this.lifespan = -this.speed.y / this.acc.y * 2.0f;
            }
        }
        this.left = this.lifespan;
    }

    @Override
    public void update() {
        super.update();
        this.left -= Game.elapsed;
        if (this.left <= 0.0f) {
            this.kill();
        } else {
            float p = 1.0f - this.left / this.lifespan;
            switch (this.type) {
                case 1: 
                case 112: {
                    this.scale.set(1.0f - p);
                    this.am = p < 0.2f ? p * 5.0f : (1.0f - p) * 1.25f;
                    break;
                }
                case 103: 
                case 104: {
                    this.am = 1.0f - p * p;
                    break;
                }
                case 0: 
                case 102: {
                    this.am = p < 0.5f ? 1.0f : 2.0f - p * 2.0f;
                    break;
                }
                case 2: 
                case 114: 
                case 120: 
                case 121: {
                    this.am = this.scale.set((float)(p < 0.2f ? p * 5.0f : (1.0f - p) * 1.25f)).x;
                    break;
                }
                case 101: {
                    this.am = 1.0f - p;
                    this.scale.set((p < 0.5f ? p : 1.0f - p) * 2.0f);
                    break;
                }
                case 3: {
                    this.scale.set((float)(Math.sqrt(p < 0.5f ? (double)p : (double)(1.0f - p)) * 3.0));
                    break;
                }
                case 4: 
                case 15: {
                    this.scale.set((float)(Math.sqrt(p < 0.5f ? (double)p : (double)(1.0f - p)) * 2.0));
                    break;
                }
                case 5: 
                case 115: {
                    this.am = (float)Math.sqrt((p < 0.5f ? p : 1.0f - p) * 2.0f);
                    this.scale.set(p * 7.0f);
                    break;
                }
                case 6: 
                case 105: {
                    this.am = p < 0.9f ? 1.0f : (1.0f - p) * 10.0f;
                    break;
                }
                case 8: {
                    this.am = p < 0.2f ? p * 5.0f : 1.0f;
                    break;
                }
                case 9: {
                    this.am = 1.0f - p * p;
                    break;
                }
                case 7: {
                    this.scale.set(1.0f - p);
                    break;
                }
                case 10: {
                    this.am = (float)Math.sqrt((p < 0.5f ? p : 1.0f - p) * 2.0f);
                    this.scale.y = (1.0f + p) * 0.5f;
                    this.scale.x = this.scale.y * (float)Math.cos(this.left * 15.0f);
                    break;
                }
                case 11: {
                    this.scale.set(1.0f - p);
                    this.am = 1.0f - p * p;
                    break;
                }
                case 12: {
                    this.am = p < 0.2f ? p * 5.0f : 1.0f;
                    break;
                }
                case 13: 
                case 107: 
                case 109: 
                case 110: 
                case 113: 
                case 117: 
                case 118: 
                case 119: {
                    this.am = (float)Math.sqrt((p < 0.5f ? p : 1.0f - p) * 0.5f);
                    this.scale.set(1.0f + p);
                    break;
                }
                case 108: {
                    this.hardlight(ColorMath.interpolate(0xAAAAAA, 0xFF8800, p));
                }
                case 111: 
                case 116: {
                    this.am = (float)Math.sqrt(p < 0.5f ? p : 1.0f - p);
                    this.scale.set(1.0f + p);
                    break;
                }
                case 106: {
                    this.am = (p < 0.5f ? p : 1.0f - p) * 2.0f;
                    this.scale.set(p * 1.5f);
                    break;
                }
                case 14: {
                    this.scale.x = (float)Math.cos(this.left * 5.0f);
                    this.gm = this.bm = (Math.abs(this.scale.x) + 1.0f) * 0.5f;
                    this.rm = this.bm;
                    this.am = p < 0.9f ? 1.0f : (1.0f - p) * 10.0f;
                }
            }
        }
    }

    public static Emitter.Factory factory(int type) {
        return Speck.factory(type, false);
    }

    public static Emitter.Factory factory(final int type, final boolean lightMode) {
        Emitter.Factory factory = (Emitter.Factory)factories.get(type);
        if (factory == null) {
            factory = new Emitter.Factory(){

                @Override
                public void emit(Emitter emitter, int index, float x, float y) {
                    Speck p = (Speck)emitter.recycle(Speck.class);
                    p.reset(index, x, y, type);
                }

                @Override
                public boolean lightMode() {
                    return lightMode;
                }
            };
            factories.put(type, factory);
        }
        return factory;
    }

    static {
        factories = new SparseArray();
    }
}

