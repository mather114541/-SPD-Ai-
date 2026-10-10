/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.input;

public interface TextInputWrapper {
    public String getText();

    public int getSelectionStart();

    public int getSelectionEnd();

    public void setText(String var1);

    public void setPosition(int var1);

    public boolean shouldClose();
}

