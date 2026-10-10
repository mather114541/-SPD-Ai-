/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Image;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;

public class Momentum
extends Buff
implements ActionIndicator.Action {
    private int momentumStacks;
    private int freerunTurns;
    private int freerunCooldown;
    private boolean movedLastTurn;
    private static final String STACKS = "stacks";
    private static final String FREERUN_TURNS = "freerun_turns";
    private static final String FREERUN_CD = "freerun_CD";

    public Momentum() {
        this.type = Buff.buffType.POSITIVE;
        this.actPriority = 1;
        this.momentumStacks = 0;
        this.freerunTurns = 0;
        this.freerunCooldown = 0;
        this.movedLastTurn = true;
    }

    @Override
    public void detach() {
        super.detach();
        ActionIndicator.clearAction(this);
    }

    @Override
    public boolean act() {
        if (this.freerunCooldown > 0) {
            --this.freerunCooldown;
        }
        if (this.freerunCooldown == 0 && !this.freerunning() && this.target.invisible > 0 && Dungeon.hero.pointsInTalent(Talent.SPEEDY_STEALTH) >= 1) {
            this.momentumStacks = Math.min(this.momentumStacks + 2, 10);
            this.movedLastTurn = true;
            ActionIndicator.setAction(this);
            BuffIndicator.refreshHero();
        }
        if (this.freerunTurns > 0) {
            if (this.target.invisible == 0 || Dungeon.hero.pointsInTalent(Talent.SPEEDY_STEALTH) < 2) {
                --this.freerunTurns;
            }
        } else if (!this.movedLastTurn) {
            this.momentumStacks = (int)GameMath.gate(0.0f, this.momentumStacks - 1, Math.round((float)this.momentumStacks * 0.667f));
            if (this.momentumStacks <= 0) {
                ActionIndicator.clearAction(this);
                BuffIndicator.refreshHero();
            } else {
                ActionIndicator.refresh();
            }
        }
        this.movedLastTurn = false;
        this.spend(1.0f);
        return true;
    }

    public void gainStack() {
        this.movedLastTurn = true;
        if (this.freerunCooldown <= 0 && !this.freerunning()) {
            this.postpone(this.target.cooldown() + 1.0f / this.target.speed());
            this.momentumStacks = Math.min(this.momentumStacks + 1, 10);
            ActionIndicator.setAction(this);
            BuffIndicator.refreshHero();
        }
    }

    public boolean freerunning() {
        return this.freerunTurns > 0;
    }

    public float speedMultiplier() {
        if (this.freerunning()) {
            return 2.0f;
        }
        if (this.target.invisible > 0 && Dungeon.hero.pointsInTalent(Talent.SPEEDY_STEALTH) == 3) {
            return 2.0f;
        }
        return 1.0f;
    }

    public int evasionBonus(int heroLvl, int excessArmorStr) {
        if (this.freerunTurns > 0) {
            return heroLvl / 2 + excessArmorStr * Dungeon.hero.pointsInTalent(Talent.EVASIVE_ARMOR);
        }
        return 0;
    }

    @Override
    public int icon() {
        if (this.momentumStacks > 0 || this.freerunCooldown > 0) {
            return 51;
        }
        return 127;
    }

    @Override
    public void tintIcon(Image icon) {
        if (this.freerunCooldown == 0 || this.freerunTurns > 0) {
            icon.hardlight(1.0f, 1.0f, 0.0f);
        } else {
            icon.hardlight(0.5f, 0.5f, 1.0f);
        }
    }

    @Override
    public float iconFadePercent() {
        if (this.freerunTurns > 0) {
            return (float)(20 - this.freerunTurns) / 20.0f;
        }
        if (this.freerunCooldown > 0) {
            return (float)this.freerunCooldown / 30.0f;
        }
        return 0.0f;
    }

    @Override
    public String iconTextDisplay() {
        if (this.freerunTurns > 0) {
            return Integer.toString(this.freerunTurns);
        }
        if (this.freerunCooldown > 0) {
            return Integer.toString(this.freerunCooldown);
        }
        return "";
    }

    @Override
    public String name() {
        if (this.freerunTurns > 0) {
            return Messages.get(this, "running", new Object[0]);
        }
        if (this.freerunCooldown > 0) {
            return Messages.get(this, "resting", new Object[0]);
        }
        return Messages.get(this, "momentum", new Object[0]);
    }

    @Override
    public String desc() {
        if (this.freerunTurns > 0) {
            return Messages.get(this, "running_desc", this.freerunTurns);
        }
        if (this.freerunCooldown > 0) {
            return Messages.get(this, "resting_desc", this.freerunCooldown);
        }
        return Messages.get(this, "momentum_desc", this.momentumStacks);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(STACKS, this.momentumStacks);
        bundle.put(FREERUN_TURNS, this.freerunTurns);
        bundle.put(FREERUN_CD, this.freerunCooldown);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.momentumStacks = bundle.getInt(STACKS);
        this.freerunTurns = bundle.getInt(FREERUN_TURNS);
        this.freerunCooldown = bundle.getInt(FREERUN_CD);
        if (this.momentumStacks > 0 && this.freerunTurns <= 0) {
            ActionIndicator.setAction(this);
        }
        this.movedLastTurn = false;
    }

    @Override
    public String actionName() {
        return Messages.get(this, "action_name", new Object[0]);
    }

    @Override
    public int actionIcon() {
        return 107;
    }

    @Override
    public Visual secondaryVisual() {
        BitmapText txt = new BitmapText(PixelScene.pixelFont);
        txt.text(Integer.toString(this.momentumStacks));
        txt.hardlight(65280);
        txt.measure();
        return txt;
    }

    @Override
    public int indicatorColor() {
        return 0x444444;
    }

    @Override
    public void doAction() {
        this.freerunTurns = 2 * this.momentumStacks;
        this.freerunCooldown = 10 + 4 * this.momentumStacks;
        Sample.INSTANCE.play("sounds/miss.mp3", 1.0f, 0.8f);
        this.target.sprite.emitter().burst(Speck.factory(106), 5 + this.momentumStacks);
        SpellSprite.show(this.target, 5, 1.0f, 1.0f, 0.0f);
        this.momentumStacks = 0;
        BuffIndicator.refreshHero();
        ActionIndicator.clearAction(this);
    }
}

