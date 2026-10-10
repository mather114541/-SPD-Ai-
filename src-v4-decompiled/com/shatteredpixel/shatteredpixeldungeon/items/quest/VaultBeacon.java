/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.quest;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.Spell;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class VaultBeacon
extends Spell {
    private static final ItemSprite.Glowing WHITE = new ItemSprite.Glowing(0xFFFFFF);

    public VaultBeacon() {
        this.image = ItemSpriteSheet.RETURN_BEACON;
        this.stackable = true;
        this.unique = true;
    }

    @Override
    protected void onCast(Hero hero) {
        if (ScrollOfTeleportation.teleportToLocation(hero, Dungeon.level.entrance())) {
            hero.spendAndNext(1.0f);
            Catalog.countUse(this.getClass());
            this.detach(hero.belongings.backpack);
        }
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return WHITE;
    }
}

