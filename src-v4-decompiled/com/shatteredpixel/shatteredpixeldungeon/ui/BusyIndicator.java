/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.watabou.noosa.Image;

public class BusyIndicator
extends Image {
    public BusyIndicator() {
        this.copy(Icons.BUSY.get());
        this.origin.set(this.width / 2.0f, this.height / 2.0f);
        this.angularSpeed = 720.0f;
    }

    @Override
    public void update() {
        super.update();
        this.visible = Dungeon.hero.isAlive() && !Dungeon.hero.ready;
    }
}

