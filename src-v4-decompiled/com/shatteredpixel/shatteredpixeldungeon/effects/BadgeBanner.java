/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.watabou.gltextures.SmartTexture;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Point;
import com.watabou.utils.PointF;
import java.util.ArrayList;
import java.util.HashMap;

public class BadgeBanner
extends Image {
    private State state;
    public static final float DEFAULT_SCALE = 3.0f;
    public static final int SIZE = 16;
    private static final float FADE_IN_TIME = 0.25f;
    private static final float STATIC_TIME = 1.0f;
    private static final float FADE_OUT_TIME = 1.75f;
    private int index;
    private float time;
    private static TextureFilm atlas;
    public static ArrayList<BadgeBanner> showing;
    private static HashMap<Integer, Point> highlightPositions;

    private BadgeBanner(int index) {
        super("interfaces/badges.png");
        if (atlas == null) {
            atlas = new TextureFilm(this.texture, 16, 16);
        }
        this.setup(index);
    }

    public void setup(int index) {
        this.index = index;
        this.frame(atlas.get(index));
        this.origin.set(this.width / 2.0f, this.height / 2.0f);
        this.alpha(0.0f);
        this.scale.set(6.0f);
        this.state = State.FADE_IN;
        this.time = 0.25f;
        Sample.INSTANCE.play("sounds/badge.mp3");
    }

    @Override
    public void update() {
        super.update();
        this.time -= Game.elapsed;
        if (this.time >= 0.0f) {
            switch (this.state.ordinal()) {
                case 0: {
                    float p = this.time / 0.25f;
                    this.scale.set((1.0f + p) * 3.0f);
                    this.alpha(1.0f - p);
                    break;
                }
                case 1: {
                    break;
                }
                case 2: {
                    this.alpha(this.time / 1.75f);
                }
            }
        } else {
            switch (this.state.ordinal()) {
                case 0: {
                    this.time = 1.0f;
                    this.state = State.STATIC;
                    this.scale.set(3.0f);
                    this.alpha(1.0f);
                    BadgeBanner.highlight(this, this.index);
                    break;
                }
                case 1: {
                    this.time = 1.75f;
                    this.state = State.FADE_OUT;
                    break;
                }
                case 2: {
                    this.killAndErase();
                }
            }
        }
    }

    @Override
    public void kill() {
        showing.remove(this);
        super.kill();
    }

    @Override
    public void destroy() {
        showing.remove(this);
        super.destroy();
    }

    public static void highlight(Image image, int index) {
        PointF p = new PointF();
        if (highlightPositions.containsKey(index)) {
            p.x = (float)BadgeBanner.highlightPositions.get((Object)Integer.valueOf((int)index)).x * image.scale.x;
            p.y = (float)BadgeBanner.highlightPositions.get((Object)Integer.valueOf((int)index)).y * image.scale.y;
        } else {
            SmartTexture tx = TextureCache.get("interfaces/badges.png");
            int size = 16;
            int cols = tx.width / size;
            int row = index / cols;
            int col = index % cols;
            int x = 3;
            int y = 4;
            int bgColor = tx.getPixel(col * size + x, row * size + y);
            int curColor = 0;
            for (x = 3; x <= 12 && (curColor = tx.getPixel(col * size + x, row * size + y)) == bgColor; ++x) {
            }
            if (curColor == bgColor) {
                ++y;
                for (x = 3; x <= 12 && (curColor = tx.getPixel(col * size + x, row * size + y)) == bgColor; ++x) {
                }
            }
            p.x = (float)x * image.scale.x;
            p.y = (float)y * image.scale.y;
            highlightPositions.put(index, new Point(x, y));
        }
        p.offset(-image.origin.x * (image.scale.x - 1.0f), -image.origin.y * (image.scale.y - 1.0f));
        p.offset(image.point());
        Speck star = new Speck();
        star.reset(0, p.x, p.y, 101);
        star.camera = image.camera();
        image.parent.add(star);
    }

    public static BadgeBanner show(int image) {
        BadgeBanner banner = new BadgeBanner(image);
        showing.add(banner);
        return banner;
    }

    public static boolean isShowingBadges() {
        return !showing.isEmpty();
    }

    public static Image image(int index) {
        Image image = new Image("interfaces/badges.png");
        if (atlas == null) {
            atlas = new TextureFilm(image.texture, 16, 16);
        }
        image.frame(atlas.get(index));
        return image;
    }

    static {
        showing = new ArrayList();
        highlightPositions = new HashMap();
        highlightPositions.put(Badges.Badge.MASTERY_COMBO.image, new Point(3, 7));
    }

    private static enum State {
        FADE_IN,
        STATIC,
        FADE_OUT;

    }
}

