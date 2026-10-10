/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.spells;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.Spell;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public abstract class TargetedSpell
extends Spell {
    protected int collisionProperties = 7;
    private static CellSelector.Listener targeter = new CellSelector.Listener(){

        @Override
        public void onSelect(Integer target) {
            if (target != null) {
                if (!(curItem instanceof TargetedSpell)) {
                    return;
                }
                final TargetedSpell curSpell = (TargetedSpell)curItem;
                final Ballistica shot = new Ballistica(curUser.pos, target, curSpell.collisionProperties);
                int cell = shot.collisionPos;
                curUser.sprite.zap(cell);
                if (Actor.findChar(target) != null) {
                    QuickSlotButton.target(Actor.findChar(target));
                } else {
                    QuickSlotButton.target(Actor.findChar(cell));
                }
                curUser.busy();
                curSpell.fx(shot, new Callback(){

                    @Override
                    public void call() {
                        curSpell.affectTarget(shot, curUser);
                    }
                });
            }
        }

        @Override
        public String prompt() {
            return Messages.get(TargetedSpell.class, "prompt", new Object[0]);
        }
    };

    @Override
    protected void onCast(Hero hero) {
        GameScene.selectCell(targeter);
    }

    protected abstract void affectTarget(Ballistica var1, Hero var2);

    protected void fx(Ballistica bolt, Callback callback) {
        MagicMissile.boltFromChar(TargetedSpell.curUser.sprite.parent, 0, TargetedSpell.curUser.sprite, bolt.collisionPos, callback);
        Sample.INSTANCE.play("sounds/zap.mp3");
    }

    protected void onSpellused() {
        this.detach(TargetedSpell.curUser.belongings.backpack);
        Invisibility.dispel();
        TargetedSpell.updateQuickslot();
        curUser.spendAndNext(this.timeToCast());
        Catalog.countUse(this.getClass());
        if (Random.Float() < this.talentChance) {
            Talent.onScrollUsed(curUser, TargetedSpell.curUser.pos, this.talentFactor, this.getClass());
        }
    }

    protected float timeToCast() {
        return 1.0f;
    }
}

