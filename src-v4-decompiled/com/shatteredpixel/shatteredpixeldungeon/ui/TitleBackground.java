/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Random;
import com.watabou.utils.RectF;
import java.util.ArrayList;

public class TitleBackground
extends Component {
    public static float SCROLL_SPEED = 15.0f;
    private float density = 1.0f;
    private static final TextureFilm ARCH_FILM = new TextureFilm("splashes/title/archs.png", 333, 100);
    private static Group archLayer;
    private static ArrayList<Image> archs;
    private static TextureFilm CLUSTER_FILM;
    private static ArrayList<Image> clusters;
    private static Group clusterLayer;
    private static ArrayList<Image> clustersFar;
    private static Group clustersFarLayer;
    private static TextureFilm SMALL_FILM;
    private static ArrayList<Image> smallFars;
    private static Group smallFarLayer;
    private static TextureFilm MID_FILM;
    private static ArrayList<Image> mids1;
    private static Group mids1Layer;
    private static ArrayList<Image> mids2;
    private static Group mids2Layer;
    private static ArrayList<Image> smallCloses;
    private static Group smallCloseLayer;
    private static boolean wasLandscape;
    private static float oldBaseScale;
    private static float oldWidth;
    ArrayList<Image> toMove = new ArrayList();
    private static final float[] INIT_ARCH_CHANCES;
    private static float[] arch_chances;
    private static final float[] INIT_CLUSTER_CHANCES;
    private static float[] cluster_chances;
    private static final float[] INIT_MID_CHANCES;
    private static float[] mid_chances;
    private static ArrayList<Integer> lastMids;
    private static final float[] INIT_SMALL_CHANCES;
    private static float[] small_chances;
    private static ArrayList<Integer> lastSmalls;

    public static void reset() {
        archs = null;
        clusters = null;
        clustersFar = null;
        smallFars = null;
        mids1 = null;
        mids2 = null;
        smallCloses = null;
    }

    public TitleBackground(int width, int height) {
        this.y = 0.0f;
        this.x = 0.0f;
        this.width = width;
        this.height = height;
        this.setupObjects();
    }

    protected void setupObjects() {
        boolean landscape = this.width > this.height;
        float scale = this.height / 450.0f;
        if (archs != null && landscape != wasLandscape) {
            archs = null;
            clustersFar = null;
            clusters = null;
            smallFarLayer = null;
            mids1 = null;
            mids2 = null;
            smallCloses = null;
        }
        wasLandscape = landscape;
        archLayer = new Group();
        if (archs == null) {
            archs = new ArrayList();
        } else {
            this.convertArchLayer(archs, archLayer, scale);
        }
        this.add(archLayer);
        Image darkness = new Image(TextureCache.createGradient(0, 0x11000000, 0x22000000, 0x33000000, 0x44000000, -2013265920));
        darkness.angle = 90.0f;
        darkness.x = this.width;
        darkness.scale.x = this.height / 6.0f;
        darkness.scale.y = this.width;
        this.add(darkness);
        if (!landscape) {
            scale /= 1.5f;
            oldBaseScale /= 1.5f;
        }
        this.density = this.width / (800.0f * scale);
        this.density = (this.density + 0.5f) / 1.5f;
        clustersFarLayer = new Group();
        if (clustersFar == null) {
            clustersFar = new ArrayList();
        } else {
            this.convertFloatingLayer(clustersFar, clustersFarLayer, scale, oldWidth);
        }
        this.add(clustersFarLayer);
        clusterLayer = new Group();
        if (clusters == null) {
            clusters = new ArrayList();
        } else {
            this.convertFloatingLayer(clusters, clusterLayer, scale, oldWidth);
        }
        this.add(clusterLayer);
        smallFarLayer = new Group();
        if (smallFars == null) {
            smallFars = new ArrayList();
        } else {
            this.convertFloatingLayer(smallFars, smallFarLayer, scale, oldWidth);
        }
        this.add(smallFarLayer);
        mids1Layer = new Group();
        if (mids1 == null) {
            mids1 = new ArrayList();
        } else {
            this.convertFloatingLayer(mids1, mids1Layer, scale, oldWidth);
        }
        this.add(mids1Layer);
        mids2Layer = new Group();
        if (mids2 == null) {
            mids2 = new ArrayList();
        } else {
            this.convertFloatingLayer(mids2, mids2Layer, scale, oldWidth);
        }
        this.add(mids2Layer);
        smallCloseLayer = new Group();
        if (smallCloses == null) {
            smallCloses = new ArrayList();
        } else {
            this.convertFloatingLayer(smallCloses, smallCloseLayer, scale, oldWidth);
        }
        this.add(smallCloseLayer);
        oldWidth = this.width / scale;
        if (!landscape) {
            scale *= 1.5f;
        }
        oldBaseScale = scale;
    }

    protected Image convertImage(Image oldImg, float newBaseScale) {
        Image newImg = new Image(oldImg.texture);
        newImg.frame(oldImg.frame());
        float oldScale = oldImg.scale.x / oldBaseScale;
        newImg.scale.set(newBaseScale * oldScale);
        newImg.brightness(oldImg.rm);
        float scaleDiff = newImg.scale.y / oldImg.scale.y;
        newImg.x = oldImg.x * scaleDiff;
        newImg.y = oldImg.y * scaleDiff;
        newImg.angle = oldImg.angle;
        return newImg;
    }

    protected void convertArchLayer(ArrayList<Image> layerList, Group layerGroup, float newBaseScale) {
        ArrayList<Image> oldImages = new ArrayList<Image>(layerList);
        layerList.clear();
        for (int i = 0; i < oldImages.size(); ++i) {
            Image oldArch = oldImages.get(i);
            Image newArch = this.convertImage(oldArch, newBaseScale);
            layerList.add(newArch);
            layerGroup.add(newArch);
            while (newArch.x + newArch.width() < this.width && (i == oldImages.size() - 1 || oldImages.get((int)(i + 1)).y != oldArch.y)) {
                Image extraArch = new Image("splashes/title/archs.png");
                extraArch.frame(this.getArchFrame());
                extraArch.scale.set(newBaseScale);
                extraArch.x = newArch.x + newArch.width();
                extraArch.x -= 9.0f * newArch.scale.x;
                extraArch.y = newArch.y;
                layerList.add(extraArch);
                layerGroup.add(extraArch);
                newArch = extraArch;
            }
        }
    }

    protected void convertFloatingLayer(ArrayList<Image> layerList, Group layerGroup, float newBaseScale, float oldWidth) {
        ArrayList<Image> oldImages = new ArrayList<Image>(layerList);
        layerList.clear();
        float xShift = this.width() / newBaseScale / oldWidth;
        for (int i = 0; i < oldImages.size(); ++i) {
            Image oldImage = oldImages.get(i);
            Image newImage = this.convertImage(oldImage, newBaseScale);
            if (newImage.x > 0.0f) {
                newImage.x *= xShift;
            }
            layerList.add(newImage);
            layerGroup.add(newImage);
        }
    }

    @Override
    public synchronized void update() {
        super.update();
        float scale = this.height / 450.0f;
        float shift = Game.elapsed * SCROLL_SPEED * scale;
        if (this.width <= this.height) {
            shift /= 1.5f;
        }
        this.updateArchLayer(scale, shift);
        if (this.width <= this.height) {
            scale /= 1.5f;
        }
        this.updateClusterFarLayer(scale, shift *= 1.33f);
        this.updateClusterLayer(scale, shift *= 1.5f);
        this.updateFarSmallLayer(scale, shift *= 1.33f);
        this.updateMid1Layer(scale, shift *= 1.33f);
        this.updateMid2Layer(scale, shift *= 1.33f);
        this.updateFrontSmallLayer(scale, shift *= 1.33f);
    }

    public RectF getArchFrame() {
        int tile = Random.chances(arch_chances);
        if (tile == -1) {
            arch_chances = (float[])INIT_ARCH_CHANCES.clone();
            tile = Random.chances(arch_chances);
        }
        int n = tile;
        arch_chances[n] = arch_chances[n] - 1.0f;
        return ARCH_FILM.get(tile);
    }

    private void updateArchLayer(float scale, float shift) {
        float bottom = 0.0f;
        for (Image arch : archs) {
            arch.y -= shift;
            if (arch.y + arch.height() < 0.0f) {
                this.toMove.add(arch);
                continue;
            }
            if (!(arch.y + arch.height() > bottom)) continue;
            bottom = arch.y + arch.height();
        }
        if (!this.toMove.isEmpty()) {
            for (Image arch : this.toMove) {
                arch.frame(this.getArchFrame());
                arch.y = bottom - 5.0f * scale;
            }
            bottom += 100.0f * scale;
            this.toMove.clear();
        }
        while (bottom < this.height) {
            Image arch;
            for (float left = -5.0f + -33.334f * (float)Random.Int(1, 9) * scale; left < this.width; left += arch.width() - 9.0f * scale) {
                arch = new Image("splashes/title/archs.png");
                arch.frame(this.getArchFrame());
                arch.scale.set(scale);
                arch.x = left;
                arch.y = bottom - 5.0f * scale;
                archLayer.add(arch);
                archs.add(arch);
            }
            bottom += 100.0f * scale;
        }
    }

    public RectF getClusterFrame() {
        int tile = Random.chances(cluster_chances);
        if (tile == -1) {
            cluster_chances = (float[])INIT_CLUSTER_CHANCES.clone();
            tile = Random.chances(cluster_chances);
        }
        int n = tile;
        cluster_chances[n] = cluster_chances[n] - 1.0f;
        return CLUSTER_FILM.get(tile);
    }

    private void updateClusterFarLayer(float scale, float shift) {
        float flex;
        float bottom = 0.0f;
        float lastX = 0.0f;
        for (Image cluster : clustersFar) {
            cluster.y -= shift;
            if (cluster.y + cluster.height() < -20.0f) {
                this.toMove.add(cluster);
                continue;
            }
            if (!(cluster.y + cluster.height() > bottom)) continue;
            bottom = cluster.y + cluster.height();
            lastX = cluster.x;
        }
        if (!this.toMove.isEmpty()) {
            for (Image cluster : this.toMove) {
                cluster.frame(this.getClusterFrame());
                flex = 0.0f;
                do {
                    cluster.x = Random.Float(-cluster.width() / 3.0f, this.width - 2.0f * cluster.width() / 3.0f);
                } while (Math.abs(cluster.x - lastX) < this.density * (cluster.width() / 2.0f - (flex += 1.0f)));
                cluster.y = bottom - cluster.height() + Random.Float(cluster.height() / 2.0f, cluster.height()) / this.density;
                cluster.angle = Random.Float(-20.0f, 20.0f);
                bottom = cluster.y + cluster.height();
                lastX = cluster.x;
            }
            this.toMove.clear();
        }
        float padding = 300.0f - 150.0f / this.density;
        while (bottom < this.height + padding) {
            Image cluster;
            cluster = new Image("splashes/title/back_clusters.png");
            cluster.frame(this.getClusterFrame());
            cluster.scale.set(scale * 0.5f);
            flex = 0.0f;
            do {
                cluster.x = Random.Float(-cluster.width() / 3.0f, this.width - 2.0f * cluster.width() / 3.0f);
            } while (Math.abs(cluster.x - lastX) < this.density * (cluster.width() / 2.0f - (flex += 1.0f)));
            cluster.y = bottom - cluster.height() + Random.Float(cluster.height() / 2.0f, cluster.height()) / this.density;
            cluster.angle = Random.Float(-20.0f, 20.0f);
            cluster.brightness(0.5f);
            clustersFar.add(cluster);
            clustersFarLayer.add(cluster);
            bottom = cluster.y + cluster.height();
            lastX = cluster.x;
        }
    }

    private void updateClusterLayer(float scale, float shift) {
        float flex;
        float bottom = 0.0f;
        float lastX = 0.0f;
        for (Image cluster : clusters) {
            cluster.y -= shift;
            if (cluster.y + cluster.height() < -20.0f) {
                this.toMove.add(cluster);
                continue;
            }
            if (!(cluster.y + cluster.height() > bottom)) continue;
            bottom = cluster.y + cluster.height();
            lastX = cluster.x;
        }
        if (!this.toMove.isEmpty()) {
            for (Image cluster : this.toMove) {
                cluster.frame(this.getClusterFrame());
                flex = 0.0f;
                do {
                    cluster.x = Random.Float(-cluster.width() / 3.0f, this.width - 2.0f * cluster.width() / 3.0f);
                } while (Math.abs(cluster.x - lastX) < this.density * (cluster.width() / 2.0f - (flex += 1.0f)));
                cluster.y = bottom - cluster.height() + Random.Float(cluster.height() / 2.0f, cluster.height()) / this.density;
                cluster.angle = Random.Float(-20.0f, 20.0f);
                bottom = cluster.y + cluster.height();
                lastX = cluster.x;
            }
            this.toMove.clear();
        }
        float padding = 300.0f - 150.0f / this.density;
        while (bottom < this.height + padding) {
            Image cluster;
            cluster = new Image("splashes/title/back_clusters.png");
            cluster.frame(this.getClusterFrame());
            cluster.scale.set(scale);
            flex = 0.0f;
            do {
                cluster.x = Random.Float(-cluster.width() / 3.0f, this.width - 2.0f * cluster.width() / 3.0f);
            } while (Math.abs(cluster.x - lastX) < this.density * (cluster.width() / 2.0f - (flex += 1.0f)));
            cluster.y = bottom - cluster.height() + Random.Float(cluster.height() / 2.0f, cluster.height()) / this.density;
            cluster.angle = Random.Float(-20.0f, 20.0f);
            cluster.brightness(0.75f);
            clusters.add(cluster);
            clusterLayer.add(cluster);
            bottom = cluster.y + cluster.height();
            lastX = cluster.x;
        }
    }

    public RectF getMidFrame() {
        int tile = -1;
        do {
            if ((tile = Random.chances(mid_chances)) != -1) continue;
            mid_chances = (float[])INIT_MID_CHANCES.clone();
            tile = Random.chances(mid_chances);
        } while (lastMids.contains(tile));
        int n = tile;
        mid_chances[n] = mid_chances[n] - 1.0f;
        lastMids.add(0, tile);
        if (lastMids.size() >= 20) {
            lastMids.remove(19);
        }
        return MID_FILM.get(tile);
    }

    private void updateMid1Layer(float scale, float shift) {
        float flex;
        float bottom = 0.0f;
        float lastX = 0.0f;
        for (Image mid : mids1) {
            mid.y -= shift;
            if (mid.y + mid.height() < -20.0f) {
                this.toMove.add(mid);
                continue;
            }
            if (!(mid.y + mid.height() > bottom)) continue;
            bottom = mid.y + mid.height();
            lastX = mid.x;
        }
        if (!this.toMove.isEmpty()) {
            for (Image mid : this.toMove) {
                mid.frame(this.getMidFrame());
                mid.scale.set(scale * Random.Float(0.75f, 1.25f));
                flex = 0.0f;
                do {
                    mid.x = Random.Float(-mid.width() / 3.0f, this.width - 2.0f * mid.width() / 3.0f);
                } while (Math.abs(mid.x - lastX) < this.density * (mid.width() * 0.75f - (flex += 1.0f)));
                mid.y = bottom - mid.height() + Random.Float(mid.height() * 0.75f, mid.height()) / this.density;
                mid.angle = Random.Float(-20.0f, 20.0f);
                bottom = mid.y + mid.height();
                lastX = mid.x;
            }
            this.toMove.clear();
        }
        float padding = 300.0f - 150.0f / this.density;
        while (bottom < this.height + padding) {
            Image mid;
            mid = new Image("splashes/title/mid_mixed.png");
            mid.frame(this.getMidFrame());
            mid.scale.set(scale * Random.Float(0.75f, 1.25f));
            mid.brightness(0.9f);
            flex = 0.0f;
            do {
                mid.x = Random.Float(-mid.width() / 3.0f, this.width - 2.0f * mid.width() / 3.0f);
            } while (Math.abs(mid.x - lastX) < this.density * (mid.width() * 0.75f - (flex += 1.0f)));
            mid.y = bottom - mid.height() + Random.Float(mid.height() / 2.0f, mid.height()) / this.density;
            mid.angle = Random.Float(-20.0f, 20.0f);
            mids1.add(mid);
            mids1Layer.add(mid);
            bottom = mid.y + mid.height();
            lastX = mid.x;
        }
    }

    private void updateMid2Layer(float scale, float shift) {
        float flex;
        float bottom = 0.0f;
        float lastX = 0.0f;
        for (Image mid : mids2) {
            mid.y -= shift;
            if (mid.y + mid.height() < -20.0f) {
                this.toMove.add(mid);
                continue;
            }
            if (!(mid.y + mid.height() > bottom)) continue;
            bottom = mid.y + mid.height();
            lastX = mid.x;
        }
        if (!this.toMove.isEmpty()) {
            for (Image mid : this.toMove) {
                mid.frame(this.getMidFrame());
                mid.scale.set(scale * Random.Float(1.25f, 1.75f));
                flex = 0.0f;
                do {
                    mid.x = Random.Float(-mid.width() / 3.0f, this.width - 2.0f * mid.width() / 3.0f);
                } while (Math.abs(mid.x - lastX) < this.density * (mid.width() * 0.75f - (flex += 1.0f)));
                mid.y = bottom - mid.height() + Random.Float(mid.height() / 2.0f, mid.height()) / this.density;
                mid.angle = Random.Float(-20.0f, 20.0f);
                bottom = mid.y + mid.height();
                lastX = mid.x;
            }
            this.toMove.clear();
        }
        float padding = 300.0f - 150.0f / this.density;
        while (bottom < this.height + padding) {
            Image mid;
            mid = new Image("splashes/title/mid_mixed.png");
            mid.frame(this.getMidFrame());
            mid.scale.set(scale * Random.Float(1.25f, 1.75f));
            flex = 0.0f;
            do {
                mid.x = Random.Float(-mid.width() / 3.0f, this.width - 2.0f * mid.width() / 3.0f);
            } while (Math.abs(mid.x - lastX) < this.density * (mid.width() * 0.75f - (flex += 1.0f)));
            mid.y = bottom - mid.height() + Random.Float(mid.height() / 2.0f, mid.height()) / this.density;
            mid.angle = Random.Float(-20.0f, 20.0f);
            mids2.add(mid);
            mids2Layer.add(mid);
            bottom = mid.y + mid.height();
            lastX = mid.x;
        }
    }

    public RectF getSmallFrame() {
        int tile = -1;
        do {
            if ((tile = Random.chances(small_chances)) != -1) continue;
            small_chances = (float[])INIT_SMALL_CHANCES.clone();
            tile = Random.chances(small_chances);
        } while (lastSmalls.contains(tile));
        int n = tile;
        small_chances[n] = small_chances[n] - 1.0f;
        lastSmalls.add(0, tile);
        if (lastSmalls.size() >= 15) {
            lastSmalls.remove(14);
        }
        return SMALL_FILM.get(tile);
    }

    private void updateFarSmallLayer(float scale, float shift) {
        float flex;
        float bottom = 0.0f;
        float lastX = 0.0f;
        for (Image small : smallFars) {
            small.y -= shift;
            if (small.y + small.height() < -20.0f) {
                this.toMove.add(small);
                continue;
            }
            if (!(small.y + small.height() > bottom)) continue;
            bottom = small.y + small.height();
            lastX = small.x;
        }
        if (!this.toMove.isEmpty()) {
            for (Image small : this.toMove) {
                small.frame(this.getSmallFrame());
                small.scale.set(scale * Random.Float(0.75f, 1.25f));
                flex = 0.0f;
                do {
                    small.x = Random.Float(small.width() / 3.0f, this.width - 4.0f * small.width() / 3.0f);
                } while (Math.abs(small.x - lastX) < this.density * (small.width() - (flex += 1.0f)));
                small.y = bottom - small.height() / 2.0f + Random.Float(small.height() / 2.0f, small.height()) / this.density;
                small.angle = Random.Float(-20.0f, 20.0f);
                bottom = small.y + small.height();
                lastX = small.x;
            }
            this.toMove.clear();
        }
        float padding = 150.0f - 75.0f / this.density;
        while (bottom < this.height + padding) {
            Image small;
            small = new Image("splashes/title/front_small.png");
            small.frame(this.getSmallFrame());
            small.scale.set(scale * Random.Float(0.75f, 1.25f));
            small.brightness(0.8f);
            flex = 0.0f;
            do {
                small.x = Random.Float(small.width() / 3.0f, this.width - 4.0f * small.width() / 3.0f);
            } while (Math.abs(small.x - lastX) < this.density * (small.width() - (flex += 1.0f)));
            small.y = bottom - small.height() / 2.0f + Random.Float(small.height() / 2.0f, small.height()) / this.density;
            small.angle = Random.Float(-20.0f, 20.0f);
            smallFars.add(small);
            smallFarLayer.add(small);
            bottom = small.y + small.height();
            lastX = small.x;
        }
    }

    private void updateFrontSmallLayer(float scale, float shift) {
        float flex;
        float bottom = 0.0f;
        float lastX = 0.0f;
        for (Image small : smallCloses) {
            small.y -= shift;
            if (small.y + small.height() < -20.0f) {
                this.toMove.add(small);
                continue;
            }
            if (!(small.y + small.height() > bottom)) continue;
            bottom = small.y + small.height();
            lastX = small.x;
        }
        if (!this.toMove.isEmpty()) {
            for (Image small : this.toMove) {
                small.frame(this.getSmallFrame());
                small.scale.set(scale * Random.Float(2.0f, 2.5f));
                flex = 0.0f;
                do {
                    small.x = Random.Float(-small.width() / 3.0f, this.width - 2.0f * small.width() / 3.0f);
                } while (Math.abs(small.x - lastX) < this.density * (small.width() - (flex += 1.0f)));
                small.y = bottom - small.height() + Random.Float(small.height() / 2.0f, small.height()) / this.density;
                small.angle = Random.Float(-20.0f, 20.0f);
                bottom = small.y + small.height();
                lastX = small.x;
            }
            this.toMove.clear();
        }
        float padding = 150.0f - 75.0f / this.density;
        while (bottom < this.height + padding) {
            Image small;
            small = new Image("splashes/title/front_small.png");
            small.frame(this.getSmallFrame());
            small.scale.set(scale * Random.Float(2.0f, 2.5f));
            flex = 0.0f;
            do {
                small.x = Random.Float(-small.width() / 3.0f, this.width - 2.0f * small.width() / 3.0f);
            } while (Math.abs(small.x - lastX) < this.density * (small.width() - (flex += 1.0f)));
            small.y = bottom - small.height() + Random.Float(small.height() / 2.0f, small.height()) / this.density;
            small.angle = Random.Float(-20.0f, 20.0f);
            smallCloses.add(small);
            smallCloseLayer.add(small);
            bottom = small.y + small.height();
            lastX = small.x;
        }
    }

    static {
        CLUSTER_FILM = new TextureFilm("splashes/title/back_clusters.png", 450, 250);
        SMALL_FILM = new TextureFilm("splashes/title/front_small.png", 112, 116);
        MID_FILM = new TextureFilm("splashes/title/mid_mixed.png", 273, 242);
        oldBaseScale = 1.0f;
        oldWidth = 0.0f;
        INIT_ARCH_CHANCES = new float[]{5.0f, 5.0f, 2.0f, 2.0f, 2.0f, 2.0f};
        arch_chances = (float[])INIT_ARCH_CHANCES.clone();
        INIT_CLUSTER_CHANCES = new float[]{2.0f, 2.0f};
        cluster_chances = (float[])INIT_CLUSTER_CHANCES.clone();
        INIT_MID_CHANCES = new float[]{1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f};
        mid_chances = (float[])INIT_MID_CHANCES.clone();
        lastMids = new ArrayList();
        INIT_SMALL_CHANCES = new float[]{1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f};
        small_chances = (float[])INIT_SMALL_CHANCES.clone();
        lastSmalls = new ArrayList();
    }
}

