/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.DamageWand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.MagicalFireRoom;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class WandOfFrost
extends DamageWand {
    public WandOfFrost() {
        this.image = ItemSpriteSheet.WAND_FROST;
    }

    @Override
    public int min(int lvl) {
        return 2 + lvl;
    }

    @Override
    public int max(int lvl) {
        return 8 + 5 * lvl;
    }

    @Override
    public void onZap(Ballistica bolt) {
        Char ch;
        MagicalFireRoom.EternalFire eternalFire;
        Fire fire;
        Heap heap = (Heap)Dungeon.level.heaps.get(bolt.collisionPos);
        if (heap != null) {
            heap.freeze();
        }
        if ((fire = (Fire)Dungeon.level.blobs.get(Fire.class)) != null && fire.volume > 0) {
            fire.clear(bolt.collisionPos);
        }
        if ((eternalFire = (MagicalFireRoom.EternalFire)Dungeon.level.blobs.get(MagicalFireRoom.EternalFire.class)) != null && eternalFire.volume > 0) {
            eternalFire.clear(bolt.collisionPos);
            if (bolt.path.size() > bolt.dist + 1) {
                eternalFire.clear(bolt.path.get(bolt.dist + 1));
            }
        }
        if ((ch = Actor.findChar(bolt.collisionPos)) != null) {
            int damage = this.damageRoll();
            if (ch.buff(Frost.class) != null) {
                return;
            }
            if (ch.buff(Chill.class) != null) {
                float chillturns = Math.min(10.0f, ch.buff(Chill.class).cooldown());
                damage = (int)Math.round((double)damage * Math.pow(0.9333f, chillturns));
            } else {
                ch.sprite.burst(-6697729, this.buffedLvl() / 2 + 2);
            }
            this.wandProc(ch, this.chargesPerCast());
            ch.damage(damage, this);
            Sample.INSTANCE.play("sounds/hit_magic.mp3", 1.0f, 1.1f * Random.Float(0.87f, 1.15f));
            if (ch.isAlive()) {
                if (Dungeon.level.water[ch.pos]) {
                    Buff.affect(ch, Chill.class, 4 + this.buffedLvl());
                } else {
                    Buff.affect(ch, Chill.class, 2 + this.buffedLvl());
                }
            }
        } else {
            Dungeon.level.pressCell(bolt.collisionPos);
        }
    }

    @Override
    public String upgradeStat2(int level) {
        return Integer.toString(2 + level);
    }

    @Override
    public void fx(Ballistica bolt, Callback callback) {
        MagicMissile.boltFromChar(WandOfFrost.curUser.sprite.parent, 1, WandOfFrost.curUser.sprite, bolt.collisionPos, callback);
        Sample.INSTANCE.play("sounds/zap.mp3");
    }

    @Override
    public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
        Chill chill = defender.buff(Chill.class);
        if (chill != null) {
            float procChance = (float)((int)Math.floor(chill.cooldown()) - 1) / 9.0f;
            procChance *= WandOfFrost.procChanceMultiplier(attacker);
            if (Random.Float() < procChance) {
                final float powerMulti = Math.max(1.0f, procChance);
                new FlavourBuff(){
                    {
                        this.actPriority = 100;
                    }

                    @Override
                    public boolean act() {
                        Buff.affect(this.target, Frost.class, Math.round(10.0f * powerMulti));
                        return super.act();
                    }
                }.attachTo(defender);
            }
        }
    }

    @Override
    public void staffFx(MagesStaff.StaffParticle particle) {
        particle.color(0x88CCFF);
        particle.am = 0.6f;
        particle.setLifespan(2.0f);
        float angle = Random.Float(6.283185f);
        particle.speed.polar(angle, 2.0f);
        particle.acc.set(0.0f, 1.0f);
        particle.setSize(0.0f, 1.5f);
        particle.radiateXY(Random.Float(1.0f));
    }
}

