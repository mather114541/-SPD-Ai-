/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.features;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.audio.Sample;

public class Door {
    public static void enter(int pos) {
        Level.set(pos, 6);
        GameScene.updateMap(pos);
        if (Dungeon.level.heroFOV[pos]) {
            Dungeon.observe();
            Sample.INSTANCE.play("sounds/door_open.mp3");
        }
    }

    public static void leave(int pos) {
        int chars = 0;
        for (Char ch : Actor.chars()) {
            if (ch.pos != pos) continue;
            ++chars;
        }
        if (Dungeon.level.heaps.get(pos) == null && chars <= 1) {
            Level.set(pos, 5);
            GameScene.updateMap(pos);
            if (Dungeon.level.heroFOV[pos]) {
                Dungeon.observe();
            }
        }
    }
}

