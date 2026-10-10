/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.spells;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.TargetedSpell;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class PhaseShift
extends TargetedSpell {
    public PhaseShift() {
        this.image = ItemSpriteSheet.PHASE_SHIFT;
        this.usesTargeting = true;
        this.talentChance = 0.16666667f;
    }

    @Override
    protected void affectTarget(Ballistica bolt, Hero hero) {
        Char ch = Actor.findChar(bolt.collisionPos);
        if (ch != null) {
            if (ScrollOfTeleportation.teleportChar(ch)) {
                if (ch instanceof Mob) {
                    if (((Mob)ch).state == ((Mob)ch).HUNTING) {
                        ((Mob)ch).state = ((Mob)ch).WANDERING;
                    }
                    ((Mob)ch).beckon(Dungeon.level.randomDestination(ch));
                }
                if (!Char.hasProp(ch, Char.Property.BOSS) && !Char.hasProp(ch, Char.Property.MINIBOSS)) {
                    Buff.affect(ch, Paralysis.class, 10.0f);
                }
            }
        } else {
            GLog.w(Messages.get(this, "no_target", new Object[0]), new Object[0]);
        }
        this.onSpellused();
    }

    @Override
    public int value() {
        return (int)(60.0f * ((float)this.quantity / 6.0f));
    }

    @Override
    public int energyVal() {
        return (int)(12.0f * ((float)this.quantity / 6.0f));
    }

    public static class Recipe
    extends Recipe.SimpleRecipe {
        private static final int OUT_QUANTITY = 6;

        public Recipe() {
            this.inputs = new Class[]{ScrollOfTeleportation.class};
            this.inQuantity = new int[]{1};
            this.cost = 10;
            this.output = PhaseShift.class;
            this.outQuantity = 6;
        }
    }
}

