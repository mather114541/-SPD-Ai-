/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.stones;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EnergyParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.Runestone;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;
import java.util.ArrayList;

public class StoneOfShock
extends Runestone {
    public StoneOfShock() {
        this.image = ItemSpriteSheet.STONE_SHOCK;
    }

    @Override
    protected void activate(int cell) {
        Sample.INSTANCE.play("sounds/lightning.mp3");
        ArrayList<Lightning.Arc> arcs = new ArrayList<Lightning.Arc>();
        int hits = 0;
        PathFinder.buildDistanceMap(cell, BArray.not(Dungeon.level.solid, null), 2);
        for (int i = 0; i < PathFinder.distance.length; ++i) {
            Char n;
            if (PathFinder.distance[i] >= Integer.MAX_VALUE || (n = Actor.findChar(i)) == null) continue;
            arcs.add(new Lightning.Arc(cell, n.sprite.center()));
            Buff.prolong(n, Paralysis.class, 1.0f);
            ++hits;
        }
        CellEmitter.center(cell).burst(SparkParticle.FACTORY, 3);
        if (hits > 0) {
            StoneOfShock.curUser.sprite.parent.addToFront(new Lightning(arcs, null));
            StoneOfShock.curUser.sprite.centerEmitter().burst(EnergyParticle.FACTORY, 10);
            Sample.INSTANCE.play("sounds/lightning.mp3");
            StoneOfShock.curUser.belongings.charge(1.0f + (float)hits);
        }
    }
}

