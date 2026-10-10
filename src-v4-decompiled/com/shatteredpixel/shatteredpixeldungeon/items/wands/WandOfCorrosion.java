/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorrosiveGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DwarfKing;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.CorrosionParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.ColorMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class WandOfCorrosion
extends Wand {
    public WandOfCorrosion() {
        this.image = ItemSpriteSheet.WAND_CORROSION;
        this.collisionProperties = 5;
    }

    @Override
    public void onZap(Ballistica bolt) {
        CorrosiveGas gas = Blob.seed(bolt.collisionPos, 50 + 10 * this.buffedLvl(), CorrosiveGas.class);
        CellEmitter.get(bolt.collisionPos).burst(Speck.factory(108), 10);
        gas.setStrength(2 + this.buffedLvl(), this.getClass());
        GameScene.add(gas);
        Sample.INSTANCE.play("sounds/gas.mp3");
        for (int i : PathFinder.NEIGHBOURS9) {
            Char ch = Actor.findChar(bolt.collisionPos + i);
            if (ch == null) continue;
            this.wandProc(ch, this.chargesPerCast());
            if (i != 0 || !(ch instanceof DwarfKing)) continue;
            Statistics.qualifiedForBossChallengeBadge = false;
        }
        if (Actor.findChar(bolt.collisionPos) == null) {
            Dungeon.level.pressCell(bolt.collisionPos);
        }
    }

    @Override
    public void fx(Ballistica bolt, Callback callback) {
        MagicMissile.boltFromChar(WandOfCorrosion.curUser.sprite.parent, 3, WandOfCorrosion.curUser.sprite, bolt.collisionPos, callback);
        Sample.INSTANCE.play("sounds/zap.mp3");
    }

    @Override
    public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
        int level = Math.max(0, this.buffedLvl());
        float procChance = ((float)level + 1.0f) / ((float)level + 3.0f) * WandOfCorrosion.procChanceMultiplier(attacker);
        if (Random.Float() < procChance) {
            float powerMulti = Math.max(1.0f, procChance);
            Buff.affect(defender, Ooze.class).set(20.0f * powerMulti);
            CellEmitter.center(defender.pos).burst(CorrosionParticle.SPLASH, 5);
        }
    }

    @Override
    public void staffFx(MagesStaff.StaffParticle particle) {
        particle.color(ColorMath.random(0xAAAAAA, 0xFF8800));
        particle.am = 0.6f;
        particle.setLifespan(1.0f);
        particle.acc.set(0.0f, 20.0f);
        particle.setSize(0.5f, 3.0f);
        particle.shuffleXY(1.0f);
    }

    @Override
    public String statsDesc() {
        if (this.levelKnown) {
            return Messages.get(this, "stats_desc", 2 + this.buffedLvl());
        }
        return Messages.get(this, "stats_desc", 2);
    }

    @Override
    public String upgradeStat1(int level) {
        return Integer.toString(level + 2);
    }

    @Override
    public String upgradeStat2(int level) {
        return Messages.decimalFormat("#.##x", 1.0f + 0.2f * (float)level);
    }
}

