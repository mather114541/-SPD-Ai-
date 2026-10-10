/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.bombs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Regrowth;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.plants.Starflower;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class RegrowthBomb
extends Bomb {
    public RegrowthBomb() {
        this.image = ItemSpriteSheet.REGROWTH_BOMB;
    }

    @Override
    public boolean explodesDestructively() {
        return false;
    }

    @Override
    protected int explosionRange() {
        return 3;
    }

    @Override
    public void explode(int cell) {
        super.explode(cell);
        if (Dungeon.level.heroFOV[cell]) {
            Splash.at(cell, 65280, 30);
        }
        ArrayList<Integer> plantCandidates = new ArrayList<Integer>();
        PathFinder.buildDistanceMap(cell, BArray.not(Dungeon.level.solid, null), this.explosionRange());
        for (int i = 0; i < PathFinder.distance.length; ++i) {
            if (PathFinder.distance[i] >= Integer.MAX_VALUE) continue;
            Char ch = Actor.findChar(i);
            int t = Dungeon.level.map[i];
            if (ch != null) {
                if (ch.alignment == Dungeon.hero.alignment) {
                    PotionOfHealing.cure(ch);
                    PotionOfHealing.heal(ch);
                }
            } else if ((t == 1 || t == 20 || t == 9 || t == 2 || t == 30 || t == 15) && Dungeon.level.plants.get(i) == null) {
                plantCandidates.add(i);
            }
            GameScene.add(Blob.seed(i, 10, Regrowth.class));
        }
        int plants = Random.chances(new float[]{0.0f, 0.0f, 2.0f, 1.0f});
        for (int i = 0; i < plants; ++i) {
            Integer plantPos = (Integer)Random.element(plantCandidates);
            if (plantPos == null) continue;
            Dungeon.level.plant((Plant.Seed)Generator.randomUsingDefaults(Generator.Category.SEED), plantPos);
            plantCandidates.remove(plantPos);
        }
        Integer plantPos = (Integer)Random.element(plantCandidates);
        if (plantPos != null) {
            Plant.Seed plant;
            switch (Random.chances(new float[]{0.0f, 6.0f, 3.0f, 1.0f})) {
                default: {
                    plant = new WandOfRegrowth.Dewcatcher.Seed();
                    break;
                }
                case 2: {
                    plant = new WandOfRegrowth.Seedpod.Seed();
                    break;
                }
                case 3: {
                    plant = new Starflower.Seed();
                }
            }
            Dungeon.level.plant(plant, plantPos);
        }
    }

    @Override
    public int value() {
        return this.quantity * 50;
    }
}

