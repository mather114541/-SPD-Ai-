/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.journal.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SheepSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Sheep
extends NPC {
    private static final String[] LINE_KEYS = new String[]{"Baa!", "Baa?", "Baa.", "Baa..."};
    private float lifespan;
    private static final String LIFESPAN = "lifespan";

    public Sheep() {
        this.spriteClass = SheepSprite.class;
    }

    @Override
    protected boolean act() {
        if (Dungeon.level.heroFOV[this.pos]) {
            Bestiary.setSeen(this.getClass());
        }
        this.HP = 0;
        this.destroy();
        this.sprite.die();
        return true;
    }

    public void initialize(float lifespan) {
        this.lifespan = lifespan;
        this.spend(lifespan + Random.Float(-2.0f, 2.0f));
    }

    @Override
    public int defenseSkill(Char enemy) {
        return INFINITE_EVASION;
    }

    @Override
    public void damage(int dmg, Object src) {
    }

    @Override
    public boolean add(Buff buff) {
        return false;
    }

    @Override
    public boolean interact(Char c) {
        Bestiary.setSeen(this.getClass());
        this.sprite.showStatus(0xFFFF00, Messages.get(this, Random.element(LINE_KEYS), new Object[0]), new Object[0]);
        if (c == Dungeon.hero) {
            Dungeon.hero.spendAndNext(1.0f);
            Sample.INSTANCE.play("sounds/sheep.mp3", 1.0f, Random.Float(0.91f, 1.1f));
            if (this.lifespan >= 20.0f) {
                this.spend(-this.cooldown());
            }
        }
        return true;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LIFESPAN, this.lifespan);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.lifespan = bundle.getInt(LIFESPAN);
    }
}

