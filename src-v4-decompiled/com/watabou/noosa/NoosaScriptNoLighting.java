/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.noosa;

import com.watabou.glscripts.Script;
import com.watabou.noosa.NoosaScript;

public class NoosaScriptNoLighting
extends NoosaScript {
    private static final String SHADER = "uniform mat4 uCamera;\nuniform mat4 uModel;\nattribute vec4 aXYZW;\nattribute vec2 aUV;\nvarying vec2 vUV;\nvoid main() {\n  gl_Position = uCamera * uModel * aXYZW;\n  vUV = aUV;\n}\n//\n#ifdef GL_ES\n  precision mediump float;\n#endif\nvarying vec2 vUV;\nuniform sampler2D uTex;\nvoid main() {\n  gl_FragColor = texture2D( uTex, vUV );\n}\n";

    @Override
    public void lighting(float rm, float gm, float bm, float am, float ra, float ga, float ba, float aa) {
    }

    public static NoosaScriptNoLighting get() {
        return Script.use(NoosaScriptNoLighting.class);
    }

    @Override
    protected String shader() {
        return SHADER;
    }
}

