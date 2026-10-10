/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndJournal;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndJournalItem;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTextInput;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndUseItem;
import com.watabou.utils.Reflection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CustomNoteButton
extends IconButton {
    private static WndNoteTypeSelect NOTE_SELECT_INSTANCE;
    private WndBag.ItemSelector itemSelector = new WndBag.ItemSelector(){

        @Override
        public String textPrompt() {
            return Messages.get(CustomNoteButton.class, "new_inv_prompt", new Object[0]);
        }

        @Override
        public boolean hideAfterSelecting() {
            return false;
        }

        @Override
        public boolean itemSelectable(Item item) {
            if (item instanceof EquipableItem) {
                if (item instanceof Ring && Notes.findCustomRecord(item.getClass()) != null) {
                    return false;
                }
                return item.customNoteID == -1 || Notes.findCustomRecord(item.customNoteID) == null;
            }
            return Notes.findCustomRecord(item.getClass()) == null;
        }

        @Override
        public void onSelect(Item item) {
            if (item != null) {
                Notes.CustomRecord custom;
                if (item instanceof EquipableItem || item instanceof Wand || item instanceof Trinket) {
                    custom = new Notes.CustomRecord(item, "", "");
                    custom.assignID();
                    item.customNoteID = custom.ID();
                } else {
                    custom = new Notes.CustomRecord(item.getClass(), "", "");
                    custom.assignID();
                }
                CustomNoteButton.addNote(null, custom, Messages.get(CustomNoteButton.class, "new_inv", new Object[0]), Messages.get(CustomNoteButton.class, "new_item_title", Messages.titleCase(item.name())));
            }
        }
    };
    private static Comparator<Item> itemVisualcomparator;

    public CustomNoteButton() {
        super(Icons.PLUS.get());
        this.width = 11.0f;
        this.height = 11.0f;
    }

    @Override
    protected void onClick() {
        super.onClick();
        if (Notes.getRecords(Notes.CustomRecord.class).size() >= Notes.customRecordLimit()) {
            GameScene.show(new WndTitledMessage(Icons.INFO.get(), Messages.get(this, "limit_title", new Object[0]), Messages.get(this, "limit_text", new Object[0])));
            return;
        }
        GameScene.show(new WndNoteTypeSelect());
    }

    @Override
    protected String hoverText() {
        return Messages.get(this, "title", new Object[0]);
    }

    private static void addNote(final Window parentWindow, final Notes.CustomRecord note, String promptTitle, String prompttext) {
        GameScene.show(new WndTextInput(promptTitle, prompttext, "", 50, false, Messages.get(CustomNoteWindow.class, "confirm", new Object[0]), Messages.get(CustomNoteWindow.class, "cancel", new Object[0])){

            @Override
            public void onSelect(boolean positive, String text) {
                if (positive && !text.isEmpty()) {
                    Notes.add(note);
                    note.editText(text, "");
                    if (parentWindow != null) {
                        parentWindow.hide();
                    }
                    if (WndBag.INSTANCE != null) {
                        WndBag.INSTANCE.hide();
                    }
                    if (NOTE_SELECT_INSTANCE != null) {
                        NOTE_SELECT_INSTANCE.onBackPressed();
                    }
                    this.hide();
                    WndJournal wnd = new WndJournal();
                    ShatteredPixelDungeon.scene().addToFront(wnd);
                    ShatteredPixelDungeon.scene().addToFront(new CustomNoteWindow(note, wnd));
                }
            }
        });
    }

    static {
        itemVisualcomparator = new Comparator<Item>(){

            @Override
            public int compare(Item i1, Item i2) {
                int i1Idx = i1.image();
                int i2Idx = i2.image();
                if (i1 instanceof Scroll) {
                    i1Idx += 1000;
                }
                if (i1 instanceof Ring) {
                    i1Idx += 2000;
                }
                if (i2 instanceof Scroll) {
                    i2Idx += 1000;
                }
                if (i2 instanceof Ring) {
                    i2Idx += 2000;
                }
                return i1Idx - i2Idx;
            }
        };
    }

    private class WndNoteTypeSelect
    extends WndOptions {
        public WndNoteTypeSelect() {
            super(Icons.SCROLL_COLOR.get(), Messages.get(CustomNoteButton.class, "title", new Object[0]), Messages.get(CustomNoteButton.class, "desc", new Object[0]), Messages.get(CustomNoteButton.class, "new_text", new Object[0]), Messages.get(CustomNoteButton.class, "new_floor", new Object[0]), Messages.get(CustomNoteButton.class, "new_inv", new Object[0]), Messages.get(CustomNoteButton.class, "new_type", new Object[0]));
            NOTE_SELECT_INSTANCE = this;
        }

        @Override
        protected void onSelect(int index) {
            if (index == 0) {
                Notes.CustomRecord custom = new Notes.CustomRecord("", "");
                CustomNoteButton.addNote(null, custom, Messages.get(CustomNoteButton.class, "new_text", new Object[0]), Messages.get(CustomNoteButton.class, "new_text_title", new Object[0]));
            } else if (index == 1) {
                GameScene.show(new WndDepthSelect());
            } else if (index == 2) {
                GameScene.selectItem(CustomNoteButton.this.itemSelector);
            } else {
                GameScene.show(new WndItemtypeSelect());
            }
        }

        @Override
        public void hide() {
        }

        @Override
        public void onBackPressed() {
            super.hide();
            NOTE_SELECT_INSTANCE = null;
        }
    }

    public static class CustomNoteWindow
    extends WndJournalItem {
        public CustomNoteWindow(final Notes.CustomRecord rec, final Window parentWindow) {
            super(rec.icon(), rec.title(), rec.desc());
            RedButton title = new RedButton(Messages.get(CustomNoteWindow.class, "edit_title", new Object[0])){

                @Override
                protected void onClick() {
                    GameScene.show(new WndTextInput(Messages.get(CustomNoteWindow.class, "edit_title", new Object[0]), "", rec.title(), 50, false, Messages.get(CustomNoteWindow.class, "confirm", new Object[0]), Messages.get(CustomNoteWindow.class, "cancel", new Object[0])){

                        @Override
                        public void onSelect(boolean positive, String text) {
                            if (positive && !text.isEmpty()) {
                                rec.editText(text, rec.desc());
                                this.hide();
                                if (parentWindow instanceof WndUseItem) {
                                    WndUseItem newParent = new WndUseItem(((WndUseItem)parentWindow).owner, ((WndUseItem)parentWindow).item);
                                    GameScene.show(newParent);
                                    GameScene.show(new CustomNoteWindow(rec, newParent));
                                } else {
                                    GameScene.show(new CustomNoteWindow(rec, parentWindow));
                                }
                            }
                        }
                    });
                }
            };
            this.add(title);
            title.setRect(0.0f, Math.min(this.height + 2, PixelScene.uiCamera.height - 50), this.width / 2 - 1, 16.0f);
            final String editBodyText = rec.desc().isEmpty() ? Messages.get(CustomNoteWindow.class, "add_text", new Object[0]) : Messages.get(CustomNoteWindow.class, "edit_text", new Object[0]);
            RedButton body = new RedButton(editBodyText){

                @Override
                protected void onClick() {
                    GameScene.show(new WndTextInput(editBodyText, "", rec.desc(), 500, true, Messages.get(CustomNoteWindow.class, "confirm", new Object[0]), Messages.get(CustomNoteWindow.class, "cancel", new Object[0])){

                        @Override
                        public void onSelect(boolean positive, String text) {
                            if (positive) {
                                rec.editText(rec.title(), text);
                                this.hide();
                                GameScene.show(new CustomNoteWindow(rec, parentWindow));
                            }
                        }
                    });
                }
            };
            this.add(body);
            body.setRect(title.right() + 2.0f, title.top(), this.width / 2 - 1, 16.0f);
            RedButton delete = new RedButton(Messages.get(CustomNoteWindow.class, "delete", new Object[0])){

                @Override
                protected void onClick() {
                    GameScene.show(new WndOptions(Icons.WARNING.get(), Messages.get(CustomNoteWindow.class, "delete", new Object[0]), Messages.get(CustomNoteWindow.class, "delete_warn", new Object[0]), new String[]{Messages.get(CustomNoteWindow.class, "confirm", new Object[0]), Messages.get(CustomNoteWindow.class, "cancel", new Object[0])}){

                        @Override
                        protected void onSelect(int index) {
                            if (index == 0) {
                                Notes.remove(rec);
                                this.hide();
                                if (parentWindow instanceof WndJournal || parentWindow == null) {
                                    ShatteredPixelDungeon.scene().addToFront(new WndJournal());
                                } else if (parentWindow instanceof WndUseItem) {
                                    GameScene.show(new WndUseItem(((WndUseItem)parentWindow).owner, ((WndUseItem)parentWindow).item));
                                }
                            }
                        }
                    });
                }
            };
            this.add(delete);
            delete.setRect(0.0f, title.bottom() + 1.0f, this.width, 16.0f);
            this.resize(this.width, (int)delete.bottom());
        }

        @Override
        protected boolean useHighlighting() {
            return false;
        }
    }

    private static class WndItemtypeSelect
    extends WndTitledMessage {
        public WndItemtypeSelect() {
            super(Icons.SCROLL_COLOR.get(), Messages.get(CustomNoteButton.class, "new_type", new Object[0]), Messages.get(CustomNoteButton.class, "new_type_prompt", new Object[0]));
            int top = this.height + 2;
            int left = 0;
            ArrayList<Item> items = new ArrayList<Item>();
            for (Class<?> potionCls : Generator.Category.POTION.classes) {
                items.add((Item)Reflection.newInstance(potionCls));
            }
            for (Class<?> potionCls : Generator.Category.SCROLL.classes) {
                items.add((Item)Reflection.newInstance(potionCls));
            }
            for (Class<?> potionCls : Generator.Category.RING.classes) {
                items.add((Item)Reflection.newInstance(potionCls));
            }
            Collections.sort(items, itemVisualcomparator);
            for (final Item item : items) {
                ItemButton itemButton = new ItemButton(){

                    @Override
                    protected void onClick() {
                        CustomNoteButton.addNote(this, new Notes.CustomRecord(item.getClass(), "", ""), Messages.get(CustomNoteButton.class, "new_type", new Object[0]), Messages.get(CustomNoteButton.class, "new_item_title", Messages.titleCase(item.name())));
                    }
                };
                itemButton.item(item);
                itemButton.setRect(left, top, 19.0f, 19.0f);
                this.add(itemButton);
                if (Notes.findCustomRecord(item.getClass()) != null) {
                    itemButton.slot.enable(false);
                }
                if ((left += 20) < this.width - 19) continue;
                top += 20;
                left = 0;
            }
            if (left > 0) {
                top += 20;
                left = 0;
            }
            this.resize(this.width, top);
        }
    }

    private class WndDepthSelect
    extends WndTitledMessage {
        public WndDepthSelect() {
            super(Icons.STAIRS.get(), Messages.get(CustomNoteButton.class, "new_floor", new Object[0]), Messages.get(CustomNoteButton.class, "new_floor_prompt", new Object[0]));
            int top = this.height + 2;
            int left = 0;
            int i = Statistics.deepestFloor;
            while (i > 0) {
                if (i % 5 == 0 && left > 0) {
                    left = 0;
                    top += 17;
                }
                final int finalI = i--;
                RedButton btnDepth = new RedButton(Integer.toString(finalI)){

                    @Override
                    protected void onClick() {
                        CustomNoteButton.addNote(WndDepthSelect.this, new Notes.CustomRecord(finalI, "", ""), Messages.get(CustomNoteButton.class, "new_floor", new Object[0]), Messages.get(CustomNoteButton.class, "new_floor_title", finalI));
                    }
                };
                btnDepth.setRect(left, top, 23.0f, 16.0f);
                left += 24;
                this.add(btnDepth);
            }
            this.resize(this.width, top + (left == 0 ? 0 : 16));
        }
    }
}

