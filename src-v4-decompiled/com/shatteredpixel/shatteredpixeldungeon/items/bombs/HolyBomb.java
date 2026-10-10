/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.bombs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class HolyBomb
extends Bomb {
    public HolyBomb() {
        this.image = ItemSpriteSheet.HOLY_BOMB;
    }

    @Override
    protected int explosionRange() {
        return 2;
    }

    @Override
    public void explode(int cell) {
        super.explode(cell);
        if (Dungeon.level.heroFOV[cell]) {
            new Flare(10, 64.0f).show(Dungeon.hero.sprite.parent, DungeonTilemap.tileCenterToWorld(cell), 2.0f);
        }
        ArrayList<Char> affected = new ArrayList<Char>();
        PathFinder.buildDistanceMap(cell, BArray.not(Dungeon.level.solid, null), this.explosionRange());
        for (int i = 0; i < PathFinder.distance.length; ++i) {
            Char ch;
            if (PathFinder.distance[i] >= Integer.MAX_VALUE || (ch = Actor.findChar(i)) == null) continue;
            affected.add(ch);
        }
        for (Char ch : affected) {
            if (!ch.properties().contains((Object)Char.Property.UNDEAD) && !ch.properties().contains((Object)Char.Property.DEMONIC)) continue;
            ch.sprite.emitter().start(ShadowParticle.UP, 0.05f, 10);
            int damage = Math.round((float)Random.NormalIntRange(Dungeon.scalingDepth() + 4, 12 + 3 * Dungeon.scalingDepth()) * 0.5f);
            ch.damage(damage, new HolyDamage());
        }
        Sample.INSTANCE.play("sounds/read.mp3");
    }

    @Override
    public int value() {
        return this.quantity * 50;
    }

    public static class HolyDamage {
    }
}

