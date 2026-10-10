/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.ClericSpell;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Piranha;
import com.shatteredpixel.shatteredpixeldungeon.items.food.PhantomMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.PhantomPiranhaSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class PhantomPiranha
extends Piranha {
    public PhantomPiranha() {
        this.spriteClass = PhantomPiranhaSprite.class;
        this.loot = PhantomMeat.class;
        this.lootChance = 1.0f;
    }

    @Override
    public void damage(int dmg, Object src) {
        Char dmgSource = null;
        if (src instanceof Char) {
            dmgSource = (Char)src;
        }
        if (src instanceof Wand || src instanceof ClericSpell) {
            dmgSource = Dungeon.hero;
        }
        if (dmgSource == null || !Dungeon.level.adjacent(this.pos, dmgSource.pos)) {
            dmg = Math.round((float)dmg / 2.0f);
        }
        super.damage(dmg, src);
        if (this.isAlive() && !(src instanceof Corruption)) {
            if (dmgSource != null) {
                if (!Dungeon.level.adjacent(this.pos, dmgSource.pos)) {
                    ArrayList<Integer> candidates = new ArrayList<Integer>();
                    for (int i : PathFinder.NEIGHBOURS8) {
                        if (!Dungeon.level.water[dmgSource.pos + i] || Actor.findChar(dmgSource.pos + i) != null) continue;
                        candidates.add(dmgSource.pos + i);
                    }
                    if (!candidates.isEmpty()) {
                        ScrollOfTeleportation.appear(this, (Integer)Random.element(candidates));
                        this.aggro(dmgSource);
                    } else {
                        this.teleportAway();
                    }
                }
            } else {
                this.teleportAway();
            }
        }
    }

    @Override
    public int defenseProc(Char enemy, int damage) {
        return super.defenseProc(enemy, damage);
    }

    @Override
    public void dieOnLand() {
        if (!this.teleportAway()) {
            super.dieOnLand();
        }
    }

    private boolean teleportAway() {
        if (this.flying) {
            return false;
        }
        ArrayList<Integer> inFOVCandidates = new ArrayList<Integer>();
        ArrayList<Integer> outFOVCandidates = new ArrayList<Integer>();
        for (int i = 0; i < Dungeon.level.length(); ++i) {
            if (!Dungeon.level.water[i] || Actor.findChar(i) != null) continue;
            if (Dungeon.level.heroFOV[i]) {
                inFOVCandidates.add(i);
                continue;
            }
            outFOVCandidates.add(i);
        }
        if (!outFOVCandidates.isEmpty()) {
            if (Dungeon.level.heroFOV[this.pos]) {
                GLog.i(Messages.get(this, "teleport_away", new Object[0]), new Object[0]);
            }
            ScrollOfTeleportation.appear(this, (Integer)Random.element(outFOVCandidates));
            return true;
        }
        if (!inFOVCandidates.isEmpty()) {
            ScrollOfTeleportation.appear(this, (Integer)Random.element(inFOVCandidates));
            return true;
        }
        return false;
    }
}

