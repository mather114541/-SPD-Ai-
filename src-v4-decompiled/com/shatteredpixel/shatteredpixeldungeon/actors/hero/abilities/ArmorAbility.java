/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

public abstract class ArmorAbility
implements Bundlable {
    protected float baseChargeUse = 35.0f;

    public void use(final ClassArmor armor, final Hero hero) {
        if (this.targetingPrompt() == null) {
            this.activate(armor, hero, hero.pos);
        } else {
            GameScene.selectCell(new CellSelector.Listener(){

                @Override
                public void onSelect(Integer cell) {
                    ArmorAbility.this.activate(armor, hero, cell);
                }

                @Override
                public String prompt() {
                    return ArmorAbility.this.targetingPrompt();
                }
            });
        }
    }

    public String targetingPrompt() {
        return null;
    }

    public boolean useTargeting() {
        return this.targetingPrompt() != null;
    }

    public int targetedPos(Char user, int dst) {
        return new Ballistica((int)user.pos, (int)dst, (int)7).collisionPos;
    }

    public float chargeUse(Hero hero) {
        float chargeUse = this.baseChargeUse;
        if (hero.hasTalent(Talent.HEROIC_ENERGY)) {
            switch (hero.pointsInTalent(Talent.HEROIC_ENERGY)) {
                default: {
                    chargeUse *= 0.88f;
                    break;
                }
                case 2: {
                    chargeUse *= 0.77f;
                    break;
                }
                case 3: {
                    chargeUse *= 0.68f;
                    break;
                }
                case 4: {
                    chargeUse *= 0.6f;
                }
            }
        }
        return chargeUse;
    }

    protected abstract void activate(ClassArmor var1, Hero var2, Integer var3);

    public String name() {
        return Messages.get(this, "name", new Object[0]);
    }

    public String shortDesc() {
        return Messages.get(this, "short_desc", new Object[0]);
    }

    public String desc() {
        return Messages.get(this, "desc", new Object[0]) + "\n\n" + Messages.get(this, "cost", (int)this.baseChargeUse);
    }

    public int icon() {
        return 127;
    }

    public abstract Talent[] talents();

    @Override
    public void storeInBundle(Bundle bundle) {
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
    }
}

