/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.potions;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BlobImmunity;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;
import java.util.ArrayList;

public class PotionOfPurity
extends Potion {
    private static final int DISTANCE = 3;
    private static ArrayList<Class> affectedBlobs;

    public PotionOfPurity() {
        this.icon = ItemSpriteSheet.Icons.POTION_PURITY;
        affectedBlobs = new ArrayList<Class>(new BlobImmunity().immunities());
    }

    @Override
    public void shatter(int cell) {
        PathFinder.buildDistanceMap(cell, BArray.not(Dungeon.level.solid, null), 3);
        ArrayList<Blob> blobs = new ArrayList<Blob>();
        for (Class c : affectedBlobs) {
            Blob b = Dungeon.level.blobs.get(c);
            if (b == null || b.volume <= 0) continue;
            blobs.add(b);
        }
        for (int i = 0; i < Dungeon.level.length(); ++i) {
            if (PathFinder.distance[i] >= Integer.MAX_VALUE) continue;
            for (Blob blob : blobs) {
                blob.clear(i);
            }
            if (!Dungeon.level.heroFOV[i]) continue;
            CellEmitter.get(i).burst(Speck.factory(101), 2);
        }
        this.splash(cell);
        if (Dungeon.level.heroFOV[cell]) {
            Sample.INSTANCE.play("sounds/shatter.mp3");
            this.identify();
            GLog.i(Messages.get(this, "freshness", new Object[0]), new Object[0]);
        }
    }

    @Override
    public void apply(Hero hero) {
        GLog.w(Messages.get(this, "protected", new Object[0]), new Object[0]);
        Buff.prolong(hero, BlobImmunity.class, 20.0f);
        SpellSprite.show(hero, 7);
        this.identify();
    }

    @Override
    public int value() {
        return this.isKnown() ? 40 * this.quantity : super.value();
    }
}

