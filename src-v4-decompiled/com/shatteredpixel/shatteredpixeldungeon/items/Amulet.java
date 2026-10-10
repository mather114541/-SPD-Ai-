/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.AmuletScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Game;
import java.io.IOException;
import java.util.ArrayList;

public class Amulet
extends Item {
    private static final String AC_END = "END";

    public Amulet() {
        this.image = ItemSpriteSheet.AMULET;
        this.unique = true;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (hero.buff(AscensionChallenge.class) != null) {
            actions.clear();
        } else {
            actions.add(AC_END);
        }
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_END)) {
            this.showAmuletScene(false);
        }
    }

    @Override
    public boolean doPickUp(Hero hero, int pos) {
        if (super.doPickUp(hero, pos)) {
            if (!Statistics.amuletObtained) {
                Statistics.amuletObtained = true;
                hero.spend(-hero.cooldown());
                Actor.add(new Actor(){
                    {
                        this.actPriority = 100;
                    }

                    @Override
                    protected boolean act() {
                        Actor.remove(this);
                        Amulet.this.showAmuletScene(true);
                        return false;
                    }
                });
            }
            return true;
        }
        return false;
    }

    private void showAmuletScene(boolean showText) {
        AmuletScene.noText = !showText;
        Game.switchScene(AmuletScene.class, new Game.SceneChangeCallback(){

            @Override
            public void beforeCreate() {
            }

            @Override
            public void afterCreate() {
                Badges.validateVictory();
                Badges.validateChampion(Challenges.activeChallenges());
                try {
                    Dungeon.saveAll();
                    Badges.saveGlobal();
                }
                catch (IOException e) {
                    ShatteredPixelDungeon.reportException(e);
                }
            }
        });
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public String desc() {
        Object desc = super.desc();
        desc = Dungeon.hero == null || Dungeon.hero.buff(AscensionChallenge.class) == null ? (String)desc + "\n\n" + Messages.get(this, "desc_origins", new Object[0]) : (String)desc + "\n\n" + Messages.get(this, "desc_ascent", new Object[0]);
        return desc;
    }
}

