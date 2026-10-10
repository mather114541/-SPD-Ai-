/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.effects.BadgeBanner;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBadge;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class BadgesGrid
extends Component {
    ArrayList<BadgeButton> badgeButtons = new ArrayList();

    public BadgesGrid(boolean global) {
        for (Badges.Badge badge : Badges.filterReplacedBadges(global)) {
            if (badge.type == Badges.BadgeType.HIDDEN) continue;
            BadgeButton button = new BadgeButton(badge, true);
            this.add(button);
            this.badgeButtons.add(button);
        }
        if (global) {
            ArrayList<Badges.Badge> lockedBadges = new ArrayList<Badges.Badge>();
            for (Badges.Badge badge : Badges.Badge.values()) {
                if (badge.type == Badges.BadgeType.HIDDEN || Badges.isUnlocked(badge)) continue;
                lockedBadges.add(badge);
            }
            Badges.filterBadgesWithoutPrerequisites(lockedBadges);
            for (Badges.Badge badge : lockedBadges) {
                BadgeButton button = new BadgeButton(badge, false);
                this.add(button);
                this.badgeButtons.add(button);
            }
        }
    }

    @Override
    protected void layout() {
        super.layout();
        float badgeArea = (float)Math.sqrt(this.width * this.height / (float)this.badgeButtons.size());
        int nCols = Math.round(this.width / badgeArea);
        int nRows = (int)Math.ceil((float)this.badgeButtons.size() / (float)nCols);
        float badgeWidth = this.width() / (float)nCols;
        float badgeHeight = this.height() / (float)nRows;
        for (int i = 0; i < this.badgeButtons.size(); ++i) {
            int row = i / nCols;
            int col = i % nCols;
            BadgeButton button = this.badgeButtons.get(i);
            button.setPos(this.left() + (float)col * badgeWidth + (badgeWidth - button.width()) / 2.0f, this.top() + (float)row * badgeHeight + (badgeHeight - button.height()) / 2.0f);
            PixelScene.align(button);
        }
    }

    private static class BadgeButton
    extends Button {
        private Badges.Badge badge;
        private boolean unlocked;
        private Image icon;

        public BadgeButton(Badges.Badge badge, boolean unlocked) {
            this.badge = badge;
            this.unlocked = unlocked;
            this.icon = BadgeBanner.image(badge.image);
            if (!unlocked) {
                this.icon.brightness(0.4f);
            }
            this.add(this.icon);
            this.setSize(this.icon.width(), this.icon.height());
        }

        @Override
        protected void layout() {
            super.layout();
            this.icon.x = this.x + (this.width - this.icon.width()) / 2.0f;
            this.icon.y = this.y + (this.height - this.icon.height()) / 2.0f;
        }

        @Override
        public void update() {
            super.update();
            if (this.unlocked && (double)Random.Float() < (double)Game.elapsed * 0.1) {
                BadgeBanner.highlight(this.icon, this.badge.image);
            }
        }

        @Override
        protected void onClick() {
            Sample.INSTANCE.play("sounds/click.mp3", 0.7f, 0.7f, 1.2f);
            Game.scene().addToFront(new WndBadge(this.badge, this.unlocked));
        }

        @Override
        protected String hoverText() {
            return this.badge.title();
        }
    }
}

