/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class Blooming
extends Weapon.Enchantment {
    private static ItemSprite.Glowing DARK_GREEN = new ItemSprite.Glowing(34816);

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        int level = Math.max(0, weapon.buffedLvl());
        float procChance = ((float)level + 1.0f) / ((float)level + 3.0f) * this.procChanceMultiplier(attacker);
        if (Random.Float() < procChance) {
            float powerMulti = Math.max(1.0f, procChance);
            float plants = (1.0f + 0.1f * (float)level) * powerMulti;
            plants = Random.Float() < plants % 1.0f ? (float)Math.ceil(plants) : (float)Math.floor(plants);
            if (this.plantGrass(defender.pos) && (plants -= 1.0f) <= 0.0f) {
                return damage;
            }
            ArrayList<Integer> positions = new ArrayList<Integer>();
            for (int i : PathFinder.NEIGHBOURS8) {
                if (defender.pos + i == attacker.pos) continue;
                positions.add(defender.pos + i);
            }
            Random.shuffle(positions);
            if (Dungeon.level.adjacent(attacker.pos, defender.pos)) {
                positions.add(attacker.pos);
            }
            Object object = positions.iterator();
            while (object.hasNext()) {
                int i = (Integer)object.next();
                if (!this.plantGrass(i) || !((plants -= 1.0f) <= 0.0f)) continue;
                return damage;
            }
        }
        return damage;
    }

    private boolean plantGrass(int cell) {
        int t = Dungeon.level.map[cell];
        if ((t == 1 || t == 20 || t == 9 || t == 2 || t == 30) && Dungeon.level.plants.get(cell) == null) {
            if (!Regeneration.regenOn()) {
                Level.set(cell, 30);
            } else {
                Level.set(cell, 15);
            }
            GameScene.updateMap(cell);
            CellEmitter.get(cell).burst(LeafParticle.LEVEL_SPECIFIC, 4);
            return true;
        }
        return false;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return DARK_GREEN;
    }
}

