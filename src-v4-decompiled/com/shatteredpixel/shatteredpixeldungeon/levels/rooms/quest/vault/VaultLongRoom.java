/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault;

import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.VaultRoom;
import com.watabou.utils.Random;

public abstract class VaultLongRoom
extends VaultRoom {
    private boolean wide = Random.Int(2) == 0;

    protected boolean wide() {
        if (this.width() == this.height()) {
            return this.wide;
        }
        return this.width() > this.height();
    }

    @Override
    public int minWidth() {
        return this.wide() ? 21 : 11;
    }

    @Override
    public int maxWidth() {
        return this.minWidth();
    }

    @Override
    public int minHeight() {
        return this.wide() ? 11 : 21;
    }

    @Override
    public int maxHeight() {
        return this.minHeight();
    }

    @Override
    public int sizeFactor() {
        return 2;
    }
}

