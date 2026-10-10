/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Shopkeeper;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ImpSprite;

public class ImpShopkeeper
extends Shopkeeper {
    private boolean seenBefore;

    public ImpShopkeeper() {
        this.spriteClass = ImpSprite.class;
        this.seenBefore = false;
    }

    @Override
    protected boolean act() {
        if (!this.seenBefore && Dungeon.level.heroFOV[this.pos]) {
            if (Dungeon.hero.buff(AscensionChallenge.class) == null) {
                this.yell(Messages.get(this, "greetings", Messages.titleCase(Dungeon.hero.name())));
            } else {
                this.yell(Messages.get(this, "greetings_ascent", Messages.titleCase(Dungeon.hero.name())));
            }
            this.seenBefore = true;
        }
        return super.act();
    }
}

