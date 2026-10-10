/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.utils;

import java.util.LinkedList;

public class Signal<T> {
    private LinkedList<Listener<T>> listeners = new LinkedList();
    private boolean stackMode;

    public Signal() {
        this(false);
    }

    public Signal(boolean stackMode) {
        this.stackMode = stackMode;
    }

    public synchronized void add(Listener<T> listener) {
        if (!this.listeners.contains(listener)) {
            if (this.stackMode) {
                this.listeners.addFirst(listener);
            } else {
                this.listeners.addLast(listener);
            }
        }
    }

    public synchronized void remove(Listener<T> listener) {
        this.listeners.remove(listener);
    }

    public synchronized void removeAll() {
        this.listeners.clear();
    }

    public synchronized void replace(Listener<T> listener) {
        this.removeAll();
        this.add(listener);
    }

    public synchronized int numListeners() {
        return this.listeners.size();
    }

    public synchronized void dispatch(T t) {
        Listener[] list;
        for (Listener listener : list = this.listeners.toArray(new Listener[0])) {
            if (!this.listeners.contains(listener) || !listener.onSignal(t)) continue;
            return;
        }
    }

    public static interface Listener<T> {
        public boolean onSignal(T var1);
    }
}

