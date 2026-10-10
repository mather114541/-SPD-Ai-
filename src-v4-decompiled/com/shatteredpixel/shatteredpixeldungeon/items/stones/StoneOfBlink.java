/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.stones;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.Runestone;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class StoneOfBlink
extends Runestone {
    private static Ballistica throwPath;

    public StoneOfBlink() {
        this.image = ItemSpriteSheet.STONE_BLINK;
    }

    @Override
    public int throwPos(Hero user, int dst) {
        throwPath = new Ballistica(user.pos, dst, 7);
        return StoneOfBlink.throwPath.collisionPos;
    }

    @Override
    protected void onThrow(int cell) {
        if (Actor.findChar(cell) != null && StoneOfBlink.throwPath.dist >= 1) {
            cell = StoneOfBlink.throwPath.path.get(StoneOfBlink.throwPath.dist - 1);
        }
        throwPath = null;
        super.onThrow(cell);
    }

    @Override
    protected void activate(int cell) {
        ScrollOfTeleportation.teleportToLocation(curUser, cell);
    }
}

