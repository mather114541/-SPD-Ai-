/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM300;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.levels.CavesBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.PylonSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.ArrayList;
import java.util.Iterator;

public class Pylon
extends Mob {
    private int targetNeighbor;
    private static final String ALIGNMENT = "alignment";
    private static final String TARGET_NEIGHBOUR = "target_neighbour";

    public Pylon() {
        this.spriteClass = PylonSprite.class;
        this.HT = Dungeon.isChallenged(256) ? 80 : 50;
        this.HP = this.HT;
        this.maxLvl = -2;
        this.properties.add(Char.Property.MINIBOSS);
        this.properties.add(Char.Property.BOSS_MINION);
        this.properties.add(Char.Property.INORGANIC);
        this.properties.add(Char.Property.ELECTRIC);
        this.properties.add(Char.Property.IMMOVABLE);
        this.properties.add(Char.Property.STATIC);
        this.state = this.PASSIVE;
        this.alignment = Char.Alignment.NEUTRAL;
        this.targetNeighbor = Random.Int(8);
    }

    @Override
    protected boolean act() {
        int cell;
        if (this.fieldOfView == null || this.fieldOfView.length != Dungeon.level.length()) {
            this.fieldOfView = new boolean[Dungeon.level.length()];
        }
        Dungeon.level.updateFieldOfView(this, this.fieldOfView);
        this.throwItems();
        this.sprite.hideAlert();
        this.sprite.hideLost();
        this.enemy = this.chooseEnemy();
        boolean bl = this.enemySeen = this.enemy != null && this.enemy.isAlive() && this.fieldOfView[this.enemy.pos] && this.enemy.invisible <= 0;
        if (this.alignment == Char.Alignment.NEUTRAL) {
            this.spend(1.0f);
            return true;
        }
        ArrayList<Integer> shockCells = new ArrayList<Integer>();
        shockCells.add(this.pos + PathFinder.CIRCLE8[this.targetNeighbor]);
        if (Dungeon.isChallenged(256)) {
            shockCells.add(this.pos + PathFinder.CIRCLE8[(this.targetNeighbor + 3) % 8]);
            shockCells.add(this.pos + PathFinder.CIRCLE8[(this.targetNeighbor + 5) % 8]);
        } else {
            shockCells.add(this.pos + PathFinder.CIRCLE8[(this.targetNeighbor + 4) % 8]);
        }
        this.sprite.flash();
        boolean visible = Dungeon.level.heroFOV[this.pos];
        Iterator iterator = shockCells.iterator();
        while (iterator.hasNext()) {
            cell = (Integer)iterator.next();
            if (!Dungeon.level.heroFOV[cell]) continue;
            visible = true;
        }
        if (visible) {
            iterator = shockCells.iterator();
            while (iterator.hasNext()) {
                cell = (Integer)iterator.next();
                this.sprite.parent.add(new Lightning(this.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(cell), null));
                CellEmitter.get(cell).burst(SparkParticle.FACTORY, 3);
            }
            Sample.INSTANCE.play("sounds/lightning.mp3");
        }
        iterator = shockCells.iterator();
        while (iterator.hasNext()) {
            cell = (Integer)iterator.next();
            this.shockChar(Actor.findChar(cell));
        }
        this.targetNeighbor = (this.targetNeighbor + 1) % 8;
        this.spend(1.0f);
        return true;
    }

    private void shockChar(Char ch) {
        if (ch != null && !(ch instanceof DM300)) {
            ch.sprite.flash();
            ch.damage(Random.NormalIntRange(10, 20), new Electricity());
            if (ch == Dungeon.hero) {
                Statistics.qualifiedForBossChallengeBadge = false;
                Statistics.bossScores[2] = Statistics.bossScores[2] - 100;
                if (!ch.isAlive()) {
                    Dungeon.fail(DM300.class);
                    GLog.n(Messages.get(Electricity.class, "ondeath", new Object[0]), new Object[0]);
                }
            }
        }
    }

    public void activate() {
        this.alignment = Char.Alignment.ENEMY;
        this.state = this.HUNTING;
        ((PylonSprite)this.sprite).activate();
    }

    @Override
    public CharSprite sprite() {
        PylonSprite p = (PylonSprite)super.sprite();
        if (this.alignment != Char.Alignment.NEUTRAL) {
            p.activate();
        }
        return p;
    }

    @Override
    public void beckon(int cell) {
    }

    @Override
    public String description() {
        if (this.alignment == Char.Alignment.NEUTRAL) {
            return Messages.get(this, "desc_inactive", new Object[0]);
        }
        return Messages.get(this, "desc_active", new Object[0]);
    }

    @Override
    public boolean interact(Char c) {
        return true;
    }

    @Override
    public boolean add(Buff buff) {
        if (this.alignment != Char.Alignment.NEUTRAL) {
            return super.add(buff);
        }
        return false;
    }

    @Override
    public boolean isInvulnerable(Class effect) {
        return this.alignment == Char.Alignment.NEUTRAL || super.isInvulnerable(effect);
    }

    @Override
    public void damage(int dmg, Object src) {
        LockedFloor lock;
        if (dmg >= 15) {
            dmg = 14 + (int)(Math.sqrt(8 * (dmg - 14) + 1) - 1.0) / 2;
        }
        if ((lock = Dungeon.hero.buff(LockedFloor.class)) != null && !this.isImmune(src.getClass()) && !this.isInvulnerable(src.getClass())) {
            if (Dungeon.isChallenged(256)) {
                lock.addTime((float)dmg / 2.0f);
            } else {
                lock.addTime(dmg);
            }
        }
        super.damage(dmg, src);
    }

    @Override
    public void die(Object cause) {
        super.die(cause);
        ((CavesBossLevel)Dungeon.level).eliminatePylon();
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(ALIGNMENT, this.alignment);
        bundle.put(TARGET_NEIGHBOUR, this.targetNeighbor);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        this.alignment = bundle.getEnum(ALIGNMENT, Char.Alignment.class);
        super.restoreFromBundle(bundle);
        this.targetNeighbor = bundle.getInt(TARGET_NEIGHBOUR);
    }
}

