/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.TeleportationTrap;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.BArray;

public class WarpingTrap
extends TeleportationTrap {
    public WarpingTrap() {
        this.color = 4;
        this.shape = 3;
    }

    @Override
    public void activate() {
        if (Dungeon.level.distance(Dungeon.hero.pos, this.pos) <= 1) {
            BArray.setFalse(Dungeon.level.visited);
            BArray.setFalse(Dungeon.level.mapped);
        }
        super.activate();
        GameScene.updateFog();
        Dungeon.observe();
    }
}

