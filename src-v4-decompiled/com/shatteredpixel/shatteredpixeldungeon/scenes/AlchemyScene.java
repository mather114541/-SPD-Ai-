/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.EnergyCrystal;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.LiquidMetal;
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.AlchemistsToolkit;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.TrinketCatalyst;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.journal.Journal;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.shatteredpixel.shatteredpixeldungeon.ui.ExitButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemSlot;
import com.shatteredpixel.shatteredpixeldungeon.ui.RadialMenu;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Toolbar;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndEnergizeItem;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoItem;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndJournal;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndKeyBindings;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.watabou.gltextures.TextureCache;
import com.watabou.glwrap.Blending;
import com.watabou.input.ControllerHandler;
import com.watabou.input.GameAction;
import com.watabou.input.KeyBindings;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.NoosaScript;
import com.watabou.noosa.NoosaScriptNoLighting;
import com.watabou.noosa.SkinnedBlock;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.RectF;
import java.io.IOException;
import java.util.ArrayList;

public class AlchemyScene
extends PixelScene {
    private static final InputButton[] inputs = new InputButton[3];
    private static final CombineButton[] combines = new CombineButton[3];
    private static final OutputSlot[] outputs = new OutputSlot[3];
    private IconButton cancel;
    private IconButton repeat;
    private static ArrayList<Item> lastIngredients = new ArrayList();
    private static Recipe lastRecipe = null;
    private Emitter smokeEmitter;
    private Emitter bubbleEmitter;
    private Emitter sparkEmitter;
    private Emitter lowerBubbles;
    private SkinnedBlock water;
    private Image energyIcon;
    private RenderedTextBlock energyLeft;
    private IconButton energyAdd;
    private boolean energyAddBlinking = false;
    private static boolean splitAlchGuide = false;
    private WndJournal.AlchemyTab alchGuide = null;
    private static int centerW;
    private static final int BTN_SIZE = 28;
    protected WndBag.ItemSelector itemSelector;
    private boolean saveNeeded;
    private static AlchemistsToolkit toolkit;

    public AlchemyScene() {
        this.inGameScene = true;
        this.itemSelector = new WndBag.ItemSelector(){

            @Override
            public String textPrompt() {
                return Messages.get(AlchemyScene.class, "select", new Object[0]);
            }

            @Override
            public boolean itemSelectable(Item item) {
                return Recipe.usableInRecipe(item);
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void onSelect(Item item) {
                InputButton[] inputButtonArray = inputs;
                synchronized (inputs) {
                    if (item != null && inputs[0] != null) {
                        for (int i = 0; i < inputs.length; ++i) {
                            if (inputs[i].item() != null) continue;
                            if (item instanceof LiquidMetal || item instanceof MissileWeapon) {
                                inputs[i].item(item.detachAll(Dungeon.hero.belongings.backpack));
                                break;
                            }
                            inputs[i].item(item.detach(Dungeon.hero.belongings.backpack));
                            break;
                        }
                        AlchemyScene.this.updateState();
                    }
                    // ** MonitorExit[var2_2] (shouldn't be in output)
                    return;
                }
            }
        };
        this.saveNeeded = false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void create() {
        super.create();
        int w = Camera.main.width;
        int h = Camera.main.height;
        RectF insets = this.getCommonInsets();
        this.water = new SkinnedBlock((float)w, (float)h, Dungeon.level.waterTex()){

            @Override
            protected NoosaScript script() {
                return NoosaScriptNoLighting.get();
            }

            @Override
            public void draw() {
                Blending.disable();
                super.draw();
                Blending.enable();
            }
        };
        this.water.autoAdjust = true;
        this.add(this.water);
        Image im = new Image(TextureCache.createGradient(0x66000000, -2013265920, -1442840576, -872415232, -16777216));
        im.angle = 90.0f;
        im.x = w;
        im.scale.x = (float)h / 5.0f;
        im.scale.y = w;
        this.add(im);
        w = (int)((float)w - (insets.left + insets.right));
        h = (int)((float)h - (insets.top + insets.bottom));
        ExitButton btnExit = new ExitButton(){

            @Override
            protected void onClick() {
                Game.switchScene(GameScene.class);
            }
        };
        btnExit.setPos(insets.left + (float)w - btnExit.width(), insets.top);
        this.add(btnExit);
        this.bubbleEmitter = new Emitter();
        this.add(this.bubbleEmitter);
        this.lowerBubbles = new Emitter();
        this.add(this.lowerBubbles);
        IconTitle title = new IconTitle(Icons.ALCHEMY.get(), Messages.get(this, "title", new Object[0]));
        title.setSize(200.0f, 0.0f);
        title.setPos(insets.left + ((float)w - title.reqWidth()) / 2.0f, insets.top + (20.0f - title.height()) / 2.0f);
        AlchemyScene.align(title);
        this.add(title);
        int pw = Math.min(50 + w / 2, 150);
        int left = (int)insets.left + (w - pw) / 2;
        centerW = left + pw / 2;
        int pos = (int)insets.top + (h - 120) / 2;
        if (splitAlchGuide && w >= 300 && (float)h >= 200.0f) {
            pw = Math.min(150, w / 2);
            left = w / 2 - pw;
            centerW = left + pw / 2;
            NinePatch guideBG = Chrome.get(Chrome.Type.TOAST);
            guideBG.size(126 + guideBG.marginHor(), Math.min(Camera.main.height - 18, 191 + guideBG.marginVer()));
            guideBG.y = Math.max(17.0f, insets.top + ((float)h - guideBG.height()) / 2.0f);
            guideBG.x = insets.left + (float)w - (float)left - guideBG.width();
            this.add(guideBG);
            this.alchGuide = new WndJournal.AlchemyTab();
            this.add(this.alchGuide);
            this.alchGuide.setRect(guideBG.x + (float)guideBG.marginLeft(), guideBG.y + (float)guideBG.marginTop(), guideBG.width() - (float)guideBG.marginHor(), guideBG.height() - (float)guideBG.marginVer());
        } else {
            splitAlchGuide = false;
        }
        RenderedTextBlock desc = PixelScene.renderTextBlock(6);
        desc.maxWidth(pw);
        desc.text(Messages.get(AlchemyScene.class, "text", new Object[0]));
        desc.setPos((float)left + ((float)pw - desc.width()) / 2.0f, pos);
        this.add(desc);
        pos = (int)((float)pos + (desc.height() + 6.0f));
        NinePatch inputBG = Chrome.get(Chrome.Type.TOAST_TR);
        inputBG.x = left + 6;
        inputBG.y = pos;
        inputBG.size(36.0f, 96.0f);
        this.add(inputBG);
        pos += 4;
        InputButton[] inputButtonArray = inputs;
        synchronized (inputs) {
            int i;
            for (i = 0; i < inputs.length; ++i) {
                if (inputs[i] == null) {
                    AlchemyScene.inputs[i] = new InputButton();
                } else {
                    Item item = inputs[i].item();
                    AlchemyScene.inputs[i] = new InputButton();
                    if (item != null) {
                        inputs[i].item(item);
                    }
                }
                inputs[i].setRect(left + 10, pos, 28.0f, 28.0f);
                this.add(inputs[i]);
                pos += 30;
            }
            // ** MonitorExit[var12_12] (shouldn't be in output)
            Button invSelector = new Button(){

                @Override
                protected void onClick() {
                    if (Dungeon.hero != null) {
                        final ArrayList<Bag> bags = Dungeon.hero.belongings.getBags();
                        String[] names = new String[bags.size()];
                        Image[] images = new Image[bags.size()];
                        for (int i = 0; i < bags.size(); ++i) {
                            names[i] = Messages.titleCase(bags.get(i).name());
                            images[i] = new ItemSprite(bags.get(i));
                        }
                        Object info = "";
                        if (ControllerHandler.controllerActive) {
                            info = (String)info + KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(GameAction.LEFT_CLICK, true)) + ": " + Messages.get(Toolbar.class, "container_select", new Object[0]) + "\n";
                            info = (String)info + KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(GameAction.BACK, true)) + ": " + Messages.get(Toolbar.class, "container_cancel", new Object[0]);
                        } else {
                            info = (String)info + Messages.get(WndKeyBindings.class, SPDAction.LEFT_CLICK.name(), new Object[0]) + ": " + Messages.get(Toolbar.class, "container_select", new Object[0]) + "\n";
                            info = (String)info + KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(GameAction.BACK, false)) + ": " + Messages.get(Toolbar.class, "container_cancel", new Object[0]);
                        }
                        Game.scene().addToFront(new RadialMenu(Messages.get(Toolbar.class, "container_prompt", new Object[0]), (String)info, names, images){

                            @Override
                            public void onSelect(int idx, boolean alt) {
                                super.onSelect(idx, alt);
                                Bag bag = (Bag)bags.get(idx);
                                final ArrayList items = (ArrayList)bag.items.clone();
                                for (Item i : bag.items) {
                                    if (Dungeon.hero.belongings.lostInventory() && !i.keptThroughLostInventory()) {
                                        items.remove(i);
                                    }
                                    if (Recipe.usableInRecipe(i)) continue;
                                    items.remove(i);
                                }
                                if (items.size() == 0) {
                                    ShatteredPixelDungeon.scene().addToFront(new WndMessage(Messages.get(AlchemyScene.class, "no_items", new Object[0])));
                                    return;
                                }
                                String[] itemNames = new String[items.size()];
                                Image[] itemIcons = new Image[items.size()];
                                for (int i = 0; i < items.size(); ++i) {
                                    itemNames[i] = Messages.titleCase(((Item)items.get(i)).name());
                                    itemIcons[i] = new ItemSprite((Item)items.get(i));
                                }
                                Object info = "";
                                if (ControllerHandler.controllerActive) {
                                    info = (String)info + KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(GameAction.LEFT_CLICK, true)) + ": " + Messages.get(Toolbar.class, "item_select", new Object[0]) + "\n";
                                    info = (String)info + KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(GameAction.BACK, true)) + ": " + Messages.get(Toolbar.class, "item_cancel", new Object[0]);
                                } else {
                                    info = (String)info + Messages.get(WndKeyBindings.class, SPDAction.LEFT_CLICK.name(), new Object[0]) + ": " + Messages.get(Toolbar.class, "item_select", new Object[0]) + "\n";
                                    info = (String)info + KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(GameAction.BACK, false)) + ": " + Messages.get(Toolbar.class, "item_cancel", new Object[0]);
                                }
                                Game.scene().addToFront(new RadialMenu(Messages.get(Toolbar.class, "item_prompt", new Object[0]), (String)info, itemNames, itemIcons){

                                    /*
                                     * WARNING - Removed try catching itself - possible behaviour change.
                                     */
                                    @Override
                                    public void onSelect(int idx, boolean alt) {
                                        super.onSelect(idx, alt);
                                        Item item = (Item)items.get(idx);
                                        InputButton[] inputButtonArray = inputs;
                                        synchronized (inputs) {
                                            if (item != null && inputs[0] != null) {
                                                for (int i = 0; i < inputs.length; ++i) {
                                                    if (inputs[i].item() != null) continue;
                                                    if (item instanceof LiquidMetal || item instanceof MissileWeapon) {
                                                        inputs[i].item(item.detachAll(Dungeon.hero.belongings.backpack));
                                                        break;
                                                    }
                                                    inputs[i].item(item.detach(Dungeon.hero.belongings.backpack));
                                                    break;
                                                }
                                                AlchemyScene.this.updateState();
                                            }
                                            // ** MonitorExit[var4_4] (shouldn't be in output)
                                            return;
                                        }
                                    }
                                });
                            }
                        });
                    }
                }

                @Override
                public GameAction keyAction() {
                    return SPDAction.INVENTORY_SELECTOR;
                }
            };
            this.add(invSelector);
            this.cancel = new IconButton(Icons.CLOSE.get()){

                @Override
                protected void onClick() {
                    super.onClick();
                    AlchemyScene.this.clearSlots();
                    AlchemyScene.this.updateState();
                }

                @Override
                public GameAction keyAction() {
                    return SPDAction.BACK;
                }

                @Override
                protected String hoverText() {
                    return Messages.get(AlchemyScene.class, "cancel", new Object[0]);
                }
            };
            this.cancel.setRect(left + 8, pos + 2, 16.0f, 16.0f);
            this.cancel.enable(false);
            this.add(this.cancel);
            this.repeat = new IconButton(Icons.REPEAT.get()){

                @Override
                protected void onClick() {
                    super.onClick();
                    if (lastRecipe != null) {
                        AlchemyScene.this.populate(lastIngredients, Dungeon.hero.belongings);
                    }
                }

                @Override
                public GameAction keyAction() {
                    return SPDAction.TAG_RESUME;
                }

                @Override
                protected String hoverText() {
                    return Messages.get(AlchemyScene.class, "repeat", new Object[0]);
                }
            };
            this.repeat.setRect(left + 24, pos + 2, 16.0f, 16.0f);
            this.repeat.enable(false);
            this.add(this.repeat);
            lastIngredients.clear();
            lastRecipe = null;
            for (i = 0; i < inputs.length; ++i) {
                AlchemyScene.combines[i] = new CombineButton(i);
                combines[i].enable(false);
                AlchemyScene.outputs[i] = new OutputSlot();
                outputs[i].item(null);
                if (i == 0) {
                    combines[i].setRect((float)left + (float)(pw - 30) / 2.0f, inputs[1].top() + 5.0f, 30.0f, inputs[1].height() - 10.0f);
                    outputs[i].setRect(left + pw - 28 - 10, inputs[1].top(), 28.0f, 28.0f);
                } else {
                    AlchemyScene.combines[i].visible = false;
                    AlchemyScene.outputs[i].visible = false;
                }
                this.add(combines[i]);
                this.add(outputs[i]);
            }
            this.smokeEmitter = new Emitter();
            this.smokeEmitter.pos(outputs[0].left() + 6.0f, outputs[0].top() + 6.0f, 16.0f, 16.0f);
            this.smokeEmitter.autoKill = false;
            this.add(this.smokeEmitter);
            pos += 10;
            if (Camera.main.height >= 280) {
                centerW = (int)insets.left + w / 2;
            }
            this.bubbleEmitter.pos(0.0f, 0.0f, 2 * centerW, Camera.main.height);
            this.bubbleEmitter.autoKill = false;
            this.lowerBubbles.pos(0.0f, pos, 2 * centerW, Math.max(0, h - pos));
            this.lowerBubbles.pour(Speck.factory(12), 0.1f);
            String energyText = Messages.get(AlchemyScene.class, "energy", new Object[0]) + " " + Dungeon.energy;
            if (toolkit != null) {
                energyText = energyText + "+" + toolkit.availableEnergy();
            }
            this.energyLeft = PixelScene.renderTextBlock(energyText, 9);
            this.energyLeft.setPos((float)centerW - this.energyLeft.width() / 2.0f, insets.top + (float)h - 8.0f - this.energyLeft.height());
            this.energyLeft.hardlight(0x44CCFF);
            this.add(this.energyLeft);
            this.energyIcon = new ItemSprite(toolkit != null ? ItemSpriteSheet.ARTIFACT_TOOLKIT : ItemSpriteSheet.ENERGY);
            this.energyIcon.x = this.energyLeft.left() - this.energyIcon.width();
            this.energyIcon.y = this.energyLeft.top() - (this.energyIcon.height() - this.energyLeft.height()) / 2.0f;
            AlchemyScene.align(this.energyIcon);
            this.add(this.energyIcon);
            this.energyAdd = new IconButton(Icons.get(Icons.PLUS)){
                private float time;
                {
                    this.time = 0.0f;
                }

                @Override
                public void update() {
                    super.update();
                    if (AlchemyScene.this.energyAddBlinking) {
                        this.icon.brightness(0.5f + (float)Math.abs(Math.cos(4.712389f * (this.time += Game.elapsed))));
                    } else {
                        if (this.time > 0.0f) {
                            this.icon.resetColor();
                        }
                        this.time = 0.0f;
                    }
                }

                @Override
                protected void onClick() {
                    WndEnergizeItem.openItemSelector();
                }

                @Override
                public GameAction keyAction() {
                    return SPDAction.TAG_ACTION;
                }

                @Override
                protected String hoverText() {
                    return Messages.get(AlchemyScene.class, "energize", new Object[0]);
                }
            };
            this.energyAdd.setRect(this.energyLeft.right(), this.energyLeft.top() - (16.0f - this.energyLeft.height()) / 2.0f, 16.0f, 16.0f);
            AlchemyScene.align(this.energyAdd);
            this.add(this.energyAdd);
            this.sparkEmitter = new Emitter();
            this.sparkEmitter.pos(this.energyLeft.left(), this.energyLeft.top(), this.energyLeft.width(), this.energyLeft.height());
            this.sparkEmitter.autoKill = false;
            this.add(this.sparkEmitter);
            StyledButton btnGuide = new StyledButton(Chrome.Type.TOAST_TR, Messages.get(AlchemyScene.class, "guide", new Object[0])){

                @Override
                protected void onClick() {
                    super.onClick();
                    if (Camera.main.width >= 300 && (float)Camera.main.height >= 200.0f) {
                        splitAlchGuide = !splitAlchGuide;
                        ShatteredPixelDungeon.seamlessResetScene();
                    } else {
                        AlchemyScene.this.clearSlots();
                        AlchemyScene.this.updateState();
                        AlchemyScene.this.addToFront(new Window(){
                            {
                                int h;
                                int w;
                                WndJournal.AlchemyTab t = new WndJournal.AlchemyTab();
                                if (PixelScene.landscape()) {
                                    w = 216;
                                    h = 138;
                                } else {
                                    w = 126;
                                    h = 190;
                                }
                                this.resize(w, h);
                                this.add(t);
                                t.setRect(0.0f, 0.0f, w, h);
                            }
                        });
                    }
                }

                @Override
                public GameAction keyAction() {
                    return SPDAction.JOURNAL;
                }

                @Override
                protected String hoverText() {
                    return Messages.titleCase(Document.ALCHEMY_GUIDE.title());
                }
            };
            btnGuide.icon(new ItemSprite(ItemSpriteSheet.ALCH_PAGE));
            btnGuide.setSize(btnGuide.reqWidth() + 4.0f, 18.0f);
            btnGuide.setPos((float)centerW - btnGuide.width() / 2.0f, this.energyAdd.top() - btnGuide.height() - 2.0f);
            AlchemyScene.align(btnGuide);
            this.add(btnGuide);
            TrinketCatalyst cata = Dungeon.hero.belongings.getItem(TrinketCatalyst.class);
            if (cata != null && cata.hasRolledTrinkets()) {
                this.addToFront(new TrinketCatalyst.WndTrinket(cata));
            }
            this.fadeIn();
            this.saveNeeded = false;
            try {
                Dungeon.saveAll();
                Badges.saveGlobal();
                Journal.saveGlobal();
            }
            catch (IOException e) {
                ShatteredPixelDungeon.reportException(e);
            }
            return;
        }
    }

    @Override
    public void update() {
        super.update();
        this.water.offset(0.0f, -5.0f * Game.elapsed);
    }

    @Override
    protected void onBackPressed() {
        Game.switchScene(GameScene.class);
    }

    private <T extends Item> ArrayList<T> filterInput(Class<? extends T> itemClass) {
        ArrayList<Item> filtered = new ArrayList<Item>();
        for (int i = 0; i < inputs.length; ++i) {
            Item item = inputs[i].item();
            if (item == null || !itemClass.isInstance(item)) continue;
            filtered.add(item);
        }
        return filtered;
    }

    private void updateState() {
        this.repeat.enable(false);
        ArrayList<Item> ingredients = this.filterInput(Item.class);
        ArrayList<Recipe> recipes = Recipe.findRecipes(ingredients);
        for (int i = recipes.size(); i < combines.length; ++i) {
            combines[i].enable(false);
            outputs[i].item(null);
            if (i == 0) continue;
            AlchemyScene.combines[i].visible = false;
            AlchemyScene.outputs[i].visible = false;
        }
        this.cancel.enable(!ingredients.isEmpty());
        if (recipes.isEmpty()) {
            combines[0].setPos(combines[0].left(), inputs[1].top() + 5.0f);
            outputs[0].setPos(outputs[0].left(), inputs[1].top());
            this.energyAddBlinking = false;
            return;
        }
        float gap = recipes.size() == 2 ? 6.0f : 2.0f;
        float height = inputs[2].bottom() - inputs[0].top();
        float top = inputs[0].top() + (height -= (float)(recipes.size() * 28) + (float)(recipes.size() - 1) * gap) / 2.0f;
        boolean promptToAddEnergy = false;
        for (int i = 0; i < recipes.size(); ++i) {
            Recipe recipe = recipes.get(i);
            int cost = recipe.cost(ingredients);
            AlchemyScene.outputs[i].visible = true;
            outputs[i].setRect(outputs[0].left(), top, 28.0f, 28.0f);
            outputs[i].item(recipe.sampleOutput(ingredients));
            top += 28.0f + gap;
            int availableEnergy = Dungeon.energy;
            if (toolkit != null) {
                availableEnergy += toolkit.availableEnergy();
            }
            AlchemyScene.combines[i].visible = true;
            combines[i].setRect(combines[0].left(), outputs[i].top() + 5.0f, 30.0f, 20.0f);
            combines[i].enable(cost <= availableEnergy, cost);
            if (cost <= availableEnergy || !(recipe instanceof TrinketCatalyst.Recipe)) continue;
            promptToAddEnergy = true;
        }
        this.energyAddBlinking = promptToAddEnergy;
        if (this.alchGuide != null) {
            this.alchGuide.updateList();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void combine(int slot) {
        ArrayList<Item> ingredients = this.filterInput(Item.class);
        if (ingredients.isEmpty()) {
            return;
        }
        lastIngredients.clear();
        for (Item i : ingredients) {
            lastIngredients.add(i.duplicate());
        }
        ArrayList<Recipe> recipes = Recipe.findRecipes(ingredients);
        if (recipes.size() <= slot) {
            return;
        }
        Recipe recipe = recipes.get(slot);
        Item result = null;
        if (recipe != null) {
            int cost = recipe.cost(ingredients);
            if (toolkit != null) {
                cost = toolkit.consumeEnergy(cost);
            }
            Catalog.countUses(EnergyCrystal.class, cost);
            String energyText = Messages.get(AlchemyScene.class, "energy", new Object[0]) + " " + (Dungeon.energy -= cost);
            if (toolkit != null) {
                energyText = energyText + "+" + toolkit.availableEnergy();
            }
            this.energyLeft.text(energyText);
            this.energyLeft.setPos((float)centerW - this.energyLeft.width() / 2.0f, this.energyLeft.top());
            this.energyIcon.x = this.energyLeft.left() - this.energyIcon.width();
            AlchemyScene.align(this.energyIcon);
            this.energyAdd.setPos(this.energyLeft.right(), this.energyAdd.top());
            AlchemyScene.align(this.energyAdd);
            result = recipe.brew(ingredients);
        }
        if (result != null) {
            this.craftItem(ingredients, result);
        }
        boolean foundItems = true;
        for (Item i : lastIngredients) {
            Item found = Dungeon.hero.belongings.getSimilar(i);
            if (found != null) continue;
            foundItems = false;
        }
        lastRecipe = recipe;
        this.repeat.enable(foundItems);
        this.cancel.enable(false);
        InputButton[] inputButtonArray = inputs;
        synchronized (inputs) {
            for (int i = 0; i < inputs.length; ++i) {
                if (inputs[i] == null || inputs[i].item() == null) continue;
                this.cancel.enable(true);
                break;
            }
            // ** MonitorExit[var7_7] (shouldn't be in output)
            if (this.alchGuide != null) {
                this.alchGuide.updateList();
            }
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void craftItem(ArrayList<Item> ingredients, Item result) {
        this.bubbleEmitter.start(Speck.factory(12), 0.01f, 100);
        this.smokeEmitter.burst(Speck.factory(7), 10);
        Sample.INSTANCE.play("sounds/puff.mp3");
        int resultQuantity = result.quantity();
        if (!result.collect()) {
            Dungeon.level.drop(result, Dungeon.hero.pos);
        }
        ++Statistics.itemsCrafted;
        Badges.validateItemsCrafted();
        this.saveNeeded = false;
        try {
            Dungeon.saveAll();
            Badges.saveGlobal();
            Journal.saveGlobal();
        }
        catch (IOException e) {
            ShatteredPixelDungeon.reportException(e);
        }
        InputButton[] inputButtonArray = inputs;
        synchronized (inputs) {
            for (int i = 0; i < inputs.length; ++i) {
                if (inputs[i] == null || inputs[i].item() == null) continue;
                Item item = inputs[i].item();
                if (item.quantity() <= 0) {
                    inputs[i].item(null);
                    continue;
                }
                AlchemyScene.inputs[i].slot.updateText();
            }
            // ** MonitorExit[var4_5] (shouldn't be in output)
            this.updateState();
            result.quantity(resultQuantity);
            outputs[0].item(result);
            return;
        }
    }

    public void populate(ArrayList<Item> toFind, Belongings inventory) {
        this.clearSlots();
        int curslot = 0;
        for (Item finding : toFind) {
            Item detached;
            ArrayList<Item> found = inventory.getAllSimilar(finding);
            for (int needed = finding.quantity(); !found.isEmpty() && needed > 0; needed -= detached.quantity()) {
                detached = finding instanceof LiquidMetal || finding instanceof MissileWeapon ? found.get(0).detachAll(inventory.backpack) : found.get(0).detach(inventory.backpack);
                inputs[curslot].item(detached);
                ++curslot;
                if (detached != found.get(0)) continue;
                found.remove(0);
            }
        }
        this.updateState();
    }

    @Override
    public void onPause() {
        if (this.saveNeeded) {
            this.saveNeeded = false;
            this.clearSlots();
            this.updateState();
            try {
                Dungeon.saveAll();
                Badges.saveGlobal();
                Journal.saveGlobal();
            }
            catch (IOException e) {
                ShatteredPixelDungeon.reportException(e);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void destroy() {
        InputButton[] inputButtonArray = inputs;
        synchronized (inputs) {
            this.clearSlots();
            for (int i = 0; i < inputs.length; ++i) {
                AlchemyScene.inputs[i] = null;
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            this.saveNeeded = false;
            try {
                Dungeon.saveAll();
                Badges.saveGlobal();
                Journal.saveGlobal();
            }
            catch (IOException e) {
                ShatteredPixelDungeon.reportException(e);
            }
            super.destroy();
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void clearSlots() {
        InputButton[] inputButtonArray = inputs;
        synchronized (inputs) {
            for (int i = 0; i < inputs.length; ++i) {
                if (inputs[i] == null || inputs[i].item() == null) continue;
                Item item = inputs[i].item();
                if (!item.collect()) {
                    Dungeon.level.drop(item, Dungeon.hero.pos);
                }
                inputs[i].item(null);
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            this.cancel.enable(false);
            this.repeat.enable(lastRecipe != null);
            if (this.alchGuide != null) {
                this.alchGuide.updateList();
            }
            return;
        }
    }

    public void createEnergy() {
        String energyText = Messages.get(AlchemyScene.class, "energy", new Object[0]) + " " + Dungeon.energy;
        if (toolkit != null) {
            energyText = energyText + "+" + toolkit.availableEnergy();
        }
        this.energyLeft.text(energyText);
        this.energyLeft.setPos((float)centerW - this.energyLeft.width() / 2.0f, this.energyLeft.top());
        this.energyIcon.x = this.energyLeft.left() - this.energyIcon.width();
        AlchemyScene.align(this.energyIcon);
        this.energyAdd.setPos(this.energyLeft.right(), this.energyAdd.top());
        AlchemyScene.align(this.energyAdd);
        this.bubbleEmitter.start(Speck.factory(12), 0.01f, 100);
        this.sparkEmitter.burst(SparkParticle.FACTORY, 20);
        Sample.INSTANCE.play("sounds/lightning.mp3");
        this.saveNeeded = true;
        this.updateState();
    }

    public void showIdentify(Item item) {
        if (item.isIdentified()) {
            return;
        }
        final NinePatch BG = Chrome.get(Chrome.Type.TOAST);
        IconTitle oldName = new IconTitle(item){

            @Override
            public synchronized void update() {
                super.update();
                this.alpha(this.alpha() - Game.elapsed);
                if (this.alpha() <= 0.0f) {
                    this.killAndErase();
                }
            }
        };
        item.identify();
        IconTitle newName = new IconTitle(item){
            boolean fading;

            @Override
            public synchronized void update() {
                super.update();
                if (!this.fading) {
                    this.alpha(this.alpha() + Game.elapsed);
                    if (this.alpha() >= 1.0f) {
                        this.fading = true;
                    }
                } else {
                    this.alpha(this.alpha() - Game.elapsed);
                    BG.alpha(this.alpha());
                    if (this.alpha() <= 0.0f) {
                        this.killAndErase();
                        BG.killAndErase();
                    }
                }
            }
        };
        newName.alpha(-0.5f);
        oldName.setSize(200.0f, oldName.height());
        newName.setSize(200.0f, newName.height());
        int w = (int)Math.ceil(Math.max(oldName.reqWidth(), newName.reqWidth()) + 5.0f);
        oldName.setSize(w, oldName.height());
        oldName.setPos((float)centerW - oldName.width() / 2.0f, this.energyAdd.top());
        AlchemyScene.align(oldName);
        newName.setSize(w, oldName.height());
        newName.setPos((float)centerW - newName.width() / 2.0f, this.energyAdd.top());
        AlchemyScene.align(newName);
        BG.x = oldName.left() - 2.0f;
        BG.y = oldName.top() - 2.0f;
        BG.size(oldName.width() + 4.0f, oldName.height() + 4.0f);
        this.add(BG);
        this.add(oldName);
        this.add(newName);
    }

    public static void assignToolkit(AlchemistsToolkit toolkit) {
        AlchemyScene.toolkit = toolkit;
    }

    public static void clearToolkit() {
        toolkit = null;
    }

    private class InputButton
    extends Component {
        protected NinePatch bg;
        protected ItemSlot slot;
        private Item item = null;

        private InputButton() {
        }

        @Override
        protected void createChildren() {
            super.createChildren();
            this.bg = Chrome.get(Chrome.Type.RED_BUTTON);
            this.add(this.bg);
            this.slot = new ItemSlot(){

                @Override
                protected void onPointerDown() {
                    InputButton.this.bg.brightness(1.2f);
                    Sample.INSTANCE.play("sounds/click.mp3");
                }

                @Override
                protected void onPointerUp() {
                    InputButton.this.bg.resetColor();
                }

                @Override
                protected void onClick() {
                    super.onClick();
                    Item item = InputButton.this.item;
                    if (item != null) {
                        if (!item.collect()) {
                            Dungeon.level.drop(item, Dungeon.hero.pos);
                        }
                        InputButton.this.item(null);
                        AlchemyScene.this.updateState();
                    }
                    AlchemyScene.this.addToFront(WndBag.getBag(AlchemyScene.this.itemSelector));
                }

                @Override
                protected boolean onLongClick() {
                    Item item = InputButton.this.item;
                    if (item != null) {
                        AlchemyScene.this.addToFront(new WndInfoItem(item));
                        return true;
                    }
                    return false;
                }

                @Override
                public GameAction keyAction() {
                    for (InputButton i : inputs) {
                        if (i.item != null && !(i.item instanceof WndBag.Placeholder)) continue;
                        if (i == InputButton.this) {
                            return SPDAction.INVENTORY;
                        }
                        return super.keyAction();
                    }
                    return super.keyAction();
                }

                @Override
                protected String hoverText() {
                    if (this.item == null || this.item instanceof WndBag.Placeholder) {
                        return Messages.get(AlchemyScene.class, "add", new Object[0]);
                    }
                    return super.hoverText();
                }

                @Override
                public GameAction secondaryTooltipAction() {
                    return SPDAction.INVENTORY_SELECTOR;
                }
            };
            this.slot.enable(true);
            this.add(this.slot);
        }

        @Override
        protected void layout() {
            super.layout();
            this.bg.x = this.x;
            this.bg.y = this.y;
            this.bg.size(this.width, this.height);
            this.slot.setRect(this.x + 2.0f, this.y + 2.0f, this.width - 4.0f, this.height - 4.0f);
        }

        public Item item() {
            return this.item;
        }

        public void item(Item item) {
            if (item == null) {
                this.item = null;
                this.slot.item(new WndBag.Placeholder(ItemSpriteSheet.SOMETHING));
            } else {
                this.item = item;
                this.slot.item(this.item);
            }
        }
    }

    private class CombineButton
    extends Component {
        protected int slot;
        protected RedButton button;
        protected RenderedTextBlock costText;

        private CombineButton(int slot) {
            this.slot = slot;
        }

        @Override
        protected void createChildren() {
            super.createChildren();
            this.button = new RedButton(""){

                @Override
                protected void onClick() {
                    super.onClick();
                    AlchemyScene.this.combine(CombineButton.this.slot);
                }

                @Override
                protected String hoverText() {
                    return Messages.get(AlchemyScene.class, "craft", new Object[0]);
                }

                @Override
                public GameAction keyAction() {
                    if (CombineButton.this.slot == 0 && !AlchemyScene.combines[1].active && !AlchemyScene.combines[2].active) {
                        return SPDAction.TAG_LOOT;
                    }
                    return super.keyAction();
                }
            };
            this.button.icon(Icons.get(Icons.ARROW));
            this.add(this.button);
            this.costText = PixelScene.renderTextBlock(6);
            this.add(this.costText);
        }

        @Override
        protected void layout() {
            super.layout();
            this.button.setRect(this.x, this.y, this.width(), this.height());
            this.costText.setPos(this.left() + (this.width() - this.costText.width()) / 2.0f, this.top() - this.costText.height());
        }

        public void enable(boolean enabled) {
            this.enable(enabled, 0);
        }

        public void enable(boolean enabled, int cost) {
            this.button.enable(enabled);
            if (enabled) {
                this.button.icon().tint(1.0f, 1.0f, 0.0f, 1.0f);
                this.button.alpha(1.0f);
                this.costText.hardlight(0x44CCFF);
            } else {
                this.button.icon().color(0.0f, 0.0f, 0.0f);
                this.button.alpha(0.6f);
                this.costText.hardlight(0xFF0000);
            }
            if (cost == 0) {
                this.costText.visible = false;
            } else {
                this.costText.visible = true;
                this.costText.text(Messages.get(AlchemyScene.class, "energy", new Object[0]) + " " + cost);
            }
            this.layout();
            this.active = enabled;
        }
    }

    private class OutputSlot
    extends Component {
        protected NinePatch bg;
        protected ItemSlot slot;

        private OutputSlot() {
        }

        @Override
        protected void createChildren() {
            this.bg = Chrome.get(Chrome.Type.TOAST_TR);
            this.add(this.bg);
            this.slot = new ItemSlot(){

                @Override
                protected void onClick() {
                    super.onClick();
                    if (this.visible && this.item != null && this.item.trueName() != null) {
                        AlchemyScene.this.addToFront(new WndInfoItem(this.item));
                    }
                }
            };
            this.slot.item(null);
            this.add(this.slot);
        }

        @Override
        protected void layout() {
            super.layout();
            this.bg.x = this.x;
            this.bg.y = this.y;
            this.bg.size(this.width(), this.height());
            this.slot.setRect(this.x + 2.0f, this.y + 2.0f, this.width() - 4.0f, this.height() - 4.0f);
        }

        public void item(Item item) {
            this.slot.item(item);
        }
    }
}

