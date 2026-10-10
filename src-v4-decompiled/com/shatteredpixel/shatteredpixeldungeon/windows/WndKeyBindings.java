/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.watabou.input.ControllerHandler;
import com.watabou.input.GameAction;
import com.watabou.input.KeyBindings;
import com.watabou.input.KeyEvent;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.PointF;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class WndKeyBindings
extends Window {
    private static final int WIDTH = 135;
    private static final int BTN_HEIGHT = 16;
    private static final int COL1_CENTER = 27;
    private static final int COL2_CENTER = 67;
    private static final int COL3_CENTER = 94;
    private static final int COL4_CENTER = 121;
    private Component bindingsList;
    private ArrayList<BindingItem> listItems = new ArrayList();
    private LinkedHashMap<Integer, GameAction> changedBindings;
    private static boolean controller = false;

    public WndKeyBindings(final Boolean controller) {
        WndKeyBindings.controller = controller;
        this.changedBindings = controller != false ? KeyBindings.getAllControllerBindings() : KeyBindings.getAllBindings();
        RenderedTextBlock ttlAction = PixelScene.renderTextBlock(Messages.get(this, "ttl_action", new Object[0]), 9);
        ttlAction.setPos(27.0f - ttlAction.width() / 2.0f, (16.0f - ttlAction.height()) / 2.0f);
        this.add(ttlAction);
        ColorBlock ttlSep1 = new ColorBlock(1.0f, 16.0f, -14540254);
        ttlSep1.x = 53.0f;
        this.add(ttlSep1);
        RenderedTextBlock ttlKey1 = PixelScene.renderTextBlock(Messages.get(this, "ttl_key1", new Object[0]), 6);
        ttlKey1.maxWidth(27);
        ttlKey1.align(2);
        ttlKey1.setPos(67.0f - ttlKey1.width() / 2.0f, (16.0f - ttlKey1.height()) / 2.0f);
        this.add(ttlKey1);
        ColorBlock ttlSep2 = new ColorBlock(1.0f, 16.0f, -14540254);
        ttlSep2.x = 80.0f;
        this.add(ttlSep2);
        RenderedTextBlock ttlKey2 = PixelScene.renderTextBlock(Messages.get(this, "ttl_key2", new Object[0]), 6);
        ttlKey2.maxWidth(27);
        ttlKey2.align(2);
        ttlKey2.setPos(94.0f - ttlKey2.width() / 2.0f, (16.0f - ttlKey2.height()) / 2.0f);
        this.add(ttlKey2);
        ColorBlock ttlSep3 = new ColorBlock(1.0f, 16.0f, -14540254);
        ttlSep3.x = 107.0f;
        this.add(ttlSep3);
        RenderedTextBlock ttlKey3 = PixelScene.renderTextBlock(Messages.get(this, "ttl_key3", new Object[0]), 6);
        ttlKey3.maxWidth(27);
        ttlKey3.align(2);
        ttlKey3.setPos(121.0f - ttlKey2.width() / 2.0f, (16.0f - ttlKey2.height()) / 2.0f);
        this.add(ttlKey3);
        ColorBlock ttlSep4 = new ColorBlock(135.0f, 1.0f, -14540254);
        ttlSep4.y = 16.0f;
        this.add(ttlSep4);
        this.bindingsList = new Component();
        ScrollPane scrollingList = new ScrollPane(this.bindingsList){

            @Override
            public void onClick(float x, float y) {
                for (BindingItem i : WndKeyBindings.this.listItems) {
                    if (i.onClick(x, y)) break;
                }
            }
        };
        this.add(scrollingList);
        int y = 0;
        if (controller.booleanValue()) {
            RenderedTextBlock controllerInfo = PixelScene.renderTextBlock(Messages.get(this, "controller_info", new Object[0]), 6);
            controllerInfo.maxWidth(135);
            controllerInfo.setPos(0.0f, 2.0f);
            controllerInfo.hardlight(0xFFFF44);
            this.bindingsList.add(controllerInfo);
            y = (int)controllerInfo.bottom() + 3;
            ColorBlock sep = new ColorBlock(135.0f, 1.0f, -14540254);
            sep.y = y;
            this.bindingsList.add(sep);
        }
        LinkedHashMap<Integer, GameAction> defaults = controller != false ? SPDAction.getControllerDefaults() : SPDAction.getDefaults();
        ArrayList<GameAction> actionList = GameAction.allActions();
        for (GameAction action : actionList.toArray(new GameAction[0])) {
            if (action.code() < 1) {
                actionList.remove(action);
                continue;
            }
            if (!(action != GameAction.LEFT_CLICK && action != GameAction.RIGHT_CLICK && action != GameAction.MIDDLE_CLICK || controller.booleanValue())) {
                actionList.remove(action);
                continue;
            }
            if (defaults.containsValue(action)) continue;
            actionList.remove(action);
            actionList.add(action);
        }
        for (GameAction action : actionList) {
            BindingItem item = new BindingItem(action);
            item.setRect(0.0f, y, 135.0f, 13.0f);
            this.bindingsList.addToBack(item);
            this.listItems.add(item);
            y = (int)((float)y + item.height());
        }
        this.bindingsList.setSize(135.0f, y + 1);
        this.resize(135, Math.min(51 + 13 * this.listItems.size(), PixelScene.uiCamera.height - 20));
        RedButton btnDefaults = new RedButton(Messages.get(this, "default", new Object[0]), 9){

            @Override
            protected void onClick() {
                WndKeyBindings.this.changedBindings = controller != false ? SPDAction.getControllerDefaults() : SPDAction.getDefaults();
                for (BindingItem i : WndKeyBindings.this.listItems) {
                    int key1 = 0;
                    int key2 = 0;
                    int key3 = 0;
                    for (int k : WndKeyBindings.this.changedBindings.keySet()) {
                        if (WndKeyBindings.this.changedBindings.get(k) != i.gameAction) continue;
                        if (key1 == 0) {
                            key1 = k;
                            continue;
                        }
                        if (key2 == 0) {
                            key2 = k;
                            continue;
                        }
                        key3 = k;
                    }
                    i.updateBindings(key1, key2, key3);
                }
            }
        };
        btnDefaults.setRect(0.0f, this.height - 32 - 1, 135.0f, 16.0f);
        this.add(btnDefaults);
        RedButton btnConfirm = new RedButton(Messages.get(this, "confirm", new Object[0]), 9){

            @Override
            protected void onClick() {
                if (controller.booleanValue()) {
                    KeyBindings.setAllControllerBindings(WndKeyBindings.this.changedBindings);
                } else {
                    KeyBindings.setAllBindings(WndKeyBindings.this.changedBindings);
                }
                SPDAction.saveBindings();
                WndKeyBindings.this.hide();
            }
        };
        btnConfirm.setRect(0.0f, this.height - 16, 67.0f, 16.0f);
        this.add(btnConfirm);
        RedButton btnCancel = new RedButton(Messages.get(this, "cancel", new Object[0]), 9){

            @Override
            protected void onClick() {
                WndKeyBindings.this.hide();
            }
        };
        btnCancel.setRect(68.0f, this.height - 16, 66.0f, 16.0f);
        this.add(btnCancel);
        scrollingList.setRect(0.0f, 17.0f, 135.0f, btnDefaults.top() - 16.0f - 1.0f);
    }

    @Override
    public void offset(int xOffset, int yOffset) {
        super.offset(xOffset, yOffset);
        this.bindingsList.setPos(this.bindingsList.left(), this.bindingsList.top());
    }

    @Override
    public void onBackPressed() {
    }

    private class BindingItem
    extends Component {
        private static final int HEIGHT = 13;
        private static final int CHANGED = 0xFFFF44;
        private static final int DEFAULT = 0xFFFFFF;
        private static final int UNBOUND = 0x888888;
        private static final int UNBOUND_CHANGED = 0x888822;
        private GameAction gameAction;
        private int key1;
        private int key2;
        private int key3;
        private int origKey1;
        private int origKey2;
        private int origKey3;
        private RenderedTextBlock actionName;
        private RenderedTextBlock key1Name;
        private RenderedTextBlock key2Name;
        private RenderedTextBlock key3Name;
        private ColorBlock sep1;
        private ColorBlock sep2;
        private ColorBlock sep3;
        private ColorBlock sep4;

        public BindingItem(GameAction action) {
            this.gameAction = action;
            this.actionName = PixelScene.renderTextBlock(Messages.get(WndKeyBindings.class, action.name(), new Object[0]), 6);
            this.actionName.setHightlighting(false);
            this.add(this.actionName);
            ArrayList<Integer> keys = controller ? KeyBindings.getControllerKeysForAction(action) : KeyBindings.getKeyboardKeysForAction(action);
            this.key1 = keys.isEmpty() ? 0 : keys.remove(0);
            this.origKey1 = this.key1;
            this.key2 = keys.isEmpty() ? 0 : keys.remove(0);
            this.origKey2 = this.key2;
            this.key3 = keys.isEmpty() ? 0 : keys.remove(0);
            this.origKey3 = this.key3;
            this.key1Name = PixelScene.renderTextBlock(KeyBindings.getKeyName(this.key1), 6);
            if (this.key1 == 0) {
                this.key1Name.hardlight(0x888888);
            }
            this.add(this.key1Name);
            this.key2Name = PixelScene.renderTextBlock(KeyBindings.getKeyName(this.key2), 6);
            if (this.key2 == 0) {
                this.key2Name.hardlight(0x888888);
            }
            this.add(this.key2Name);
            this.key3Name = PixelScene.renderTextBlock(KeyBindings.getKeyName(this.key3), 6);
            if (this.key3 == 0) {
                this.key3Name.hardlight(0x888888);
            }
            this.add(this.key3Name);
            this.sep1 = new ColorBlock(1.0f, 1.0f, -14540254);
            this.add(this.sep1);
            this.sep2 = new ColorBlock(1.0f, 1.0f, -14540254);
            this.add(this.sep2);
            this.sep3 = new ColorBlock(1.0f, 1.0f, -14540254);
            this.add(this.sep3);
            this.sep4 = new ColorBlock(1.0f, 1.0f, -14540254);
            this.add(this.sep4);
        }

        public void updateBindings(int first, int second, int third) {
            if (second == 0 && third != 0) {
                second = third;
                third = 0;
            }
            if (first == 0 && second != 0) {
                first = second;
                second = 0;
            }
            this.key1 = first;
            this.key2 = second;
            this.key3 = third;
            this.key1Name.text(KeyBindings.getKeyName(this.key1));
            if (this.key1 != this.origKey1) {
                this.key1Name.hardlight(this.key1 == 0 ? 0x888822 : 0xFFFF44);
            } else {
                this.key1Name.hardlight(this.key1 == 0 ? 0x888888 : 0xFFFFFF);
            }
            this.key2Name.text(KeyBindings.getKeyName(this.key2));
            if (this.key2 != this.origKey2) {
                this.key2Name.hardlight(this.key2 == 0 ? 0x888822 : 0xFFFF44);
            } else {
                this.key2Name.hardlight(this.key2 == 0 ? 0x888888 : 0xFFFFFF);
            }
            this.key3Name.text(KeyBindings.getKeyName(this.key3));
            if (this.key3 != this.origKey3) {
                this.key3Name.hardlight(this.key3 == 0 ? 0x888822 : 0xFFFF44);
            } else {
                this.key3Name.hardlight(this.key3 == 0 ? 0x888888 : 0xFFFFFF);
            }
            this.layout();
        }

        @Override
        protected void layout() {
            super.layout();
            this.actionName.maxWidth((int)(2.0f * this.width / 5.0f));
            this.key1Name.maxWidth((int)(this.width / 5.0f) - 2);
            this.key2Name.maxWidth((int)(this.width / 5.0f) - 2);
            this.key3Name.maxWidth((int)(this.width / 5.0f) - 2);
            this.actionName.setPos(this.x, this.y + (this.height() - this.actionName.height()) / 2.0f);
            this.key1Name.setPos(this.x + 2.0f * this.width() / 5.0f + 1.0f, this.y + 0.5f + (this.height() - this.key1Name.height()) / 2.0f);
            this.key2Name.setPos(this.x + 3.0f * this.width() / 5.0f + 1.0f, this.y + 0.5f + (this.height() - this.key2Name.height()) / 2.0f);
            this.key3Name.setPos(this.x + 4.0f * this.width() / 5.0f + 1.0f, this.y + 0.5f + (this.height() - this.key3Name.height()) / 2.0f);
            this.sep1.size(this.width, 1.0f);
            this.sep1.x = this.x;
            this.sep1.y = this.bottom();
            this.sep2.size(1.0f, this.height);
            this.sep2.x = this.key1Name.left() - 2.0f;
            this.sep2.y = this.y;
            this.sep3.size(1.0f, this.height);
            this.sep3.x = this.key2Name.left() - 2.0f;
            this.sep3.y = this.y;
            this.sep4.size(1.0f, this.height);
            this.sep4.x = this.key3Name.left() - 2.0f;
            this.sep4.y = this.y;
        }

        private boolean onClick(float x, float y) {
            if (this.inside(x, y)) {
                if (x >= this.x + 4.0f * this.width() / 5.0f - 1.0f && this.key2 != 0) {
                    ShatteredPixelDungeon.scene().addToFront(new WndChangeBinding(this.gameAction, this, 3, this.key3, this.key1, this.key2));
                } else if (x >= this.x + 3.0f * this.width() / 5.0f - 1.0f && this.key1 != 0) {
                    ShatteredPixelDungeon.scene().addToFront(new WndChangeBinding(this.gameAction, this, 2, this.key2, this.key1, this.key3));
                } else if (x >= this.x + 2.0f * this.width() / 5.0f) {
                    ShatteredPixelDungeon.scene().addToFront(new WndChangeBinding(this.gameAction, this, 1, this.key1, this.key2, this.key3));
                }
                return true;
            }
            return false;
        }
    }

    private class WndChangeBinding
    extends Window {
        private int curKeyCode;
        private int otherBoundKey1;
        private int otherBoundKey2;
        private int changedKeyCode = -1;
        private BindingItem changedAction;
        private RenderedTextBlock changedKey;
        private RenderedTextBlock warnErr;
        private RedButton btnUnbind;
        private RedButton btnConfirm;
        private RedButton btnCancel;

        public WndChangeBinding(final GameAction action, final BindingItem listItem, final int keyAssigning, int curKeyCode, int otherBoundKey1, int otherBoundKey2) {
            this.curKeyCode = curKeyCode;
            this.otherBoundKey1 = otherBoundKey1;
            this.otherBoundKey2 = otherBoundKey2;
            String descKey = "";
            if (keyAssigning == 1) {
                descKey = "desc_first";
            } else if (keyAssigning == 2) {
                descKey = "desc_second";
            } else if (keyAssigning == 3) {
                descKey = "desc_third";
            }
            RenderedTextBlock desc = PixelScene.renderTextBlock(Messages.get(this, descKey, Messages.get(WndKeyBindings.class, action.name(), new Object[0]), KeyBindings.getKeyName(curKeyCode)), 6);
            desc.maxWidth(135);
            desc.setRect(0.0f, 0.0f, 135.0f, desc.height());
            this.add(desc);
            RenderedTextBlock curBind = PixelScene.renderTextBlock(Messages.get(this, "desc_current", KeyBindings.getKeyName(curKeyCode)), 6);
            curBind.maxWidth(135);
            curBind.setRect((135.0f - curBind.width()) / 2.0f, desc.bottom() + 6.0f, 135.0f, curBind.height());
            this.add(curBind);
            this.changedKey = PixelScene.renderTextBlock(6);
            this.changedKey.maxWidth(135);
            this.changedKey.setRect(0.0f, curBind.bottom() + 2.0f, 135.0f, this.changedKey.height());
            this.add(this.changedKey);
            this.warnErr = PixelScene.renderTextBlock(6);
            this.warnErr.maxWidth(135);
            this.warnErr.setRect(0.0f, this.changedKey.bottom() + 10.0f, 135.0f, this.warnErr.height());
            this.add(this.warnErr);
            this.btnUnbind = new RedButton(Messages.get(this, "unbind", new Object[0]), 9){

                @Override
                protected void onClick() {
                    if (action == GameAction.LEFT_CLICK && listItem.key2 == 0 && listItem.key3 == 0) {
                        ShatteredPixelDungeon.scene().addToFront(new WndMessage(Messages.get(WndChangeBinding.class, "cant_unbind", new Object[0])));
                    } else {
                        WndChangeBinding.this.onSignal(new KeyEvent(0, true));
                    }
                }
            };
            this.btnUnbind.setRect(0.0f, this.warnErr.bottom() + 6.0f, 135.0f, 16.0f);
            this.add(this.btnUnbind);
            this.btnConfirm = new RedButton(Messages.get(this, "confirm", new Object[0]), 9){

                @Override
                protected void onClick() {
                    if (WndChangeBinding.this.changedKeyCode != -1) {
                        WndKeyBindings.this.changedBindings.remove(WndChangeBinding.this.changedKeyCode);
                        WndKeyBindings.this.changedBindings.remove(listItem.key1);
                        WndKeyBindings.this.changedBindings.remove(listItem.key2);
                        WndKeyBindings.this.changedBindings.remove(listItem.key3);
                        if (keyAssigning == 1) {
                            if (WndChangeBinding.this.changedKeyCode != 0) {
                                WndKeyBindings.this.changedBindings.put(WndChangeBinding.this.changedKeyCode, action);
                            }
                            if (listItem.key2 != 0) {
                                WndKeyBindings.this.changedBindings.put(listItem.key2, action);
                            }
                            if (listItem.key3 != 0) {
                                WndKeyBindings.this.changedBindings.put(listItem.key3, action);
                            }
                            listItem.updateBindings(WndChangeBinding.this.changedKeyCode, listItem.key2, listItem.key3);
                        } else if (keyAssigning == 2) {
                            if (listItem.key1 != 0) {
                                WndKeyBindings.this.changedBindings.put(listItem.key1, action);
                            }
                            if (WndChangeBinding.this.changedKeyCode != 0) {
                                WndKeyBindings.this.changedBindings.put(WndChangeBinding.this.changedKeyCode, action);
                            }
                            if (listItem.key3 != 0) {
                                WndKeyBindings.this.changedBindings.put(listItem.key3, action);
                            }
                            listItem.updateBindings(listItem.key1, WndChangeBinding.this.changedKeyCode, listItem.key3);
                        } else {
                            if (listItem.key1 != 0) {
                                WndKeyBindings.this.changedBindings.put(listItem.key1, action);
                            }
                            if (listItem.key2 != 0) {
                                WndKeyBindings.this.changedBindings.put(listItem.key2, action);
                            }
                            if (WndChangeBinding.this.changedKeyCode != 0) {
                                WndKeyBindings.this.changedBindings.put(WndChangeBinding.this.changedKeyCode, action);
                            }
                            listItem.updateBindings(listItem.key1, listItem.key2, WndChangeBinding.this.changedKeyCode);
                        }
                        if (WndChangeBinding.this.changedAction != null) {
                            if (WndChangeBinding.this.changedAction.key1 == WndChangeBinding.this.changedKeyCode) {
                                WndChangeBinding.this.changedAction.updateBindings(0, WndChangeBinding.this.changedAction.key2, WndChangeBinding.this.changedAction.key3);
                            } else if (WndChangeBinding.this.changedAction.key2 == WndChangeBinding.this.changedKeyCode) {
                                WndChangeBinding.this.changedAction.updateBindings(WndChangeBinding.this.changedAction.key1, 0, WndChangeBinding.this.changedAction.key3);
                            } else if (WndChangeBinding.this.changedAction.key3 == WndChangeBinding.this.changedKeyCode) {
                                WndChangeBinding.this.changedAction.updateBindings(WndChangeBinding.this.changedAction.key1, WndChangeBinding.this.changedAction.key2, 0);
                            }
                        }
                    }
                    WndChangeBinding.this.hide();
                }
            };
            this.btnConfirm.setRect(0.0f, this.btnUnbind.bottom() + 1.0f, 67.0f, 16.0f);
            this.btnConfirm.enable(false);
            this.add(this.btnConfirm);
            this.btnCancel = new RedButton(Messages.get(this, "cancel", new Object[0]), 9){

                @Override
                protected void onClick() {
                    WndChangeBinding.this.hide();
                }
            };
            this.btnCancel.setRect(this.btnConfirm.right() + 1.0f, this.btnUnbind.bottom() + 1.0f, 66.0f, 16.0f);
            this.add(this.btnCancel);
            this.resize(135, (int)this.btnCancel.bottom());
            KeyBindings.bindingKey = true;
        }

        @Override
        public boolean onSignal(KeyEvent event) {
            if (KeyBindings.getActionForKey(event) == GameAction.LEFT_CLICK) {
                PointF hoverPos = this.camera().screenToCamera((int)PointerEvent.currentHoverPos().x, (int)PointerEvent.currentHoverPos().y);
                if (this.btnUnbind.inside(hoverPos.x, hoverPos.y)) {
                    return true;
                }
                if (this.btnConfirm.inside(hoverPos.x, hoverPos.y)) {
                    return true;
                }
                if (this.btnCancel.inside(hoverPos.x, hoverPos.y)) {
                    return true;
                }
            }
            if (controller && !ControllerHandler.icControllerKey(event.code)) {
                return true;
            }
            if (!controller && !KeyEvent.isKeyboardKey(event.code)) {
                return true;
            }
            if (event.pressed) {
                this.changedKey.text(Messages.get(this, "changed_bind", KeyBindings.getKeyName(event.code)));
                this.changedKey.setPos((135.0f - this.changedKey.width()) / 2.0f, this.changedKey.top());
                this.changedKeyCode = event.code;
                this.changedAction = null;
                if (event.code != 0 && (event.code == this.curKeyCode || event.code == this.otherBoundKey1 || event.code == this.otherBoundKey2)) {
                    this.warnErr.text(Messages.get(this, "error", new Object[0]));
                    this.warnErr.hardlight(0xFF0000);
                    this.btnConfirm.enable(false);
                } else if (event.code != 0 && WndKeyBindings.this.changedBindings.get(this.changedKeyCode) != null) {
                    for (BindingItem i : WndKeyBindings.this.listItems) {
                        if (i.gameAction != WndKeyBindings.this.changedBindings.get(this.changedKeyCode)) continue;
                        this.changedAction = i;
                        break;
                    }
                    this.warnErr.text(Messages.get(this, "warning", Messages.get(WndKeyBindings.class, WndKeyBindings.this.changedBindings.get(this.changedKeyCode).name(), new Object[0])));
                    this.warnErr.hardlight(0xFF8800);
                    this.btnConfirm.enable(true);
                } else {
                    this.warnErr.text(" ");
                    this.btnConfirm.enable(true);
                }
            }
            return true;
        }

        @Override
        public void destroy() {
            super.destroy();
            KeyBindings.bindingKey = false;
        }
    }
}

