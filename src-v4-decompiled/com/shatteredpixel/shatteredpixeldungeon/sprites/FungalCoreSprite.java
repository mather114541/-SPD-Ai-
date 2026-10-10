/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonWallsTilemap;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class FungalCoreSprite
extends MobSprite {
    boolean wasVisible = false;

    public FungalCoreSprite() {
        this.texture("sprites/fungal_core.png");
        TextureFilm frames = new TextureFilm(this.texture, 27, 27);
        this.idle = new MovieClip.Animation(0, true);
        this.idle.frames(frames, 0);
        this.run = new MovieClip.Animation(0, true);
        this.run.frames(frames, 0);
        this.attack = new MovieClip.Animation(24, false);
        this.attack.frames(frames, 0);
        this.zap = this.attack.clone();
        this.die = new MovieClip.Animation(12, false);
        this.die.frames(frames, 0);
        this.play(this.idle);
    }

    @Override
    public void update() {
        super.update();
        if (this.curAnim != this.die && this.ch != null && this.visible != this.wasVisible) {
            if (this.visible) {
                DungeonWallsTilemap.skipCells.add(this.ch.pos - 2 * Dungeon.level.width());
                DungeonWallsTilemap.skipCells.add(this.ch.pos - Dungeon.level.width());
            } else {
                DungeonWallsTilemap.skipCells.remove(this.ch.pos - 2 * Dungeon.level.width());
                DungeonWallsTilemap.skipCells.remove(this.ch.pos - Dungeon.level.width());
            }
            GameScene.updateMap(this.ch.pos - 2 * Dungeon.level.width());
            GameScene.updateMap(this.ch.pos - Dungeon.level.width());
            this.wasVisible = this.visible;
        }
    }

    @Override
    public void die() {
        super.die();
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

    @Override
    public int blood() {
        return -7812028;
    }
}

