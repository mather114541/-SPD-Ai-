/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.TalentIcon;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public class Transmuting
extends Component {
    private static final float FADE_IN_TIME = 0.2f;
    private static final float TRANSMUTING_TIME = 1.4f;
    private static final float FADE_OUT_TIME = 0.4f;
    private static final float ALPHA = 0.6f;
    Image oldSprite;
    Image newSprite;
    private Char target;
    private Phase phase;
    private float duration;
    private float passed;

    public Transmuting(Item oldItem, Item newItem) {
        this.oldSprite = new ItemSprite(oldItem);
        this.oldSprite.originToCenter();
        this.add(this.oldSprite);
        this.newSprite = new ItemSprite(newItem);
        this.newSprite.originToCenter();
        this.add(this.newSprite);
        this.oldSprite.alpha(0.0f);
        this.newSprite.alpha(0.0f);
        this.phase = Phase.FADE_IN;
        this.duration = 0.2f;
        this.passed = 0.0f;
    }

    public Transmuting(Talent oldTalent, Talent newTalent) {
        this.oldSprite = new TalentIcon(oldTalent);
        this.oldSprite.originToCenter();
        this.add(this.oldSprite);
        this.newSprite = new TalentIcon(newTalent);
        this.newSprite.originToCenter();
        this.add(this.newSprite);
        this.oldSprite.alpha(0.0f);
        this.newSprite.alpha(0.0f);
        this.phase = Phase.FADE_IN;
        this.duration = 0.2f;
        this.passed = 0.0f;
    }

    @Override
    public void update() {
        float f;
        super.update();
        if (this.passed == 0.0f) {
            this.oldSprite.x = this.target.sprite.center().x - this.oldSprite.width() / 2.0f;
            this.oldSprite.y = this.target.sprite.y - this.oldSprite.height();
            this.newSprite.x = this.target.sprite.center().x - this.newSprite.width() / 2.0f;
            this.newSprite.y = this.target.sprite.y - this.newSprite.height();
        }
        switch (this.phase.ordinal()) {
            case 0: {
                this.oldSprite.alpha(this.passed / this.duration * 0.6f);
                this.oldSprite.scale.set(this.passed / this.duration);
                break;
            }
            case 1: {
                this.oldSprite.alpha((1.4f - this.passed) / this.duration * 0.6f);
                this.newSprite.alpha(this.passed / this.duration * 0.6f);
                break;
            }
            case 2: {
                this.newSprite.alpha((1.0f - this.passed / this.duration) * 0.6f);
                this.newSprite.scale.set(1.0f + this.passed / this.duration);
            }
        }
        this.passed += Game.elapsed;
        if (f > this.duration) {
            switch (this.phase.ordinal()) {
                case 0: {
                    this.phase = Phase.TRANSMUTING;
                    this.duration = 1.4f;
                    break;
                }
                case 1: {
                    this.phase = Phase.FADE_OUT;
                    this.duration = 0.4f;
                    break;
                }
                case 2: {
                    this.kill();
                }
            }
            this.passed = 0.0f;
        }
    }

    public static void show(Char ch, Item oldItem, Item newItem) {
        if (!ch.sprite.visible) {
            return;
        }
        Transmuting sprite = new Transmuting(oldItem, newItem);
        sprite.target = ch;
        ch.sprite.parent.add(sprite);
    }

    public static void show(Char ch, Talent oldTalent, Talent newTalent) {
        if (!ch.sprite.visible) {
            return;
        }
        Transmuting sprite = new Transmuting(oldTalent, newTalent);
        sprite.target = ch;
        ch.sprite.parent.add(sprite);
    }

    private static enum Phase {
        FADE_IN,
        TRANSMUTING,
        FADE_OUT;

    }
}

