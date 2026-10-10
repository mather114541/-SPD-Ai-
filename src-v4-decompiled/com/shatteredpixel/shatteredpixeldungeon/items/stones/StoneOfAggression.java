/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.stones;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.Runestone;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;

public class StoneOfAggression
extends Runestone {
    public StoneOfAggression() {
        this.image = ItemSpriteSheet.STONE_AGGRESSION;
    }

    @Override
    protected void activate(int cell) {
        Char ch = Actor.findChar(cell);
        if (ch != null && !Char.hasProp(ch, Char.Property.BOSS) && !Char.hasProp(ch, Char.Property.MINIBOSS)) {
            Buff.prolong(ch, Aggression.class, 20.0f);
        }
        CellEmitter.center(cell).start(Speck.factory(5), 0.3f, 3);
        Sample.INSTANCE.play("sounds/read.mp3");
    }

    public static class Aggression
    extends FlavourBuff {
        public static final float DURATION = 20.0f;

        public Aggression() {
            this.type = Buff.buffType.NEGATIVE;
            this.announced = true;
        }

        @Override
        public int icon() {
            return 54;
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0.0f, (20.0f - this.visualcooldown()) / 20.0f);
        }

        @Override
        public void detach() {
            if (this.target.isAlive() && this.target.alignment == Char.Alignment.ENEMY) {
                for (Mob m : Dungeon.level.mobs) {
                    if (m.alignment == Char.Alignment.ENEMY && m.isTargeting(this.target)) {
                        m.aggro(null);
                    }
                    if (!(this.target instanceof Mob) || !((Mob)this.target).isTargeting(m)) continue;
                    ((Mob)this.target).aggro(null);
                }
            }
            super.detach();
        }
    }
}

