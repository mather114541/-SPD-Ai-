/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.CustomNoteButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTextInput;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndUseItem;

public class ItemJournalButton
extends IconButton {
    Item item;
    Window parentWnd;

    public ItemJournalButton(Item item, Window parentWnd) {
        super(Icons.JOURNAL.get());
        this.item = item;
        this.parentWnd = parentWnd;
    }

    @Override
    protected void onClick() {
        this.customNote();
    }

    private void customNote() {
        Notes.CustomRecord note = null;
        note = this.item instanceof EquipableItem || this.item instanceof Wand || this.item instanceof Trinket ? Notes.findCustomRecord(this.item.customNoteID) : Notes.findCustomRecord(this.item.getClass());
        if (note == null) {
            if (Notes.getRecords(Notes.CustomRecord.class).size() >= Notes.customRecordLimit()) {
                GameScene.show(new WndTitledMessage(Icons.INFO.get(), Messages.get(CustomNoteButton.class, "limit_title", new Object[0]), Messages.get(CustomNoteButton.class, "limit_text", new Object[0])));
            } else {
                if (this.item instanceof EquipableItem || this.item instanceof Wand || this.item instanceof Trinket) {
                    note = new Notes.CustomRecord(this.item, "", "");
                    note.assignID();
                    this.item.customNoteID = note.ID();
                } else {
                    note = new Notes.CustomRecord(this.item.getClass(), "", "");
                    note.assignID();
                }
                ItemJournalButton.addNote(this.parentWnd, note, Messages.get(CustomNoteButton.class, "new_inv", new Object[0]), Messages.get(CustomNoteButton.class, "new_item_title", Messages.titleCase(this.item.name())));
            }
        } else {
            GameScene.show(new CustomNoteButton.CustomNoteWindow(note, this.parentWnd));
        }
    }

    private static void addNote(final Window parentWindow, final Notes.CustomRecord note, String promptTitle, String prompttext) {
        GameScene.show(new WndTextInput(promptTitle, prompttext, "", 50, false, Messages.get(CustomNoteButton.CustomNoteWindow.class, "confirm", new Object[0]), Messages.get(CustomNoteButton.CustomNoteWindow.class, "cancel", new Object[0])){

            @Override
            public void onSelect(boolean positive, String text) {
                if (positive && !text.isEmpty()) {
                    Notes.add(note);
                    note.editText(text, "");
                    if (parentWindow != null) {
                        parentWindow.hide();
                    }
                    this.hide();
                    if (parentWindow instanceof WndUseItem) {
                        GameScene.show(new WndUseItem(((WndUseItem)parentWindow).owner, ((WndUseItem)parentWindow).item));
                    }
                }
            }
        });
    }
}

