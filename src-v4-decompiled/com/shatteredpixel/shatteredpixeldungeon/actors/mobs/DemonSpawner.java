/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.RipperDemon;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SpawnerSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class DemonSpawner
extends Mob {
    private float spawnCooldown;
    public boolean spawnRecorded;
    public static final String SPAWN_COOLDOWN = "spawn_cooldown";
    public static final String SPAWN_RECORDED = "spawn_recorded";

    public DemonSpawner() {
        this.spriteClass = SpawnerSprite.class;
        this.HT = 120;
        this.HP = 120;
        this.defenseSkill = 0;
        this.EXP = 15;
        this.maxLvl = 29;
        this.state = this.PASSIVE;
        this.loot = PotionOfHealing.class;
        this.lootChance = 1.0f;
        this.properties.add(Char.Property.IMMOVABLE);
        this.properties.add(Char.Property.MINIBOSS);
        this.properties.add(Char.Property.DEMONIC);
        this.properties.add(Char.Property.STATIC);
        this.spawnCooldown = 0.0f;
        this.spawnRecorded = false;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 12);
    }

    @Override
    public void beckon(int cell) {
    }

    @Override
    public boolean reset() {
        return true;
    }

    @Override
    protected boolean act() {
        if (!this.spawnRecorded) {
            ++Statistics.spawnersAlive;
            this.spawnRecorded = true;
        }
        if (Dungeon.hero.buff(AscensionChallenge.class) != null && this.spawnCooldown > 20.0f) {
            this.spawnCooldown = 20.0f;
        }
        this.spawnCooldown -= 1.0f;
        if (this.spawnCooldown <= 0.0f) {
            if (this.spawnCooldown < -20.0f) {
                this.spawnCooldown = -20.0f;
            }
            ArrayList<Integer> candidates = new ArrayList<Integer>();
            for (int n : PathFinder.NEIGHBOURS8) {
                if (!Dungeon.level.passable[this.pos + n] || Actor.findChar(this.pos + n) != null) continue;
                candidates.add(this.pos + n);
            }
            if (!candidates.isEmpty()) {
                RipperDemon spawn = new RipperDemon();
                spawn.pos = (Integer)Random.element(candidates);
                spawn.state = spawn.HUNTING;
                GameScene.add(spawn, 1.0f);
                Dungeon.level.occupyCell(spawn);
                if (this.sprite.visible) {
                    Actor.add(new Pushing(spawn, this.pos, spawn.pos));
                }
                this.spawnCooldown += 60.0f;
                if (Dungeon.depth > 21) {
                    this.spawnCooldown = (float)((double)this.spawnCooldown - Math.min(20.0, (double)(Dungeon.depth - 21) * 6.67));
                }
            }
        }
        this.alerted = false;
        return super.act();
    }

    @Override
    public void damage(int dmg, Object src) {
        if (dmg >= 20) {
            dmg = 19 + (int)(Math.sqrt(8 * (dmg - 19) + 1) - 1.0) / 2;
        }
        this.spawnCooldown -= (float)dmg;
        super.damage(dmg, src);
    }

    @Override
    public Notes.Landmark landmark() {
        return Notes.Landmark.DEMON_SPAWNER;
    }

    @Override
    public void die(Object cause) {
        if (this.spawnRecorded) {
            --Statistics.spawnersAlive;
            Notes.remove(this.landmark());
        }
        GLog.h(Messages.get(this, "on_death", new Object[0]), new Object[0]);
        super.die(cause);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(SPAWN_COOLDOWN, this.spawnCooldown);
        bundle.put(SPAWN_RECORDED, this.spawnRecorded);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.spawnCooldown = bundle.getFloat(SPAWN_COOLDOWN);
        this.spawnRecorded = bundle.getBoolean(SPAWN_RECORDED);
    }
}

