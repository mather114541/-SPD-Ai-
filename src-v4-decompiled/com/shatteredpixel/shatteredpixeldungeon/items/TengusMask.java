/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Preparation;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndChooseSubclass;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import java.util.ArrayList;

public class TengusMask
extends Item {
    private static final String AC_WEAR = "WEAR";

    public TengusMask() {
        this.stackable = false;
        this.image = ItemSpriteSheet.MASK;
        this.defaultAction = AC_WEAR;
        this.unique = true;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_WEAR);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_WEAR)) {
            curUser = hero;
            GameScene.show(new WndChooseSubclass(this, hero));
        }
    }

    @Override
    public boolean doPickUp(Hero hero, int pos) {
        Badges.validateMastery();
        return super.doPickUp(hero, pos);
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    public void choose(HeroSubClass way) {
        this.detach(TengusMask.curUser.belongings.backpack);
        Catalog.countUse(this.getClass());
        curUser.spend(1.0f);
        curUser.busy();
        TengusMask.curUser.subClass = way;
        Talent.initSubclassTalents(curUser);
        if (way == HeroSubClass.ASSASSIN && TengusMask.curUser.invisible > 0) {
            Buff.affect(curUser, Preparation.class);
        }
        TengusMask.curUser.sprite.operate(TengusMask.curUser.pos);
        Sample.INSTANCE.play("sounds/mastery.mp3");
        Emitter e = TengusMask.curUser.sprite.centerEmitter();
        e.pos(e.x - 2.0f, e.y - 6.0f, 4.0f, 4.0f);
        e.start(Speck.factory(103), 0.05f, 20);
        GLog.p(Messages.get(this, "used", new Object[0]), new Object[0]);
    }
}

