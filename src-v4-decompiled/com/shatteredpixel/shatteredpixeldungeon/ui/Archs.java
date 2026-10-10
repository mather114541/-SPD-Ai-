/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.watabou.gltextures.TextureCache;
import com.watabou.glwrap.Blending;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.NoosaScript;
import com.watabou.noosa.NoosaScriptNoLighting;
import com.watabou.noosa.SkinnedBlock;
import com.watabou.noosa.ui.Component;

public class Archs
extends Component {
    private static final float SCROLL_SPEED = 20.0f;
    private SkinnedBlock arcsBg;
    private SkinnedBlock arcsFg;
    private Image darkness;
    private static float offsB = 0.0f;
    private static float offsF = 0.0f;
    public boolean reversed = false;

    @Override
    protected void createChildren() {
        this.arcsBg = new SkinnedBlock(1.0f, 1.0f, "interfaces/arcs1.png"){

            @Override
            protected NoosaScript script() {
                return NoosaScriptNoLighting.get();
            }

            @Override
            public void draw() {
                Blending.disable();
                super.draw();
                Blending.enable();
            }
        };
        this.arcsBg.autoAdjust = true;
        this.arcsBg.offsetTo(0.0f, offsB);
        this.add(this.arcsBg);
        this.arcsFg = new SkinnedBlock(1.0f, 1.0f, "interfaces/arcs2.png"){

            @Override
            protected NoosaScript script() {
                return NoosaScriptNoLighting.get();
            }
        };
        this.arcsFg.autoAdjust = true;
        this.arcsFg.offsetTo(0.0f, offsF);
        this.add(this.arcsFg);
        this.darkness = new Image(TextureCache.createGradient(0, 0x22000000, 0x55000000, -1728053248, -301989888));
        this.darkness.angle = 90.0f;
        this.add(this.darkness);
    }

    @Override
    protected void layout() {
        this.arcsBg.size(this.width, this.height);
        this.arcsBg.offset((float)(this.arcsBg.texture.width / 4) - this.width % (float)this.arcsBg.texture.width / 2.0f, 0.0f);
        this.arcsFg.size(this.width, this.height);
        this.arcsFg.offset((float)(this.arcsFg.texture.width / 4) - this.width % (float)this.arcsFg.texture.width / 2.0f, 0.0f);
        this.darkness.x = this.width;
        this.darkness.scale.x = this.height / 5.0f;
        this.darkness.scale.y = this.width;
    }

    @Override
    public void update() {
        super.update();
        float shift = Game.elapsed * 20.0f;
        if (this.reversed) {
            shift = -shift;
        }
        this.arcsBg.offset(0.0f, shift);
        this.arcsFg.offset(0.0f, shift * 2.0f);
        offsB = this.arcsBg.offsetY();
        offsF = this.arcsFg.offsetY();
    }
}

