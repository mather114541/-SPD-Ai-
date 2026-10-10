/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.TimeStasis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Thief;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Brimstone;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.items.food.ChargrilledMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.food.FrozenCarpaccio;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.levels.VaultLevel;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class Burning
extends Buff
implements Hero.Doom,
Buff.DOTbuff {
    private static final float DURATION = 8.0f;
    private float left;
    private boolean acted = false;
    private int burnIncrement = 0;
    private int nextHit = 0;
    private static final String LEFT = "left";
    private static final String ACTED = "acted";
    private static final String BURN = "burnIncrement";
    private static final String NEXT_DMG = "next_dmg";

    public Burning() {
        this.type = Buff.buffType.NEGATIVE;
        this.announced = true;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LEFT, this.left);
        bundle.put(ACTED, this.acted);
        bundle.put(BURN, this.burnIncrement);
        bundle.put(NEXT_DMG, this.nextHit);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.left = bundle.getFloat(LEFT);
        this.acted = bundle.getBoolean(ACTED);
        this.burnIncrement = bundle.getInt(BURN);
        this.nextHit = bundle.getInt(NEXT_DMG);
    }

    @Override
    public boolean attachTo(Char target) {
        Buff.detach(target, Chill.class);
        return super.attachTo(target);
    }

    @Override
    public boolean act() {
        if (this.acted && Dungeon.level.water[this.target.pos] && !this.target.flying) {
            this.detach();
        } else if (this.target.isAlive() && !this.target.isImmune(this.getClass())) {
            this.acted = true;
            if (this.nextHit == 0) {
                this.nextHit = Random.NormalIntRange(1, 3 + Dungeon.scalingDepth() / 4);
            }
            int damage = this.nextHit;
            this.nextHit = Random.NormalIntRange(1, 3 + Dungeon.scalingDepth() / 4);
            Buff.detach(this.target, Chill.class);
            if (this.target instanceof Hero && this.target.buff(TimekeepersHourglass.timeStasis.class) == null && this.target.buff(TimeStasis.class) == null) {
                Hero hero = (Hero)this.target;
                hero.damage(damage, this);
                ++this.burnIncrement;
                if (Random.Int(3) < this.burnIncrement - 3 && !(Dungeon.level instanceof VaultLevel)) {
                    this.burnIncrement = 0;
                    ArrayList<Item> burnable = new ArrayList<Item>();
                    if (!hero.belongings.lostInventory()) {
                        for (Item i : hero.belongings.backpack.items) {
                            if (i.unique || !(i instanceof Scroll) && !(i instanceof MysteryMeat) && !(i instanceof FrozenCarpaccio)) continue;
                            burnable.add(i);
                        }
                    }
                    if (!burnable.isEmpty()) {
                        ChargrilledMeat steak;
                        Item toBurn = ((Item)Random.element(burnable)).detach(hero.belongings.backpack);
                        GLog.w(Messages.capitalize(Messages.get(this, "burnsup", toBurn.title())), new Object[0]);
                        if ((toBurn instanceof MysteryMeat || toBurn instanceof FrozenCarpaccio) && !(steak = new ChargrilledMeat()).collect(hero.belongings.backpack)) {
                            Dungeon.level.drop((Item)steak, (int)hero.pos).sprite.drop();
                        }
                        Heap.burnFX(hero.pos);
                    }
                }
            } else {
                this.target.damage(damage, this);
            }
            if (this.target instanceof Thief && ((Thief)this.target).item != null) {
                Item item = ((Thief)this.target).item;
                if (!item.unique && item instanceof Scroll) {
                    this.target.sprite.emitter().burst(ElmoParticle.FACTORY, 6);
                    ((Thief)this.target).item = null;
                } else if (item instanceof MysteryMeat) {
                    this.target.sprite.emitter().burst(ElmoParticle.FACTORY, 6);
                    ((Thief)this.target).item = new ChargrilledMeat();
                }
            }
        } else {
            this.detach();
        }
        if (Dungeon.level.flamable[this.target.pos] && Blob.volumeAt(this.target.pos, Fire.class) == 0) {
            GameScene.add(Blob.seed(this.target.pos, 4, Fire.class));
        }
        this.spend(1.0f);
        this.left -= 1.0f;
        if (this.left <= 0.0f || Dungeon.level.water[this.target.pos] && !this.target.flying) {
            this.detach();
        }
        this.target.needsIncomingDOTUpdate = true;
        return true;
    }

    @Override
    public void detach() {
        this.target.needsIncomingDOTUpdate = true;
        super.detach();
    }

    public void reignite(Char ch) {
        this.reignite(ch, 8.0f);
    }

    public void reignite(Char ch, float duration) {
        if (ch.isImmune(Burning.class) && ch.glyphLevel(Brimstone.class) >= 0) {
            Barrier barrier;
            float shieldChance = 2.0f * (Armor.Glyph.genericProcChanceMultiplier(ch) - 1.0f);
            int shieldCap = Math.round(shieldChance * 4.0f);
            int shieldGain = (int)shieldChance;
            if (Random.Float() < shieldChance % 1.0f) {
                ++shieldGain;
            }
            if (shieldCap > 0 && shieldGain > 0 && (barrier = Buff.affect(ch, Barrier.class)).shielding() < shieldCap) {
                barrier.incShield(Math.min(shieldGain, shieldCap - barrier.shielding()));
            }
        }
        if (this.left < duration) {
            this.left = duration;
            ch.needsIncomingDOTUpdate = true;
        }
        this.acted = false;
    }

    public void extend(float duration) {
        this.left += duration;
        if (this.target != null) {
            this.target.needsIncomingDOTUpdate = true;
        }
    }

    @Override
    public int icon() {
        return 2;
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0.0f, (8.0f - this.left) / 8.0f);
    }

    @Override
    public String iconTextDisplay() {
        return Integer.toString((int)this.left);
    }

    @Override
    public void fx(boolean on) {
        if (on) {
            this.target.sprite.add(CharSprite.State.BURNING);
        } else {
            this.target.sprite.remove(CharSprite.State.BURNING);
        }
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", this.dispTurns(this.left));
    }

    @Override
    public void onDeath() {
        Badges.validateDeathFromFire();
        Dungeon.fail(this);
        GLog.n(Messages.get(this, "ondeath", new Object[0]), new Object[0]);
    }

    @Override
    public int totalIncomingDMG() {
        if (this.nextHit == 0) {
            this.nextHit = Random.NormalIntRange(1, 3 + Dungeon.scalingDepth() / 4);
        }
        float avgDmg = 2.0f + (float)Dungeon.scalingDepth() / 8.0f;
        return (int)Math.round((double)this.nextHit + Math.ceil(this.left - 1.0f) * (double)avgDmg);
    }
}

