/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.gltextures.SmartTexture;
import com.watabou.gltextures.TextureCache;
import com.watabou.glwrap.Matrix;
import com.watabou.glwrap.Vertexbuffer;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.NoosaScript;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;
import java.nio.Buffer;

public class ItemSprite
extends MovieClip {
    public static final int SIZE = 16;
    private static final float DROP_INTERVAL = 0.4f;
    public Heap heap;
    private Glowing glowing;
    protected Emitter emitter;
    private float phase;
    private boolean glowUp;
    private float dropInterval;
    protected float perspectiveRaise = 0.3125f;
    protected boolean renderShadow = false;
    protected float shadowWidth = 1.0f;
    protected float shadowHeight = 0.25f;
    protected float shadowOffset = 0.5f;
    private float[] shadowMatrix = new float[16];

    public ItemSprite() {
        this(ItemSpriteSheet.SOMETHING, null);
    }

    public ItemSprite(Heap heap) {
        super("sprites/items.png");
        this.view(heap);
    }

    public ItemSprite(Item item) {
        super("sprites/items.png");
        this.view(item);
    }

    public ItemSprite(int image) {
        this(image, null);
    }

    public ItemSprite(int image, Glowing glowing) {
        super("sprites/items.png");
        this.view(image, glowing);
    }

    public void link() {
        this.link(this.heap);
    }

    public void link(Heap heap) {
        this.heap = heap;
        this.view(heap);
        this.renderShadow = true;
        this.visible = heap.seen;
        this.place(heap.pos);
    }

    @Override
    public void revive() {
        super.revive();
        this.speed.set(0.0f);
        this.acc.set(0.0f);
        this.dropInterval = 0.0f;
        this.heap = null;
        if (this.emitter != null) {
            this.emitter.killAndErase();
            this.emitter = null;
        }
    }

    @Override
    public void copy(Image other) {
        super.copy(other);
        if (other instanceof ItemSprite && ((ItemSprite)other).glowing != null) {
            this.glow(((ItemSprite)other).glowing);
        }
    }

    public void visible(boolean value) {
        this.visible = value;
        if (this.emitter != null && !this.visible) {
            this.emitter.killAndErase();
            this.emitter = null;
        }
    }

    public PointF worldToCamera(int cell) {
        int csize = 16;
        return new PointF(PixelScene.align(Camera.main, ((float)(cell % Dungeon.level.width()) + 0.5f) * 16.0f - this.width() * 0.5f), PixelScene.align(Camera.main, ((float)(cell / Dungeon.level.width()) + 1.0f) * 16.0f - this.height() - 16.0f * this.perspectiveRaise));
    }

    public void place(int p) {
        if (Dungeon.level != null) {
            this.point(this.worldToCamera(p));
            this.shadowOffset = 0.5f;
        }
    }

    public void drop() {
        if (this.heap.isEmpty()) {
            return;
        }
        if (this.heap.size() == 1) {
            this.place(this.heap.pos);
        }
        this.dropInterval = 0.4f;
        this.speed.set(0.0f, -100.0f);
        this.acc.set(0.0f, -this.speed.y / 0.4f * 2.0f);
        if (this.heap != null && this.heap.seen && this.heap.peek() instanceof Gold) {
            CellEmitter.center(this.heap.pos).burst(Speck.factory(14), 5);
            Sample.INSTANCE.play("sounds/gold.mp3", 1.0f, 1.0f, Random.Float(0.9f, 1.1f));
        }
    }

    public void drop(int from) {
        if (this.heap.pos == from) {
            this.drop();
        } else {
            float px = this.x;
            float py = this.y;
            this.drop();
            this.place(from);
            this.speed.offset((px - this.x) / 0.4f, (py - this.y) / 0.4f);
        }
    }

    public ItemSprite view(Item item) {
        this.view(item.image(), item.glowing());
        Emitter emitter = item.emitter();
        if (emitter != null && this.parent != null) {
            emitter.pos(this);
            this.parent.add(emitter);
            this.emitter = emitter;
        }
        return this;
    }

    public ItemSprite view(Heap heap) {
        if (heap.size() <= 0 || heap.items == null) {
            return this.view(0, null);
        }
        switch (heap.type) {
            case HEAP: 
            case FOR_SALE: {
                this.view(heap.peek());
                break;
            }
            case CHEST: {
                this.view(ItemSpriteSheet.CHEST, null);
                break;
            }
            case LOCKED_CHEST: {
                this.view(ItemSpriteSheet.LOCKED_CHEST, null);
                break;
            }
            case CRYSTAL_CHEST: {
                this.view(ItemSpriteSheet.CRYSTAL_CHEST, null);
                break;
            }
            case TOMB: {
                this.view(ItemSpriteSheet.TOMB, null);
                break;
            }
            case SKELETON: {
                this.view(ItemSpriteSheet.BONES, null);
                break;
            }
            case REMAINS: {
                this.view(ItemSpriteSheet.REMAINS, null);
                break;
            }
            default: {
                this.view(0, null);
            }
        }
        this.alpha(heap.hidden ? 0.1f : 1.0f);
        return this;
    }

    public ItemSprite view(int image, Glowing glowing) {
        if (this.emitter != null) {
            this.emitter.killAndErase();
        }
        this.emitter = null;
        this.frame(image);
        this.glow(glowing);
        return this;
    }

    public void frame(int image) {
        this.frame(ItemSpriteSheet.film.get(image));
        float height = ItemSpriteSheet.film.height(image);
        if (height < 8.0f) {
            this.perspectiveRaise = (13.0f - height) / 16.0f;
        }
    }

    public synchronized void glow(Glowing glowing) {
        this.glowing = glowing;
        if (glowing == null) {
            this.resetColor();
        }
    }

    @Override
    public void kill() {
        super.kill();
        if (this.emitter != null) {
            this.emitter.on = false;
            this.emitter.autoKill = true;
        }
        this.emitter = null;
    }

    @Override
    protected void updateMatrix() {
        super.updateMatrix();
        Matrix.copy(this.matrix, this.shadowMatrix);
        Matrix.translate(this.shadowMatrix, this.width() * (1.0f - this.shadowWidth) / 2.0f, this.height() * (1.0f - this.shadowHeight) + this.shadowOffset);
        Matrix.scale(this.shadowMatrix, this.shadowWidth, this.shadowHeight);
    }

    @Override
    public void draw() {
        if (this.texture == null || !this.dirty && this.buffer == null) {
            return;
        }
        if (this.renderShadow) {
            if (this.dirty) {
                ((Buffer)this.verticesBuffer).position(0);
                this.verticesBuffer.put(this.vertices);
                if (this.buffer == null) {
                    this.buffer = new Vertexbuffer(this.verticesBuffer);
                } else {
                    this.buffer.updateVertices(this.verticesBuffer);
                }
                this.dirty = false;
            }
            NoosaScript script = this.script();
            this.texture.bind();
            script.camera(this.camera());
            this.updateMatrix();
            script.uModel.valueM4(this.shadowMatrix);
            script.lighting(0.0f, 0.0f, 0.0f, this.am * 0.6f, 0.0f, 0.0f, 0.0f, this.aa * 0.6f);
            script.drawQuad(this.buffer);
        }
        super.draw();
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public synchronized void update() {
        block17: {
            super.update();
            v0 = this.visible = this.heap == null || this.heap.seen != false;
            if (this.emitter != null) {
                this.emitter.visible = this.visible;
            }
            if (this.dropInterval > 0.0f) {
                this.shadowOffset -= this.speed.y * Game.elapsed * 0.8f;
                this.dropInterval -= Game.elapsed;
                if (v1 <= 0.0f) {
                    this.speed.set(0.0f);
                    this.acc.set(0.0f);
                    this.shadowOffset = 0.25f;
                    this.place(this.heap.pos);
                    if (this.visible) {
                        if (Dungeon.level.water[this.heap.pos]) {
                            GameScene.ripple(this.heap.pos);
                        }
                        if (Dungeon.level.water[this.heap.pos]) {
                            Sample.INSTANCE.play("sounds/water.mp3", 0.8f, Random.Float(1.0f, 1.45f));
                        } else if (Dungeon.level.map[this.heap.pos] == 14) {
                            Sample.INSTANCE.play("sounds/sturdy.mp3", 0.8f, Random.Float(1.16f, 1.25f));
                        } else if (Dungeon.level.map[this.heap.pos] == 2 || Dungeon.level.map[this.heap.pos] == 9 || Dungeon.level.map[this.heap.pos] == 30) {
                            Sample.INSTANCE.play("sounds/grass.mp3", 0.8f, Random.Float(1.16f, 1.25f));
                        } else if (Dungeon.level.map[this.heap.pos] == 15) {
                            Sample.INSTANCE.play("sounds/step.mp3", 0.8f, Random.Float(1.16f, 1.25f));
                        } else {
                            Sample.INSTANCE.play("sounds/step.mp3", 0.8f, Random.Float(1.16f, 1.25f));
                        }
                    }
                }
            }
            if (!this.visible || this.glowing == null) break block17;
            if (!this.glowUp) ** GOTO lbl-1000
            this.phase += Game.elapsed;
            if (v2 > this.glowing.period) {
                this.glowUp = false;
                this.phase = this.glowing.period;
            } else if (!this.glowUp) {
                this.phase -= Game.elapsed;
                if (v3 < 0.0f) {
                    this.glowUp = true;
                    this.phase = 0.0f;
                }
            }
            value = this.phase / this.glowing.period * 0.6f;
            this.gm = this.bm = 1.0f - value;
            this.rm = this.bm;
            this.ra = this.glowing.red * value;
            this.ga = this.glowing.green * value;
            this.ba = this.glowing.blue * value;
        }
    }

    public static int pick(int index, int x, int y) {
        SmartTexture tx = TextureCache.get("sprites/items.png");
        int rows = tx.width / 16;
        int row = index / rows;
        int col = index % rows;
        return tx.getPixel(col * 16 + x, row * 16 + y);
    }

    public static class Glowing {
        public int color;
        public float red;
        public float green;
        public float blue;
        public float period;

        public Glowing(int color) {
            this(color, 1.0f);
        }

        public Glowing(int color, float period) {
            this.color = color;
            this.red = (float)(color >> 16) / 255.0f;
            this.green = (float)(color >> 8 & 0xFF) / 255.0f;
            this.blue = (float)(color & 0xFF) / 255.0f;
            this.period = period;
        }
    }
}

