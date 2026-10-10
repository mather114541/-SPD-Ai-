/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.glscripts;

import com.watabou.glwrap.Program;
import com.watabou.glwrap.Shader;
import com.watabou.utils.Reflection;
import java.util.HashMap;

public class Script
extends Program {
    private static final HashMap<Class<? extends Script>, Script> all = new HashMap();
    private static Script curScript = null;
    private static Class<? extends Script> curScriptClass = null;

    public static synchronized <T extends Script> T use(Class<T> c) {
        if (c != curScriptClass) {
            Script script = all.get(c);
            if (script == null) {
                script = (Script)Reflection.newInstance(c);
                all.put(c, script);
            }
            curScript = script;
            curScriptClass = c;
            curScript.use();
        }
        return (T)curScript;
    }

    public static synchronized void unuse() {
        curScript = null;
        curScriptClass = null;
    }

    public static synchronized void reset() {
        for (Script script : all.values()) {
            script.delete();
        }
        all.clear();
        curScript = null;
        curScriptClass = null;
    }

    public void compile(String src) {
        String[] srcShaders = src.split("//\n");
        this.attach(Shader.createCompiled(Shader.VERTEX, srcShaders[0]));
        this.attach(Shader.createCompiled(Shader.FRAGMENT, srcShaders[1]));
        this.link();
    }
}

