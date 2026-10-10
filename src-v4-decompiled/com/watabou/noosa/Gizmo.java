/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.noosa;

import com.watabou.noosa.Camera;
import com.watabou.noosa.Group;

public class Gizmo {
    public boolean exists = true;
    public boolean alive = true;
    public boolean active = true;
    public boolean visible = true;
    public Group parent;
    public Camera camera;

    public void destroy() {
        this.parent = null;
    }

    public void update() {
    }

    public void draw() {
    }

    public void kill() {
        this.alive = false;
        this.exists = false;
    }

    public void revive() {
        this.alive = true;
        this.exists = true;
    }

    public Camera camera() {
        if (this.camera != null) {
            return this.camera;
        }
        if (this.parent != null) {
            this.camera = this.parent.camera();
            return this.camera;
        }
        return null;
    }

    public boolean isVisible() {
        if (this.parent == null) {
            return this.visible;
        }
        return this.visible && this.parent.isVisible();
    }

    public boolean isActive() {
        if (this.parent == null) {
            return this.active;
        }
        return this.active && this.parent.isActive();
    }

    public void killAndErase() {
        this.kill();
        if (this.parent != null) {
            this.parent.erase(this);
        }
    }

    public void remove() {
        if (this.parent != null) {
            this.parent.remove(this);
        }
    }
}

