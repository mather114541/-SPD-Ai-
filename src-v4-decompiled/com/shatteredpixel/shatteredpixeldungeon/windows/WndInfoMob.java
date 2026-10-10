/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HealthBar;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.ui.Component;

public class WndInfoMob
extends WndTitledMessage {
    public WndInfoMob(Mob mob) {
        super(new MobTitle(mob), mob.info());
    }

    private static class MobTitle
    extends Component {
        private static final int GAP = 2;
        private CharSprite image;
        private RenderedTextBlock name;
        private HealthBar health;
        private BuffIndicator buffs;

        public MobTitle(Mob mob) {
            this.name = PixelScene.renderTextBlock(Messages.titleCase(mob.name()), 9);
            this.name.hardlight(0xFFFF44);
            this.add(this.name);
            this.image = mob.sprite();
            this.add(this.image);
            this.health = new HealthBar();
            this.health.level(mob);
            if (!Char.hasProp(mob, Char.Property.OBJECT)) {
                this.add(this.health);
            }
            this.buffs = new BuffIndicator(mob, false);
            if (!Char.hasProp(mob, Char.Property.OBJECT)) {
                this.add(this.buffs);
            }
        }

        @Override
        protected void layout() {
            this.image.x = 0.0f;
            this.image.y = Math.max(0.0f, this.name.height() + this.health.height() - this.image.height());
            float w = this.width - this.image.width() - 2.0f;
            this.name.setPos(this.x + this.image.width() + 2.0f, this.image.height() > this.name.height() ? this.y + (this.image.height() - this.name.height()) / 2.0f : this.y);
            this.health.setRect(this.image.width() + 2.0f, this.name.bottom() + 2.0f, w, this.health.height());
            this.buffs.maxBuffs = 50;
            this.buffs.setRect(this.name.right(), this.name.bottom() - 7.0f - 2.0f, w - this.name.width(), 8.0f);
            if (!this.buffs.allBuffsVisible()) {
                this.buffs.setRect(0.0f, this.health.bottom(), this.width, 8.0f);
                this.height = Math.max(this.image.y + this.image.height(), this.buffs.bottom());
            } else {
                this.height = Math.max(this.image.y + this.image.height(), this.health.bottom());
            }
        }
    }
}

