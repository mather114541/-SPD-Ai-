/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public abstract class KindOfWeapon
extends EquipableItem {
    protected String hitSound = "sounds/hit.mp3";
    protected float hitSoundPitch = 1.0f;
    private static boolean isSwiftEquipping = false;

    @Override
    public void execute(final Hero hero, String action) {
        if (hero.subClass == HeroSubClass.CHAMPION && action.equals("EQUIP")) {
            this.usesTargeting = false;
            Object primaryName = Messages.titleCase(hero.belongings.weapon != null ? hero.belongings.weapon.trueName() : Messages.get(KindOfWeapon.class, "empty", new Object[0]));
            Object secondaryName = Messages.titleCase(hero.belongings.secondWep != null ? hero.belongings.secondWep.trueName() : Messages.get(KindOfWeapon.class, "empty", new Object[0]));
            if (((String)primaryName).length() > 18) {
                primaryName = ((String)primaryName).substring(0, 15) + "...";
            }
            if (((String)secondaryName).length() > 18) {
                secondaryName = ((String)secondaryName).substring(0, 15) + "...";
            }
            GameScene.show(new WndOptions(new ItemSprite(this), Messages.titleCase(this.name()), Messages.get(KindOfWeapon.class, "which_equip_msg", new Object[0]), new String[]{Messages.get(KindOfWeapon.class, "which_equip_primary", primaryName), Messages.get(KindOfWeapon.class, "which_equip_secondary", secondaryName)}){

                @Override
                protected void onSelect(int index) {
                    super.onSelect(index);
                    if (index == 0 || index == 1) {
                        int slot = Dungeon.quickslot.getSlot(KindOfWeapon.this);
                        EquipableItem.slotOfUnequipped = -1;
                        if (index == 0) {
                            KindOfWeapon.this.doEquip(hero);
                        } else {
                            KindOfWeapon.this.equipSecondary(hero);
                        }
                        if (slot != -1) {
                            Dungeon.quickslot.setSlot(slot, KindOfWeapon.this);
                            Item.updateQuickslot();
                        } else if (EquipableItem.slotOfUnequipped != -1 && KindOfWeapon.this.defaultAction() != null) {
                            Dungeon.quickslot.setSlot(EquipableItem.slotOfUnequipped, KindOfWeapon.this);
                            Item.updateQuickslot();
                        }
                    }
                }
            });
        } else {
            super.execute(hero, action);
        }
    }

    @Override
    public boolean isEquipped(Hero hero) {
        return hero != null && (hero.belongings.weapon() == this || hero.belongings.secondWep() == this);
    }

    @Override
    protected float timeToEquip(Hero hero) {
        return isSwiftEquipping ? 0.0f : super.timeToEquip(hero);
    }

    @Override
    public boolean doEquip(Hero hero) {
        isSwiftEquipping = false;
        if (hero.belongings.contains(this) && hero.hasTalent(Talent.SWIFT_EQUIP) && (hero.buff(Talent.SwiftEquipCooldown.class) == null || hero.buff(Talent.SwiftEquipCooldown.class).hasSecondUse())) {
            isSwiftEquipping = true;
        }
        if (hero.heroClass != HeroClass.CLERIC && hero.hasTalent(Talent.HOLY_INTUITION) && this.cursed && !this.cursedKnown && Random.Int(20) < 1 + 2 * hero.pointsInTalent(Talent.HOLY_INTUITION)) {
            this.cursedKnown = true;
            GLog.p(Messages.get(this, "curse_detected", new Object[0]), new Object[0]);
            return false;
        }
        this.detachAll(hero.belongings.backpack);
        if (hero.belongings.weapon == null || hero.belongings.weapon.doUnequip(hero, true)) {
            hero.belongings.weapon = this;
            this.activate(hero);
            Talent.onItemEquipped(hero, this);
            Badges.validateDuelistUnlock();
            KindOfWeapon.updateQuickslot();
            this.cursedKnown = true;
            if (this.cursed) {
                KindOfWeapon.equipCursed(hero);
                GLog.n(Messages.get(KindOfWeapon.class, "equip_cursed", new Object[0]), new Object[0]);
            }
            hero.spendAndNext(this.timeToEquip(hero));
            if (isSwiftEquipping) {
                GLog.i(Messages.get(this, "swift_equip", new Object[0]), new Object[0]);
                if (hero.buff(Talent.SwiftEquipCooldown.class) == null) {
                    Buff.affect((Char)hero, Talent.SwiftEquipCooldown.class, (float)19.0f).secondUse = hero.pointsInTalent(Talent.SWIFT_EQUIP) == 2;
                } else if (hero.buff(Talent.SwiftEquipCooldown.class).hasSecondUse()) {
                    hero.buff(Talent.SwiftEquipCooldown.class).secondUse = false;
                }
                isSwiftEquipping = false;
            }
            return true;
        }
        isSwiftEquipping = false;
        this.collect(hero.belongings.backpack);
        return false;
    }

    public boolean equipSecondary(Hero hero) {
        isSwiftEquipping = false;
        if (hero.belongings.contains(this) && hero.hasTalent(Talent.SWIFT_EQUIP) && (hero.buff(Talent.SwiftEquipCooldown.class) == null || hero.buff(Talent.SwiftEquipCooldown.class).hasSecondUse())) {
            isSwiftEquipping = true;
        }
        boolean wasInInv = hero.belongings.contains(this);
        this.detachAll(hero.belongings.backpack);
        if (hero.belongings.secondWep == null || hero.belongings.secondWep.doUnequip(hero, true)) {
            hero.belongings.secondWep = this;
            this.activate(hero);
            Talent.onItemEquipped(hero, this);
            Badges.validateDuelistUnlock();
            KindOfWeapon.updateQuickslot();
            this.cursedKnown = true;
            if (this.cursed) {
                KindOfWeapon.equipCursed(hero);
                GLog.n(Messages.get(KindOfWeapon.class, "equip_cursed", new Object[0]), new Object[0]);
            }
            hero.spendAndNext(this.timeToEquip(hero));
            if (isSwiftEquipping) {
                GLog.i(Messages.get(this, "swift_equip", new Object[0]), new Object[0]);
                if (hero.buff(Talent.SwiftEquipCooldown.class) == null) {
                    Buff.affect((Char)hero, Talent.SwiftEquipCooldown.class, (float)19.0f).secondUse = hero.pointsInTalent(Talent.SWIFT_EQUIP) == 2;
                } else if (hero.buff(Talent.SwiftEquipCooldown.class).hasSecondUse()) {
                    hero.buff(Talent.SwiftEquipCooldown.class).secondUse = false;
                }
                isSwiftEquipping = false;
            }
            return true;
        }
        isSwiftEquipping = false;
        this.collect(hero.belongings.backpack);
        return false;
    }

    @Override
    public boolean doUnequip(Hero hero, boolean collect, boolean single) {
        boolean second;
        boolean bl = second = hero.belongings.secondWep == this;
        if (second) {
            hero.belongings.secondWep = null;
        }
        if (super.doUnequip(hero, collect, single)) {
            if (!second) {
                hero.belongings.weapon = null;
            }
            return true;
        }
        if (second) {
            hero.belongings.secondWep = this;
        }
        return false;
    }

    public int min() {
        return this.min(this.buffedLvl());
    }

    public int max() {
        return this.max(this.buffedLvl());
    }

    public abstract int min(int var1);

    public abstract int max(int var1);

    public int damageRoll(Char owner) {
        if (owner instanceof Hero) {
            return Hero.heroDamageIntRange(this.min(), this.max());
        }
        return Random.NormalIntRange(this.min(), this.max());
    }

    public float accuracyFactor(Char owner, Char target) {
        return 1.0f;
    }

    public float delayFactor(Char owner) {
        return 1.0f;
    }

    public int reachFactor(Char owner) {
        return 1;
    }

    public boolean canReach(Char owner, int target) {
        int reach = this.reachFactor(owner);
        if (Dungeon.level.distance(owner.pos, target) > reach) {
            return false;
        }
        boolean[] passable = BArray.not(Dungeon.level.solid, null);
        for (Char ch : Actor.chars()) {
            if (ch == owner) continue;
            passable[ch.pos] = false;
        }
        PathFinder.buildDistanceMap(target, passable, reach);
        return PathFinder.distance[owner.pos] <= reach;
    }

    public int defenseFactor(Char owner) {
        return 0;
    }

    public int proc(Char attacker, Char defender, int damage) {
        return damage;
    }

    public void hitSound(float pitch) {
        Sample.INSTANCE.play(this.hitSound, 1.0f, pitch * this.hitSoundPitch);
    }
}

