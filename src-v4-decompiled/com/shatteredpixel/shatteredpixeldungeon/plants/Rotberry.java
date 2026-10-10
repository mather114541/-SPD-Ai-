/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.plants;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AdrenalineSurge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Rotberry
extends Plant {
    public Rotberry() {
        this.image = 0;
        this.seedClass = Seed.class;
    }

    @Override
    public void activate(Char ch) {
        if (ch instanceof Hero && ((Hero)ch).subClass == HeroSubClass.WARDEN) {
            Buff.affect(ch, AdrenalineSurge.class).reset(1, AdrenalineSurge.DURATION);
        } else {
            GameScene.add(Blob.seed(this.pos, 100, ToxicGas.class));
        }
    }

    @Override
    public void wither() {
        Dungeon.level.uproot(this.pos);
        if (Dungeon.level.heroFOV[this.pos]) {
            CellEmitter.get(this.pos).burst(LeafParticle.GENERAL, 6);
        }
        Dungeon.level.drop((Item)new Seed(), (int)this.pos).sprite.drop();
    }

    public static class Seed
    extends Plant.Seed {
        public Seed() {
            this.image = ItemSpriteSheet.SEED_ROTBERRY;
            this.plantClass = Rotberry.class;
            this.unique = true;
        }

        @Override
        public int value() {
            return 30 * this.quantity;
        }

        @Override
        public int energyVal() {
            return 3 * this.quantity;
        }
    }
}

