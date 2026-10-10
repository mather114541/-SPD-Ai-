/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.quest;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class DwarfToken
extends Item {
    public DwarfToken() {
        this.image = ItemSpriteSheet.TOKEN;
        this.stackable = true;
        this.unique = true;
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
    public boolean doPickUp(Hero hero, int pos) {
        if (Imp.Quest.mirrorUsed) {
            GLog.i(Messages.get(this, "discard", new Object[0]), new Object[0]);
            hero.next();
            return true;
        }
        return super.doPickUp(hero, pos);
    }

    @Override
    public String desc() {
        if (Imp.Quest.isOld()) {
            return Messages.get(this, "desc_old", new Object[0]);
        }
        return super.desc();
    }
}

