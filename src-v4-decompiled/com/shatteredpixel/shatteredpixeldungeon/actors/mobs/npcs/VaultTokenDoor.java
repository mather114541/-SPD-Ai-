/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DwarfToken;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.VaultTokenDoorSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class VaultTokenDoor
extends NPC {
    public VaultTokenDoor() {
        this.spriteClass = VaultTokenDoorSprite.class;
        this.properties.add(Char.Property.IMMOVABLE);
        this.properties.add(Char.Property.OBJECT);
    }

    @Override
    protected void throwItems() {
        Heap heap = (Heap)Dungeon.level.heaps.get(this.pos);
        if (heap != null) {
            Dungeon.level.drop((Item)heap.pickUp(), (int)(this.pos + Dungeon.level.width())).sprite.drop(this.pos);
        }
    }

    @Override
    public boolean interact(Char c) {
        if (c instanceof Hero) {
            Hero h = (Hero)c;
            final DwarfToken tokens = h.belongings.getItem(DwarfToken.class);
            Object descText = this.description();
            descText = tokens == null ? (String)descText + "\n\n" + Messages.get(this, "no_tokens", new Object[0]) : (tokens.quantity() < 10 ? (String)descText + "\n\n" + Messages.get(this, "too_few_tokens", new Object[0]) : (String)descText + "\n\n" + Messages.get(this, "enough_tokens", new Object[0]));
            Object finalDescText = descText;
            ShatteredPixelDungeon.runOnRenderThread(new Callback(){
                final /* synthetic */ String val$finalDescText;
                final /* synthetic */ Char val$c;
                final /* synthetic */ Hero val$h;
                {
                    this.val$finalDescText = string;
                    this.val$c = char_;
                    this.val$h = hero;
                }

                @Override
                public void call() {
                    if (tokens != null && tokens.quantity() >= 10) {
                        GameScene.show(new WndOptions(VaultTokenDoor.this.sprite(), Messages.titleCase(VaultTokenDoor.this.name()), this.val$finalDescText, new String[]{Messages.get(VaultTokenDoor.class, "open", new Object[0]), Messages.get(VaultTokenDoor.class, "not_yet", new Object[0])}){

                            @Override
                            protected void onSelect(int index) {
                                super.onSelect(index);
                                if (index == 0) {
                                    val$c.sprite.operate(VaultTokenDoor.this.pos);
                                    Sample.INSTANCE.play("sounds/teleport.mp3");
                                    Sample.INSTANCE.playDelayed("sounds/unlock.mp3", 0.25f);
                                    GLog.p(Messages.get(VaultTokenDoor.class, "unlocked", new Object[0]), new Object[0]);
                                    VaultTokenDoor.this.destroy();
                                    Level.set(VaultTokenDoor.this.pos, 5);
                                    GameScene.updateMap(VaultTokenDoor.this.pos);
                                    ScrollOfMagicMapping.discover(VaultTokenDoor.this.pos);
                                    VaultTokenDoor.this.sprite.killAndErase();
                                    tokens.detachAll(val$h.belongings.backpack);
                                }
                            }
                        });
                    } else {
                        GameScene.show(new WndTitledMessage(VaultTokenDoor.this.sprite(), Messages.titleCase(VaultTokenDoor.this.name()), this.val$finalDescText));
                    }
                }
            });
        }
        return false;
    }

    @Override
    public int defenseSkill(Char enemy) {
        return INFINITE_EVASION;
    }

    @Override
    public void damage(int dmg, Object src) {
    }

    @Override
    public boolean add(Buff buff) {
        return false;
    }

    @Override
    public boolean reset() {
        return true;
    }
}

