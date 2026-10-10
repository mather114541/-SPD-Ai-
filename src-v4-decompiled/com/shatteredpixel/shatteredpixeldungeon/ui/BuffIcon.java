/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;

public class BuffIcon
extends Image {
    private static TextureFilm smallFilm;
    private static final int SML_SIZE = 7;
    private static TextureFilm largeFilm;
    private static final int LRG_SIZE = 16;
    private final boolean large;

    public BuffIcon(Buff buff, boolean large) {
        super(large ? "interfaces/large_buffs.png" : "interfaces/buffs.png");
        this.large = large;
        this.refresh(buff);
    }

    public BuffIcon(int icon, boolean large) {
        super(large ? "interfaces/large_buffs.png" : "interfaces/buffs.png");
        this.large = large;
        this.refresh(icon);
    }

    public void refresh(Buff buff) {
        this.refresh(buff.icon());
        buff.tintIcon(this);
    }

    public void refresh(int icon) {
        if (this.large) {
            if (largeFilm == null) {
                largeFilm = new TextureFilm(this.texture, 16, 16);
            }
            this.frame(largeFilm.get(icon));
        } else {
            if (smallFilm == null) {
                smallFilm = new TextureFilm(this.texture, 7, 7);
            }
            this.frame(smallFilm.get(icon));
        }
    }
}

