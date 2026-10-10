/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.features;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barkskin;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Blacksmith;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Dewdrop;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Camouflage;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.SandalsOfNature;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Berry;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.PetrifiedSeed;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.MiningLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.VaultLevel;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.Random;

public class HighGrass {
    private static boolean freezeTrample = false;

    public static void trample(Level level, int pos) {
        if (freezeTrample) {
            return;
        }
        Char ch = Actor.findChar(pos);
        if (level.map[pos] == 30) {
            if (ch instanceof Hero && ((Hero)ch).heroClass == HeroClass.HUNTRESS) {
                freezeTrample = true;
            } else {
                Level.set(pos, 2);
            }
        } else {
            if (ch instanceof Hero && ((Hero)ch).heroClass == HeroClass.HUNTRESS) {
                if (((Hero)ch).hasTalent(Talent.BARKSKIN)) {
                    Barkskin.conditionallyAppend(ch, ((Hero)ch).lvl * ((Hero)ch).pointsInTalent(Talent.BARKSKIN) / 3, 1);
                }
                Level.set(pos, 30);
                freezeTrample = true;
            } else {
                Level.set(pos, 2);
            }
            int naturalismLevel = 0;
            if (ch != null) {
                SandalsOfNature.Naturalism naturalism = ch.buff(SandalsOfNature.Naturalism.class);
                if (naturalism != null) {
                    if (!naturalism.isCursed()) {
                        naturalismLevel = naturalism.itemLevel() + 1;
                        naturalism.charge();
                    } else {
                        naturalismLevel = -1;
                    }
                }
                if (ch instanceof Hero && ((Hero)ch).hasTalent(Talent.NATURES_BOUNTY)) {
                    int berriesAvailable = 2 + 2 * ((Hero)ch).pointsInTalent(Talent.NATURES_BOUNTY);
                    Talent.NatureBerriesDropped dropped = Buff.affect(ch, Talent.NatureBerriesDropped.class);
                    if ((berriesAvailable = (int)((float)berriesAvailable - dropped.count())) > 0) {
                        int targetFloor = 2 + 2 * ((Hero)ch).pointsInTalent(Talent.NATURES_BOUNTY);
                        int n = (targetFloor -= berriesAvailable) >= 5 ? 3 : 2;
                        boolean droppingBerry = false;
                        if (Dungeon.depth > (targetFloor += n)) {
                            droppingBerry = Random.Int(10) == 0;
                        } else if (Dungeon.depth == targetFloor) {
                            droppingBerry = Random.Int(30) == 0;
                        } else if (Dungeon.depth < targetFloor) {
                            boolean bl = droppingBerry = Random.Int(90) == 0;
                        }
                        if (droppingBerry) {
                            dropped.countUp(1.0f);
                            level.drop((Item)new Berry(), (int)pos).sprite.drop();
                        }
                    }
                }
            }
            if (Dungeon.level instanceof MiningLevel && Blacksmith.Quest.Type() == 3 && Random.Int(3) != 0) {
                naturalismLevel = -1;
            }
            if (naturalismLevel >= 0) {
                float lootChance = 1.0f / (25.0f - (float)naturalismLevel * 4.0f);
                lootChance *= PetrifiedSeed.grassLootMultiplier();
                if (Dungeon.level instanceof VaultLevel) {
                    lootChance /= 3.0f;
                }
                if (Random.Float() < lootChance) {
                    if (Random.Float() < PetrifiedSeed.stoneInsteadOfSeedChance()) {
                        com.shatteredpixel.shatteredpixeldungeon.items.Heap h1 = level.drop((Item)Generator.randomUsingDefaults((Generator.Category)Generator.Category.STONE), (int)pos);
                        if (h1 != null && h1.sprite != null) h1.sprite.drop();
                    } else {
                        com.shatteredpixel.shatteredpixeldungeon.items.Heap h2 = level.drop((Item)Generator.random((Generator.Category)Generator.Category.SEED), (int)pos);
                        if (h2 != null && h2.sprite != null) h2.sprite.drop();
                    }
                }
                lootChance = 1.0f / (6.0f - (float)naturalismLevel / 2.0f);
                if (Dungeon.level != null && Dungeon.level.feeling == Level.Feeling.GRASS) {
                    lootChance /= 2.0f;
                }
                if (Random.Float() < lootChance) {
                    com.shatteredpixel.shatteredpixeldungeon.items.Heap h3 = level.drop((Item)new Dewdrop(), (int)pos);
                    if (h3 != null && h3.sprite != null) h3.sprite.drop();
                }
            }
            if (ch != null) {
                Camouflage.activate(ch, ch.glyphLevel(Camouflage.class));
            }
        }
        freezeTrample = false;
        if (!rl.RLMode.headless && ShatteredPixelDungeon.scene() instanceof GameScene) {
            GameScene.updateMap(pos);
            CellEmitter.get(pos).burst(LeafParticle.LEVEL_SPECIFIC, 4);
            if (Dungeon.level.heroFOV[pos]) {
                Dungeon.observe();
            }
        }
    }
}

