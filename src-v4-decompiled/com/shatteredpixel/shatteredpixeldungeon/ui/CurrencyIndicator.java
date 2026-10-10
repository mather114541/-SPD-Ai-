/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Game;
import com.watabou.noosa.ui.Component;

public class CurrencyIndicator
extends Component {
    private static final float TIME = 2.0f;
    private int lastGold = 0;
    private int lastEnergy = 0;
    private BitmapText gold;
    private BitmapText energy;
    private float goldTime;
    private float energyTime;
    public static boolean showGold = false;

    @Override
    protected void createChildren() {
        this.gold = new BitmapText(PixelScene.pixelFont);
        this.gold.text(Integer.toString(this.lastGold));
        this.gold.measure();
        this.gold.hardlight(0xFFFF00);
        this.add(this.gold);
        this.energy = new BitmapText(PixelScene.pixelFont);
        this.energy.text(Integer.toString(this.lastEnergy));
        this.energy.measure();
        this.energy.hardlight(0x44CCFF);
        this.add(this.energy);
        this.energy.visible = false;
        this.gold.visible = false;
    }

    @Override
    protected void layout() {
        this.gold.x = this.x + 1.0f;
        this.gold.y = this.top() + 1.0f;
        this.energy.x = this.x + 1.0f;
        this.energy.y = this.gold.visible ? this.top() + this.energy.height() - 1.0f : this.top() + 1.0f;
    }

    @Override
    public void update() {
        super.update();
        if (this.gold.visible) {
            this.goldTime -= Game.elapsed;
            if (this.goldTime > 0.0f) {
                this.gold.alpha(this.goldTime > 1.0f ? 1.0f : this.goldTime * 2.0f / 2.0f);
            } else {
                this.gold.visible = false;
            }
        }
        if (this.energy.visible) {
            this.energyTime -= Game.elapsed;
            if (this.energyTime > 0.0f) {
                this.energy.alpha(this.energyTime > 1.0f ? 1.0f : this.energyTime * 2.0f / 2.0f);
            } else {
                this.energy.visible = false;
            }
        }
        if (Dungeon.gold != this.lastGold) {
            this.lastGold = Dungeon.gold;
            this.gold.text(Integer.toString(this.lastGold));
            this.gold.measure();
            this.gold.visible = true;
            this.goldTime = 2.0f;
            this.layout();
        }
        if (Dungeon.energy != this.lastEnergy) {
            this.lastEnergy = Dungeon.energy;
            this.energy.text(Integer.toString(this.lastEnergy));
            this.energy.measure();
            this.energy.visible = true;
            this.energyTime = 2.0f;
            this.layout();
        }
        if (showGold) {
            if (!this.gold.visible) {
                this.gold.visible = true;
                this.layout();
            }
            this.goldTime = 1.0f;
        }
    }
}

