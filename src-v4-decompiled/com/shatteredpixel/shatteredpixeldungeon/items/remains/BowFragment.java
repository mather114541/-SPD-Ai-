/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.remains;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.remains.RemainsItem;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.ArrayList;
import java.util.Iterator;

public class BowFragment
extends RemainsItem {
    public BowFragment() {
        this.image = ItemSpriteSheet.BOW_FRAGMENT;
    }

    @Override
    protected void doEffect(Hero hero) {
        ArrayList<Integer> grassCells = new ArrayList<Integer>();
        for (int i : PathFinder.NEIGHBOURS9) {
            grassCells.add(hero.pos + i);
        }
        Random.shuffle(grassCells);
        Object object = grassCells.iterator();
        while (object.hasNext()) {
            int grassCell = (Integer)object.next();
            if (Dungeon.level.map[grassCell] == 1 || Dungeon.level.map[grassCell] == 9 || Dungeon.level.map[grassCell] == 20) {
                Level.set(grassCell, 2);
                GameScene.updateMap(grassCell);
            }
            CellEmitter.get(grassCell).burst(LeafParticle.LEVEL_SPECIFIC, 4);
        }
        int totalGrassCells = 5;
        while (grassCells.size() > totalGrassCells) {
            grassCells.remove(0);
        }
        Iterator iterator = grassCells.iterator();
        while (iterator.hasNext()) {
            int grassCell = (Integer)iterator.next();
            int t = Dungeon.level.map[grassCell];
            if (t != 1 && t != 20 && t != 9 && t != 2 && t != 30 || Dungeon.level.plants.get(grassCell) != null) continue;
            Level.set(grassCell, 15);
            GameScene.updateMap(grassCell);
        }
        Dungeon.observe();
        Sample.INSTANCE.play("sounds/plant.mp3");
    }
}

