/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.ArcaneResin;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.LiquidMetal;
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Blandfruit;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MeatPie;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Pasty;
import com.shatteredpixel.shatteredpixeldungeon.items.food.StewedMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.AquaBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.BlizzardBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.CausticBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.InfernalBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.ShockingBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.UnstableBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfAquaticRejuvenation;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfArcaneArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfDragonsBlood;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfFeatherFall;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfHoneyedHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfIcyTouch;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfMight;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfToxicEssence;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.Alchemize;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.BeaconOfReturning;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.CurseInfusion;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.MagicalInfusion;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.PhaseShift;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.ReclaimTrap;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.Recycle;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.SummonElemental;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.TelekineticGrab;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.UnstableSpell;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.WildEnergy;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.Runestone;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.AlchemyScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemSlot;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoItem;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Reflection;
import java.util.ArrayList;
import java.util.Arrays;

public class QuickRecipe
extends Component {
    private ArrayList<Item> ingredients;
    private ArrayList<ItemSlot> inputs;
    private arrow arrow;
    private ItemSlot output;

    public QuickRecipe(Recipe.SimpleRecipe r) {
        this(r, r.getIngredients(), r.sampleOutput(null));
    }

    public QuickRecipe(Recipe r, ArrayList<Item> inputs, final Item output) {
        this.ingredients = inputs;
        int cost = r.cost(inputs);
        boolean hasInputs = true;
        this.inputs = new ArrayList();
        for (final Item in : inputs) {
            this.anonymize(in);
            ItemSlot curr = new ItemSlot(in){
                {
                    super(item);
                    this.hotArea.blockLevel = 2;
                }

                @Override
                protected void onClick() {
                    ShatteredPixelDungeon.scene().addToFront(new WndInfoItem(in));
                }
            };
            int quantity = 0;
            if (Dungeon.hero != null) {
                ArrayList<Item> similar = Dungeon.hero.belongings.getAllSimilar(in);
                for (Item sim : similar) {
                    if (sim.getClass() == in.getClass() && !sim.isIdentified()) continue;
                    quantity += sim.quantity();
                }
                if (quantity < in.quantity()) {
                    curr.sprite.alpha(0.3f);
                    hasInputs = false;
                }
            } else {
                hasInputs = false;
            }
            curr.showExtraInfo(false);
            this.add(curr);
            this.inputs.add(curr);
        }
        if (cost > 0) {
            this.arrow = new arrow(Icons.get(Icons.ARROW), cost);
            this.arrow.hardlightText(0x44CCFF);
        } else {
            this.arrow = new arrow(Icons.get(Icons.ARROW));
        }
        if (hasInputs) {
            this.arrow.icon.tint(1.0f, 1.0f, 0.0f, 1.0f);
            if (!(ShatteredPixelDungeon.scene() instanceof AlchemyScene)) {
                this.arrow.enable(false);
            }
        } else {
            this.arrow.icon.color(0.0f, 0.0f, 0.0f);
            this.arrow.enable(false);
        }
        this.add(this.arrow);
        this.anonymize(output);
        this.output = new ItemSlot(output){

            @Override
            protected void onClick() {
                ShatteredPixelDungeon.scene().addToFront(new WndInfoItem(output));
            }
        };
        if (Dungeon.hero != null && !hasInputs) {
            this.output.sprite.alpha(0.3f);
        }
        this.output.showExtraInfo(false);
        this.add(this.output);
        this.layout();
    }

    @Override
    protected void layout() {
        this.height = 16.0f;
        this.width = 0.0f;
        int padding = this.inputs.size() == 1 ? 8 : 0;
        for (ItemSlot item : this.inputs) {
            item.setRect(this.x + this.width + (float)padding, this.y, 16.0f, 16.0f);
            this.width += (float)(16 + padding);
        }
        this.arrow.setRect(this.x + this.width, this.y, 14.0f, 16.0f);
        this.width += 14.0f;
        this.output.setRect(this.x + this.width, this.y, 16.0f, 16.0f);
        this.width += 16.0f;
        this.width += (float)padding;
    }

    private void anonymize(Item item) {
        if (item instanceof Potion) {
            ((Potion)item).anonymize();
        } else if (item instanceof Scroll) {
            ((Scroll)item).anonymize();
        }
    }

    public static ArrayList<QuickRecipe> getRecipes(int pageIdx) {
        ArrayList<QuickRecipe> result = new ArrayList<QuickRecipe>();
        switch (pageIdx) {
            default: {
                result.add(new QuickRecipe(new Potion.SeedToPotion(), new ArrayList<Item>(Arrays.asList(new Plant.Seed.PlaceHolder().quantity(3))), new WndBag.Placeholder(ItemSpriteSheet.POTION_HOLDER){

                    @Override
                    public String name() {
                        return Messages.get(Potion.SeedToPotion.class, "name", new Object[0]);
                    }

                    @Override
                    public String info() {
                        return "";
                    }
                }));
                return result;
            }
            case 1: {
                Scroll.ScrollToStone r = new Scroll.ScrollToStone();
                for (Class<?> cls : Generator.Category.SCROLL.classes) {
                    Scroll scroll = (Scroll)Reflection.newInstance(cls);
                    if (!scroll.isKnown()) {
                        scroll.anonymize();
                    }
                    ArrayList<Item> in = new ArrayList<Item>(Arrays.asList(scroll));
                    result.add(new QuickRecipe(r, in, ((Recipe)r).sampleOutput(in)));
                }
                return result;
            }
            case 2: {
                result.add(new QuickRecipe(new StewedMeat.oneMeat()));
                result.add(new QuickRecipe(new StewedMeat.twoMeat()));
                result.add(new QuickRecipe(new StewedMeat.threeMeat()));
                result.add(null);
                result.add(new QuickRecipe(new MeatPie.Recipe(), new ArrayList<Item>(Arrays.asList(new Pasty(), new Food(), new MysteryMeat.PlaceHolder())), new MeatPie()));
                result.add(null);
                result.add(new QuickRecipe(new Blandfruit.CookFruit(), new ArrayList<Item>(Arrays.asList(new Blandfruit(), new Plant.Seed.PlaceHolder())), new Blandfruit(){

                    @Override
                    public String name() {
                        return Messages.get(Blandfruit.class, "cooked", new Object[0]);
                    }

                    @Override
                    public String info() {
                        return "";
                    }
                }));
                return result;
            }
            case 3: {
                ExoticPotion.PotionToExotic r = new ExoticPotion.PotionToExotic();
                for (Class<?> cls : Generator.Category.POTION.classes) {
                    Potion pot = (Potion)Reflection.newInstance(cls);
                    ArrayList<Item> in = new ArrayList<Item>(Arrays.asList(pot));
                    result.add(new QuickRecipe(r, in, ((Recipe)r).sampleOutput(in)));
                }
                return result;
            }
            case 4: {
                ExoticScroll.ScrollToExotic r = new ExoticScroll.ScrollToExotic();
                for (Class<?> cls : Generator.Category.SCROLL.classes) {
                    Scroll scroll = (Scroll)Reflection.newInstance(cls);
                    ArrayList<Item> in = new ArrayList<Item>(Arrays.asList(scroll));
                    result.add(new QuickRecipe(r, in, ((Recipe)r).sampleOutput(in)));
                }
                return result;
            }
            case 5: {
                Bomb.EnhanceBomb r = new Bomb.EnhanceBomb();
                int i = 0;
                for (Class<? extends Item> cls : Bomb.EnhanceBomb.validIngredients.keySet()) {
                    if (i == 2) {
                        result.add(null);
                        i = 0;
                    }
                    Item item = Reflection.newInstance(cls);
                    ArrayList<Item> in = new ArrayList<Item>(Arrays.asList(new Bomb(), item));
                    result.add(new QuickRecipe(r, in, ((Recipe)r).sampleOutput(in)));
                    ++i;
                }
                return result;
            }
            case 6: {
                result.add(new QuickRecipe(new LiquidMetal.Recipe(), new ArrayList<Item>(Arrays.asList(new MissileWeapon.PlaceHolder())), new LiquidMetal()));
                result.add(new QuickRecipe(new ArcaneResin.Recipe(), new ArrayList<Item>(Arrays.asList(new Wand.PlaceHolder())), new ArcaneResin()));
                return result;
            }
            case 7: {
                result.add(new QuickRecipe(new UnstableBrew.Recipe(), new ArrayList<Item>(Arrays.asList(new Potion.PlaceHolder(), new Plant.Seed.PlaceHolder())), new UnstableBrew()));
                result.add(new QuickRecipe(new CausticBrew.Recipe()));
                result.add(new QuickRecipe(new BlizzardBrew.Recipe()));
                result.add(new QuickRecipe(new ShockingBrew.Recipe()));
                result.add(new QuickRecipe(new InfernalBrew.Recipe()));
                result.add(new QuickRecipe(new AquaBrew.Recipe()));
                result.add(null);
                result.add(null);
                result.add(new QuickRecipe(new ElixirOfHoneyedHealing.Recipe()));
                result.add(new QuickRecipe(new ElixirOfAquaticRejuvenation.Recipe()));
                result.add(new QuickRecipe(new ElixirOfArcaneArmor.Recipe()));
                result.add(new QuickRecipe(new ElixirOfIcyTouch.Recipe()));
                result.add(new QuickRecipe(new ElixirOfToxicEssence.Recipe()));
                result.add(new QuickRecipe(new ElixirOfDragonsBlood.Recipe()));
                result.add(new QuickRecipe(new ElixirOfFeatherFall.Recipe()));
                result.add(new QuickRecipe(new ElixirOfMight.Recipe()));
                return result;
            }
            case 8: 
        }
        result.add(new QuickRecipe(new UnstableSpell.Recipe(), new ArrayList<Item>(Arrays.asList(new Scroll.PlaceHolder(), new Runestone.PlaceHolder())), new UnstableSpell()));
        result.add(new QuickRecipe(new WildEnergy.Recipe()));
        result.add(new QuickRecipe(new TelekineticGrab.Recipe()));
        result.add(new QuickRecipe(new PhaseShift.Recipe()));
        if (!PixelScene.landscape()) {
            result.add(null);
        }
        result.add(null);
        result.add(new QuickRecipe(new Alchemize.Recipe(), new ArrayList<Item>(Arrays.asList(new Plant.Seed.PlaceHolder(), new Runestone.PlaceHolder())), new Alchemize().quantity(8)));
        result.add(new QuickRecipe(new CurseInfusion.Recipe()));
        result.add(new QuickRecipe(new MagicalInfusion.Recipe()));
        result.add(new QuickRecipe(new Recycle.Recipe()));
        if (!PixelScene.landscape()) {
            result.add(null);
        }
        result.add(null);
        result.add(new QuickRecipe(new ReclaimTrap.Recipe()));
        result.add(new QuickRecipe(new SummonElemental.Recipe()));
        result.add(new QuickRecipe(new BeaconOfReturning.Recipe()));
        return result;
    }

    public class arrow
    extends IconButton {
        BitmapText text;

        public arrow() {
        }

        public arrow(Image icon) {
            super(icon);
        }

        public arrow(Image icon, int count) {
            super(icon);
            this.hotArea.blockLevel = 2;
            this.text = new BitmapText(Integer.toString(count), PixelScene.pixelFont);
            this.text.measure();
            this.add(this.text);
        }

        @Override
        protected void layout() {
            super.layout();
            if (this.text != null) {
                this.text.x = this.x;
                this.text.y = this.y;
                PixelScene.align(this.text);
            }
        }

        @Override
        protected void onPointerUp() {
            this.icon.brightness(1.0f);
        }

        @Override
        protected void onClick() {
            super.onClick();
            Group parent = this.parent;
            while (parent != null) {
                if (parent instanceof Window) {
                    ((Window)parent).hide();
                    break;
                }
                parent = parent.parent;
            }
            ((AlchemyScene)ShatteredPixelDungeon.scene()).populate(QuickRecipe.this.ingredients, Dungeon.hero.belongings);
        }

        public void hardlightText(int color) {
            if (this.text != null) {
                this.text.hardlight(color);
            }
        }
    }
}

