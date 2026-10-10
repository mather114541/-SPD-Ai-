/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.VelvetPouch;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Crossbow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.TippedDart;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class Dart
extends MissileWeapon {
    protected static final String AC_TIP = "TIP";
    protected static Crossbow bow;
    protected boolean processingChargedShot;
    private int chargedShotPos;
    private final WndBag.ItemSelector itemSelector;

    public Dart() {
        this.levelKnown = true;
        this.image = ItemSpriteSheet.DART;
        this.hitSound = "sounds/hit_arrow.mp3";
        this.hitSoundPitch = 1.3f;
        this.tier = 1;
        this.baseUses = 1000.0f;
        this.setID = 0L;
        this.processingChargedShot = false;
        this.itemSelector = new WndBag.ItemSelector(){

            @Override
            public String textPrompt() {
                return Messages.get(Dart.class, "prompt", new Object[0]);
            }

            @Override
            public Class<? extends Bag> preferredBag() {
                return VelvetPouch.class;
            }

            @Override
            public boolean itemSelectable(Item item) {
                return item instanceof Plant.Seed;
            }

            @Override
            public void onSelect(final Item item) {
                String[] options;
                int singleSeedDarts;
                if (item == null) {
                    return;
                }
                final int maxToTip = Math.min(curItem.quantity(), item.quantity() * 2);
                final int maxSeedsToUse = (maxToTip + 1) / 2;
                if (curItem.quantity() == 1) {
                    singleSeedDarts = 1;
                    options = new String[]{Messages.get(Dart.class, "tip_one", new Object[0]), Messages.get(Dart.class, "tip_cancel", new Object[0])};
                } else {
                    singleSeedDarts = 2;
                    options = maxToTip <= 2 ? new String[]{Messages.get(Dart.class, "tip_two", new Object[0]), Messages.get(Dart.class, "tip_cancel", new Object[0])} : new String[]{Messages.get(Dart.class, "tip_all", maxToTip, maxSeedsToUse), Messages.get(Dart.class, "tip_two", new Object[0]), Messages.get(Dart.class, "tip_cancel", new Object[0])};
                }
                TippedDart tipResult = TippedDart.getTipped((Plant.Seed)item, 1);
                GameScene.show(new WndOptions(new ItemSprite(item), Messages.titleCase(item.name()), Messages.get(Dart.class, "tip_desc", tipResult.name()) + "\n\n" + tipResult.desc(), options){

                    @Override
                    protected void onSelect(int index) {
                        super.onSelect(index);
                        if (index == 0 && options.length == 3) {
                            if (item.quantity() <= maxSeedsToUse) {
                                item.detachAll(curUser.belongings.backpack);
                            } else {
                                item.quantity(item.quantity() - maxSeedsToUse);
                            }
                            if (maxToTip < curItem.quantity()) {
                                curItem.quantity(curItem.quantity() - maxToTip);
                            } else {
                                curItem.detachAll(curUser.belongings.backpack);
                            }
                            TippedDart newDart = TippedDart.getTipped((Plant.Seed)item, maxToTip);
                            if (!newDart.collect()) {
                                Dungeon.level.drop((Item)newDart, (int)curUser.pos).sprite.drop();
                            }
                            curUser.spend(1.0f);
                            curUser.busy();
                            curUser.sprite.operate(curUser.pos);
                        } else if (index == 1 && options.length == 3 || index == 0 && options.length == 2) {
                            item.detach(curUser.belongings.backpack);
                            if (curItem.quantity() <= singleSeedDarts) {
                                curItem.detachAll(curUser.belongings.backpack);
                            } else {
                                curItem.quantity(curItem.quantity() - singleSeedDarts);
                            }
                            TippedDart newDart = TippedDart.getTipped((Plant.Seed)item, singleSeedDarts);
                            if (!newDart.collect()) {
                                Dungeon.level.drop((Item)newDart, (int)curUser.pos).sprite.drop();
                            }
                            curUser.spend(1.0f);
                            curUser.busy();
                            curUser.sprite.operate(curUser.pos);
                        }
                    }
                });
            }
        };
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_TIP);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_TIP)) {
            GameScene.selectItem(this.itemSelector);
        }
    }

    @Override
    public int min(int lvl) {
        if (bow != null) {
            if (!(this instanceof TippedDart) && Dungeon.hero.buff(Crossbow.ChargedShot.class) != null) {
                return bow.dartMin() + 4 + bow.buffedLvl() + lvl;
            }
            return bow.dartMin() + lvl;
        }
        return 1 + lvl;
    }

    @Override
    public int max(int lvl) {
        if (bow != null) {
            if (!(this instanceof TippedDart) && Dungeon.hero.buff(Crossbow.ChargedShot.class) != null) {
                return bow.dartMax() + 4 + bow.buffedLvl() + 2 * lvl;
            }
            return bow.dartMax() + 2 * lvl;
        }
        return 2 + 2 * lvl;
    }

    private void updateCrossbow() {
        bow = Dungeon.hero == null ? null : (Dungeon.hero.belongings.weapon() instanceof Crossbow ? (Crossbow)Dungeon.hero.belongings.weapon() : (Dungeon.hero.belongings.secondWep() instanceof Crossbow ? (Crossbow)Dungeon.hero.belongings.secondWep() : null));
    }

    public boolean crossbowHasEnchant(Char owner) {
        return bow != null && Dart.bow.enchantment != null && owner.buff(MagicImmune.class) == null;
    }

    @Override
    public boolean hasEnchant(Class<? extends Weapon.Enchantment> type, Char owner) {
        if (bow != null && bow.hasEnchant(type, owner)) {
            return true;
        }
        return super.hasEnchant(type, owner);
    }

    @Override
    public float accuracyFactor(Char owner, Char target) {
        if (bow != null && owner.buff(Crossbow.ChargedShot.class) != null) {
            return Char.INFINITE_ACCURACY;
        }
        return super.accuracyFactor(owner, target);
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (bow != null && !this.processingChargedShot) {
            damage = bow.proc(attacker, defender, damage);
        }
        int dmg = super.proc(attacker, defender, damage);
        if (!this.processingChargedShot) {
            this.processChargedShot(defender, damage);
        }
        return dmg;
    }

    @Override
    public int throwPos(Hero user, int dst) {
        this.updateCrossbow();
        return super.throwPos(user, dst);
    }

    @Override
    protected void onThrow(int cell) {
        this.updateCrossbow();
        this.chargedShotPos = cell;
        super.onThrow(cell);
    }

    protected void processChargedShot(Char target, int dmg) {
        this.processingChargedShot = true;
        if (this.chargedShotPos != -1 && bow != null && Dungeon.hero.buff(Crossbow.ChargedShot.class) != null) {
            PathFinder.buildDistanceMap(this.chargedShotPos, Dungeon.level.passable, 3);
            int[] distance = (int[])PathFinder.distance.clone();
            for (final Char ch : Actor.chars()) {
                if (ch == target) {
                    Actor.add(new Actor(){
                        {
                            this.actPriority = 100;
                        }

                        @Override
                        protected boolean act() {
                            if (!ch.isAlive()) {
                                Crossbow.onAbilityKill(Dungeon.hero, ch);
                            }
                            Actor.remove(this);
                            return true;
                        }
                    });
                    continue;
                }
                if (distance[ch.pos] == Integer.MAX_VALUE) continue;
                this.proc(Dungeon.hero, ch, dmg);
            }
        }
        this.chargedShotPos = -1;
        this.processingChargedShot = false;
    }

    @Override
    protected void decrementDurability() {
        super.decrementDurability();
        if (Dungeon.hero.buff(Crossbow.ChargedShot.class) != null) {
            Dungeon.hero.buff(Crossbow.ChargedShot.class).detach();
        }
    }

    @Override
    public void throwSound() {
        this.updateCrossbow();
        if (bow != null) {
            Sample.INSTANCE.play("sounds/atk_crossbow.mp3", 1.0f, Random.Float(0.87f, 1.15f));
        } else {
            super.throwSound();
        }
    }

    @Override
    public String info() {
        this.updateCrossbow();
        if (bow != null && !bow.isIdentified()) {
            Crossbow realBow = bow;
            bow = new Crossbow();
            String info = super.info();
            bow = realBow;
            return info;
        }
        return super.info();
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
    public int defaultQuantity() {
        return 2;
    }

    @Override
    public int value() {
        return Math.round((float)super.value() / 2.0f);
    }
}

