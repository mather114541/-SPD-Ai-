/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;

public class TalentIcon
extends Image {
    private static TextureFilm film;
    private static final int SIZE = 16;

    public TalentIcon(Talent talent) {
        this(talent.icon());
    }

    public TalentIcon(int icon) {
        super("interfaces/talent_icons.png");
        if (film == null) {
            film = new TextureFilm(this.texture, 16, 16);
        }
        this.frame(film.get(icon));
    }
}

