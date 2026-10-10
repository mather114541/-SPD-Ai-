/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Regrowth;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.FungalSpinnerSprite;
import com.watabou.utils.PathFinder;

public class FungalSpinner
extends Spinner {
    public FungalSpinner() {
        this.spriteClass = FungalSpinnerSprite.class;
        this.HT = 40;
        this.HP = 40;
        this.defenseSkill = 16;
        this.EXP = 7;
        this.maxLvl = -2;
        this.immunities.add(Regrowth.class);
    }

    @Override
    protected void applyWebToCell(int cell) {
        GameScene.add(Blob.seed(cell, 40, Regrowth.class));
    }

    @Override
    public void damage(int dmg, Object src) {
        int grassCells = 0;
        for (int i : PathFinder.NEIGHBOURS9) {
            if (Dungeon.level.map[this.pos + i] != 30 && Dungeon.level.map[this.pos + i] != 15) continue;
            ++grassCells;
        }
        if (grassCells > 0) {
            dmg = Math.round((float)(dmg * (8 - grassCells)) / 10.0f);
        }
        super.damage(dmg, src);
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        return damage;
    }
}

