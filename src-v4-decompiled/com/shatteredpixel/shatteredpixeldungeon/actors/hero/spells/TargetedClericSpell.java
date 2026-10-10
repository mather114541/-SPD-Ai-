/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.ClericSpell;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;

public abstract class TargetedClericSpell
extends ClericSpell {
    @Override
    public void onCast(final HolyTome tome, final Hero hero) {
        GameScene.selectCell(new CellSelector.Listener(){

            @Override
            public void onSelect(Integer cell) {
                TargetedClericSpell.this.onTargetSelected(tome, hero, cell);
            }

            @Override
            public String prompt() {
                return TargetedClericSpell.this.targetingPrompt();
            }
        });
    }

    @Override
    public int targetingFlags() {
        return 6;
    }

    protected String targetingPrompt() {
        return Messages.get(this, "prompt", new Object[0]);
    }

    protected abstract void onTargetSelected(HolyTome var1, Hero var2, Integer var3);
}

