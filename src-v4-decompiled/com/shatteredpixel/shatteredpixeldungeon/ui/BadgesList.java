/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.effects.BadgeBanner;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBadge;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Component;
import java.util.ArrayList;

public class BadgesList
extends ScrollPane {
    private ArrayList<ListItem> items = new ArrayList();

    public BadgesList(boolean global) {
        super(new Component());
        for (Badges.Badge badge : Badges.filterReplacedBadges(global)) {
            if (badge.type == Badges.BadgeType.HIDDEN) continue;
            ListItem item = new ListItem(badge);
            this.content.add(item);
            this.items.add(item);
        }
    }

    @Override
    protected void layout() {
        float pos = 0.0f;
        int size = this.items.size();
        for (int i = 0; i < size; ++i) {
            this.items.get(i).setRect(0.0f, pos, this.width, 18.0f);
            pos += 18.0f;
        }
        this.content.setSize(this.width, pos);
        super.layout();
    }

    @Override
    public void onClick(float x, float y) {
        int size = this.items.size();
        for (int i = 0; i < size && !this.items.get(i).onClick(x, y); ++i) {
        }
    }

    private class ListItem
    extends Component {
        private static final float HEIGHT = 18.0f;
        private Badges.Badge badge;
        private Image icon;
        private RenderedTextBlock label;

        public ListItem(Badges.Badge badge) {
            this.badge = badge;
            this.icon.copy(BadgeBanner.image(badge.image));
            this.label.text(badge.title());
        }

        @Override
        protected void createChildren() {
            this.icon = new Image();
            this.add(this.icon);
            this.label = PixelScene.renderTextBlock(6);
            this.add(this.label);
        }

        @Override
        protected void layout() {
            this.icon.x = this.x;
            this.icon.y = this.y + (this.height - this.icon.height) / 2.0f;
            PixelScene.align(this.icon);
            this.label.setPos(this.icon.x + this.icon.width + 2.0f, this.y + (this.height - this.label.height()) / 2.0f);
            PixelScene.align(this.label);
        }

        public boolean onClick(float x, float y) {
            if (this.inside(x, y)) {
                Sample.INSTANCE.play("sounds/click.mp3", 0.7f, 0.7f, 1.2f);
                Game.scene().addToFront(new WndBadge(this.badge, true));
                return true;
            }
            return false;
        }
    }
}

