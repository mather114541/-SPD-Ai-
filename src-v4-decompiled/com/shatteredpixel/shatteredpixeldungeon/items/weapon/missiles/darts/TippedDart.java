/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.PinCushion;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Crossbow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.AdrenalineDart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.BlindingDart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.ChillingDart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.CleansingDart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.Dart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.DisplacingDart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.HealingDart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.HolyDart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.IncendiaryDart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.ParalyticDart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.PoisonDart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.RotDart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.ShockingDart;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Blindweed;
import com.shatteredpixel.shatteredpixeldungeon.plants.Earthroot;
import com.shatteredpixel.shatteredpixeldungeon.plants.Fadeleaf;
import com.shatteredpixel.shatteredpixeldungeon.plants.Firebloom;
import com.shatteredpixel.shatteredpixeldungeon.plants.Icecap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Mageroyal;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.plants.Rotberry;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sorrowmoss;
import com.shatteredpixel.shatteredpixeldungeon.plants.Starflower;
import com.shatteredpixel.shatteredpixeldungeon.plants.Stormvine;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sungrass;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.utils.Reflection;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public abstract class TippedDart
extends Dart {
    private static final String AC_CLEAN = "CLEAN";
    public static int lostDarts = 0;
    private static int targetPos = -1;
    public static final LinkedHashMap<Class<? extends Plant.Seed>, Class<? extends TippedDart>> types = new LinkedHashMap();

    public TippedDart() {
        this.tier = 2;
        this.baseUses = 1.0f;
        this.useRoundingInDurabilityCalc = false;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.remove("TIP");
        actions.add(AC_CLEAN);
        return actions;
    }

    @Override
    public void execute(final Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_CLEAN)) {
            String[] options = this.quantity() > 1 ? new String[]{Messages.get(this, "clean_all", new Object[0]), Messages.get(this, "clean_one", new Object[0]), Messages.get(this, "cancel", new Object[0])} : new String[]{Messages.get(this, "clean_one", new Object[0]), Messages.get(this, "cancel", new Object[0])};
            GameScene.show(new WndOptions(new ItemSprite(this), Messages.titleCase(this.name()), Messages.get(this, "clean_desc", new Object[0]), options){

                @Override
                protected void onSelect(int index) {
                    if (index == 0) {
                        TippedDart.this.detachAll(hero.belongings.backpack);
                        new Dart().quantity(TippedDart.this.quantity).collect();
                        hero.spend(1.0f);
                        hero.busy();
                        hero.sprite.operate(hero.pos);
                    } else if (index == 1 && TippedDart.this.quantity() > 1) {
                        TippedDart.this.detach(hero.belongings.backpack);
                        if (!new Dart().quantity(1).collect()) {
                            Dungeon.level.drop((Item)new Dart().quantity((int)1), (int)hero.pos).sprite.drop();
                        }
                        TippedDart.this.durability = 100.0f;
                        hero.spend(1.0f);
                        hero.busy();
                        hero.sprite.operate(hero.pos);
                    }
                }
            });
        }
    }

    @Override
    protected void rangedHit(Char enemy, int cell) {
        targetPos = cell;
        super.rangedHit(enemy, cell);
        if (this.durability <= 0.0f && !this.spawnedForEffect) {
            Dart d = new Dart();
            d.quantity(1);
            Catalog.countUse(this.getClass());
            if (this.sticky && enemy != null && enemy.isAlive() && enemy.alignment != Char.Alignment.ALLY) {
                PinCushion p = Buff.affect(enemy, PinCushion.class);
                if (p.target == enemy) {
                    p.stick(d);
                    return;
                }
            }
            Dungeon.level.drop((Item)d, (int)enemy.pos).sprite.drop();
        }
    }

    @Override
    public Item merge(Item other) {
        int total = this.quantity() + other.quantity();
        super.merge(other);
        int extra = total - this.quantity();
        if (extra > 0) {
            lostDarts += extra;
        }
        return this;
    }

    @Override
    public float durabilityPerUse(int level) {
        float use = super.durabilityPerUse(level);
        if (Dungeon.hero != null) {
            use /= (float)(1 + Dungeon.hero.pointsInTalent(Talent.DURABLE_TIPS));
            float lotusPreserve = 0.0f;
            if (targetPos != -1) {
                for (Char ch : Actor.chars()) {
                    WandOfRegrowth.Lotus l;
                    if (!(ch instanceof WandOfRegrowth.Lotus) || !(l = (WandOfRegrowth.Lotus)ch).inRange(targetPos)) continue;
                    lotusPreserve = Math.max(lotusPreserve, l.seedPreservation());
                }
                targetPos = -1;
            }
            int p = curUser == null ? Dungeon.hero.pos : TippedDart.curUser.pos;
            for (Char ch : Actor.chars()) {
                WandOfRegrowth.Lotus l;
                if (!(ch instanceof WandOfRegrowth.Lotus) || !(l = (WandOfRegrowth.Lotus)ch).inRange(p)) continue;
                lotusPreserve = Math.max(lotusPreserve, l.seedPreservation());
            }
            use *= 1.0f - lotusPreserve;
        }
        float usages = Math.round(100.0f / use);
        if (bow != null && Dungeon.hero != null && Dungeon.hero.buff(Crossbow.ChargedShot.class) != null) {
            usages += (float)(3 + bow.buffedLvl());
        }
        if (usages >= 100.0f) {
            return 0.0f;
        }
        return 100.0f / usages + 0.001f;
    }

    @Override
    public int value() {
        return Math.round(7.5f * (float)this.quantity);
    }

    public static TippedDart getTipped(Plant.Seed s, int quantity) {
        return (TippedDart)Reflection.newInstance(types.get(s.getClass())).quantity(quantity);
    }

    public static TippedDart randomTipped(int quantity) {
        Plant.Seed s;
        while (!types.containsKey((s = (Plant.Seed)Generator.randomUsingDefaults(Generator.Category.SEED)).getClass())) {
        }
        return TippedDart.getTipped(s, quantity);
    }

    static {
        types.put(Rotberry.Seed.class, RotDart.class);
        types.put(Sungrass.Seed.class, HealingDart.class);
        types.put(Fadeleaf.Seed.class, DisplacingDart.class);
        types.put(Icecap.Seed.class, ChillingDart.class);
        types.put(Firebloom.Seed.class, IncendiaryDart.class);
        types.put(Sorrowmoss.Seed.class, PoisonDart.class);
        types.put(Swiftthistle.Seed.class, AdrenalineDart.class);
        types.put(Blindweed.Seed.class, BlindingDart.class);
        types.put(Stormvine.Seed.class, ShockingDart.class);
        types.put(Earthroot.Seed.class, ParalyticDart.class);
        types.put(Mageroyal.Seed.class, CleansingDart.class);
        types.put(Starflower.Seed.class, HolyDart.class);
    }
}

