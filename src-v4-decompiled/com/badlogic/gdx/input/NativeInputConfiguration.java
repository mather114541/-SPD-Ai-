/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.input.TextInputWrapper;

public class NativeInputConfiguration {
    private Input.OnscreenKeyboardType type = Input.OnscreenKeyboardType.Default;
    private boolean preventCorrection = false;
    private TextInputWrapper textInputWrapper;
    private boolean isMultiLine = false;
    private Integer maxLength;
    private Input.InputStringValidator validator;
    private String placeholder = "";
    private boolean showPasswordButton = false;
    private String[] autoComplete = null;

    public Input.OnscreenKeyboardType getType() {
        return this.type;
    }

    public NativeInputConfiguration setType(Input.OnscreenKeyboardType type) {
        this.type = type;
        return this;
    }

    public boolean isPreventCorrection() {
        return this.preventCorrection;
    }

    public NativeInputConfiguration setPreventCorrection(boolean preventCorrection) {
        this.preventCorrection = preventCorrection;
        return this;
    }

    public TextInputWrapper getTextInputWrapper() {
        return this.textInputWrapper;
    }

    public NativeInputConfiguration setTextInputWrapper(TextInputWrapper textInputWrapper) {
        this.textInputWrapper = textInputWrapper;
        return this;
    }

    public boolean isMultiLine() {
        return this.isMultiLine;
    }

    public NativeInputConfiguration setMultiLine(boolean multiLine) {
        this.isMultiLine = multiLine;
        return this;
    }

    public Integer getMaxLength() {
        return this.maxLength;
    }

    public NativeInputConfiguration setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
        return this;
    }

    public Input.InputStringValidator getValidator() {
        return this.validator;
    }

    public NativeInputConfiguration setValidator(Input.InputStringValidator validator) {
        this.validator = validator;
        return this;
    }

    public String getPlaceholder() {
        return this.placeholder;
    }

    public NativeInputConfiguration setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    public boolean isShowPasswordButton() {
        return this.showPasswordButton;
    }

    public NativeInputConfiguration setShowPasswordButton(boolean showPasswordButton) {
        this.showPasswordButton = showPasswordButton;
        return this;
    }

    public String[] getAutoComplete() {
        return this.autoComplete;
    }

    public NativeInputConfiguration setAutoComplete(String[] autoComplete) {
        this.autoComplete = autoComplete;
        return this;
    }

    public void validate() {
        String message = null;
        if (this.type == null) {
            message = "OnscreenKeyboardType needs to be non null";
        }
        if (this.textInputWrapper == null) {
            message = "TextInputWrapper needs to be non null";
        }
        if (this.showPasswordButton && this.type != Input.OnscreenKeyboardType.Password) {
            message = "ShowPasswordButton only works with OnscreenKeyboardType.Password";
        }
        if (this.placeholder == null) {
            message = "Placeholder needs to be non null";
        }
        if (this.autoComplete != null && this.type != Input.OnscreenKeyboardType.Default) {
            message = "AutoComplete should only be used with OnscreenKeyboardType.Default";
        }
        if (this.autoComplete != null && this.isMultiLine) {
            message = "AutoComplete shouldn't be used with multiline";
        }
        if (message != null) {
            throw new IllegalArgumentException("NativeInputConfiguration validation failed: " + message);
        }
    }
}

