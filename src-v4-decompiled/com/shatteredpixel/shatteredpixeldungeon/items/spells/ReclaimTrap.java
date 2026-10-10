/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.spells;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.MetalShard;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.TargetedSpell;
import com.shatteredpixel.shatteredpixeldungeon.journal.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Reflection;
import java.util.ArrayList;

public class ReclaimTrap
extends TargetedSpell {
    private static final ItemSprite.Glowing[] COLORS = new ItemSprite.Glowing[]{new ItemSprite.Glowing(0xFF0000), new ItemSprite.Glowing(0xFF8000), new ItemSprite.Glowing(0xFFFF00), new ItemSprite.Glowing(65280), new ItemSprite.Glowing(65535), new ItemSprite.Glowing(0x8000FF), new ItemSprite.Glowing(0xFFFFFF), new ItemSprite.Glowing(0x808080), new ItemSprite.Glowing(0)};

    public ReclaimTrap() {
        this.image = ItemSpriteSheet.RECLAIM_TRAP;
        this.talentChance = 0.2f;
    }

    @Override
    protected void affectTarget(Ballistica bolt, Hero hero) {
        Class<? extends Trap> storedTrap = null;
        if (hero.buff(ReclaimedTrap.class) != null) {
            storedTrap = hero.buff(ReclaimedTrap.class).trap;
            hero.buff(ReclaimedTrap.class).detach();
        }
        if (storedTrap == null) {
            Trap t = (Trap)Dungeon.level.traps.get(bolt.collisionPos);
            if (t != null && t.active && t.visible) {
                t.disarm();
                Sample.INSTANCE.play("sounds/lightning.mp3");
                ScrollOfRecharging.charge(hero);
                Buff.affect((Char)hero, ReclaimedTrap.class).trap = t.getClass();
                Bestiary.setSeen(t.getClass());
            } else {
                GLog.w(Messages.get(this, "no_trap", new Object[0]), new Object[0]);
            }
            Invisibility.dispel();
            curUser.spendAndNext(this.timeToCast());
        } else {
            Trap t = (Trap)Reflection.newInstance(storedTrap);
            t.pos = bolt.collisionPos;
            t.reclaimed = true;
            Bestiary.countEncounter(t.getClass());
            t.activate();
            this.onSpellused();
        }
    }

    @Override
    public String desc() {
        Object desc = super.desc();
        if (Dungeon.hero != null && Dungeon.hero.belongings.contains(this) && Dungeon.hero.buff(ReclaimedTrap.class) != null) {
            desc = (String)desc + "\n\n" + Messages.get(this, "desc_trap", Messages.get(Dungeon.hero.buff(ReclaimedTrap.class).trap, "name", new Object[0]));
        }
        return desc;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        if (Dungeon.hero != null && Dungeon.hero.belongings.contains(this) && Dungeon.hero.buff(ReclaimedTrap.class) != null) {
            return COLORS[Reflection.newInstance(Dungeon.hero.buff(ReclaimedTrap.class).trap).color];
        }
        return null;
    }

    @Override
    public int value() {
        return (int)(60.0f * ((float)this.quantity / 5.0f));
    }

    @Override
    public int energyVal() {
        return (int)(12.0f * ((float)this.quantity / 5.0f));
    }

    public static class Recipe
    extends Recipe.SimpleRecipe {
        private static final int OUT_QUANTITY = 5;

        public Recipe() {
            this.inputs = new Class[]{ScrollOfMagicMapping.class, MetalShard.class};
            this.inQuantity = new int[]{1, 1};
            this.cost = 8;
            this.output = ReclaimTrap.class;
            this.outQuantity = 5;
        }

        @Override
        public Item brew(ArrayList<Item> ingredients) {
            Catalog.countUse(MetalShard.class);
            return super.brew(ingredients);
        }
    }

    public static class ReclaimedTrap
    extends Buff {
        private Class<? extends Trap> trap;
        private static final String TRAP = "trap";

        public ReclaimedTrap() {
            this.revivePersists = true;
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(TRAP, this.trap);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.trap = bundle.getClass(TRAP);
        }
    }
}

