/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.stones;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Sheep;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.Runestone;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;
import java.util.ArrayList;
import java.util.Iterator;

public class StoneOfFlock
extends Runestone {
    public StoneOfFlock() {
        this.image = ItemSpriteSheet.STONE_FLOCK;
    }

    @Override
    protected void activate(int cell) {
        PathFinder.buildDistanceMap(cell, BArray.not(Dungeon.level.solid, null), 2);
        ArrayList<Integer> spawnPoints = new ArrayList<Integer>();
        for (int i = 0; i < PathFinder.distance.length; ++i) {
            if (PathFinder.distance[i] >= Integer.MAX_VALUE) continue;
            spawnPoints.add(i);
        }
        Iterator iterator = spawnPoints.iterator();
        while (iterator.hasNext()) {
            int i = (Integer)iterator.next();
            if (!Dungeon.level.insideMap(i) || Actor.findChar(i) != null || Dungeon.level.pit[i]) continue;
            Sheep sheep = new Sheep();
            sheep.initialize(8.0f);
            sheep.pos = i;
            GameScene.add(sheep);
            Dungeon.level.occupyCell(sheep);
            CellEmitter.get(i).burst(Speck.factory(7), 4);
        }
        CellEmitter.get(cell).burst(Speck.factory(7), 4);
        Sample.INSTANCE.play("sounds/puff.mp3");
        Sample.INSTANCE.play("sounds/sheep.mp3");
    }
}

