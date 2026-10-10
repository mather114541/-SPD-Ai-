/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.Tag;
import com.watabou.input.GameAction;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Visual;

public class ActionIndicator
extends Tag {
    Visual primaryVis;
    Visual secondVis;
    public static Action action;
    public static ActionIndicator instance;
    private boolean needsRefresh = false;

    public ActionIndicator() {
        super(0);
        instance = this;
        this.setSize(SIZE, SIZE);
        this.visible = false;
    }

    @Override
    public GameAction keyAction() {
        return SPDAction.TAG_ACTION;
    }

    @Override
    public void destroy() {
        super.destroy();
        instance = null;
    }

    @Override
    protected synchronized void layout() {
        super.layout();
        if (this.primaryVis != null) {
            this.primaryVis.x = !this.flipped ? this.x + ((float)SIZE - this.primaryVis.width()) / 2.0f + 1.0f : this.x + this.width - ((float)SIZE + this.primaryVis.width()) / 2.0f - 1.0f;
            this.primaryVis.y = this.y + (this.height - this.primaryVis.height()) / 2.0f;
            PixelScene.align(this.primaryVis);
            if (this.secondVis != null) {
                this.secondVis.x = this.secondVis.width() > 16.0f ? this.primaryVis.center().x - this.secondVis.width() / 2.0f : this.primaryVis.center().x + 8.0f - this.secondVis.width();
                this.secondVis.y = this.secondVis instanceof BitmapText ? this.primaryVis.center().y + 8.0f - ((BitmapText)this.secondVis).baseLine() : this.primaryVis.center().y + 8.0f - this.secondVis.height();
                PixelScene.align(this.secondVis);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void update() {
        super.update();
        Class<ActionIndicator> clazz = ActionIndicator.class;
        synchronized (ActionIndicator.class) {
            if (!this.visible && action != null) {
                this.visible = true;
                this.needsRefresh = true;
                this.flash();
            } else {
                boolean bl = this.visible = action != null;
            }
            if (this.needsRefresh) {
                if (this.primaryVis != null) {
                    this.primaryVis.destroy();
                    this.primaryVis.killAndErase();
                    this.primaryVis = null;
                }
                if (this.secondVis != null) {
                    this.secondVis.destroy();
                    this.secondVis.killAndErase();
                    this.secondVis = null;
                }
                if (action != null) {
                    this.primaryVis = action.primaryVisual();
                    this.add(this.primaryVis);
                    this.secondVis = action.secondaryVisual();
                    if (this.secondVis != null) {
                        this.add(this.secondVis);
                    }
                    this.setColor(action.indicatorColor());
                }
                this.layout();
                this.needsRefresh = false;
            }
            if (!Dungeon.hero.ready) {
                if (this.primaryVis != null) {
                    this.primaryVis.alpha(0.5f);
                }
                if (this.secondVis != null) {
                    this.secondVis.alpha(0.5f);
                }
            } else {
                if (this.primaryVis != null) {
                    this.primaryVis.alpha(1.0f);
                }
                if (this.secondVis != null) {
                    this.secondVis.alpha(1.0f);
                }
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return;
        }
    }

    @Override
    protected void onClick() {
        super.onClick();
        if (action != null && Dungeon.hero.ready) {
            action.doAction();
        }
    }

    @Override
    protected String hoverText() {
        String text;
        String string = text = action == null ? null : action.actionName();
        if (text != null) {
            return Messages.titleCase(text);
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void setAction(Action action) {
        Class<ActionIndicator> clazz = ActionIndicator.class;
        synchronized (ActionIndicator.class) {
            ActionIndicator.action = action;
            ActionIndicator.refresh();
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return;
        }
    }

    public static void clearAction() {
        ActionIndicator.clearAction(null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void clearAction(Action action) {
        Class<ActionIndicator> clazz = ActionIndicator.class;
        synchronized (ActionIndicator.class) {
            if (action == null || ActionIndicator.action == action) {
                ActionIndicator.action = null;
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void refresh() {
        Class<ActionIndicator> clazz = ActionIndicator.class;
        synchronized (ActionIndicator.class) {
            if (instance != null) {
                ActionIndicator.instance.needsRefresh = true;
            }
            // ** MonitorExit[var0] (shouldn't be in output)
            return;
        }
    }

    public static interface Action {
        public String actionName();

        default public int actionIcon() {
            return 127;
        }

        default public Visual primaryVisual() {
            return new HeroIcon(this);
        }

        default public Visual secondaryVisual() {
            return null;
        }

        public int indicatorColor();

        public void doAction();
    }
}

