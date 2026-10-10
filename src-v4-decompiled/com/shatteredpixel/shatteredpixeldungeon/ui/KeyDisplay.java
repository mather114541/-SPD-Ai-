/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.CrystalKey;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.GoldenKey;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.Key;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.WornKey;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.watabou.gltextures.SmartTexture;
import com.watabou.gltextures.TextureCache;
import com.watabou.glwrap.Quad;
import com.watabou.glwrap.Vertexbuffer;
import com.watabou.noosa.NoosaScript;
import com.watabou.noosa.Visual;
import com.watabou.utils.RectF;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.LinkedHashMap;

public class KeyDisplay
extends Visual {
    private float[] vertices = new float[16];
    private FloatBuffer quads;
    private Vertexbuffer buffer;
    private SmartTexture tx = TextureCache.get("interfaces/menu_button.png");
    private boolean dirty = true;
    private int[] keys;
    private static final LinkedHashMap<Class<? extends Key>, Integer> keyMap = new LinkedHashMap();
    private int totalKeys = 0;

    public KeyDisplay() {
        super(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public void updateKeys() {
        this.keys = new int[keyMap.size() + 1];
        for (Notes.KeyRecord rec : Notes.getRecords(Notes.KeyRecord.class)) {
            if (rec.depth() < Dungeon.depth) {
                this.keys[0] = 1;
                continue;
            }
            if (rec.depth() != Dungeon.depth || Dungeon.branch != 0) continue;
            int n = keyMap.get(rec.type());
            this.keys[n] = this.keys[n] + rec.quantity();
        }
        this.totalKeys = 0;
        for (Object k : (Object)this.keys) {
            this.totalKeys += k;
        }
        this.dirty = true;
    }

    public int keyCount() {
        return this.totalKeys;
    }

    @Override
    public void draw() {
        super.draw();
        if (this.dirty) {
            this.updateVertices();
            ((Buffer)this.quads).limit(this.quads.position());
            if (this.buffer == null) {
                this.buffer = new Vertexbuffer(this.quads);
            } else {
                this.buffer.updateVertices(this.quads);
            }
        }
        NoosaScript script = NoosaScript.get();
        this.tx.bind();
        script.camera(this.camera());
        script.uModel.valueM4(this.matrix);
        script.lighting(this.rm, this.gm, this.bm, this.am, this.ra, this.ga, this.ba, this.aa);
        script.drawQuadSet(this.buffer, this.totalKeys, 0);
    }

    private void updateVertices() {
        int maxRows = (int)(this.height + 1.0f) / 5;
        int maxPerRow = (int)(this.width + 1.0f) / 4;
        int maxKeys = maxPerRow * maxRows;
        while (this.totalKeys > maxKeys) {
            Class<? extends Key> mostType = null;
            int mostNum = 0;
            for (Class<? extends Key> k : keyMap.keySet()) {
                if (this.keys[keyMap.get(k)] < mostNum) continue;
                mostType = k;
                mostNum = this.keys[keyMap.get(k)];
            }
            int n = keyMap.get(mostType);
            this.keys[n] = this.keys[n] - 1;
            --this.totalKeys;
        }
        int rows = (int)Math.ceil((float)this.totalKeys / (float)maxPerRow);
        boolean shortKeys = (float)(rows * 8) > this.height;
        float left = this.totalKeys > maxPerRow ? 0.0f : (this.width + 1.0f - (float)(this.totalKeys * 4)) / 2.0f;
        float top = (this.height + 1.0f - (float)(rows * (shortKeys ? 5 : 8))) / 2.0f;
        this.quads = Quad.createSet(this.totalKeys);
        for (int i = 0; i < this.totalKeys; ++i) {
            int keyIdx = 0;
            if (i == 0 && this.keys[0] > 0) {
                keyIdx = 0;
            } else {
                for (int j = 1; j < this.keys.length; ++j) {
                    if (this.keys[j] <= 0) continue;
                    int n = j;
                    this.keys[n] = this.keys[n] - 1;
                    keyIdx = j;
                    break;
                }
            }
            RectF r = this.tx.uvRect(43 + 3 * keyIdx, shortKeys ? 8.0f : 0.0f, 46 + 3 * keyIdx, shortKeys ? 12.0f : 7.0f);
            this.vertices[2] = r.left;
            this.vertices[3] = r.top;
            this.vertices[6] = r.right;
            this.vertices[7] = r.top;
            this.vertices[10] = r.right;
            this.vertices[11] = r.bottom;
            this.vertices[14] = r.left;
            this.vertices[15] = r.bottom;
            this.vertices[0] = left;
            this.vertices[1] = top;
            this.vertices[4] = left + 3.0f;
            this.vertices[5] = top;
            this.vertices[8] = left + 3.0f;
            this.vertices[9] = top + (float)(shortKeys ? 4 : 7);
            this.vertices[12] = left;
            this.vertices[13] = top + (float)(shortKeys ? 4 : 7);
            this.quads.put(this.vertices);
            left += 4.0f;
            if (!(left + 3.0f > this.width)) continue;
            left = 0.0f;
            top += (float)(shortKeys ? 5 : 8);
        }
        this.dirty = false;
    }

    @Override
    public void destroy() {
        super.destroy();
        if (this.buffer != null) {
            this.buffer.delete();
        }
    }

    static {
        keyMap.put(WornKey.class, 1);
        keyMap.put(CrystalKey.class, 2);
        keyMap.put(GoldenKey.class, 3);
        keyMap.put(IronKey.class, 4);
    }
}

