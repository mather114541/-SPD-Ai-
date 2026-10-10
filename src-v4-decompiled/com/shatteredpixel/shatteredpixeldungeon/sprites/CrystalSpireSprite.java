/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonWallsTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.RaisedTerrainTilemap;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public abstract class CrystalSpireSprite
extends MobSprite {
    boolean wasVisible = false;

    public CrystalSpireSprite() {
        this.texture("sprites/crystal_spire.png");
        TextureFilm frames = new TextureFilm(this.texture, 29, 40);
        int c = this.texOffset();
        this.idle = new MovieClip.Animation(1, true);
        this.idle.frames(frames, 0 + c);
        this.run = this.idle.clone();
        this.attack = this.idle.clone();
        this.zap = this.idle.clone();
        this.die = new MovieClip.Animation(1, false);
        this.die.frames(frames, 4 + c);
        this.play(this.idle);
    }

    public void updateIdle() {
        float hpPercent = 1.0f;
        if (this.ch != null) {
            hpPercent = (float)this.ch.HP / (float)this.ch.HT;
        }
        TextureFilm frames = new TextureFilm(this.texture, 29, 40);
        if (hpPercent > 0.9f) {
            this.idle.frames(frames, 0 + this.texOffset());
        } else if (hpPercent > 0.67f) {
            this.idle.frames(frames, 1 + this.texOffset());
        } else if (hpPercent > 0.33f) {
            this.idle.frames(frames, 2 + this.texOffset());
        } else {
            this.idle.frames(frames, 3 + this.texOffset());
        }
        this.play(this.idle, true);
        this.run = this.idle.clone();
        this.attack = this.idle.clone();
        this.zap = this.idle.clone();
    }

    @Override
    public void link(Char ch) {
        super.link(ch);
        this.updateIdle();
    }

    @Override
    public void update() {
        super.update();
        if (this.curAnim != this.die && this.ch != null && this.visible != this.wasVisible) {
            if (this.visible) {
                DungeonWallsTilemap.skipCells.add(this.ch.pos - 2 * Dungeon.level.width());
                DungeonWallsTilemap.skipCells.add(this.ch.pos - Dungeon.level.width());
                RaisedTerrainTilemap.skipCells.add(this.ch.pos - 2 * Dungeon.level.width());
                RaisedTerrainTilemap.skipCells.add(this.ch.pos - Dungeon.level.width());
            } else {
                DungeonWallsTilemap.skipCells.remove(this.ch.pos - 2 * Dungeon.level.width());
                DungeonWallsTilemap.skipCells.remove(this.ch.pos - Dungeon.level.width());
                RaisedTerrainTilemap.skipCells.remove(this.ch.pos - 2 * Dungeon.level.width());
                RaisedTerrainTilemap.skipCells.remove(this.ch.pos - Dungeon.level.width());
            }
            GameScene.updateMap(this.ch.pos - 2 * Dungeon.level.width());
            GameScene.updateMap(this.ch.pos - Dungeon.level.width());
            this.wasVisible = this.visible;
        }
    }

    @Override
    public void die() {
        super.die();
        Splash.around(this, this.blood(), 100);
        if (this.ch != null && this.visible) {
            DungeonWallsTilemap.skipCells.remove(this.ch.pos - 2 * Dungeon.level.width());
            DungeonWallsTilemap.skipCells.remove(this.ch.pos - Dungeon.level.width());
            GameScene.updateMap(this.ch.pos - 2 * Dungeon.level.width());
            GameScene.updateMap(this.ch.pos - Dungeon.level.width());
        }
    }

    @Override
    public void turnTo(int from, int to) {
    }

    protected abstract int texOffset();

    public static class Red
    extends CrystalSpireSprite {
        @Override
        protected int texOffset() {
            return 16;
        }

        @Override
        public int blood() {
            return -17613;
        }
    }

    public static class Green
    extends CrystalSpireSprite {
        @Override
        protected int texOffset() {
            return 8;
        }

        @Override
        public int blood() {
            return -7995448;
        }
    }

    public static class Blue
    extends CrystalSpireSprite {
        @Override
        protected int texOffset() {
            return 0;
        }

        @Override
        public int blood() {
            return -7412737;
        }
    }
}

