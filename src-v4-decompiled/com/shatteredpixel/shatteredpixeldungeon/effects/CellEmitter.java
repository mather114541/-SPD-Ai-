/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.PointF;

public class CellEmitter {
    public static Emitter floor(int cell) {
        if (rl.RLMode.headless) return rl.RLNoopEmitter.shared();
        PointF p = DungeonTilemap.tileToWorld(cell);
        Emitter emitter = GameScene.floorEmitter();
        emitter.pos(p.x, p.y, 16.0f, 16.0f);
        return emitter;
    }

    public static Emitter get(int cell) {
        if (rl.RLMode.headless) return rl.RLNoopEmitter.shared();
        PointF p = DungeonTilemap.tileToWorld(cell);
        Emitter emitter = GameScene.emitter();
        emitter.pos(p.x, p.y, 16.0f, 16.0f);
        return emitter;
    }

    public static Emitter center(int cell) {
        if (rl.RLMode.headless) return rl.RLNoopEmitter.shared();
        PointF p = DungeonTilemap.tileToWorld(cell);
        Emitter emitter = GameScene.emitter();
        emitter.pos(p.x + 8.0f, p.y + 8.0f);
        return emitter;
    }

    public static Emitter bottom(int cell) {
        if (rl.RLMode.headless) return rl.RLNoopEmitter.shared();
        PointF p = DungeonTilemap.tileToWorld(cell);
        Emitter emitter = GameScene.emitter();
        emitter.pos(p.x, p.y + 16.0f, 16.0f, 0.0f);
        return emitter;
    }
}

