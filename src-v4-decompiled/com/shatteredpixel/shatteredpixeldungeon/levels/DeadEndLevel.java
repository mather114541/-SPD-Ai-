/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Bones;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class DeadEndLevel
extends Level {
    private static final int SIZE = 5;

    public DeadEndLevel() {
        this.color1 = 5459774;
        this.color2 = 12179041;
    }

    @Override
    public String tilesTex() {
        return "environment/tiles_caves.png";
    }

    @Override
    public String waterTex() {
        return "environment/water4.png";
    }

    @Override
    protected boolean build() {
        int i;
        this.setSize(7, 7);
        for (i = 2; i < 5; ++i) {
            for (int j = 2; j < 5; ++j) {
                this.map[i * this.width() + j] = 1;
            }
        }
        for (i = 1; i <= 5; ++i) {
            this.map[this.width() * i + 5] = 29;
            this.map[this.width() * i + 1] = 29;
            this.map[this.width() * 5 + i] = 29;
            this.map[this.width() + i] = 29;
        }
        int entrance = 5 * this.width() + 2 + 1;
        if (Dungeon.branch == 0) {
            this.transitions.add(new LevelTransition(this, entrance, LevelTransition.Type.REGULAR_ENTRANCE));
        } else {
            this.transitions.add(new LevelTransition(this, entrance, LevelTransition.Type.BRANCH_ENTRANCE, Dungeon.depth, 0, LevelTransition.Type.BRANCH_EXIT));
        }
        this.map[entrance] = 7;
        return true;
    }

    @Override
    public Mob createMob() {
        return null;
    }

    @Override
    protected void createMobs() {
    }

    @Override
    public Actor addRespawner() {
        return null;
    }

    @Override
    protected void createItems() {
        Random.pushGenerator(Random.Long());
        ArrayList<Item> bonesItems = Bones.get();
        if (bonesItems != null) {
            for (Item i : bonesItems) {
                this.drop((Item)i, (int)(this.entrance() - this.width())).setHauntedIfCursed().type = Heap.Type.REMAINS;
            }
        }
        Random.popGenerator();
    }

    @Override
    public int randomRespawnCell(Char ch) {
        return this.entrance() - this.width();
    }
}

