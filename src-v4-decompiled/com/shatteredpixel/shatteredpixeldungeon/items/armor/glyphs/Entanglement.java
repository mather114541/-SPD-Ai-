/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EarthParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.plants.Earthroot;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class Entanglement
extends Armor.Glyph {
    private static ItemSprite.Glowing BROWN = new ItemSprite.Glowing(0x663300);

    @Override
    public int proc(Armor armor, Char attacker, Char defender, int damage) {
        int level = Math.max(0, armor.buffedLvl());
        float procChance = 0.25f * this.procChanceMultiplier(defender);
        if (Random.Float() < procChance) {
            float powerMulti = Math.max(1.0f, procChance);
            Buff.affect(defender, Earthroot.Armor.class).level(Math.round((float)(5 + 2 * level) * powerMulti));
            CellEmitter.bottom(defender.pos).start(EarthParticle.FACTORY, 0.05f, 8);
            if (defender == Dungeon.hero) {
                PixelScene.shake(1.0f, 0.4f);
            }
        }
        return damage;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return BROWN;
    }
}

