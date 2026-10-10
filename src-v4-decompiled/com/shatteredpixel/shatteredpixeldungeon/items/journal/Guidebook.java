/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.journal;

import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.GameLog;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.input.ControllerHandler;
import com.watabou.input.KeyBindings;
import com.watabou.noosa.audio.Sample;

public class Guidebook
extends Item {
    public Guidebook() {
        this.image = ItemSpriteSheet.MASTERY;
    }

    @Override
    public final boolean doPickUp(Hero hero, int pos) {
        Document.ADVENTURERS_GUIDE.findPage("Intro");
        Document.ADVENTURERS_GUIDE.findPage("Examining");
        Document.ADVENTURERS_GUIDE.findPage("Surprise_Attacks");
        Document.ADVENTURERS_GUIDE.findPage("Identifying");
        Document.ADVENTURERS_GUIDE.findPage("Food");
        Document.ADVENTURERS_GUIDE.findPage("Alchemy");
        Document.ADVENTURERS_GUIDE.findPage("Dieing");
        GameScene.pickUpJournal(this, pos);
        GameLog.wipe();
        GLog.i(Messages.capitalize(Messages.get(Hero.class, "you_now_have", this.name())), new Object[0]);
        if (SPDSettings.interfaceSize() == 0) {
            GLog.p(Messages.get(GameScene.class, "tutorial_guidebook_mobile", new Object[0]), new Object[0]);
        } else {
            GLog.p(Messages.get(GameScene.class, "tutorial_guidebook_desktop", KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(SPDAction.JOURNAL, ControllerHandler.isControllerConnected()))), new Object[0]);
        }
        GameScene.flashForDocument(Document.ADVENTURERS_GUIDE, "Intro");
        Sample.INSTANCE.play("sounds/item.mp3");
        hero.spendAndNext(this.pickupDelay());
        return true;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }
}

