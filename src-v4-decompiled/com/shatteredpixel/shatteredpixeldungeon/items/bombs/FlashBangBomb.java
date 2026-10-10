/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.bombs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class FlashBangBomb
extends Bomb {
    public FlashBangBomb() {
        this.image = ItemSpriteSheet.FLASHBANG;
    }

    @Override
    protected int explosionRange() {
        return 2;
    }

    @Override
    public void explode(int cell) {
        super.explode(cell);
        ArrayList<Char> affected = new ArrayList<Char>();
        PathFinder.buildDistanceMap(cell, BArray.not(Dungeon.level.solid, null), this.explosionRange());
        for (int i = 0; i < PathFinder.distance.length; ++i) {
            if (PathFinder.distance[i] >= Integer.MAX_VALUE || Actor.findChar(i) == null) continue;
            affected.add(Actor.findChar(i));
        }
        ArrayList<Lightning.Arc> arcs = new ArrayList<Lightning.Arc>();
        for (Char ch : affected) {
            int damage = Math.round((float)Random.NormalIntRange(4 + Dungeon.scalingDepth(), 12 + 3 * Dungeon.scalingDepth()) / 4.0f);
            ch.damage(damage, new Electricity());
            if (ch.isAlive()) {
                Buff.prolong(ch, Paralysis.class, 10.0f);
            }
            arcs.add(new Lightning.Arc(DungeonTilemap.tileCenterToWorld(cell), ch.sprite.center()));
            if (ch == Dungeon.hero) {
                GameScene.flash(-2130706433);
            }
            if (ch != Dungeon.hero || ch.isAlive()) continue;
            Badges.validateDeathFromFriendlyMagic();
            GLog.n(Messages.get(this, "ondeath", new Object[0]), new Object[0]);
            Dungeon.fail(this);
        }
        CellEmitter.center(cell).burst(SparkParticle.FACTORY, 20);
        Dungeon.hero.sprite.parent.addToFront(new Lightning(arcs, null));
        Sample.INSTANCE.play("sounds/lightning.mp3");
    }

    @Override
    public int value() {
        return this.quantity * 50;
    }
}

