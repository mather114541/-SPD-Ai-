/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.food;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HornOfPlenty;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import java.util.ArrayList;

public class Food
extends Item {
    public static final float TIME_TO_EAT = 3.0f;
    public static final String AC_EAT = "EAT";
    public float energy = 300.0f;

    public Food() {
        this.stackable = true;
        this.image = ItemSpriteSheet.RATION;
        this.defaultAction = AC_EAT;
        this.bones = true;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_EAT);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_EAT)) {
            this.detach(hero.belongings.backpack);
            Catalog.countUse(this.getClass());
            this.satisfy(hero);
            GLog.i(Messages.get(this, "eat_msg", new Object[0]), new Object[0]);
            if (!rl.RLMode.headless && hero.sprite != null) hero.sprite.operate(hero.pos);
            hero.busy();
            if (!rl.RLMode.headless) SpellSprite.show(hero, 0);
            if (!rl.RLMode.headless) this.eatSFX();
            hero.spend(this.eatingTime());
            Talent.onFoodEaten(hero, this.energy, this);
            ++Statistics.foodEaten;
            Badges.validateFoodEaten();
        }
    }

    protected void eatSFX() {
        Sample.INSTANCE.play("sounds/eat.mp3");
    }

    protected float eatingTime() {
        if (Dungeon.hero.hasTalent(Talent.IRON_STOMACH) || Dungeon.hero.hasTalent(Talent.ENERGIZING_MEAL) || Dungeon.hero.hasTalent(Talent.MYSTICAL_MEAL) || Dungeon.hero.hasTalent(Talent.INVIGORATING_MEAL) || Dungeon.hero.hasTalent(Talent.FOCUSED_MEAL) || Dungeon.hero.hasTalent(Talent.ENLIGHTENING_MEAL)) {
            return 1.0f;
        }
        return 3.0f;
    }

    protected void satisfy(Hero hero) {
        Artifact.ArtifactBuff buff;
        float foodVal = this.energy;
        if (Dungeon.isChallenged(1)) {
            foodVal /= 3.0f;
        }
        if ((buff = (Artifact.ArtifactBuff)hero.buff(HornOfPlenty.hornRecharge.class)) != null && buff.isCursed()) {
            foodVal *= 0.67f;
            GLog.n(Messages.get(Hunger.class, "cursedhorn", new Object[0]), new Object[0]);
        }
        Buff.affect(hero, Hunger.class).satisfy(foodVal);
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public int value() {
        return 10 * this.quantity;
    }
}

