/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.ClericSpell;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;

public class AuraOfProtection
extends ClericSpell {
    public static AuraOfProtection INSTANCE = new AuraOfProtection();

    @Override
    public int icon() {
        return 56;
    }

    @Override
    public String desc() {
        int dmgReduction = 10 + 10 * Dungeon.hero.pointsInTalent(Talent.AURA_OF_PROTECTION);
        int glyphPow = 25 + 25 * Dungeon.hero.pointsInTalent(Talent.AURA_OF_PROTECTION);
        return Messages.get(this, "desc", dmgReduction, glyphPow) + "\n\n" + Messages.get(this, "charge_cost", (int)this.chargeUse(Dungeon.hero));
    }

    @Override
    public float chargeUse(Hero hero) {
        return 2.0f;
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero) && hero.hasTalent(Talent.AURA_OF_PROTECTION);
    }

    @Override
    public void onCast(HolyTome tome, Hero hero) {
        Buff.affect(hero, AuraBuff.class, AuraBuff.DURATION);
        Sample.INSTANCE.play("sounds/read.mp3");
        hero.spend(1.0f);
        hero.busy();
        hero.sprite.operate(hero.pos);
        this.onSpellCast(tome, hero);
    }

    public static class AuraBuff
    extends FlavourBuff {
        public static float DURATION = 20.0f;
        private Emitter particles;

        public AuraBuff() {
            this.type = Buff.buffType.POSITIVE;
        }

        @Override
        public int icon() {
            return 80;
        }

        @Override
        public void fx(boolean on) {
            if (on && (this.particles == null || this.particles.parent == null)) {
                this.particles = this.target.sprite.emitter();
                this.particles.pos(this.target.sprite, -32.0f, -32.0f, 80.0f, 80.0f);
                this.particles.fillTarget = false;
                this.particles.pour(Speck.factory(2), 0.02f);
            } else if (!on && this.particles != null) {
                this.particles.on = false;
            }
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0.0f, (DURATION - this.visualcooldown()) / DURATION);
        }
    }
}

