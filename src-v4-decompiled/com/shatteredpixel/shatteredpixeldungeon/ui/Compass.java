/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Image;
import com.watabou.utils.PointF;

public class Compass
extends Image {
    private static final float RAD_2_G = 57.295784f;
    private static final float RADIUS = 12.0f;
    private int cell;
    private PointF cellCenter;
    private PointF lastScroll = new PointF();

    public Compass(int cell) {
        this.copy(Icons.COMPASS.get());
        this.origin.set(this.width / 2.0f, 12.0f);
        this.cell = cell;
        this.cellCenter = DungeonTilemap.tileCenterToWorld(cell);
        this.visible = false;
    }

    @Override
    public void update() {
        PointF scroll;
        super.update();
        if (this.cell < 0 || this.cell >= Dungeon.level.length()) {
            this.visible = false;
            return;
        }
        boolean bl = this.visible = Dungeon.level.visited[this.cell] || Dungeon.level.mapped[this.cell];
        if (this.visible && !(scroll = Camera.main.scroll).equals(this.lastScroll)) {
            this.lastScroll.set(scroll);
            PointF center = Camera.main.center().offset(scroll);
            this.angle = (float)Math.atan2(this.cellCenter.x - center.x, center.y - this.cellCenter.y) * 57.295784f;
        }
    }
}

