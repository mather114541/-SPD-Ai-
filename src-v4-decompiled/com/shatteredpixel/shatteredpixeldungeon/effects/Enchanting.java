/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.noosa.Game;

public class Enchanting
extends ItemSprite {
    private static final float FADE_IN_TIME = 0.2f;
    private static final float STATIC_TIME = 1.0f;
    private static final float FADE_OUT_TIME = 0.4f;
    private static final float ALPHA = 0.6f;
    private int color;
    private Char target;
    private Phase phase;
    private float duration;
    private float passed;

    public Enchanting(Item item) {
        super(item.image(), null);
        this.color = item.glowing() != null ? item.glowing().color : -1;
        this.phase = Phase.FADE_IN;
        this.duration = 0.2f;
        this.passed = 0.0f;
    }

    @Override
    public void update() {
        float f;
        super.update();
        this.x = this.target.sprite.center().x - this.width() / 2.0f;
        this.y = this.target.sprite.y - 8.0f - this.height() / 2.0f;
        switch (this.phase.ordinal()) {
            case 0: {
                this.alpha(this.passed / this.duration * 0.6f);
                this.scale.set(this.passed / this.duration);
                break;
            }
            case 1: {
                if (this.color == -1) break;
                this.tint(this.color, this.passed / this.duration * 0.8f);
                break;
            }
            case 2: {
                this.alpha((1.0f - this.passed / this.duration) * 0.6f);
                this.scale.set(1.0f + this.passed / this.duration);
            }
        }
        this.passed += Game.elapsed;
        if (f > this.duration) {
            switch (this.phase.ordinal()) {
                case 0: {
                    this.phase = Phase.STATIC;
                    this.duration = 1.0f;
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

    public static void show(Char ch, Item item) {
        if (!ch.sprite.visible) {
            return;
        }
        Enchanting sprite = new Enchanting(item);
        sprite.target = ch;
        ch.sprite.parent.add(sprite);
    }

    private static enum Phase {
        FADE_IN,
        STATIC,
        FADE_OUT;

    }
}

