/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.Dart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.TippedDart;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class IncendiaryDart
extends TippedDart {
    public IncendiaryDart() {
        this.image = ItemSpriteSheet.INCENDIARY_DART;
    }

    @Override
    protected void onThrow(int cell) {
        Char enemy = Actor.findChar(cell);
        if ((enemy == null || enemy == curUser) && Dungeon.level.flamable[cell]) {
            GameScene.add(Blob.seed(cell, 4, Fire.class));
            this.decrementDurability();
            if (this.durability > 0.0f || this.spawnedForEffect) {
                super.onThrow(cell);
            } else {
                Dungeon.level.drop((Item)new Dart().quantity((int)1), (int)cell).sprite.drop();
            }
        } else {
            super.onThrow(cell);
        }
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (!this.processingChargedShot || attacker.alignment != defender.alignment) {
            Buff.affect(defender, Burning.class).reignite(defender);
        }
        return super.proc(attacker, defender, damage);
    }
}

