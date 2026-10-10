/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.MimicTooth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MimicSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class GoldenMimic
extends Mimic {
    public GoldenMimic() {
        this.spriteClass = MimicSprite.Golden.class;
    }

    @Override
    public String name() {
        if (this.alignment == Char.Alignment.NEUTRAL) {
            return Messages.get(Heap.class, "locked_chest", new Object[0]);
        }
        return super.name();
    }

    @Override
    public String description() {
        if (this.alignment == Char.Alignment.NEUTRAL) {
            if (MimicTooth.stealthyMimics()) {
                return Messages.get(Heap.class, "locked_chest_desc", new Object[0]);
            }
            return Messages.get(Heap.class, "locked_chest_desc", new Object[0]) + "\n\n" + Messages.get(this, "hidden_hint", new Object[0]);
        }
        return super.description();
    }

    @Override
    public void stopHiding() {
        this.state = this.HUNTING;
        if (this.sprite != null) {
            this.sprite.idle();
        }
        if (Actor.chars().contains(this) && Dungeon.level.heroFOV[this.pos]) {
            this.enemy = Dungeon.hero;
            this.target = Dungeon.hero.pos;
            GLog.w(Messages.get(this, "reveal", new Object[0]), new Object[0]);
            CellEmitter.get(this.pos).burst(Speck.factory(1), 10);
            Sample.INSTANCE.play("sounds/mimic.mp3", 1.0f, 0.85f);
        }
    }

    @Override
    public void setLevel(int level) {
        super.setLevel(Math.round((float)level * 1.33f));
    }

    @Override
    protected void generatePrize(boolean useDecks) {
        super.generatePrize(useDecks);
        for (Item i : this.items) {
            if (!(i instanceof EquipableItem) && !(i instanceof Wand)) continue;
            i.cursed = false;
            i.cursedKnown = true;
            if (i instanceof Weapon && ((Weapon)i).hasCurseEnchant()) {
                ((Weapon)i).enchant(null);
            }
            if (i instanceof Armor && ((Armor)i).hasCurseGlyph()) {
                ((Armor)i).inscribe(null);
            }
            if (i instanceof Artifact || i.level() != 0 || Random.Int(2) != 0) continue;
            i.upgrade();
        }
    }
}

