/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Haste;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Honeypot;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.MimicTooth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MimicSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class CrystalMimic
extends Mimic {
    public CrystalMimic() {
        this.spriteClass = MimicSprite.Crystal.class;
        this.FLEEING = new Fleeing();
    }

    @Override
    public String name() {
        if (this.alignment == Char.Alignment.NEUTRAL) {
            return Messages.get(Heap.class, "crystal_chest", new Object[0]);
        }
        return super.name();
    }

    @Override
    public String description() {
        if (this.alignment == Char.Alignment.NEUTRAL) {
            Object desc = null;
            for (Item i : this.items) {
                if (i instanceof Artifact) {
                    desc = Messages.get(Heap.class, "crystal_chest_desc", Messages.get(Heap.class, "artifact", new Object[0]));
                    break;
                }
                if (i instanceof Ring) {
                    desc = Messages.get(Heap.class, "crystal_chest_desc", Messages.get(Heap.class, "ring", new Object[0]));
                    break;
                }
                if (!(i instanceof Wand)) continue;
                desc = Messages.get(Heap.class, "crystal_chest_desc", Messages.get(Heap.class, "wand", new Object[0]));
                break;
            }
            if (desc == null) {
                desc = Messages.get(Heap.class, "locked_chest_desc", new Object[0]);
            }
            if (!MimicTooth.stealthyMimics()) {
                desc = (String)desc + "\n\n" + Messages.get(this, "hidden_hint", new Object[0]);
            }
            return desc;
        }
        return super.description();
    }

    @Override
    public int damageRoll() {
        if (this.alignment == Char.Alignment.NEUTRAL) {
            this.alignment = Char.Alignment.ENEMY;
            int dmg = super.damageRoll();
            this.alignment = Char.Alignment.NEUTRAL;
            return dmg;
        }
        return super.damageRoll();
    }

    @Override
    public void stopHiding() {
        this.state = this.FLEEING;
        if (this.sprite != null) {
            this.sprite.idle();
        }
        if (this.alignment == Char.Alignment.NEUTRAL) {
            Buff.affect(this, Haste.class, 2.0f);
        } else {
            Buff.affect(this, Haste.class, 1.0f);
        }
        if (Actor.chars().contains(this) && Dungeon.level.heroFOV[this.pos]) {
            this.enemy = Dungeon.hero;
            this.target = Dungeon.hero.pos;
            GLog.w(Messages.get(this, "reveal", new Object[0]), new Object[0]);
            CellEmitter.get(this.pos).burst(Speck.factory(1), 10);
            Sample.INSTANCE.play("sounds/mimic.mp3", 1.0f, 1.25f);
        }
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        if (this.alignment == Char.Alignment.NEUTRAL && enemy == Dungeon.hero) {
            this.steal(Dungeon.hero);
        } else {
            ArrayList<Integer> candidates = new ArrayList<Integer>();
            for (int i : PathFinder.NEIGHBOURS8) {
                if (!Dungeon.level.passable[this.pos + i] || Actor.findChar(this.pos + i) != null) continue;
                candidates.add(this.pos + i);
            }
            if (!candidates.isEmpty()) {
                ScrollOfTeleportation.appear(enemy, (Integer)Random.element(candidates));
            }
            if (this.alignment == Char.Alignment.ENEMY) {
                this.state = this.FLEEING;
            }
        }
        return super.attackProc(enemy, damage);
    }

    protected void steal(Hero hero) {
        Item item;
        int tries = 10;
        do {
            item = hero.belongings.randomUnequipped();
        } while (tries-- > 0 && (item == null || item.unique || item.level() > 0));
        if (item != null && !item.unique && item.level() < 1) {
            GLog.w(Messages.get(this, "ate", item.name()), new Object[0]);
            if (!item.stackable) {
                Dungeon.quickslot.convertToPlaceholder(item);
            }
            item.updateQuickslot();
            if (item instanceof Honeypot) {
                this.items.add(((Honeypot)item).shatter(this, this.pos));
                item.detach(hero.belongings.backpack);
            } else {
                this.items.add(item.detach(hero.belongings.backpack));
                if (item instanceof Honeypot.ShatteredPot) {
                    ((Honeypot.ShatteredPot)item).pickupPot(this);
                }
            }
        }
    }

    @Override
    protected void generatePrize(boolean useDecks) {
        for (Item i : this.items) {
            i.cursed = false;
            i.cursedKnown = true;
        }
    }

    private class Fleeing
    extends Mob.Fleeing {
        private Fleeing() {
            super(CrystalMimic.this);
        }

        @Override
        protected void escaped() {
            if (!Dungeon.level.heroFOV[CrystalMimic.this.pos] && Dungeon.level.distance(Dungeon.hero.pos, CrystalMimic.this.pos) >= 6) {
                GLog.n(Messages.get(CrystalMimic.class, "escaped", new Object[0]), new Object[0]);
                CrystalMimic.this.destroy();
                CrystalMimic.this.sprite.killAndErase();
            } else {
                CrystalMimic.this.state = CrystalMimic.this.WANDERING;
            }
        }

        @Override
        protected void nowhereToRun() {
            super.nowhereToRun();
            if (CrystalMimic.this.state == CrystalMimic.this.HUNTING) {
                CrystalMimic.this.spend(-1.0f);
            }
        }
    }
}

