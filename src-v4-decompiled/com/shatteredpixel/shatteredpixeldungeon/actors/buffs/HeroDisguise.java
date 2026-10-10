/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class HeroDisguise
extends FlavourBuff {
    private HeroClass cls;
    public static float DURATION = 1000.0f;
    private static final String CLASS = "class";

    public HeroDisguise() {
        this.announced = true;
        this.cls = null;
    }

    public HeroClass getDisguise() {
        return this.cls;
    }

    @Override
    public int icon() {
        return 71;
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0.0f, (DURATION - this.visualcooldown()) / DURATION);
    }

    @Override
    public void fx(boolean on) {
        if (this.target instanceof Hero && this.target.sprite instanceof HeroSprite) {
            if (this.cls == null) {
                do {
                    this.cls = Random.oneOf(HeroClass.values());
                } while (this.cls == ((Hero)this.target).heroClass);
            }
            if (on) {
                ((HeroSprite)this.target.sprite).disguise(this.cls);
            } else {
                ((HeroSprite)this.target.sprite).disguise(((Hero)this.target).heroClass);
            }
            GameScene.updateAvatar();
        }
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(CLASS, this.cls);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.cls = bundle.getEnum(CLASS, HeroClass.class);
    }
}

