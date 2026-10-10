/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.HealthBar;

public class TargetHealthIndicator
extends HealthBar {
    public static TargetHealthIndicator instance;
    private Char target;

    public TargetHealthIndicator() {
        instance = this;
    }

    @Override
    public void update() {
        super.update();
        if (this.target != null && this.target.isAlive() && this.target.isActive() && this.target.sprite != null && this.target.sprite.visible) {
            CharSprite sprite = this.target.sprite;
            this.width = sprite.width();
            this.x = sprite.x;
            this.y = sprite.y - 3.0f;
            this.level(this.target);
            this.visible = true;
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

