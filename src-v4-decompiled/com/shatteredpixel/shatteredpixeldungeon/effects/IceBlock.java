/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.noosa.Game;
import com.watabou.noosa.Gizmo;
import com.watabou.noosa.audio.Sample;

public class IceBlock
extends Gizmo {
    private float phase;
    private CharSprite target;

    public IceBlock(CharSprite target) {
        this.target = target;
        this.phase = 0.0f;
    }

    @Override
    public void update() {
        float f;
        super.update();
        this.phase += Game.elapsed * 2.0f;
        if (f < 1.0f) {
            this.target.tint(0.83f, 1.17f, 1.33f, this.phase * 0.6f);
        } else {
            this.target.tint(0.83f, 1.17f, 1.33f, 0.6f);
        }
    }

    public void melt() {
        this.target.resetColor();
        this.killAndErase();
        if (this.visible) {
            Splash.at(this.target.center(), -5056769, 5);
            Sample.INSTANCE.play("sounds/shatter.mp3");
        }
    }

    public static IceBlock freeze(CharSprite sprite) {
        IceBlock iceBlock = new IceBlock(sprite);
        if (sprite.parent != null) {
            sprite.parent.add(iceBlock);
        }
        return iceBlock;
    }
}

