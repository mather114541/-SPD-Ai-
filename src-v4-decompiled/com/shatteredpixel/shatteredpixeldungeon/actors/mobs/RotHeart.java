/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.RotLasher;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.journal.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.plants.Rotberry;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RotHeartSprite;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class RotHeart
extends Mob {
    public RotHeart() {
        this.spriteClass = RotHeartSprite.class;
        this.HT = 80;
        this.HP = 80;
        this.defenseSkill = 0;
        this.EXP = 4;
        this.state = this.PASSIVE;
        this.properties.add(Char.Property.IMMOVABLE);
        this.properties.add(Char.Property.MINIBOSS);
        this.properties.add(Char.Property.STATIC);
        this.immunities.add(ToxicGas.class);
    }

    @Override
    protected boolean act() {
        this.alerted = false;
        return super.act();
    }

    @Override
    public void damage(int dmg, Object src) {
        if (src instanceof Burning) {
            this.destroy();
            this.sprite.die();
        } else {
            super.damage(dmg, src);
        }
    }

    @Override
    public int defenseProc(Char enemy, int damage) {
        int openNearby = 0;
        for (int i : PathFinder.NEIGHBOURS8) {
            if (Dungeon.level.solid[this.pos + i]) continue;
            ++openNearby;
        }
        GameScene.add(Blob.seed(this.pos, 5 + 3 * openNearby, ToxicGas.class));
        return super.defenseProc(enemy, damage);
    }

    @Override
    public void beckon(int cell) {
    }

    @Override
    protected boolean getCloser(int target) {
        return false;
    }

    @Override
    public void destroy() {
        super.destroy();
        Bestiary.skipCountingEncounters = true;
        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[Dungeon.level.mobs.size()])) {
            if (!(mob instanceof RotLasher)) continue;
            mob.die(null);
        }
        Bestiary.skipCountingEncounters = false;
    }

    @Override
    public void die(Object cause) {
        super.die(cause);
        Dungeon.level.drop((Item)new Rotberry.Seed(), (int)this.pos).sprite.drop();
        Statistics.questScores[1] = Statistics.questScores[1] + 2000;
    }

    @Override
    public boolean reset() {
        return true;
    }

    @Override
    public int damageRoll() {
        return 0;
    }

    @Override
    public int attackSkill(Char target) {
        return 0;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 5);
    }
}

