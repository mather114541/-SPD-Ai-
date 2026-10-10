/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Tag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndKeyBindings;
import com.watabou.input.GameAction;
import com.watabou.noosa.Game;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import java.util.ArrayList;

public class AttackIndicator
extends Tag {
    private static final float ENABLED = 1.0f;
    private static final float DISABLED = 0.3f;
    private static float delay;
    private static AttackIndicator instance;
    private CharSprite sprite = null;
    private Mob lastTarget;
    private ArrayList<Mob> candidates = new ArrayList();
    private boolean enabled = true;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public AttackIndicator() {
        super(12597304);
        AttackIndicator attackIndicator = this;
        synchronized (attackIndicator) {
            instance = this;
            this.lastTarget = null;
            this.setSize(SIZE, SIZE);
            this.visible(false);
            this.enable(false);
        }
    }

    @Override
    public GameAction keyAction() {
        return SPDAction.TAG_ATTACK;
    }

    @Override
    protected void createChildren() {
        super.createChildren();
    }

    @Override
    protected synchronized void layout() {
        super.layout();
        if (this.sprite != null) {
            this.sprite.x = !this.flipped ? this.x + ((float)SIZE - this.sprite.width()) / 2.0f + 1.0f : this.x + this.width - ((float)SIZE + this.sprite.width()) / 2.0f - 1.0f;
            this.sprite.y = this.y + (this.height - this.sprite.height()) / 2.0f;
            PixelScene.align(this.sprite);
        }
    }

    @Override
    public synchronized void update() {
        super.update();
        if (!this.bg.visible) {
            if (this.sprite != null) {
                this.sprite.visible = false;
            }
            this.enable(false);
            if (delay > 0.0f) {
                delay -= Game.elapsed;
            }
            if (delay <= 0.0f) {
                this.active = false;
            }
        } else {
            delay = 0.75f;
            this.active = true;
            if (this.bg.width > 0.0f && this.sprite != null) {
                this.sprite.visible = true;
            }
            if (Dungeon.hero.isAlive()) {
                this.enable(Dungeon.hero.ready);
            } else {
                this.visible(false);
                this.enable(false);
            }
        }
    }

    private synchronized void checkEnemies() {
        this.candidates.clear();
        int v = Dungeon.hero.visibleEnemies();
        for (int i = 0; i < v; ++i) {
            Mob mob = Dungeon.hero.visibleEnemy(i);
            if (!Dungeon.hero.canAttack(mob)) continue;
            this.candidates.add(mob);
        }
        if (this.lastTarget == null || !this.candidates.contains(this.lastTarget)) {
            if (this.candidates.isEmpty()) {
                this.lastTarget = null;
            } else {
                this.active = true;
                this.lastTarget = Random.element(this.candidates);
                this.updateImage();
                this.flash();
            }
        } else {
            this.active = true;
            if (!this.bg.visible) {
                this.flash();
            }
        }
        this.visible(this.lastTarget != null);
        this.enable(this.bg.visible);
    }

    private synchronized void updateImage() {
        if (this.sprite != null) {
            this.sprite.killAndErase();
            this.sprite = null;
        }
        this.sprite = Reflection.newInstance(this.lastTarget.spriteClass);
        this.active = true;
        this.sprite.linkVisuals(this.lastTarget);
        this.sprite.idle();
        this.sprite.paused = true;
        this.sprite.visible = this.bg.visible;
        if (this.sprite.width() > 20.0f || this.sprite.height() > 20.0f) {
            this.sprite.scale.set(this.sprite.scale.x * PixelScene.align(20.0f / Math.max(this.sprite.width(), this.sprite.height())));
        }
        this.add(this.sprite);
        this.layout();
    }

    private synchronized void enable(boolean value) {
        this.enabled = value;
        if (this.sprite != null) {
            this.sprite.alpha(value ? 1.0f : 0.3f);
        }
    }

    private synchronized void visible(boolean value) {
        this.bg.visible = value;
    }

    @Override
    protected void onClick() {
        super.onClick();
        if (this.enabled && Dungeon.hero.ready && Dungeon.hero.handle(this.lastTarget.pos)) {
            Dungeon.hero.next();
        }
    }

    @Override
    protected String hoverText() {
        return Messages.titleCase(Messages.get(WndKeyBindings.class, "tag_attack", new Object[0]));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void target(Char target) {
        if (target == null) {
            return;
        }
        AttackIndicator attackIndicator = instance;
        synchronized (attackIndicator) {
            AttackIndicator.instance.lastTarget = (Mob)target;
            instance.updateImage();
            QuickSlotButton.target(target);
        }
    }

    public static void updateState() {
        instance.checkEnemies();
    }
}

