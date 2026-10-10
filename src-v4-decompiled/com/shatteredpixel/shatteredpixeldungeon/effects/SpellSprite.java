/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;
import java.util.HashMap;

public class SpellSprite
extends Image {
    public static final int FOOD = 0;
    public static final int MAP = 1;
    public static final int CHARGE = 2;
    public static final int BERSERK = 3;
    public static final int ANKH = 4;
    public static final int HASTE = 5;
    public static final int VISION = 6;
    public static final int PURITY = 7;
    private static final int SIZE = 16;
    private static final float FADE_IN_TIME = 0.2f;
    private static final float STATIC_TIME = 0.8f;
    private static final float FADE_OUT_TIME = 0.4f;
    private static TextureFilm film;
    private Char target;
    private Phase phase;
    private float duration;
    private float passed;
    private static HashMap<Char, SpellSprite> all;

    public SpellSprite() {
        super("effects/spell_icons.png");
        if (film == null) {
            film = new TextureFilm(this.texture, 16);
        }
    }

    public void reset(int index) {
        this.frame(film.get(index));
        this.origin.set(this.width / 2.0f, this.height / 2.0f);
        this.phase = Phase.FADE_IN;
        this.duration = 0.2f;
        this.passed = 0.0f;
    }

    @Override
    public void update() {
        float f;
        super.update();
        if (this.target != null && this.target.sprite != null) {
            this.x = this.target.sprite.center().x - 8.0f;
            this.y = this.target.sprite.y - 16.0f;
        }
        if (this.phase == null || this.target == null) {
            return;
        }
        switch (this.phase.ordinal()) {
            case 0: {
                this.alpha(this.passed / this.duration);
                this.scale.set(this.passed / this.duration);
                break;
            }
            case 1: {
                break;
            }
            case 2: {
                this.alpha(1.0f - this.passed / this.duration);
            }
        }
        this.passed += Game.elapsed;
        if (f > this.duration) {
            switch (this.phase.ordinal()) {
                case 0: {
                    this.phase = Phase.STATIC;
                    this.duration = 0.8f;
                    break;
                }
                case 1: {
                    this.phase = Phase.FADE_OUT;
                    this.duration = 0.4f;
                    break;
                }
                case 2: {
                    this.kill();
                }
            }
            this.passed = 0.0f;
        }
    }

    @Override
    public void kill() {
        super.kill();
        all.remove(this.target);
    }

    public static void show(Char ch, int index) {
        SpellSprite.show(ch, index, 1.0f, 1.0f, 1.0f);
    }

    public static void show(Char ch, int index, float r, float g, float b) {
        if (!ch.sprite.visible) {
            return;
        }
        SpellSprite old = all.get(ch);
        if (old != null) {
            old.kill();
        }
        SpellSprite sprite = GameScene.spellSprite();
        sprite.target = ch;
        sprite.reset(index);
        sprite.hardlight(r, g, b);
        sprite.revive();
        all.put(ch, sprite);
    }

    static {
        all = new HashMap();
    }

    private static enum Phase {
        FADE_IN,
        STATIC,
        FADE_OUT;

    }
}

