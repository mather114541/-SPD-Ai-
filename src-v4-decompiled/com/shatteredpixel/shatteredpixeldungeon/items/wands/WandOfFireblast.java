/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.WildMagic;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.DamageWand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class WandOfFireblast
extends DamageWand {
    ConeAOE cone;

    public WandOfFireblast() {
        this.image = ItemSpriteSheet.WAND_FIREBOLT;
        this.collisionProperties = 0;
    }

    @Override
    public int min(int lvl) {
        return (1 + lvl) * this.chargesPerCast();
    }

    @Override
    public int max(int lvl) {
        switch (this.chargesPerCast()) {
            default: {
                return 2 + 2 * lvl;
            }
            case 2: {
                return 2 * (4 + 2 * lvl);
            }
            case 3: 
        }
        return 3 * (6 + 2 * lvl);
    }

    @Override
    public void onZap(Ballistica bolt) {
        ArrayList<Char> affectedChars = new ArrayList<Char>();
        ArrayList<Integer> adjacentCells = new ArrayList<Integer>();
        for (int cell : this.cone.cells) {
            Char ch;
            if (cell == bolt.sourcePos) continue;
            if (Dungeon.level.map[cell] == 5) {
                Level.set(cell, 6);
                GameScene.updateMap(cell);
            }
            if (Dungeon.level.adjacent(bolt.sourcePos, cell) && !Dungeon.level.flamable[cell] && !Dungeon.level.solid[cell]) {
                adjacentCells.add(cell);
                if (Dungeon.level.heaps.get(cell) != null) {
                    ((Heap)Dungeon.level.heaps.get(cell)).burn();
                }
            } else {
                GameScene.add(Blob.seed(cell, 1 + this.chargesPerCast(), Fire.class));
            }
            if ((ch = Actor.findChar(cell)) == null) continue;
            affectedChars.add(ch);
        }
        if (this.cone.cells.isEmpty()) {
            adjacentCells.add(bolt.sourcePos);
        }
        for (int cell : adjacentCells) {
            for (int i : PathFinder.NEIGHBOURS8) {
                if (!(Dungeon.level.trueDistance(cell + i, bolt.collisionPos) < Dungeon.level.trueDistance(cell, bolt.collisionPos)) || !Dungeon.level.flamable[cell + i] || Fire.volumeAt(cell + i, Fire.class) != 0) continue;
                GameScene.add(Blob.seed(cell + i, 1 + this.chargesPerCast(), Fire.class));
            }
        }
        for (Char ch : affectedChars) {
            this.wandProc(ch, this.chargesPerCast());
            ch.damage(this.damageRoll(), this);
            if (!ch.isActive()) continue;
            Buff.affect(ch, Burning.class).reignite(ch);
            switch (this.chargesPerCast()) {
                case 1: {
                    break;
                }
                case 2: {
                    Buff.affect(ch, Cripple.class, 4.0f);
                    break;
                }
                case 3: {
                    Buff.affect(ch, Paralysis.class, 4.0f);
                }
            }
        }
    }

    @Override
    public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
        float procChance = 0.0f;
        for (int i : PathFinder.NEIGHBOURS9) {
            if (Actor.findChar(defender.pos + i) != null && Actor.findChar(defender.pos + i).buff(Burning.class) != null) {
                procChance += 0.25f;
                continue;
            }
            if (Fire.volumeAt(defender.pos + i, Fire.class) <= 0) continue;
            procChance += 0.05f;
        }
        procChance = Math.min(1.0f, procChance);
        procChance *= Wand.procChanceMultiplier(attacker);
        if (Random.Float() < procChance) {
            float powerMulti = Math.max(1.0f, procChance);
            Blob fire = Dungeon.level.blobs.get(Fire.class);
            CellEmitter.center(defender.pos).burst(BlastParticle.FACTORY, 30);
            if (fire != null) {
                for (int i : PathFinder.NEIGHBOURS9) {
                    Char ch;
                    CellEmitter.get(defender.pos + i).burst(SmokeParticle.FACTORY, 4);
                    if (Fire.volumeAt(defender.pos + i, Fire.class) > 0) {
                        Dungeon.level.destroy(defender.pos + i);
                        GameScene.updateMap(defender.pos + i);
                        fire.clear(defender.pos + i);
                    }
                    if ((ch = Actor.findChar(defender.pos + i)) == null) continue;
                    if (ch.buff(Burning.class) != null) {
                        ch.buff(Burning.class).detach();
                    }
                    if (ch.alignment != Char.Alignment.ENEMY) continue;
                    ch.damage(Math.round(powerMulti * (float)Hero.heroDamageIntRange(2 + 2 * this.buffedLvl(), 8 + 4 * this.buffedLvl())), this);
                }
            }
            Sample.INSTANCE.play("sounds/blast.mp3");
        }
    }

    @Override
    public void fx(Ballistica bolt, Callback callback) {
        int maxDist = 3 + 2 * this.chargesPerCast();
        this.cone = new ConeAOE(bolt, maxDist, 30 + 20 * this.chargesPerCast(), 13);
        Ballistica longestRay = null;
        for (Ballistica ray : this.cone.outerRays) {
            if (longestRay == null || ray.dist > longestRay.dist) {
                longestRay = ray;
            }
            ((MagicMissile)WandOfFireblast.curUser.sprite.parent.recycle(MagicMissile.class)).reset(102, (Visual)WandOfFireblast.curUser.sprite, (int)ray.path.get(ray.dist), null);
        }
        MagicMissile.boltFromChar(WandOfFireblast.curUser.sprite.parent, 102, WandOfFireblast.curUser.sprite, longestRay.path.get(longestRay.dist / 2), callback);
        Sample.INSTANCE.play("sounds/zap.mp3");
        Sample.INSTANCE.play("sounds/burning.mp3");
    }

    @Override
    protected int chargesPerCast() {
        if (this.cursed || this.charger != null && this.charger.target != null && this.charger.target.buff(WildMagic.WildMagicTracker.class) != null) {
            return 1;
        }
        return (int)GameMath.gate(1.0f, (int)Math.ceil((float)this.curCharges * 0.3f), 3.0f);
    }

    @Override
    public String statsDesc() {
        if (this.levelKnown) {
            return Messages.get(this, "stats_desc", this.chargesPerCast(), this.min(), this.max());
        }
        return Messages.get(this, "stats_desc", this.chargesPerCast(), this.min(0), this.max(0));
    }

    @Override
    public String upgradeStat1(int level) {
        return 1 + level + "-" + (2 + 2 * level);
    }

    @Override
    public String upgradeStat2(int level) {
        return 2 + 2 * level + "-" + 2 * (4 + 2 * level);
    }

    @Override
    public String upgradeStat3(int level) {
        return 3 + 3 * level + "-" + 3 * (6 + 2 * level);
    }

    @Override
    public void staffFx(MagesStaff.StaffParticle particle) {
        particle.color(0xEE7722);
        particle.am = 0.5f;
        particle.setLifespan(0.6f);
        particle.acc.set(0.0f, -40.0f);
        particle.setSize(0.0f, 3.0f);
        particle.shuffleXY(1.5f);
    }
}

