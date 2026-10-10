/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Haste;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.CrystalGuardian;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.CrystalWisp;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Blacksmith;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Pickaxe;
import com.shatteredpixel.shatteredpixeldungeon.journal.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CrystalSpireSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.ArrayList;
import java.util.Iterator;

public class CrystalSpire
extends Mob {
    private float abilityCooldown;
    private static final int ABILITY_CD = 15;
    private ArrayList<ArrayList<Integer>> targetedCells;
    private int hits;
    public static final String SPRITE = "sprite";
    public static final String HITS = "hits";
    public static final String ABILITY_COOLDOWN = "ability_cooldown";
    public static final String TARGETED_CELLS = "targeted_cells";

    @Override
    protected boolean act() {
        if (this.fieldOfView == null || this.fieldOfView.length != Dungeon.level.length()) {
            this.fieldOfView = new boolean[Dungeon.level.length()];
        }
        Dungeon.level.updateFieldOfView(this, this.fieldOfView);
        this.throwItems();
        this.sprite.hideAlert();
        this.sprite.hideLost();
        this.enemy = Dungeon.hero;
        boolean bl = this.enemySeen = this.enemy.isAlive() && this.fieldOfView[this.enemy.pos];
        if (!this.targetedCells.isEmpty()) {
            Char ch;
            ArrayList<Integer> cellsToAttack = this.targetedCells.remove(0);
            for (int i : cellsToAttack) {
                ch = Actor.findChar(i);
                if (ch instanceof CrystalSpire) continue;
                Level.set(i, 35);
                GameScene.updateMap(i);
                Splash.at(i, 0xFFFFFF, 5);
            }
            for (int i : cellsToAttack) {
                ch = Actor.findChar(i);
                if (ch == null || ch instanceof CrystalWisp || ch instanceof CrystalSpire) continue;
                int dmg = Random.NormalIntRange(6, 15);
                if (ch instanceof CrystalGuardian) {
                    dmg += 12;
                    Buff.prolong(ch, Cripple.class, 30.0f);
                } else if (ch == Dungeon.hero) {
                    Statistics.questScores[2] = Statistics.questScores[2] - 100;
                }
                ch.damage(dmg, new SpireSpike());
                int movePos = i;
                if (ch instanceof CrystalGuardian) {
                    for (int j : PathFinder.NEIGHBOURS8) {
                        if (Dungeon.level.solid[i + j] || Actor.findChar(i + j) != null || !(Dungeon.level.trueDistance(i + j, Dungeon.hero.pos) > Dungeon.level.trueDistance(movePos, Dungeon.hero.pos))) continue;
                        movePos = i + j;
                    }
                } else if (!Char.hasProp(ch, Char.Property.IMMOVABLE)) {
                    for (int j : PathFinder.NEIGHBOURS8) {
                        if (Dungeon.level.solid[i + j] || Actor.findChar(i + j) != null || !(Dungeon.level.trueDistance(i + j, this.pos) > Dungeon.level.trueDistance(movePos, this.pos))) continue;
                        movePos = i + j;
                    }
                }
                if (ch.isAlive()) {
                    if (movePos == i) continue;
                    Actor.add(new Pushing(ch, i, movePos));
                    ch.pos = movePos;
                    Dungeon.level.occupyCell(ch);
                    continue;
                }
                if (ch != Dungeon.hero) continue;
                GLog.n(Messages.capitalize(Messages.get(Char.class, "kill", this.name())), new Object[0]);
                Dungeon.fail(this);
            }
            PixelScene.shake(1.0f, 0.7f);
            Sample.INSTANCE.play("sounds/shatter.mp3");
            if (!this.targetedCells.isEmpty()) {
                for (int i : this.targetedCells.get(0)) {
                    GameScene.targetedCell(i, 1.0f);
                }
            }
        }
        if (this.hits < 3 || !this.enemySeen) {
            this.spend(1.0f);
            return true;
        }
        if (this.abilityCooldown <= 0.0f) {
            if (Random.Int(2) == 0) {
                this.diamondAOEAttack();
            } else {
                this.lineAttack();
            }
            float delay = GameMath.gate(1.0f, (int)Math.ceil(Dungeon.hero.cooldown()), 3.0f);
            for (int i : this.targetedCells.get(0)) {
                GameScene.targetedCell(i, delay);
            }
            this.abilityCooldown += 15.0f;
            this.spend(delay);
            Dungeon.hero.interrupt();
        } else {
            this.abilityCooldown -= 1.0f;
            this.spend(1.0f);
        }
        return true;
    }

    private void diamondAOEAttack() {
        this.targetedCells.clear();
        ArrayList<Integer> aoeCells = new ArrayList<Integer>();
        aoeCells.add(Dungeon.hero.pos);
        aoeCells.addAll(this.spreadDiamondAOE(aoeCells));
        this.targetedCells.add(new ArrayList<Integer>(aoeCells));
        if ((float)this.HP < (float)(2 * this.HT) / 3.0f) {
            aoeCells.addAll(this.spreadDiamondAOE(aoeCells));
            this.targetedCells.add(new ArrayList<Integer>(aoeCells));
            if ((float)this.HP < (float)this.HT / 3.0f) {
                aoeCells.addAll(this.spreadDiamondAOE(aoeCells));
                this.targetedCells.add(aoeCells);
            }
        }
    }

    private ArrayList<Integer> spreadDiamondAOE(ArrayList<Integer> currentCells) {
        ArrayList<Integer> spreadCells = new ArrayList<Integer>();
        for (int i : currentCells) {
            for (int j : PathFinder.NEIGHBOURS4) {
                if (Dungeon.level.solid[i + j] && Dungeon.level.map[i + j] != 35 || spreadCells.contains(i + j) || currentCells.contains(i + j)) continue;
                spreadCells.add(i + j);
            }
        }
        return spreadCells;
    }

    private void lineAttack() {
        int i;
        this.targetedCells.clear();
        ArrayList<Integer> lineCells = new ArrayList<Integer>();
        Ballistica aim = new Ballistica(this.pos, Dungeon.hero.pos, 0);
        Iterator<Integer> iterator = aim.subPath(1, 7).iterator();
        while (iterator.hasNext() && (!Dungeon.level.solid[i = iterator.next().intValue()] || Dungeon.level.map[i] == 35)) {
            lineCells.add(i);
        }
        this.targetedCells.add(new ArrayList(lineCells));
        if ((float)this.HP < (float)(2 * this.HT) / 3.0f) {
            lineCells.addAll(this.spreadDiamondAOE(lineCells));
            this.targetedCells.add(new ArrayList<Integer>(lineCells));
            if ((float)this.HP < (float)this.HT / 3.0f) {
                lineCells.addAll(this.spreadDiamondAOE(lineCells));
                this.targetedCells.add(lineCells);
            }
        }
    }

    private ArrayList<Integer> spreadAOE(ArrayList<Integer> currentCells) {
        ArrayList<Integer> spreadCells = new ArrayList<Integer>();
        for (int i : currentCells) {
            for (int j : PathFinder.NEIGHBOURS8) {
                if (Dungeon.level.solid[i + j] && Dungeon.level.map[i + j] != 35 || spreadCells.contains(i + j) || currentCells.contains(i + j)) continue;
                spreadCells.add(i + j);
            }
        }
        return spreadCells;
    }

    @Override
    public void beckon(int cell) {
    }

    @Override
    public boolean reset() {
        return true;
    }

    @Override
    public void damage(int dmg, Object src) {
        if (!(src instanceof Pickaxe)) {
            dmg = 0;
        }
        super.damage(dmg, src);
    }

    @Override
    public boolean isInvulnerable(Class effect) {
        return super.isInvulnerable(effect) || effect != Pickaxe.class;
    }

    @Override
    public boolean add(Buff buff) {
        return false;
    }

    @Override
    public boolean interact(Char c) {
        if (c == Dungeon.hero) {
            final Pickaxe p = Dungeon.hero.belongings.getItem(Pickaxe.class);
            if (p == null) {
                return true;
            }
            Dungeon.hero.sprite.attack(this.pos, new Callback(){

                @Override
                public void call() {
                    int dmg = p.damageRoll(CrystalSpire.this);
                    CrystalSpire.this.damage(dmg, p);
                    CrystalSpire.this.abilityCooldown -= (float)dmg / 10.0f;
                    CrystalSpire.this.sprite.bloodBurstA(Dungeon.hero.sprite.center(), dmg);
                    CrystalSpire.this.sprite.flash();
                    BossHealthBar.bleed(CrystalSpire.this.HP <= CrystalSpire.this.HT / 3);
                    if (CrystalSpire.this.isAlive()) {
                        Sample.INSTANCE.play("sounds/shatter.mp3", 1.0f, Random.Float(1.15f, 1.25f));
                        ((CrystalSpireSprite)CrystalSpire.this.sprite).updateIdle();
                    } else {
                        Sample.INSTANCE.play("sounds/shatter.mp3");
                        Sample.INSTANCE.playDelayed("sounds/rocks.mp3", 0.1f);
                        PixelScene.shake(3.0f, 0.7f);
                        Blacksmith.Quest.beatBoss();
                        GameScene.bossSlain();
                        Game.runOnRenderThread(new Callback(){

                            @Override
                            public void call() {
                                Music.INSTANCE.fadeOut(5.0f, new Callback(){

                                    @Override
                                    public void call() {
                                        Music.INSTANCE.end();
                                    }
                                });
                            }
                        });
                        Bestiary.setSeen(CrystalSpire.class);
                        Bestiary.countEncounter(CrystalSpire.class);
                        if (CrystalSpire.this.fieldOfView == null || CrystalSpire.this.fieldOfView.length != Dungeon.level.length()) {
                            CrystalSpire.this.fieldOfView = new boolean[Dungeon.level.length()];
                            Dungeon.level.updateFieldOfView(CrystalSpire.this, CrystalSpire.this.fieldOfView);
                        }
                        for (int i = 0; i < Dungeon.level.length(); ++i) {
                            if (!CrystalSpire.this.fieldOfView[i] || Dungeon.level.map[i] != 35) continue;
                            Level.set(i, 1);
                            GameScene.updateMap(i);
                            Splash.at(i, 0xFFFFFF, 5);
                        }
                        Bestiary.skipCountingEncounters = true;
                        for (Char char_ : Actor.chars()) {
                            if (!CrystalSpire.this.fieldOfView[char_.pos]) continue;
                            if (char_ instanceof CrystalGuardian) {
                                char_.damage(char_.HT, new SpireSpike());
                            }
                            if (!(char_ instanceof CrystalWisp)) continue;
                            Buff.affect(char_, Blindness.class, 5.0f);
                        }
                        Bestiary.skipCountingEncounters = false;
                    }
                    ++CrystalSpire.this.hits;
                    if (CrystalSpire.this.hits == 1) {
                        GLog.w(Messages.get(CrystalSpire.class, "warning", new Object[0]), new Object[0]);
                        PixelScene.shake(1.0f, 0.7f);
                        Sample.INSTANCE.play("sounds/mine.mp3");
                    } else if (CrystalSpire.this.hits >= 3) {
                        if (CrystalSpire.this.hits == 3) {
                            Sample.INSTANCE.play("sounds/rocks.mp3");
                            PixelScene.shake(3.0f, 0.7f);
                            GLog.n(Messages.get(CrystalSpire.class, "alert", new Object[0]), new Object[0]);
                            BossHealthBar.assignBoss(CrystalSpire.this);
                            CrystalSpire.this.abilityCooldown = 1.0f;
                        }
                        boolean affectingGuardians = false;
                        for (Char ch : Actor.chars()) {
                            if (ch instanceof CrystalWisp) {
                                if (((CrystalWisp)ch).state == ((CrystalWisp)ch).HUNTING || ((CrystalWisp)ch).target == CrystalSpire.this.pos) continue;
                                ((CrystalWisp)ch).beckon(CrystalSpire.this.pos);
                                continue;
                            }
                            if (!(ch instanceof CrystalGuardian) || ((CrystalGuardian)ch).state == ((CrystalGuardian)ch).HUNTING || ((CrystalGuardian)ch).target == CrystalSpire.this.pos) continue;
                            affectingGuardians = true;
                        }
                        if (affectingGuardians) {
                            boolean[] blArray = new boolean[Dungeon.level.length()];
                            for (int i = 0; i < Dungeon.level.length(); ++i) {
                                blArray[i] = Dungeon.level.map[i] == 35 ? true : (Terrain.flags[Dungeon.level.map[i]] & 1) != 0;
                            }
                            PathFinder.buildDistanceMap(CrystalSpire.this.pos, blArray);
                            for (Char ch : Actor.chars()) {
                                if (!(ch instanceof CrystalGuardian)) continue;
                                if (((CrystalGuardian)ch).state == ((CrystalGuardian)ch).SLEEPING) {
                                    ((CrystalGuardian)ch).aggro(Dungeon.hero);
                                    ((CrystalGuardian)ch).beckon(CrystalSpire.this.pos);
                                    if (PathFinder.distance[ch.pos] >= 20) continue;
                                    Buff.affect(ch, Paralysis.class, 20 - PathFinder.distance[ch.pos]);
                                    continue;
                                }
                                if (((CrystalGuardian)ch).state == ((CrystalGuardian)ch).HUNTING || ((CrystalGuardian)ch).target == CrystalSpire.this.pos) continue;
                                ((CrystalGuardian)ch).beckon(CrystalSpire.this.pos);
                                if (((CrystalGuardian)ch).state != CrystalSpire.this.HUNTING) {
                                    ((CrystalGuardian)ch).aggro(Dungeon.hero);
                                }
                                if (PathFinder.distance[ch.pos] <= 8) continue;
                                Buff.affect(ch, Haste.class, Math.max(15, Math.round((float)(PathFinder.distance[ch.pos] - 8) / 2.0f)));
                            }
                        }
                    }
                    Invisibility.dispel(Dungeon.hero);
                    Dungeon.hero.spendAndNext(p.delayFactor(CrystalSpire.this));
                }
            });
            return false;
        }
        return true;
    }

    public CrystalSpire() {
        this.HT = 300;
        this.HP = 300;
        this.spriteClass = CrystalSpireSprite.class;
        this.EXP = 20;
        this.actPriority = -21;
        this.state = this.PASSIVE;
        this.alignment = Char.Alignment.NEUTRAL;
        this.properties.add(Char.Property.IMMOVABLE);
        this.properties.add(Char.Property.BOSS);
        this.properties.add(Char.Property.INORGANIC);
        this.properties.add(Char.Property.STATIC);
        this.targetedCells = new ArrayList();
        this.hits = 0;
        this.immunities.add(Blindness.class);
        switch (Random.Int(3)) {
            default: {
                this.spriteClass = CrystalSpireSprite.Blue.class;
                break;
            }
            case 1: {
                this.spriteClass = CrystalSpireSprite.Green.class;
                break;
            }
            case 2: {
                this.spriteClass = CrystalSpireSprite.Red.class;
            }
        }
    }

    @Override
    public float spawningWeight() {
        return 0.0f;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(SPRITE, this.spriteClass);
        bundle.put(HITS, this.hits);
        bundle.put(ABILITY_COOLDOWN, this.abilityCooldown);
        for (int i = 0; i < this.targetedCells.size(); ++i) {
            int[] bundleArr = new int[this.targetedCells.get(i).size()];
            for (int j = 0; j < this.targetedCells.get(i).size(); ++j) {
                bundleArr[j] = this.targetedCells.get(i).get(j);
            }
            bundle.put(TARGETED_CELLS + i, bundleArr);
        }
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.spriteClass = bundle.getClass(SPRITE);
        this.hits = bundle.getInt(HITS);
        if (this.hits >= 3) {
            BossHealthBar.assignBoss(this);
        }
        this.abilityCooldown = bundle.getFloat(ABILITY_COOLDOWN);
        this.targetedCells.clear();
        int i = 0;
        while (bundle.contains(TARGETED_CELLS + i)) {
            ArrayList<Integer> targets = new ArrayList<Integer>();
            for (int j : bundle.getIntArray(TARGETED_CELLS + i)) {
                targets.add(j);
            }
            this.targetedCells.add(targets);
            ++i;
        }
    }

    public static class SpireSpike {
    }
}

