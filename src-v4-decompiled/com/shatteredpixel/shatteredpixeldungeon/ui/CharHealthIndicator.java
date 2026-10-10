/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.HealthBar;

public class CharHealthIndicator
extends HealthBar {
    private static final int HEIGHT = 1;
    private Char target;

    public CharHealthIndicator(Char c) {
        this.target = c;
        GameScene.add(this);
    }

    @Override
    protected void createChildren() {
        super.createChildren();
        this.height = 1.0f;
    }

    @Override
    public void update() {
        super.update();
        if (this.target != null && this.target.isAlive() && this.target.isActive() && this.target.sprite.visible) {
            CharSprite sprite = this.target.sprite;
            this.width = sprite.width() * 0.6666667f;
            this.x = sprite.x + sprite.width() / 6.0f;
            this.y = sprite.y - 2.0f;
            this.level(this.target);
            this.visible = this.target.HP < this.target.HT || this.target.shielding() > 0 || this.target.incomingDOT() > 0;
        } else {
            this.visible = false;
        }
    }

    public void target(Char ch) {
        this.target = ch != null && ch.isAlive() && ch.isActive() ? ch : null;
    }

    public Char target() {
        return this.target;
    }
}

