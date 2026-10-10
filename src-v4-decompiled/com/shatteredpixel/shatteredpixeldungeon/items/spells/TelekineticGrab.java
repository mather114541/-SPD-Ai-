/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.spells;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.PinCushion;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DwarfKing;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.LiquidMetal;
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.TargetedSpell;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class TelekineticGrab
extends TargetedSpell {
    public TelekineticGrab() {
        this.image = ItemSpriteSheet.TELE_GRAB;
        this.talentChance = 0.125f;
    }

    @Override
    protected void fx(Ballistica bolt, Callback callback) {
        MagicMissile.boltFromChar(TelekineticGrab.curUser.sprite.parent, 6, TelekineticGrab.curUser.sprite, bolt.collisionPos, callback);
        Sample.INSTANCE.play("sounds/zap.mp3");
    }

    @Override
    protected float timeToCast() {
        return 0.0f;
    }

    @Override
    protected void affectTarget(Ballistica bolt, Hero hero) {
        Char ch = Actor.findChar(bolt.collisionPos);
        if (!(ch != null || bolt.path.size() <= bolt.dist + 1 || (ch = Actor.findChar(bolt.path.get(bolt.dist + 1))) instanceof DwarfKing && Dungeon.level.solid[ch.pos])) {
            ch = null;
        }
        float totalpickupTime = 0.0f;
        if (ch != null && ch.buff(PinCushion.class) != null) {
            while (ch.buff(PinCushion.class) != null) {
                Item item = ch.buff(PinCushion.class).grabOne();
                if (item.doPickUp(hero, ch.pos)) {
                    totalpickupTime += item.pickupDelay();
                    GLog.i(Messages.capitalize(Messages.get(hero, "you_now_have", item.name())), new Object[0]);
                    continue;
                }
                GLog.w(Messages.get(this, "cant_grab", new Object[0]), new Object[0]);
                Dungeon.level.drop((Item)item, (int)ch.pos).sprite.drop();
                break;
            }
            if (totalpickupTime > 1.0f) {
                hero.spend(-(totalpickupTime - 1.0f));
            }
        } else if (Dungeon.level.heaps.get(bolt.collisionPos) != null) {
            Heap h = (Heap)Dungeon.level.heaps.get(bolt.collisionPos);
            if (h.type != Heap.Type.HEAP) {
                GLog.w(Messages.get(this, "cant_grab", new Object[0]), new Object[0]);
                hero.spend(1.0f);
                h.sprite.drop();
            } else {
                while (!h.isEmpty()) {
                    Item item = h.peek();
                    if (item.doPickUp(hero, h.pos)) {
                        h.pickUp();
                        totalpickupTime += item.pickupDelay();
                        GLog.i(Messages.capitalize(Messages.get(hero, "you_now_have", item.name())), new Object[0]);
                        continue;
                    }
                    GLog.w(Messages.get(this, "cant_grab", new Object[0]), new Object[0]);
                    h.sprite.drop();
                    break;
                }
                if (totalpickupTime > 1.0f) {
                    hero.spend(-(totalpickupTime - 1.0f));
                }
            }
        } else {
            GLog.w(Messages.get(this, "no_target", new Object[0]), new Object[0]);
            hero.spend(1.0f);
        }
        this.onSpellused();
    }

    @Override
    public int value() {
        return (int)(50.0f * ((float)this.quantity / 8.0f));
    }

    @Override
    public int energyVal() {
        return (int)(10.0f * ((float)this.quantity / 8.0f));
    }

    public static class Recipe
    extends Recipe.SimpleRecipe {
        private static final int OUT_QUANTITY = 8;

        public Recipe() {
            this.inputs = new Class[]{LiquidMetal.class};
            this.inQuantity = new int[]{10};
            this.cost = 10;
            this.output = TelekineticGrab.class;
            this.outQuantity = 8;
        }
    }
}

