/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Thief;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.food.FrozenCarpaccio;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.levels.VaultLevel;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class Frost
extends FlavourBuff {
    public static final float DURATION = 10.0f;

    public Frost() {
        this.type = Buff.buffType.NEGATIVE;
        this.announced = true;
        this.immunities.add(Chill.class);
    }

    @Override
    public boolean attachTo(Char target) {
        Buff.detach(target, Burning.class);
        if (super.attachTo(target)) {
            ++target.paralysed;
            Buff.detach(target, Chill.class);
            if (target instanceof Hero && !(Dungeon.level instanceof VaultLevel)) {
                Hero hero = (Hero)target;
                ArrayList<Item> freezable = new ArrayList<Item>();
                if (!hero.belongings.lostInventory()) {
                    for (Item i : hero.belongings.backpack.items) {
                        if (i.unique || !(i instanceof Potion) && !(i instanceof MysteryMeat)) continue;
                        freezable.add(i);
                    }
                }
                if (!freezable.isEmpty()) {
                    FrozenCarpaccio carpaccio;
                    Item toFreeze = ((Item)Random.element(freezable)).detach(hero.belongings.backpack);
                    GLog.w(Messages.capitalize(Messages.get(this, "freezes", toFreeze.title())), new Object[0]);
                    if (toFreeze instanceof Potion) {
                        ((Potion)toFreeze).shatter(hero.pos);
                    } else if (toFreeze instanceof MysteryMeat && !(carpaccio = new FrozenCarpaccio()).collect(hero.belongings.backpack)) {
                        Dungeon.level.drop((Item)carpaccio, (int)target.pos).sprite.drop();
                    }
                }
            } else if (target instanceof Thief) {
                Item item = ((Thief)target).item;
                if (item instanceof Potion && !item.unique) {
                    ((Potion)((Thief)target).item).shatter(target.pos);
                    ((Thief)target).item = null;
                } else if (item instanceof MysteryMeat) {
                    ((Thief)target).item = new FrozenCarpaccio();
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void detach() {
        super.detach();
        if (this.target.paralysed > 0) {
            --this.target.paralysed;
        }
        if (Dungeon.level.water[this.target.pos]) {
            Buff.prolong(this.target, Chill.class, 5.0f);
        }
    }

    @Override
    public int icon() {
        return 15;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(0.0f, 0.75f, 1.0f);
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0.0f, (10.0f - this.visualcooldown()) / 10.0f);
    }

    @Override
    public void fx(boolean on) {
        if (on) {
            this.target.sprite.add(CharSprite.State.FROZEN);
            this.target.sprite.add(CharSprite.State.PARALYSED);
        } else {
            this.target.sprite.remove(CharSprite.State.FROZEN);
            if (this.target.paralysed <= 1) {
                this.target.sprite.remove(CharSprite.State.PARALYSED);
            }
        }
    }
}

