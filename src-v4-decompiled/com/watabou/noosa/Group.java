/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.noosa;

import com.watabou.noosa.Gizmo;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Group
extends Gizmo {
    protected ArrayList<Gizmo> members = new ArrayList();
    public int length = 0;

    @Override
    public synchronized void destroy() {
        super.destroy();
        for (int i = 0; i < this.length; ++i) {
            Gizmo g = this.members.get(i);
            if (g == null) continue;
            g.destroy();
        }
        if (this.members != null) {
            this.members.clear();
            this.members = null;
        }
        this.length = 0;
    }

    @Override
    public synchronized void update() {
        for (int i = 0; i < this.length; ++i) {
            Gizmo g = this.members.get(i);
            if (g == null || !g.exists || !g.active) continue;
            g.update();
        }
    }

    @Override
    public synchronized void draw() {
        for (int i = 0; i < this.length; ++i) {
            Gizmo g = this.members.get(i);
            if (g == null || !g.exists || !g.isVisible()) continue;
            g.draw();
        }
    }

    @Override
    public synchronized void kill() {
        for (int i = 0; i < this.length; ++i) {
            Gizmo g = this.members.get(i);
            if (g == null || !g.exists) continue;
            g.kill();
        }
        super.kill();
    }

    public synchronized int indexOf(Gizmo g) {
        return this.members.indexOf(g);
    }

    public synchronized Gizmo add(Gizmo g) {
        if (g.parent == this) {
            return g;
        }
        if (g.parent != null) {
            g.parent.remove(g);
        }
        for (int i = 0; i < this.length; ++i) {
            if (this.members.get(i) != null) continue;
            this.members.set(i, g);
            g.parent = this;
            return g;
        }
        this.members.add(g);
        g.parent = this;
        ++this.length;
        return g;
    }

    public synchronized Gizmo addToFront(Gizmo g) {
        if (g.parent == this) {
            return g;
        }
        if (g.parent != null) {
            g.parent.remove(g);
        }
        for (int i = this.length - 1; i >= 0 && this.members.get(i) == null; --i) {
            if (i != 0 && this.members.get(i - 1) == null) continue;
            this.members.set(i, g);
            g.parent = this;
            return g;
        }
        this.members.add(g);
        g.parent = this;
        ++this.length;
        return g;
    }

    public synchronized Gizmo addToBack(Gizmo g) {
        if (g.parent == this) {
            this.sendToBack(g);
            return g;
        }
        if (g.parent != null) {
            g.parent.remove(g);
        }
        if (!this.members.isEmpty() && this.members.get(0) == null) {
            this.members.set(0, g);
            g.parent = this;
            return g;
        }
        this.members.add(0, g);
        g.parent = this;
        ++this.length;
        return g;
    }

    public synchronized Gizmo recycle(Class<? extends Gizmo> c) {
        Gizmo g = this.getFirstAvailable(c);
        if (g != null) {
            return g;
        }
        if (c == null) {
            return null;
        }
        g = Reflection.newInstance(c);
        if (g != null) {
            return this.add(g);
        }
        return null;
    }

    public synchronized Gizmo erase(Gizmo g) {
        int index = this.members.indexOf(g);
        if (index != -1) {
            this.members.set(index, null);
            g.parent = null;
            return g;
        }
        return null;
    }

    public synchronized Gizmo remove(Gizmo g) {
        if (this.members.remove(g)) {
            --this.length;
            g.parent = null;
            return g;
        }
        return null;
    }

    public synchronized Gizmo replace(Gizmo oldOne, Gizmo newOne) {
        int index = this.members.indexOf(oldOne);
        if (index != -1) {
            this.members.set(index, newOne);
            newOne.parent = this;
            oldOne.parent = null;
            return newOne;
        }
        return null;
    }

    public synchronized Gizmo getFirstAvailable(Class<? extends Gizmo> c) {
        for (int i = 0; i < this.length; ++i) {
            Gizmo g = this.members.get(i);
            if (g == null || g.exists || c != null && g.getClass() != c) continue;
            return g;
        }
        return null;
    }

    public synchronized int countLiving() {
        int count = 0;
        for (int i = 0; i < this.length; ++i) {
            Gizmo g = this.members.get(i);
            if (g == null || !g.exists || !g.alive) continue;
            ++count;
        }
        return count;
    }

    public synchronized int countDead() {
        int count = 0;
        for (int i = 0; i < this.length; ++i) {
            Gizmo g = this.members.get(i);
            if (g == null || g.alive) continue;
            ++count;
        }
        return count;
    }

    public synchronized Gizmo random() {
        if (this.length > 0) {
            return this.members.get(Random.Int(this.length));
        }
        return null;
    }

    public synchronized void clear() {
        if (this.length == 0) {
            return;
        }
        for (int i = 0; i < this.length; ++i) {
            Gizmo g = this.members.get(i);
            if (g == null) continue;
            g.parent = null;
        }
        this.members.clear();
        this.length = 0;
    }

    public synchronized Gizmo bringToFront(Gizmo g) {
        if (this.members.contains(g)) {
            this.members.remove(g);
            this.members.add(g);
            return g;
        }
        return null;
    }

    public synchronized Gizmo sendToBack(Gizmo g) {
        if (this.members.contains(g)) {
            this.members.remove(g);
            this.members.add(0, g);
            return g;
        }
        return null;
    }

    public synchronized void sort(Comparator c) {
        for (int i = 0; i < this.length - 1; ++i) {
            if (c.compare(this.members.get(i), this.members.get(i + 1)) <= 0) continue;
            Collections.sort(this.members, c);
            return;
        }
    }
}

