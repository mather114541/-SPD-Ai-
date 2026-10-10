/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.quest;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.VaultTokenDoor;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.quest.vault.VaultBossElemental;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DwarfToken;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.ImpStatue;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.VaultLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.VaultFinalRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ImpSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndError;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Reflection;
import java.util.ArrayList;

public class EscapeCrystal
extends Item {
    public static final String AC_USE = "USE";
    public static String BELONGINGS = "belongings";
    public static String QUICKSLOTS = "quickslots";
    public static String GOLD = "gold";
    public static String ENERGY = "energy";
    public Bundle storedItems;
    public static String STORED_ITEMS = "stored_items";

    public EscapeCrystal() {
        this.image = ItemSpriteSheet.ESCAPE;
        this.unique = true;
        this.defaultAction = AC_USE;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = new ArrayList<String>();
        actions.add(AC_USE);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_USE)) {
            if (Dungeon.level instanceof VaultLevel) {
                if (Imp.Quest.isOld()) {
                    this.leaveVault(null, 0);
                    return;
                }
                int score = 0;
                if (hero.belongings.getItem(ImpStatue.class) != null) {
                    score = 4000;
                } else {
                    score += (int)(1000.0f * Dungeon.level.levelExplorePercent(Dungeon.depth));
                    boolean doorOpened = true;
                    for (Char char_ : Dungeon.level.mobs) {
                        if (!(char_ instanceof VaultTokenDoor)) continue;
                        doorOpened = false;
                        break;
                    }
                    if (doorOpened) {
                        score += 1250;
                    } else {
                        DwarfToken tokens = hero.belongings.getItem(DwarfToken.class);
                        if (tokens != null) {
                            score += Math.min(1000, 100 * tokens.quantity());
                        }
                    }
                    VaultFinalRoom r = (VaultFinalRoom)((VaultLevel)Dungeon.level).room(VaultFinalRoom.class);
                    if (r.elementalWasSummoned()) {
                        boolean bl;
                        boolean bl2 = false;
                        for (Char char_ : Dungeon.level.mobs) {
                            if (!(char_ instanceof VaultBossElemental)) continue;
                            bl = true;
                            score += (int)(750.0f * ((float)char_.HP / (float)char_.HT));
                            break;
                        }
                        if (!bl) {
                            score += 750;
                        }
                    }
                    score = score / 50 * 50;
                }
                if (score < 50) {
                    GameScene.show(new WndTitledMessage(new ImpSprite(), Messages.titleCase(Messages.get(Imp.class, "name", new Object[0])), Messages.get(EscapeCrystal.class, "leaving_start", new Object[0])));
                } else {
                    String message = score < 500 ? Messages.get(EscapeCrystal.class, "leaving_early", new Object[0]) : (score <= 1000 ? Messages.get(EscapeCrystal.class, "leaving_partly_explored", new Object[0]) : (score <= 2000 ? Messages.get(EscapeCrystal.class, "leaving_fully_explored", new Object[0]) : (score < 4000 ? Messages.get(EscapeCrystal.class, "leaving_partial_victory", new Object[0]) : Messages.get(EscapeCrystal.class, "leaving_victory", new Object[0]))));
                    final int finalScore = score;
                    GameScene.show(new WndOptions(new ImpSprite(), Messages.titleCase(Messages.get(Imp.class, "name", new Object[0])), message, new String[]{Messages.get(EscapeCrystal.class, "leaving_yes", new Object[0]), Messages.get(EscapeCrystal.class, "leaving_no", new Object[0])}){

                        @Override
                        protected void onSelect(int index) {
                            if (index == 0) {
                                if (finalScore >= 500) {
                                    GameScene.selectItem(new WndBag.ItemSelector(){

                                        @Override
                                        public String textPrompt() {
                                            return Messages.get(EscapeCrystal.class, "prompt", new Object[0]);
                                        }

                                        @Override
                                        public boolean itemSelectable(Item item) {
                                            if (item instanceof EscapeCrystal) {
                                                return false;
                                            }
                                            if (finalScore <= 1000) {
                                                return !item.unique && !(item instanceof EquipableItem) && !(item instanceof Wand);
                                            }
                                            if (finalScore < 4000) {
                                                int maxLevel;
                                                int n = maxLevel = finalScore > 2000 ? 1 : 0;
                                                if (item instanceof MagesStaff) {
                                                    return ((MagesStaff)item).wandClass() != null && item.level() <= maxLevel + 1;
                                                }
                                                if (item instanceof Armor && ((Armor)item).checkSeal() != null) {
                                                    return item.level() <= maxLevel + 1;
                                                }
                                                return item.level() <= maxLevel && !item.unique;
                                            }
                                            if (item instanceof MagesStaff) {
                                                return ((MagesStaff)item).wandClass() != null;
                                            }
                                            return !item.unique;
                                        }

                                        @Override
                                        public void onSelect(Item item) {
                                            if (item != null) {
                                                Object desc = Messages.get(EscapeCrystal.class, "leaving_item", new Object[0]);
                                                if (item instanceof Armor && ((Armor)item).checkSeal() != null) {
                                                    desc = (String)desc + "\n\n" + Messages.get(EscapeCrystal.class, "leaving_seal", new Object[0]);
                                                } else if (item instanceof MagesStaff) {
                                                    desc = (String)desc + "\n\n" + Messages.get(EscapeCrystal.class, "leaving_staff", new Object[0]);
                                                }
                                                if (item.quantity() > 0 && !(item instanceof EquipableItem)) {
                                                    item = item.duplicate().quantity(1);
                                                }
                                                final Item finalItem = item;
                                                GameScene.show(new WndOptions(new ItemSprite(finalItem), Messages.titleCase(finalItem.title()), (String)desc, new String[]{Messages.get(EscapeCrystal.class, "leaving_yes", new Object[0]), Messages.get(EscapeCrystal.class, "leaving_no", new Object[0])}){

                                                    @Override
                                                    protected void onSelect(int index) {
                                                        if (index == 0) {
                                                            EscapeCrystal.this.leaveVault(finalItem, finalScore);
                                                        }
                                                        super.onSelect(index);
                                                    }
                                                });
                                            }
                                        }
                                    });
                                } else {
                                    EscapeCrystal.this.leaveVault(null, finalScore);
                                }
                            }
                        }
                    });
                }
            } else if (this.storedItems == null || !this.storedItems.contains(BELONGINGS)) {
                GameScene.show(new WndError(Messages.get(EscapeCrystal.class, "error_start", new Object[0]) + "\n\n" + Messages.get(EscapeCrystal.class, "error_no_items", new Object[0])));
                this.detachAll(hero.belongings.backpack);
            } else {
                GameScene.show(new WndError(Messages.get(EscapeCrystal.class, "error_start", new Object[0]) + "\n\n" + Messages.get(EscapeCrystal.class, "error_with_items", new Object[0])));
            }
        }
    }

    private void leaveVault(Item preserve, int score) {
        Sample.INSTANCE.play("sounds/teleport.mp3");
        Dungeon.hero.live();
        Dungeon.hero.HP = Dungeon.hero.HT;
        if (preserve instanceof Armor && ((Armor)preserve).checkSeal() != null) {
            BrokenSeal seal = ((Armor)preserve).checkSeal();
            Armor.Glyph attached = seal.getGlyph();
            ((Armor)preserve).detachSeal();
            if (((Armor)preserve).glyph == null && attached != null) {
                ((Armor)preserve).inscribe(attached);
            }
        } else if (preserve instanceof MagesStaff) {
            Wand w = Reflection.newInstance(((MagesStaff)preserve).wandClass());
            w.identify(false);
            w.upgrade(preserve.level() - 1);
            preserve = w;
        }
        this.restoreHeroBelongings(Dungeon.hero, preserve);
        Dungeon.hero.updateHT(false);
        this.detachAll(Dungeon.hero.belongings.backpack);
        if (!Imp.Quest.isOld()) {
            Imp.Quest.complete(score);
        }
        Level.beforeTransition();
        InterlevelScene.curTransition = new LevelTransition(Dungeon.level, Dungeon.hero.pos, LevelTransition.Type.BRANCH_ENTRANCE, Dungeon.depth, 0, LevelTransition.Type.BRANCH_EXIT);
        InterlevelScene.mode = InterlevelScene.Mode.ASCEND;
        Game.switchScene(InterlevelScene.class);
    }

    public void storeHeroBelongings(Hero hero) {
        this.storedItems = new Bundle();
        Bundle belongings = new Bundle();
        hero.belongings.storeInBundle(belongings);
        this.storedItems.put(BELONGINGS, belongings);
        Bundle quickslots = new Bundle();
        Dungeon.quickslot.storePlaceholders(quickslots);
        this.storedItems.put(QUICKSLOTS, quickslots);
        this.storedItems.put(GOLD, Dungeon.gold);
        this.storedItems.put(ENERGY, Dungeon.energy);
        Dungeon.quickslot.reset();
        QuickSlotButton.reset();
        Dungeon.energy = 0;
        Dungeon.gold = 0;
        hero.belongings.clear();
    }

    public void restoreHeroBelongings(Hero hero, Item preserve) {
        if (preserve != null) {
            preserve.detachAll(hero.belongings.backpack);
        }
        hero.belongings.clear();
        Dungeon.quickslot.reset();
        Dungeon.quickslot.restorePlaceholders(this.storedItems.getBundle(QUICKSLOTS));
        QuickSlotButton.reset();
        Dungeon.hero.belongings.restoreFromBundle(this.storedItems.getBundle(BELONGINGS));
        Dungeon.gold = this.storedItems.getInt(GOLD);
        Dungeon.energy = this.storedItems.getInt(ENERGY);
        if (preserve != null && !preserve.collect()) {
            Imp.Quest.reward = preserve;
        }
        this.storedItems = null;
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
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(STORED_ITEMS, this.storedItems);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.storedItems = bundle.getBundle(STORED_ITEMS);
    }
}

