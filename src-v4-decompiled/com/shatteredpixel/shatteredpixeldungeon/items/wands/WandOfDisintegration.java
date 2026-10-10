/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Web;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PurpleParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.DamageWand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class WandOfDisintegration
extends DamageWand {
    public WandOfDisintegration() {
        this.image = ItemSpriteSheet.WAND_DISINTEGRATION;
        this.collisionProperties = 0;
    }

    @Override
    public int min(int lvl) {
        return 2 + lvl;
    }

    @Override
    public int max(int lvl) {
        return 8 + 4 * lvl;
    }

    @Override
    public int targetingPos(Hero user, int dst) {
        if (!this.cursed || !this.cursedKnown) {
            return dst;
        }
        return super.targetingPos(user, dst);
    }

    @Override
    public void onZap(Ballistica beam) {
        boolean terrainAffected = false;
        int level = this.buffedLvl();
        int maxDistance = Math.min(this.distance(), beam.dist);
        ArrayList<Char> chars = new ArrayList<Char>();
        Blob web = Dungeon.level.blobs.get(Web.class);
        int terrainPassed = 2;
        int terrainBonus = 0;
        for (int c : beam.subPath(1, maxDistance)) {
            Char ch = Actor.findChar(c);
            if (ch != null) {
                terrainBonus += terrainPassed / 3;
                terrainPassed %= 3;
                if (!(ch instanceof Mob) || ((Mob)ch).state != ((Mob)ch).PASSIVE || Dungeon.level.mapped[c] || Dungeon.level.visited[c]) {
                    chars.add(ch);
                }
            }
            if (Dungeon.level.solid[c]) {
                ++terrainPassed;
            }
            if (Dungeon.level.flamable[c]) {
                Dungeon.level.destroy(c);
                GameScene.updateMap(c);
                terrainAffected = true;
            }
            CellEmitter.center(c).burst(PurpleParticle.BURST, Random.IntRange(1, 2));
        }
        if (terrainAffected) {
            Dungeon.observe();
        }
        int lvl = level + (chars.size() - 1) + terrainBonus;
        for (Char ch : chars) {
            this.wandProc(ch, this.chargesPerCast());
            ch.damage(this.damageRoll(lvl), this);
            ch.sprite.centerEmitter().burst(PurpleParticle.BURST, Random.IntRange(1, 2));
            ch.sprite.flash();
        }
    }

    @Override
    public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
    }

    private int distance() {
        return this.buffedLvl() * 2 + 6;
    }

    @Override
    public String upgradeStat2(int level) {
        return Integer.toString(6 + level * 2);
    }

    @Override
    public void fx(Ballistica beam, Callback callback) {
        int cell = beam.path.get(Math.min(beam.dist, this.distance()));
        WandOfDisintegration.curUser.sprite.parent.add(new Beam.DeathRay(WandOfDisintegration.curUser.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(cell)));
        Sample.INSTANCE.play("sounds/ray.mp3");
        callback.call();
    }

    @Override
    public void staffFx(MagesStaff.StaffParticle particle) {
        particle.color(0x220022);
        particle.am = 0.6f;
        particle.setLifespan(1.0f);
        particle.acc.set(10.0f, -10.0f);
        particle.setSize(0.5f, 3.0f);
        particle.shuffleXY(1.0f);
    }
}

