/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class DisintegrationTrap
extends Trap {
    public DisintegrationTrap() {
        this.color = 5;
        this.shape = 5;
        this.canBeHidden = false;
        this.avoidsHallways = true;
    }

    @Override
    public void activate() {
        Heap heap;
        Char target = Actor.findChar(this.pos);
        float range = (float)Math.max(6, Dungeon.level.viewDistance) + 0.5f;
        if (target == null) {
            float closestDist = Float.MAX_VALUE;
            for (Char ch : Actor.chars()) {
                if (!ch.isAlive()) continue;
                float curDist = Dungeon.level.trueDistance(this.pos, ch.pos);
                if (ch.invisible > 0) {
                    curDist = Math.max(curDist, range);
                }
                Ballistica bolt = new Ballistica(this.pos, ch.pos, 7);
                if (bolt.collisionPos != ch.pos || !(curDist < closestDist) && (curDist != closestDist || !(target instanceof Hero))) continue;
                target = ch;
                closestDist = curDist;
            }
            if (closestDist > range) {
                target = null;
            }
        }
        if ((heap = (Heap)Dungeon.level.heaps.get(this.pos)) != null) {
            heap.explode();
        }
        if (target != null) {
            Hero hero;
            if (target instanceof Mob) {
                Buff.prolong(target, Trap.HazardAssistTracker.class, 50.0f);
            }
            if (Dungeon.level.heroFOV[this.pos] || Dungeon.level.heroFOV[target.pos]) {
                Sample.INSTANCE.play("sounds/ray.mp3");
                ShatteredPixelDungeon.scene().add(new Beam.DeathRay(DungeonTilemap.tileCenterToWorld(this.pos), target.sprite.center()));
                Sample.INSTANCE.play("sounds/ray.mp3");
            }
            target.damage(Random.NormalIntRange(30, 50) + this.scalingDepth(), this);
            if (target == Dungeon.hero && !(hero = (Hero)target).isAlive()) {
                Badges.validateDeathFromGrimOrDisintTrap();
                Dungeon.fail(this);
                GLog.n(Messages.get(this, "ondeath", new Object[0]), new Object[0]);
                if (this.reclaimed) {
                    Badges.validateDeathFromFriendlyMagic();
                }
            }
        }
    }
}

