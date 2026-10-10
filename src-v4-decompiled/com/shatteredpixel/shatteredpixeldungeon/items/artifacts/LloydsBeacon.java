/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import java.util.ArrayList;

public class LloydsBeacon
extends Artifact {
    public static final float TIME_TO_USE = 1.0f;
    public static final String AC_ZAP = "ZAP";
    public static final String AC_SET = "SET";
    public static final String AC_RETURN = "RETURN";
    public int returnDepth = -1;
    public int returnPos;
    private static final String DEPTH = "depth";
    private static final String POS = "pos";
    protected CellSelector.Listener zapper;
    private static final ItemSprite.Glowing WHITE = new ItemSprite.Glowing(0xFFFFFF);

    public LloydsBeacon() {
        this.image = ItemSpriteSheet.ARTIFACT_BEACON;
        this.levelCap = 3;
        this.charge = 0;
        this.chargeCap = 3 + this.level();
        this.defaultAction = AC_ZAP;
        this.usesTargeting = true;
        this.zapper = new CellSelector.Listener(){

            @Override
            public void onSelect(Integer target) {
                if (target == null) {
                    return;
                }
                Invisibility.dispel();
                LloydsBeacon.this.charge = LloydsBeacon.this.charge - (Dungeon.scalingDepth() > 20 ? 2 : 1);
                Item.updateQuickslot();
                if (Actor.findChar(target) == curUser) {
                    ScrollOfTeleportation.teleportChar(curUser);
                    curUser.spendAndNext(1.0f);
                } else {
                    Ballistica bolt = new Ballistica(curUser.pos, target, 6);
                    final Char ch = Actor.findChar(bolt.collisionPos);
                    if (ch == curUser) {
                        ScrollOfTeleportation.teleportChar(curUser);
                        curUser.spendAndNext(1.0f);
                    } else {
                        Sample.INSTANCE.play("sounds/zap.mp3");
                        curUser.sprite.zap(bolt.collisionPos);
                        curUser.busy();
                        MagicMissile.boltFromChar(curUser.sprite.parent, 6, curUser.sprite, bolt.collisionPos, new Callback(){

                            @Override
                            public void call() {
                                if (ch != null) {
                                    int pos;
                                    int count = 10;
                                    do {
                                        pos = Dungeon.level.randomRespawnCell(ch);
                                    } while (count-- > 0 && pos == -1);
                                    if (pos == -1 || Dungeon.bossLevel()) {
                                        GLog.w(Messages.get(ScrollOfTeleportation.class, "no_tele", new Object[0]), new Object[0]);
                                    } else if (ch.properties().contains((Object)Char.Property.IMMOVABLE)) {
                                        GLog.w(Messages.get(LloydsBeacon.class, "tele_fail", new Object[0]), new Object[0]);
                                    } else {
                                        ch.pos = pos;
                                        if (ch instanceof Mob && ((Mob)ch).state == ((Mob)ch).HUNTING) {
                                            ((Mob)ch).state = ((Mob)ch).WANDERING;
                                        }
                                        ch.sprite.place(ch.pos);
                                        ch.sprite.visible = Dungeon.level.heroFOV[pos];
                                    }
                                }
                                curUser.spendAndNext(1.0f);
                            }
                        });
                    }
                }
            }

            @Override
            public String prompt() {
                return Messages.get(LloydsBeacon.class, "prompt", new Object[0]);
            }
        };
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(DEPTH, this.returnDepth);
        if (this.returnDepth != -1) {
            bundle.put(POS, this.returnPos);
        }
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.returnDepth = bundle.getInt(DEPTH);
        this.returnPos = bundle.getInt(POS);
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_ZAP);
        actions.add(AC_SET);
        if (this.returnDepth != -1) {
            actions.add(AC_RETURN);
        }
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action == AC_SET || action == AC_RETURN) {
            if (Dungeon.bossLevel() || !Dungeon.interfloorTeleportAllowed()) {
                hero.spend(1.0f);
                GLog.w(Messages.get(this, "preventing", new Object[0]), new Object[0]);
                return;
            }
            for (int i = 0; i < PathFinder.NEIGHBOURS8.length; ++i) {
                Char ch = Actor.findChar(hero.pos + PathFinder.NEIGHBOURS8[i]);
                if (ch == null || ch.alignment != Char.Alignment.ENEMY) continue;
                GLog.w(Messages.get(this, "creatures", new Object[0]), new Object[0]);
                return;
            }
        }
        if (action == AC_ZAP) {
            int chargesToUse;
            curUser = hero;
            int n = chargesToUse = Dungeon.depth > 20 ? 2 : 1;
            if (!this.isEquipped(hero)) {
                GLog.i(Messages.get(Artifact.class, "need_to_equip", new Object[0]), new Object[0]);
                QuickSlotButton.cancel();
            } else if (this.charge < chargesToUse) {
                GLog.i(Messages.get(this, "no_charge", new Object[0]), new Object[0]);
                QuickSlotButton.cancel();
            } else {
                GameScene.selectCell(this.zapper);
            }
        } else if (action == AC_SET) {
            this.returnDepth = Dungeon.depth;
            this.returnPos = hero.pos;
            hero.spend(1.0f);
            hero.busy();
            hero.sprite.operate(hero.pos);
            Sample.INSTANCE.play("sounds/beacon.mp3");
            GLog.i(Messages.get(this, "return", new Object[0]), new Object[0]);
        } else if (action == AC_RETURN) {
            if (this.returnDepth == Dungeon.depth) {
                ScrollOfTeleportation.appear(hero, this.returnPos);
                block1: for (Mob m : Dungeon.level.mobs) {
                    if (m.pos != hero.pos) continue;
                    for (int i : PathFinder.NEIGHBOURS8) {
                        if (Actor.findChar(m.pos + i) != null || !Dungeon.level.passable[m.pos + i]) continue;
                        m.pos += i;
                        m.sprite.point(m.sprite.worldToCamera(m.pos));
                        continue block1;
                    }
                }
                Dungeon.level.occupyCell(hero);
                Dungeon.observe();
                GameScene.updateFog();
            } else {
                Level.beforeTransition();
                InterlevelScene.mode = InterlevelScene.Mode.RETURN;
                InterlevelScene.returnDepth = this.returnDepth;
                InterlevelScene.returnPos = this.returnPos;
                Game.switchScene(InterlevelScene.class);
            }
        }
    }

    @Override
    protected Artifact.ArtifactBuff passiveBuff() {
        return new beaconRecharge();
    }

    @Override
    public void charge(Hero target, float amount) {
        if (this.charge < this.chargeCap) {
            this.partialCharge += 0.25f * amount;
            while (this.partialCharge >= 1.0f) {
                this.partialCharge -= 1.0f;
                ++this.charge;
            }
            if (this.charge >= this.chargeCap) {
                this.partialCharge = 0.0f;
                this.charge = this.chargeCap;
            }
            LloydsBeacon.updateQuickslot();
        }
    }

    @Override
    public Item upgrade() {
        if (this.level() == this.levelCap) {
            return this;
        }
        ++this.chargeCap;
        GLog.p(Messages.get(this, "levelup", new Object[0]), new Object[0]);
        return super.upgrade();
    }

    @Override
    public String desc() {
        Object desc = super.desc();
        if (this.returnDepth != -1) {
            desc = (String)desc + "\n\n" + Messages.get(this, "desc_set", this.returnDepth);
        }
        return desc;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return this.returnDepth != -1 ? WHITE : null;
    }

    public class beaconRecharge
    extends Artifact.ArtifactBuff {
        @Override
        public boolean act() {
            if (LloydsBeacon.this.charge < LloydsBeacon.this.chargeCap && !LloydsBeacon.this.cursed && Regeneration.regenOn()) {
                LloydsBeacon.this.partialCharge += 1.0f / (100.0f - (float)(LloydsBeacon.this.chargeCap - LloydsBeacon.this.charge) * 10.0f);
                while (LloydsBeacon.this.partialCharge >= 1.0f) {
                    LloydsBeacon.this.partialCharge -= 1.0f;
                    ++LloydsBeacon.this.charge;
                    if (LloydsBeacon.this.charge != LloydsBeacon.this.chargeCap) continue;
                    LloydsBeacon.this.partialCharge = 0.0f;
                }
            }
            Item.updateQuickslot();
            this.spend(1.0f);
            return true;
        }
    }
}

