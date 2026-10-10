/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;

public class HeavyBoomerang
extends MissileWeapon {
    boolean circlingBack;

    public HeavyBoomerang() {
        this.image = ItemSpriteSheet.BOOMERANG;
        this.hitSound = "sounds/hit_crush.mp3";
        this.hitSoundPitch = 1.0f;
        this.tier = 4;
        this.sticky = false;
        this.baseUses = 5.0f;
        this.circlingBack = false;
    }

    @Override
    public int max(int lvl) {
        return 4 * this.tier + (this.tier - 1) * lvl;
    }

    @Override
    protected float adjacentAccFactor(Char owner, Char target) {
        if (this.circlingBack) {
            return 1.5f;
        }
        return super.adjacentAccFactor(owner, target);
    }

    @Override
    public float pickupDelay() {
        return this.circlingBack ? 0.0f : super.pickupDelay();
    }

    @Override
    protected void rangedHit(Char enemy, int cell) {
        this.decrementDurability();
        if (this.durability > 0.0f) {
            Buff.append(Dungeon.hero, CircleBack.class).setup(this, cell, Dungeon.hero.pos, Dungeon.depth, Dungeon.branch);
        }
    }

    @Override
    protected void rangedMiss(int cell) {
        this.parent = null;
        Buff.append(Dungeon.hero, CircleBack.class).setup(this, cell, Dungeon.hero.pos, Dungeon.depth, Dungeon.branch);
    }

    public static class CircleBack
    extends Buff {
        private HeavyBoomerang boomerang;
        private int thrownPos;
        private int returnPos;
        private int returnDepth;
        private int returnBranch;
        private int left;
        private static final String BOOMERANG = "boomerang";
        private static final String THROWN_POS = "thrown_pos";
        private static final String RETURN_POS = "return_pos";
        private static final String RETURN_DEPTH = "return_depth";
        private static final String RETURN_BRANCH = "return_branch";

        public CircleBack() {
            this.revivePersists = true;
        }

        public void setup(HeavyBoomerang boomerang, int thrownPos, int returnPos, int returnDepth, int returnBranch) {
            this.boomerang = boomerang;
            this.thrownPos = thrownPos;
            this.returnPos = returnPos;
            this.returnDepth = returnDepth;
            this.returnBranch = returnBranch;
            this.left = 5;
        }

        public int returnPos() {
            return this.returnPos;
        }

        public MissileWeapon cancel() {
            this.detach();
            return this.boomerang;
        }

        public int activeDepth() {
            return this.returnDepth;
        }

        @Override
        public boolean act() {
            if (this.returnDepth == Dungeon.depth && this.returnBranch == Dungeon.branch) {
                --this.left;
                if (this.left <= 0) {
                    final Char returnTarget = Actor.findChar(this.returnPos);
                    final Char target = this.target;
                    MissileSprite visual = (MissileSprite)Dungeon.hero.sprite.parent.recycle(MissileSprite.class);
                    visual.reset(this.thrownPos, this.returnPos, (Item)this.boomerang, new Callback(){

                        @Override
                        public void call() {
                            this.detach();
                            boomerang.circlingBack = true;
                            if (returnTarget == target) {
                                if (!(boomerang.spawnedForEffect || target instanceof Hero && boomerang.doPickUp((Hero)target))) {
                                    Dungeon.level.drop((Item)boomerang, (int)returnPos).sprite.drop();
                                }
                            } else if (returnTarget != null) {
                                if (((Hero)target).shoot(returnTarget, boomerang)) {
                                    boomerang.decrementDurability();
                                }
                                if (!boomerang.spawnedForEffect && boomerang.durability > 0.0f) {
                                    Dungeon.level.drop((Item)boomerang, (int)returnPos).sprite.drop();
                                }
                            } else if (!boomerang.spawnedForEffect) {
                                Dungeon.level.drop((Item)boomerang, (int)returnPos).sprite.drop();
                            }
                            boomerang.circlingBack = false;
                            this.next();
                        }
                    });
                    visual.alpha(0.0f);
                    float duration = Dungeon.level.trueDistance(this.thrownPos, this.returnPos) / 20.0f;
                    target.sprite.parent.add(new AlphaTweener(visual, 1.0f, duration));
                    return false;
                }
            }
            this.spend(1.0f);
            return true;
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(BOOMERANG, this.boomerang);
            bundle.put(THROWN_POS, this.thrownPos);
            bundle.put(RETURN_POS, this.returnPos);
            bundle.put(RETURN_DEPTH, this.returnDepth);
            bundle.put(RETURN_BRANCH, this.returnBranch);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.boomerang = (HeavyBoomerang)bundle.get(BOOMERANG);
            this.thrownPos = bundle.getInt(THROWN_POS);
            this.returnPos = bundle.getInt(RETURN_POS);
            this.returnDepth = bundle.getInt(RETURN_DEPTH);
            this.returnBranch = bundle.contains(RETURN_BRANCH) ? bundle.getInt(RETURN_BRANCH) : 0;
        }
    }
}

