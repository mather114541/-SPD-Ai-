/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.bombs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ShadowCaster;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class ShrapnelBomb
extends Bomb {
    public ShrapnelBomb() {
        this.image = ItemSpriteSheet.SHRAPNEL_BOMB;
    }

    @Override
    public boolean explodesDestructively() {
        return false;
    }

    @Override
    protected int explosionRange() {
        return 8;
    }

    @Override
    public void explode(int cell) {
        super.explode(cell);
        boolean[] FOV = new boolean[Dungeon.level.length()];
        Point c = Dungeon.level.cellToPoint(cell);
        ShadowCaster.castShadow(c.x, c.y, Dungeon.level.width(), FOV, Dungeon.level.losBlocking, this.explosionRange());
        ArrayList<Char> affected = new ArrayList<Char>();
        for (int i = 0; i < FOV.length; ++i) {
            Char ch;
            if (!FOV[i]) continue;
            if (Dungeon.level.heroFOV[i] && !Dungeon.level.solid[i]) {
                CellEmitter.center(i).burst(BlastParticle.FACTORY, 5);
            }
            if ((ch = Actor.findChar(i)) == null) continue;
            affected.add(ch);
        }
        for (Char ch : affected) {
            int damage = Random.NormalIntRange(4 + Dungeon.scalingDepth(), 12 + 3 * Dungeon.scalingDepth());
            ch.damage(damage -= ch.drRoll(), this);
            if (ch != Dungeon.hero || ch.isAlive()) continue;
            Dungeon.fail(this);
        }
    }

    @Override
    public int value() {
        return this.quantity * 70;
    }
}

