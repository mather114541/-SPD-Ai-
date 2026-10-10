/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Waterskin;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import java.util.ArrayList;

public class Ankh
extends Item {
    public static final String AC_BLESS = "BLESS";
    private boolean blessed;
    private static final ItemSprite.Glowing WHITE = new ItemSprite.Glowing(0xFFFFCC);
    private static final String BLESSED = "blessed";

    public Ankh() {
        this.image = ItemSpriteSheet.ANKH;
        this.bones = true;
        this.blessed = false;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        Waterskin waterskin = hero.belongings.getItem(Waterskin.class);
        if (waterskin != null && waterskin.isFull() && !this.blessed) {
            actions.add(AC_BLESS);
        }
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        Waterskin waterskin;
        super.execute(hero, action);
        if (action.equals(AC_BLESS) && (waterskin = hero.belongings.getItem(Waterskin.class)) != null) {
            this.blessed = true;
            waterskin.empty();
            GLog.p(Messages.get(this, "bless", new Object[0]), new Object[0]);
            hero.spend(1.0f);
            hero.busy();
            Sample.INSTANCE.play("sounds/drink.mp3");
            CellEmitter.get(hero.pos).start(Speck.factory(2), 0.2f, 3);
            hero.sprite.operate(hero.pos);
        }
    }

    @Override
    public String desc() {
        if (this.blessed) {
            return Messages.get(this, "desc_blessed", new Object[0]);
        }
        return super.desc();
    }

    public boolean isBlessed() {
        return this.blessed;
    }

    public void bless() {
        this.blessed = true;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return this.isBlessed() ? WHITE : null;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(BLESSED, this.blessed);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.blessed = bundle.getBoolean(BLESSED);
    }

    @Override
    public int value() {
        return 50 * this.quantity;
    }
}

