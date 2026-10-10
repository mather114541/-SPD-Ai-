/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic;

import com.shatteredpixel.shatteredpixeldungeon.effects.Identification;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import java.util.ArrayList;
import java.util.HashSet;

public class ScrollOfDivination
extends ExoticScroll {
    public ScrollOfDivination() {
        this.icon = ItemSpriteSheet.Icons.SCROLL_DIVINATE;
    }

    @Override
    public void doRead() {
        this.detach(ScrollOfDivination.curUser.belongings.backpack);
        ScrollOfDivination.curUser.sprite.parent.add(new Identification(ScrollOfDivination.curUser.sprite.center().offset(0.0f, -16.0f)));
        Sample.INSTANCE.play("sounds/read.mp3");
        HashSet<Class<? extends Potion>> potions = Potion.getUnknown();
        HashSet<Class<? extends Scroll>> scrolls = Scroll.getUnknown();
        HashSet<Class<? extends Ring>> rings = Ring.getUnknown();
        int total = potions.size() + scrolls.size() + rings.size();
        ArrayList<Item> IDed = new ArrayList<Item>();
        int left = 4;
        float[] baseProbs = new float[]{3.0f, 3.0f, 3.0f};
        float[] probs = (float[])baseProbs.clone();
        block5: while (left > 0 && total > 0) {
            switch (Random.chances(probs)) {
                default: {
                    probs = (float[])baseProbs.clone();
                    continue block5;
                }
                case 0: {
                    if (potions.isEmpty()) {
                        probs[0] = 0.0f;
                        continue block5;
                    }
                    probs[0] = probs[0] - 1.0f;
                    Potion p = Reflection.newInstance(Random.element(potions));
                    p.identify();
                    IDed.add(p);
                    potions.remove(p.getClass());
                    break;
                }
                case 1: {
                    if (scrolls.isEmpty()) {
                        probs[1] = 0.0f;
                        continue block5;
                    }
                    probs[1] = probs[1] - 1.0f;
                    Scroll s = Reflection.newInstance(Random.element(scrolls));
                    s.identify();
                    IDed.add(s);
                    scrolls.remove(s.getClass());
                    break;
                }
                case 2: {
                    if (rings.isEmpty()) {
                        probs[2] = 0.0f;
                        continue block5;
                    }
                    probs[2] = probs[2] - 1.0f;
                    Ring r = Reflection.newInstance(Random.element(rings));
                    r.setKnown();
                    IDed.add(r);
                    rings.remove(r.getClass());
                }
            }
            --left;
            --total;
        }
        if (left == 4) {
            GLog.n(Messages.get(this, "nothing_left", new Object[0]), new Object[0]);
        } else {
            GameScene.show(new WndDivination(IDed));
        }
        this.readAnimation();
        this.identify();
    }

    private class WndDivination
    extends Window {
        private static final int WIDTH = 120;

        WndDivination(ArrayList<Item> IDed) {
            IconTitle cur = new IconTitle(new ItemSprite(ScrollOfDivination.this), Messages.titleCase(Messages.get(ScrollOfDivination.class, "name", new Object[0])));
            cur.setRect(0.0f, 0.0f, 120.0f, 0.0f);
            this.add(cur);
            RenderedTextBlock msg = PixelScene.renderTextBlock(Messages.get(this, "desc", new Object[0]), 6);
            msg.maxWidth(120);
            msg.setPos(0.0f, cur.bottom() + 2.0f);
            this.add(msg);
            float pos = msg.bottom() + 10.0f;
            for (Item i : IDed) {
                cur = new IconTitle(i);
                cur.setRect(0.0f, pos, 120.0f, 0.0f);
                this.add(cur);
                pos = cur.bottom() + 2.0f;
            }
            this.resize(120, (int)pos);
        }
    }
}

