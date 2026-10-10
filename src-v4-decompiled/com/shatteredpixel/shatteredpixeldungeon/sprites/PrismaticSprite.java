/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.PrismaticImage;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MirrorSprite;
import com.watabou.noosa.Game;

public class PrismaticSprite
extends MirrorSprite {
    public PrismaticSprite() {
        float interval = Game.timeTotal % 9.0f / 3.0f;
        this.tint(interval > 2.0f ? interval - 2.0f : Math.max(0.0f, 1.0f - interval), interval > 1.0f ? Math.max(0.0f, 2.0f - interval) : interval, interval > 2.0f ? Math.max(0.0f, 3.0f - interval) : interval - 1.0f, 0.5f);
    }

    @Override
    public void updateArmor() {
        this.updateArmor(((PrismaticImage)this.ch).armTier);
    }

    @Override
    public void update() {
        super.update();
        if (this.flashTime <= 0.0f) {
            float interval = Game.timeTotal % 9.0f / 3.0f;
            this.tint(interval > 2.0f ? interval - 2.0f : Math.max(0.0f, 1.0f - interval), interval > 1.0f ? Math.max(0.0f, 2.0f - interval) : interval, interval > 2.0f ? Math.max(0.0f, 3.0f - interval) : interval - 1.0f, 0.5f);
        }
    }
}

