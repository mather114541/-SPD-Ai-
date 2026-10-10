/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class EmoIcon
extends Image {
    protected float maxSize = 2.0f;
    protected float timeScale = 1.0f;
    protected boolean growing = true;
    protected CharSprite owner;

    public EmoIcon(CharSprite owner) {
        this.owner = owner;
        GameScene.add(this);
    }

    @Override
    public void update() {
        super.update();
        if (this.visible) {
            if (this.growing) {
                this.scale.set(Math.min(this.scale.x + Game.elapsed * this.timeScale, this.maxSize));
                if (this.scale.x >= this.maxSize) {
                    this.growing = false;
                }
            } else {
                this.scale.set(Math.max(this.scale.x - Game.elapsed * this.timeScale, 1.0f));
                if (this.scale.x <= 1.0f) {
                    this.growing = true;
                }
            }
            if (this.camera != null) {
                PointF center = this.centerPoint();
                this.x = PixelScene.align(this.camera, this.owner.x + this.owner.width() - center.x);
                this.y = PixelScene.align(this.camera, this.owner.y - center.y);
            }
        }
    }

    protected PointF centerPoint() {
        return new PointF(this.width() / 2.0f, this.height() / 2.0f);
    }

    public static class Lost
    extends EmoIcon {
        public Lost(CharSprite owner) {
            super(owner);
            this.copy(Icons.get(Icons.LOST));
            this.maxSize = 1.25f;
            this.timeScale = 1.0f;
            this.scale.set(Random.Float(1.0f, this.maxSize));
            this.x = owner.x + owner.width - this.width / 2.0f;
            this.y = owner.y - this.height;
        }

        @Override
        protected PointF centerPoint() {
            return new PointF(2.5f + 0.25f * this.width(), 2.5f + 0.75f * this.height());
        }
    }

    public static class Investigate
    extends EmoIcon {
        public Investigate(CharSprite owner) {
            super(owner);
            this.copy(Icons.get(Icons.INVESTIGATE));
            this.maxSize = 1.3f;
            this.timeScale = 1.5f;
            this.scale.set(Random.Float(1.0f, this.maxSize));
            this.x = owner.x + owner.width - this.width / 2.0f;
            this.y = owner.y - this.height;
        }

        @Override
        protected PointF centerPoint() {
            return new PointF(2.5f + 0.25f * this.width(), 2.5f + 0.75f * this.height());
        }
    }

    public static class Alert
    extends EmoIcon {
        public Alert(CharSprite owner) {
            super(owner);
            this.copy(Icons.get(Icons.ALERT));
            this.maxSize = 1.3f;
            this.timeScale = 2.0f;
            this.scale.set(Random.Float(1.0f, this.maxSize));
            this.x = owner.x + owner.width - this.width / 2.0f;
            this.y = owner.y - this.height;
        }

        @Override
        protected PointF centerPoint() {
            return new PointF(2.5f + 0.25f * this.width(), 2.5f + 0.75f * this.height());
        }
    }

    public static class Sleep
    extends EmoIcon {
        public Sleep(CharSprite owner) {
            super(owner);
            this.copy(Icons.get(Icons.SLEEP));
            this.maxSize = 1.2f;
            this.timeScale = 0.5f;
            this.scale.set(Random.Float(1.0f, this.maxSize));
            this.x = owner.x + owner.width - this.width / 2.0f;
            this.y = owner.y - this.height;
        }

        @Override
        protected PointF centerPoint() {
            return new PointF(this.width() / 2.0f, 4.0f + this.height() / 2.0f);
        }
    }
}

