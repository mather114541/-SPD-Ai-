/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.ui.Component;

public class HealthBar
extends Component {
    private static final int COLOR_BG = -5636096;
    private static final int COLOR_HP = -16716288;
    private static final int COLOR_SHLD = -1;
    private static final int COLOR_DOT = 0x66000000;
    private static final int HEIGHT = 2;
    private ColorBlock Bg;
    private ColorBlock Shld;
    private ColorBlock Hp;
    private ColorBlock Dot;
    private float health;
    private float shield;
    private float incomingDOT;

    @Override
    protected void createChildren() {
        this.Bg = new ColorBlock(1.0f, 1.0f, -5636096);
        this.add(this.Bg);
        this.Shld = new ColorBlock(1.0f, 1.0f, -1);
        this.add(this.Shld);
        this.Hp = new ColorBlock(1.0f, 1.0f, -16716288);
        this.add(this.Hp);
        this.Dot = new ColorBlock(1.0f, 1.0f, 0x66000000);
        this.add(this.Dot);
        this.height = 2.0f;
    }

    @Override
    protected void layout() {
        this.Hp.x = this.Dot.x = this.x;
        this.Shld.x = this.Dot.x;
        this.Bg.x = this.Dot.x;
        this.Hp.y = this.Dot.y = this.y;
        this.Shld.y = this.Dot.y;
        this.Bg.y = this.Dot.y;
        this.Bg.size(this.width, this.height);
        float pixelWidth = this.width;
        if (this.camera() != null) {
            pixelWidth *= this.camera().zoom;
        }
        this.Shld.size(this.width * (float)Math.ceil(this.shield * pixelWidth) / pixelWidth, this.height);
        this.Hp.size(this.width * (float)Math.ceil(this.health * pixelWidth) / pixelWidth, this.height);
        this.Dot.size(this.width * (float)Math.ceil(this.incomingDOT * pixelWidth) / pixelWidth, this.height);
        this.Dot.scale.x = Math.min(this.Dot.scale.x, this.Shld.scale.x);
        this.Dot.x += this.Shld.width() - this.Dot.width();
    }

    public void level(float value) {
        this.level(value, 0.0f, 0.0f);
    }

    public void level(float health, float shield, float DOT) {
        this.health = health;
        this.shield = shield;
        this.incomingDOT = DOT;
        this.layout();
    }

    public void level(Char c) {
        float health = c.HP;
        float shield = c.shielding();
        float incomingDot = c.incomingDOT();
        float max = Math.max(health + shield, (float)c.HT);
        this.level(health / max, (health + shield) / max, incomingDot / max);
    }
}

