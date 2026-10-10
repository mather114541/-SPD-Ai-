/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Greatsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.VaultMirrorSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class VaultMirror
extends NPC {
    public Item reward;
    private static final String REWARD = "reward";

    public VaultMirror() {
        this.spriteClass = VaultMirrorSprite.class;
        this.properties.add(Char.Property.IMMOVABLE);
        this.properties.add(Char.Property.OBJECT);
        this.reward = null;
    }

    @Override
    protected void throwItems() {
        Heap heap = (Heap)Dungeon.level.heaps.get(this.pos);
        if (heap != null) {
            Dungeon.level.drop((Item)heap.pickUp(), (int)(this.pos + Dungeon.level.width())).sprite.drop(this.pos);
        }
    }

    public void createReward(HeroClass cls) {
        Random.pushGenerator(Random.Long());
        switch (cls) {
            case WARRIOR: {
                this.reward = new BrokenSeal().upgrade().identify(false);
                ((BrokenSeal)this.reward).setGlyph(Armor.Glyph.random(new Class[0]));
                break;
            }
            case MAGE: {
                this.reward = new MagesStaff().upgrade(3).identify(false);
                ((MagesStaff)this.reward).enchant();
                break;
            }
            case ROGUE: {
                this.reward = new CloakOfShadows().upgrade(8).identify(false);
                ((CloakOfShadows)this.reward).directCharge(8);
                break;
            }
            case HUNTRESS: {
                this.reward = new SpiritBow().identify(false);
                ((SpiritBow)this.reward).enchant();
                break;
            }
            case DUELIST: {
                this.reward = new MirrorSword().upgrade(3).identify(false);
                ((MeleeWeapon)this.reward).enchant();
                break;
            }
            case CLERIC: {
                this.reward = new HolyTome().upgrade(8).identify(false);
                ((HolyTome)this.reward).directCharge(8.0f);
            }
        }
        Random.popGenerator();
    }

    @Override
    public boolean interact(final Char c) {
        if (c instanceof Hero) {
            ShatteredPixelDungeon.runOnRenderThread(new Callback(){

                @Override
                public void call() {
                    if (VaultMirror.this.reward != null) {
                        String sceneText = Messages.get(VaultMirror.class, "approach", new Object[0]) + "\n\n";
                        switch (((Hero)c).heroClass) {
                            case WARRIOR: {
                                sceneText = sceneText + Messages.get(VaultMirror.class, "scene_warrior", new Object[0]);
                                break;
                            }
                            case MAGE: {
                                sceneText = sceneText + Messages.get(VaultMirror.class, "scene_mage", new Object[0]);
                                break;
                            }
                            case ROGUE: {
                                sceneText = sceneText + Messages.get(VaultMirror.class, "scene_rogue", new Object[0]);
                                break;
                            }
                            case HUNTRESS: {
                                sceneText = sceneText + Messages.get(VaultMirror.class, "scene_huntress", new Object[0]);
                                break;
                            }
                            case DUELIST: {
                                sceneText = sceneText + Messages.get(VaultMirror.class, "scene_duelist", new Object[0]);
                                break;
                            }
                            case CLERIC: {
                                sceneText = sceneText + Messages.get(VaultMirror.class, "scene_cleric", new Object[0]);
                            }
                        }
                        sceneText = sceneText + "\n\n" + Messages.get(VaultMirror.class, "scene_final", new Object[0]);
                        GameScene.show(new WndOptions(VaultMirror.this.sprite(), Messages.titleCase(VaultMirror.this.name()), sceneText, new String[]{Messages.get(VaultMirror.class, "take", new Object[0])}){

                            @Override
                            protected void onSelect(int index) {
                                super.onSelect(index);
                                if (index == 0) {
                                    GameScene.show(new WndTitledMessage(VaultMirror.this.sprite(), Messages.titleCase(VaultMirror.this.name()), Messages.get(VaultMirror.class, "scene_take", new Object[0])));
                                    if (VaultMirror.this.reward.doPickUp((Hero)c)) {
                                        GLog.i(Messages.capitalize(Messages.get(Dungeon.hero, "you_now_have", VaultMirror.this.reward.name())), new Object[0]);
                                    } else {
                                        Dungeon.level.drop((Item)VaultMirror.this.reward, (int)c.pos).sprite.drop();
                                    }
                                    Imp.Quest.mirrorUsed = true;
                                    VaultMirror.this.reward = null;
                                }
                            }
                        });
                    } else {
                        GameScene.show(new WndTitledMessage(VaultMirror.this.sprite(), Messages.titleCase(VaultMirror.this.name()), Messages.get(VaultMirror.class, "scene_nothing", new Object[0])));
                    }
                }
            });
        }
        return false;
    }

    @Override
    public int defenseSkill(Char enemy) {
        return INFINITE_EVASION;
    }

    @Override
    public void damage(int dmg, Object src) {
    }

    @Override
    public boolean add(Buff buff) {
        return false;
    }

    @Override
    public boolean reset() {
        return true;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        if (this.reward != null) {
            bundle.put(REWARD, this.reward);
        }
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if (bundle.contains(REWARD)) {
            this.reward = (Item)bundle.get(REWARD);
        }
    }

    public static class MirrorSword
    extends Greatsword {
        public MirrorSword() {
            this.unique = true;
        }
    }
}

