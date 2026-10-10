/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.WellWater;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Healing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShaftParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Ankh;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Waterskin;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.VialOfBlood;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class WaterOfHealth
extends WellWater {
    @Override
    protected boolean affectHero(Hero hero) {
        if (!hero.isAlive()) {
            return false;
        }
        Sample.INSTANCE.play("sounds/drink.mp3");
        PotionOfHealing.cure(hero);
        hero.belongings.uncurseEquipped();
        hero.buff(Hunger.class).satisfy(450.0f);
        if (VialOfBlood.delayBurstHealing()) {
            Healing healing = Buff.affect(hero, Healing.class);
            healing.setHeal(hero.HT, 0.0f, VialOfBlood.maxHealPerTurn(), true);
        } else {
            hero.HP = hero.HT;
            hero.sprite.emitter().start(Speck.factory(0), 0.4f, 4);
            hero.sprite.showStatusWithIcon(65280, Integer.toString(hero.HT), FloatingText.HEALING, new Object[0]);
        }
        CellEmitter.get(hero.pos).start(ShaftParticle.FACTORY, 0.2f, 3);
        Dungeon.hero.interrupt();
        GLog.p(Messages.get(this, "procced", new Object[0]), new Object[0]);
        return true;
    }

    @Override
    protected Item affectItem(Item item, int pos) {
        if (item instanceof Waterskin && !((Waterskin)item).isFull()) {
            ((Waterskin)item).fill();
            CellEmitter.get(pos).start(Speck.factory(0), 0.4f, 4);
            Sample.INSTANCE.play("sounds/drink.mp3");
            return item;
        }
        if (item instanceof Ankh && !((Ankh)item).isBlessed()) {
            ((Ankh)item).bless();
            CellEmitter.get(pos).start(Speck.factory(2), 0.2f, 3);
            Sample.INSTANCE.play("sounds/drink.mp3");
            return item;
        }
        if (ScrollOfRemoveCurse.uncursable(item)) {
            if (ScrollOfRemoveCurse.uncurse(null, item)) {
                CellEmitter.get(pos).start(ShadowParticle.UP, 0.05f, 10);
            }
            Sample.INSTANCE.play("sounds/drink.mp3");
            return item;
        }
        return null;
    }

    @Override
    public Notes.Landmark landmark() {
        return Notes.Landmark.WELL_OF_HEALTH;
    }

    @Override
    public void use(BlobEmitter emitter) {
        super.use(emitter);
        emitter.start(Speck.factory(0), 0.5f, 0);
    }

    @Override
    public String tileDesc() {
        return Messages.get(this, "desc", new Object[0]);
    }
}

