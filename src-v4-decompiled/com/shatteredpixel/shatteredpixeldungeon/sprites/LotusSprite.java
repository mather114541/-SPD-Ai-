/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.particles.Emitter;
import java.util.ArrayList;

public class LotusSprite
extends MobSprite {
    private ArrayList<Emitter> grassVfx;

    public LotusSprite() {
        this.perspectiveRaise = 0.0f;
        this.texture("sprites/lotus.png");
        TextureFilm frames = new TextureFilm(this.texture, 19, 16);
        this.idle = new MovieClip.Animation(1, true);
        this.idle.frames(frames, 0);
        this.run = new MovieClip.Animation(1, true);
        this.run.frames(frames, 0);
        this.attack = new MovieClip.Animation(1, false);
        this.attack.frames(frames, 0);
        this.die = new MovieClip.Animation(1, false);
        this.die.frames(frames, 0);
        this.play(this.idle);
    }

    @Override
    public void link(Char ch) {
        super.link(ch);
        this.renderShadow = false;
        if (this.grassVfx == null && ch instanceof WandOfRegrowth.Lotus) {
            WandOfRegrowth.Lotus l = (WandOfRegrowth.Lotus)ch;
            this.grassVfx = new ArrayList();
            for (int i = 0; i < Dungeon.level.length(); ++i) {
                if (Dungeon.level.solid[i] || !l.inRange(i)) continue;
                Emitter e = CellEmitter.get(i);
                e.pour(LeafParticle.LEVEL_SPECIFIC, 0.5f);
                this.grassVfx.add(e);
            }
        }
    }

    @Override
    public void place(int cell) {
        if (this.parent != null) {
            this.parent.sendToBack(this);
        }
        super.place(cell);
    }

    @Override
    public void turnTo(int from, int to) {
    }

    @Override
    public void update() {
        this.visible = true;
        super.update();
    }

    @Override
    public void die() {
        super.die();
        if (this.grassVfx != null) {
            for (Emitter e : this.grassVfx) {
                e.on = false;
            }
            this.grassVfx = null;
        }
    }

    @Override
    public void kill() {
        super.kill();
        if (this.grassVfx != null) {
            for (Emitter e : this.grassVfx) {
                e.on = false;
            }
            this.grassVfx = null;
        }
    }
}

