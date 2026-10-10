/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Shadows;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShaftParticle;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.PathFinder;

public class Foliage
extends Blob {
    @Override
    public Notes.Landmark landmark() {
        return Notes.Landmark.GARDEN;
    }

    @Override
    protected void evolve() {
        Shadows s;
        int[] map = Dungeon.level.map;
        boolean seen = false;
        Fire fire = (Fire)Dungeon.level.blobs.get(Fire.class);
        for (int i = this.area.left; i < this.area.right; ++i) {
            for (int j = this.area.top; j < this.area.bottom; ++j) {
                int cell = i + j * Dungeon.level.width();
                if (this.cur[cell] > 0) {
                    this.off[cell] = this.cur[cell];
                    this.volume += this.off[cell];
                    if (map[cell] == 9) {
                        boolean valid = true;
                        if (fire != null && fire.volume > 0) {
                            for (int k : PathFinder.NEIGHBOURS9) {
                                if (fire.cur[cell + k] <= 0) continue;
                                valid = false;
                            }
                        }
                        if (valid) {
                            Level.set(cell, 2);
                            GameScene.updateMap(cell);
                        }
                    }
                    seen = seen || Dungeon.level.visited[cell];
                    continue;
                }
                this.off[cell] = 0;
            }
        }
        Hero hero = Dungeon.hero;
        if (hero.isAlive() && this.cur[hero.pos] > 0 && (s = Buff.affect(hero, Shadows.class)) != null) {
            s.prolong();
        }
    }

    @Override
    public void use(BlobEmitter emitter) {
        super.use(emitter);
        emitter.start(ShaftParticle.FACTORY, 0.9f, 0);
    }

    @Override
    public String tileDesc() {
        return Messages.get(this, "desc", new Object[0]);
    }
}

