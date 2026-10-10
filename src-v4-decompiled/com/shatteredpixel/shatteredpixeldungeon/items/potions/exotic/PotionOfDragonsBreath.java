/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class PotionOfDragonsBreath
extends ExoticPotion {
    protected static boolean identifiedByUse = false;
    private CellSelector.Listener targeter;

    public PotionOfDragonsBreath() {
        this.icon = ItemSpriteSheet.Icons.POTION_DRGBREATH;
        this.targeter = new CellSelector.Listener(){
            private boolean showingWindow = false;
            private boolean potionAlreadyUsed = false;

            @Override
            public void onSelect(final Integer cell) {
                if (this.showingWindow) {
                    return;
                }
                if (this.potionAlreadyUsed) {
                    this.potionAlreadyUsed = false;
                    return;
                }
                if (cell == null && identifiedByUse) {
                    this.showingWindow = true;
                    ShatteredPixelDungeon.runOnRenderThread(new Callback(){

                        @Override
                        public void call() {
                            GameScene.show(new WndOptions(new ItemSprite(PotionOfDragonsBreath.this), Messages.titleCase(PotionOfDragonsBreath.this.name()), Messages.get(ExoticPotion.class, "warning", new Object[0]), new String[]{Messages.get(ExoticPotion.class, "yes", new Object[0]), Messages.get(ExoticPotion.class, "no", new Object[0])}){

                                @Override
                                protected void onSelect(int index) {
                                    showingWindow = false;
                                    switch (index) {
                                        case 0: {
                                            curUser.spendAndNext(1.0f);
                                            identifiedByUse = false;
                                            break;
                                        }
                                        case 1: {
                                            GameScene.selectCell(PotionOfDragonsBreath.this.targeter);
                                        }
                                    }
                                }

                                @Override
                                public void onBackPressed() {
                                }
                            });
                        }
                    });
                } else if (cell != null) {
                    if (!identifiedByUse) {
                        curItem.detach(curUser.belongings.backpack);
                    }
                    this.potionAlreadyUsed = true;
                    identifiedByUse = false;
                    curUser.busy();
                    Sample.INSTANCE.play("sounds/drink.mp3");
                    curUser.sprite.operate(curUser.pos, new Callback(){

                        @Override
                        public void call() {
                            curUser.sprite.idle();
                            curUser.sprite.zap(cell);
                            Sample.INSTANCE.play("sounds/burning.mp3");
                            final Ballistica bolt = new Ballistica(curUser.pos, cell, 0);
                            int maxDist = 6;
                            int dist = Math.min(bolt.dist, maxDist);
                            final ConeAOE cone = new ConeAOE(bolt, 6.0f, 60.0f, 13);
                            for (Ballistica ray : cone.outerRays) {
                                ((MagicMissile)curUser.sprite.parent.recycle(MagicMissile.class)).reset(102, (Visual)curUser.sprite, (int)ray.path.get(ray.dist), null);
                            }
                            MagicMissile.boltFromChar(curUser.sprite.parent, 102, curUser.sprite, bolt.path.get(dist / 2), new Callback(){

                                @Override
                                public void call() {
                                    ArrayList<Integer> adjacentCells = new ArrayList<Integer>();
                                    for (int cell : cone.cells) {
                                        Char ch;
                                        if (cell == bolt.sourcePos) continue;
                                        if (Dungeon.level.map[cell] == 5) {
                                            Level.set(cell, 6);
                                            GameScene.updateMap(cell);
                                        }
                                        if (Dungeon.level.adjacent(bolt.sourcePos, cell) && !Dungeon.level.flamable[cell]) {
                                            adjacentCells.add(cell);
                                        } else {
                                            GameScene.add(Blob.seed(cell, 5, Fire.class));
                                        }
                                        if ((ch = Actor.findChar(cell)) == null) continue;
                                        Buff.affect(ch, Burning.class).reignite(ch);
                                        Buff.prolong(ch, Cripple.class, 5.0f);
                                    }
                                    for (int cell : adjacentCells) {
                                        for (int i : PathFinder.NEIGHBOURS4) {
                                            if (!(Dungeon.level.trueDistance(cell + i, bolt.sourcePos) > Dungeon.level.trueDistance(cell, bolt.sourcePos)) || !Dungeon.level.flamable[cell + i] || Fire.volumeAt(cell + i, Fire.class) != 0) continue;
                                            GameScene.add(Blob.seed(cell + i, 5, Fire.class));
                                        }
                                    }
                                    curUser.spendAndNext(1.0f);
                                    if (!PotionOfDragonsBreath.this.anonymous) {
                                        Catalog.countUse(PotionOfDragonsBreath.class);
                                        if (Random.Float() < PotionOfDragonsBreath.this.talentChance) {
                                            Talent.onPotionUsed(curUser, curUser.pos, PotionOfDragonsBreath.this.talentFactor);
                                        }
                                    }
                                }
                            });
                        }
                    });
                }
            }

            @Override
            public String prompt() {
                return Messages.get(PotionOfDragonsBreath.class, "prompt", new Object[0]);
            }
        };
    }

    @Override
    protected void drink(Hero hero) {
        if (!this.isKnown()) {
            this.identify();
            curItem = this.detach(hero.belongings.backpack);
            identifiedByUse = true;
        } else {
            identifiedByUse = false;
        }
        GameScene.selectCell(this.targeter);
    }
}

