/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SwarmSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class Swarm
extends Mob {
    private static final float SPLIT_DELAY = 1.0f;
    int generation;
    private static final String GENERATION = "generation";

    public Swarm() {
        this.spriteClass = SwarmSprite.class;
        this.HT = 50;
        this.HP = 50;
        this.defenseSkill = 5;
        this.EXP = 3;
        this.maxLvl = 9;
        this.flying = true;
        this.loot = PotionOfHealing.class;
        this.lootChance = 0.1667f;
        this.generation = 0;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(GENERATION, this.generation);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.generation = bundle.getInt(GENERATION);
        if (this.generation > 0) {
            this.EXP = 0;
        }
    }

    @Override
    public void die(Object cause) {
        this.flying = false;
        super.die(cause);
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(1, 4);
    }

    @Override
    public int defenseProc(Char enemy, int damage) {
        if (this.HP >= damage + 2) {
            int[] neighbours;
            ArrayList<Integer> candidates = new ArrayList<Integer>();
            for (int n : neighbours = new int[]{this.pos + 1, this.pos - 1, this.pos + Dungeon.level.width(), this.pos - Dungeon.level.width()}) {
                if (Dungeon.level.solid[n] || Actor.findChar(n) != null || !Dungeon.level.passable[n] && !Dungeon.level.avoid[n] || this.properties().contains((Object)Char.Property.LARGE) && !Dungeon.level.openSpace[n]) continue;
                candidates.add(n);
            }
            if (candidates.size() > 0) {
                Swarm clone = this.split();
                clone.pos = (Integer)Random.element(candidates);
                clone.state = clone.HUNTING;
                GameScene.add(clone, 1.0f);
                clone.HP = (this.HP - damage) / 2;
                Actor.add(new Pushing(clone, this.pos, clone.pos));
                Dungeon.level.occupyCell(clone);
                this.HP -= clone.HP;
            }
        }
        return super.defenseProc(enemy, damage);
    }

    @Override
    public int attackSkill(Char target) {
        return 10;
    }

    private Swarm split() {
        Swarm clone = new Swarm();
        clone.generation = this.generation + 1;
        clone.EXP = 0;
        if (this.buff(Burning.class) != null) {
            Buff.affect(clone, Burning.class).reignite(clone);
        }
        if (this.buff(Poison.class) != null) {
            Buff.affect(clone, Poison.class).set(2.0f);
        }
        for (Buff b : this.buffs()) {
            if (!b.revivePersists) continue;
            Buff.affect(clone, b.getClass());
        }
        return clone;
    }

    @Override
    public float lootChance() {
        this.lootChance = 1.0f / (float)(6 * (this.generation + 1));
        return super.lootChance() * (5.0f - (float)Dungeon.LimitedDrops.SWARM_HP.count) / 5.0f;
    }

    @Override
    public Item createLoot() {
        ++Dungeon.LimitedDrops.SWARM_HP.count;
        return super.createLoot();
    }
}

