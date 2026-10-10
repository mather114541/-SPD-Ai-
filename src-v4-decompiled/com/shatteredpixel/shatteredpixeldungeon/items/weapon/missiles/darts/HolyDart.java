/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bless;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.TippedDart;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class HolyDart
extends TippedDart {
    public HolyDart() {
        this.image = ItemSpriteSheet.HOLY_DART;
    }

    @Override
    public int damageRoll(Char owner) {
        if (owner instanceof Hero && ((Hero)owner).attackTarget().alignment == owner.alignment) {
            return 0;
        }
        return super.damageRoll(owner);
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (this.processingChargedShot && defender == attacker) {
            return super.proc(attacker, defender, damage);
        }
        if (attacker.alignment == defender.alignment) {
            Buff.affect(defender, Bless.class, Math.round(30.0f));
            return 0;
        }
        if (Char.hasProp(defender, Char.Property.UNDEAD) || Char.hasProp(defender, Char.Property.DEMONIC)) {
            defender.sprite.emitter().start(ShadowParticle.UP, 0.05f, 10 + this.buffedLvl());
            Sample.INSTANCE.play("sounds/burning.mp3");
            defender.damage(Random.NormalIntRange(10 + Dungeon.scalingDepth() / 3, 20 + Dungeon.scalingDepth() / 3), this);
        } else if (!this.processingChargedShot) {
            Buff.affect(defender, Bless.class, Math.round(30.0f));
        }
        return super.proc(attacker, defender, damage);
    }
}

