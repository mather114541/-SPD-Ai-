/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.VaultFlameTraps;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.quest.vault.VaultDM100;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.quest.vault.VaultDM200;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.quest.vault.VaultElemental;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.quest.vault.VaultGhoul;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.quest.vault.VaultGolem;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.quest.vault.VaultShaman;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.quest.vault.VaultSkeleton;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Torch;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.LeatherArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.MailArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.PlateArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ScaleArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfExperience;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfInvisibility;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLevitation;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMindVision;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfParalyticGas;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfForce;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfMight;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfLullaby;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMirrorImage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRetribution;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTerror;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTransmutation;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAggression;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAugmentation;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfBlink;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfClairvoyance;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfDeepSleep;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfFear;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfFlock;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfShock;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorruption;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfTransfusion;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.Dart;
import com.shatteredpixel.shatteredpixeldungeon.levels.CityLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.Builder;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.GridBuilder;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.CityPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.VaultEntranceRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.VaultFinalRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.VaultRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.VaultSimpleEnemyTreasureRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.VaultTokensRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.treasure.VaultTreasureRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Blindweed;
import com.shatteredpixel.shatteredpixeldungeon.plants.Earthroot;
import com.shatteredpixel.shatteredpixeldungeon.plants.Firebloom;
import com.shatteredpixel.shatteredpixeldungeon.plants.Icecap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Mageroyal;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sorrowmoss;
import com.shatteredpixel.shatteredpixeldungeon.plants.Starflower;
import com.shatteredpixel.shatteredpixeldungeon.plants.Stormvine;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sungrass;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class VaultLevel
extends CityLevel {
    Item[][] equipmentLoot = new Item[4][];
    int higherTierIdx = 0;
    int lowerTierIdx = 0;
    private HashSet<Class<? extends Item>> generatedClasses = new HashSet();
    ArrayList<ArrayList<Item>> consumableLoot = new ArrayList();
    static Class<? extends Item>[] T3SolveItems = new Class[]{StoneOfBlink.class, PotionOfInvisibility.class};
    static Class<? extends Item>[] T2SolveItems = new Class[]{StoneOfBlink.class, PotionOfInvisibility.class};
    public static Class<? extends Mob>[] T1Mobs = new Class[]{VaultSkeleton.class, VaultDM100.class};
    public static Class<? extends Mob>[] T2Mobs = new Class[]{VaultShaman.class, VaultDM200.class, VaultGhoul.class};
    public static Class<? extends Mob>[] T3Mobs = new Class[]{VaultElemental.class, VaultGolem.class};
    private ArrayList<Class<? extends Mob>> mobsToSpawn = new ArrayList();

    @Override
    public void playLevelMusic() {
        Music.INSTANCE.play("music/city_tense.ogg", true);
    }

    @Override
    protected boolean build() {
        int i;
        this.itemsToSpawn.clear();
        for (i = 0; i < 4; ++i) {
            this.addItemToSpawn(this.createEquipment(0));
        }
        this.addItemToSpawn(new Dart());
        for (i = 0; i < 5; ++i) {
            this.addItemToSpawn(this.createConsumabe(0));
        }
        this.addItemToSpawn(Generator.randomUsingDefaults(Generator.Category.FOOD));
        this.addItemToSpawn(Generator.randomUsingDefaults(Generator.Category.FOOD));
        this.addItemToSpawn(Generator.randomUsingDefaults(Generator.Category.FOOD));
        return super.build();
    }

    @Override
    protected ArrayList<Room> initRooms() {
        int i;
        VaultRoom r;
        ArrayList<Room> initRooms = new ArrayList<Room>();
        this.roomEntrance = new VaultEntranceRoom();
        initRooms.add(this.roomEntrance);
        VaultRoom.setupChances();
        for (i = 0; i < 9; i += r.sizeFactor()) {
            r = VaultRoom.createRoom();
            initRooms.add(r);
        }
        initRooms.add(new VaultTokensRoom());
        initRooms.add(new VaultSimpleEnemyTreasureRoom());
        VaultTreasureRoom.generateRoomList();
        for (i = 0; i < 7; ++i) {
            initRooms.add(VaultTreasureRoom.nextRoom());
        }
        initRooms.add(new VaultFinalRoom());
        return initRooms;
    }

    @Override
    public float levelExplorePercent(int depth) {
        int seen = 0;
        int total = 0;
        for (int i = 0; i < this.length; ++i) {
            if (this.discoverable[i]) {
                ++total;
            }
            if (!this.visited[i]) continue;
            ++seen;
        }
        return Math.min(1.0f, (float)seen * 1.25f / (float)total);
    }

    @Override
    protected Builder builder() {
        return new GridBuilder();
    }

    @Override
    protected Painter painter() {
        return new CityPainter(){

            @Override
            public float hiddenDoorChance(Level l) {
                return 0.0f;
            }
        }.setWater(0.15f, 12).setGrass(0.3f, 3).setTraps(this.nTraps(), this.trapClasses(), this.trapChances());
    }

    @Override
    protected int nTraps() {
        return 0;
    }

    @Override
    public boolean activateTransition(Hero hero, LevelTransition transition) {
        return false;
    }

    public void setupEquipment() {
        for (int i = 0; i < this.equipmentLoot.length; ++i) {
            boolean empty = true;
            if (this.equipmentLoot[i] != null) {
                for (int j = 0; j < this.equipmentLoot[i].length; ++j) {
                    if (this.equipmentLoot[i][j] == null) continue;
                    empty = false;
                }
            }
            if (!empty) continue;
            this.setupEquipmentAtTier(i);
        }
    }

    public void setupEquipmentAtTier(int lootTier) {
        Item loot;
        ArrayList<Item> lootList = new ArrayList<Item>();
        do {
            switch (lootTier) {
                default: {
                    loot = Generator.randomUsingDefaults(Generator.Category.WEP_T2);
                    break;
                }
                case 1: {
                    loot = Generator.randomUsingDefaults(Generator.Category.WEP_T2);
                    break;
                }
                case 2: {
                    loot = Generator.randomUsingDefaults(Generator.Category.WEP_T3);
                    break;
                }
                case 3: {
                    loot = Generator.randomUsingDefaults(Generator.Category.WEP_T4);
                }
            }
        } while (lootTier > 1 && this.generatedClasses.contains(loot.getClass()));
        this.generatedClasses.add(loot.getClass());
        if (lootTier == 0) {
            loot.level(lootTier);
        } else {
            loot.level(lootTier + 1);
        }
        if (Random.Int(3) >= lootTier) {
            ((Weapon)loot).enchant(null);
        } else {
            ((Weapon)loot).enchant();
        }
        lootList.add(loot);
        do {
            switch (lootTier) {
                default: {
                    loot = Generator.randomUsingDefaults(Generator.Category.WEP_T2);
                    break;
                }
                case 1: {
                    loot = Generator.randomUsingDefaults(Generator.Category.WEP_T3);
                    break;
                }
                case 2: {
                    loot = Generator.randomUsingDefaults(Generator.Category.WEP_T4);
                    break;
                }
                case 3: {
                    loot = Generator.randomUsingDefaults(Generator.Category.WEP_T5);
                }
            }
        } while (lootTier > 0 && this.generatedClasses.contains(loot.getClass()));
        this.generatedClasses.add(loot.getClass());
        loot.level(lootTier);
        if (Random.Int(3) >= lootTier) {
            ((Weapon)loot).enchant(null);
        } else {
            ((Weapon)loot).enchant();
        }
        lootList.add(loot);
        do {
            switch (lootTier) {
                default: {
                    loot = Generator.randomUsingDefaults(Generator.Category.MIS_T2);
                    break;
                }
                case 1: {
                    loot = Generator.randomUsingDefaults(Generator.Category.MIS_T3);
                    break;
                }
                case 2: {
                    loot = Generator.randomUsingDefaults(Generator.Category.MIS_T4);
                    break;
                }
                case 3: {
                    loot = Generator.randomUsingDefaults(Generator.Category.MIS_T5);
                }
            }
        } while (this.generatedClasses.contains(loot.getClass()));
        this.generatedClasses.add(loot.getClass());
        loot.level(lootTier);
        if (Random.Int(3) >= lootTier) {
            ((Weapon)loot).enchant(null);
        } else {
            ((Weapon)loot).enchant();
        }
        lootList.add(loot);
        switch (lootTier) {
            default: {
                loot = new LeatherArmor();
                break;
            }
            case 1: {
                loot = new MailArmor();
                break;
            }
            case 2: {
                loot = new ScaleArmor();
                break;
            }
            case 3: {
                loot = new PlateArmor();
            }
        }
        this.generatedClasses.add(loot.getClass());
        loot.level(lootTier);
        if (Random.Int(3) >= lootTier) {
            ((Armor)loot).inscribe(null);
        } else {
            ((Armor)loot).inscribe();
        }
        lootList.add(loot);
        while (this.generatedClasses.contains((loot = Generator.randomUsingDefaults(Generator.Category.WAND)).getClass()) || loot instanceof WandOfRegrowth || loot instanceof WandOfTransfusion || loot instanceof WandOfCorruption) {
        }
        this.generatedClasses.add(loot.getClass());
        loot.level(lootTier);
        ((Wand)loot).curCharges = ((Wand)loot).maxCharges;
        lootList.add(loot);
        while (this.generatedClasses.contains((loot = Generator.randomUsingDefaults(Generator.Category.RING)).getClass()) || loot instanceof RingOfWealth || loot instanceof RingOfMight || loot instanceof RingOfForce) {
        }
        this.generatedClasses.add(loot.getClass());
        loot.level(lootTier);
        lootList.add(loot);
        this.equipmentLoot[lootTier] = lootList.toArray(new Item[0]);
    }

    public Item createEquipment(int lootTier) {
        int idx;
        this.setupEquipment();
        if (lootTier >= 2) {
            if ((idx = this.higherTierIdx++) >= this.equipmentLoot[lootTier].length) {
                idx = 0;
                this.higherTierIdx = 0;
            }
        } else if ((idx = this.lowerTierIdx++) >= this.equipmentLoot[lootTier].length) {
            idx = 0;
            this.lowerTierIdx = 0;
        }
        while (this.equipmentLoot[lootTier][idx] == null) {
            if (++idx < this.equipmentLoot[lootTier].length) continue;
            idx = 0;
        }
        Item loot = this.equipmentLoot[lootTier][idx];
        this.equipmentLoot[lootTier][idx] = null;
        loot.cursed = false;
        if (loot instanceof Ring) {
            loot.cursedKnown = true;
            loot.levelKnown = true;
        } else {
            loot.identify(false);
        }
        return loot;
    }

    private void setupConsumables() {
        if (this.consumableLoot.isEmpty()) {
            this.consumableLoot.add(new ArrayList());
            this.consumableLoot.add(new ArrayList());
            this.consumableLoot.add(new ArrayList());
            this.consumableLoot.add(new ArrayList());
        }
        if (this.consumableLoot.get(0).isEmpty()) {
            this.consumableLoot.get(0).addAll(Arrays.asList((Item)Reflection.newInstance(Random.oneOf(PotionOfFrost.class, PotionOfLevitation.class)), (Item)Reflection.newInstance(Random.oneOf(Mageroyal.Seed.class, Icecap.Seed.class, Stormvine.Seed.class)), (Item)Reflection.newInstance(Random.oneOf(ScrollOfMirrorImage.class, ScrollOfTeleportation.class)), (Item)Reflection.newInstance(Random.oneOf(StoneOfFlock.class, StoneOfShock.class, StoneOfFear.class))));
            Collections.shuffle((List)this.consumableLoot.get(0));
            this.consumableLoot.get(0).add(0, new PotionOfHealing());
        }
        if (this.consumableLoot.get(1).isEmpty()) {
            this.consumableLoot.get(1).addAll(Arrays.asList((Item)Reflection.newInstance(Random.oneOf(PotionOfToxicGas.class, PotionOfParalyticGas.class)), (Item)Reflection.newInstance(Random.oneOf(Firebloom.Seed.class, Sorrowmoss.Seed.class, Blindweed.Seed.class)), (Item)Reflection.newInstance(Random.oneOf(ScrollOfRecharging.class, ScrollOfTerror.class)), (Item)Reflection.newInstance(Random.oneOf(StoneOfDeepSleep.class, StoneOfClairvoyance.class, StoneOfAggression.class))));
            Collections.shuffle((List)this.consumableLoot.get(1));
            this.consumableLoot.get(1).add(0, new PotionOfHealing());
        }
        if (this.consumableLoot.get(2).isEmpty()) {
            this.consumableLoot.get(2).addAll(Arrays.asList((Item)Reflection.newInstance(Random.oneOf(PotionOfMindVision.class, PotionOfLiquidFlame.class)), (Item)Reflection.newInstance(Random.oneOf(Swiftthistle.Seed.class, Sungrass.Seed.class)), (Item)Reflection.newInstance(Random.oneOf(ScrollOfLullaby.class, ScrollOfMagicMapping.class)), (Item)Reflection.newInstance(Random.oneOf(StoneOfBlast.class, StoneOfBlink.class))));
            Collections.shuffle((List)this.consumableLoot.get(2));
            this.consumableLoot.get(2).add(0, new PotionOfHealing());
        }
        if (this.consumableLoot.get(3).isEmpty()) {
            this.consumableLoot.get(3).addAll(Arrays.asList((Item)Reflection.newInstance(Random.oneOf(PotionOfExperience.class, PotionOfInvisibility.class)), (Item)Reflection.newInstance(Random.oneOf(Earthroot.Seed.class, Starflower.Seed.class)), (Item)Reflection.newInstance(Random.oneOf(ScrollOfRetribution.class, ScrollOfTransmutation.class)), (Item)Reflection.newInstance(Random.oneOf(StoneOfEnchantment.class, StoneOfAugmentation.class)), new PotionOfHealing()));
            Collections.shuffle((List)this.consumableLoot.get(3));
        }
    }

    public Item createConsumabe(int tier) {
        if (this.consumableLoot.isEmpty() || this.consumableLoot.get(tier).isEmpty()) {
            this.setupConsumables();
        }
        Item result = this.consumableLoot.get(tier).remove(0);
        return result;
    }

    public Item findT3SolveItem() {
        Random.shuffle(T3SolveItems);
        Item result = null;
        for (Class<? extends Item> itemCls : T3SolveItems) {
            result = this.findPrizeItem(itemCls);
            if (result == null) continue;
            return result;
        }
        return null;
    }

    public Item findT2SolveItem() {
        Random.shuffle(T2SolveItems);
        Item result = null;
        for (Class<? extends Item> itemCls : T2SolveItems) {
            result = this.findPrizeItem(itemCls);
            if (result == null) continue;
            return result;
        }
        return this.findT3SolveItem();
    }

    @Override
    public Mob createMob() {
        Class<? extends Mob> cls;
        if (this.mobsToSpawn.isEmpty()) {
            Collections.addAll(this.mobsToSpawn, T1Mobs);
            this.mobsToSpawn.add(Random.oneOf(T1Mobs));
            Collections.addAll(this.mobsToSpawn, T2Mobs);
            Collections.addAll(this.mobsToSpawn, T3Mobs);
            this.mobsToSpawn.add(Random.oneOf(T3Mobs));
            Random.shuffle(this.mobsToSpawn);
        }
        if ((cls = this.mobsToSpawn.remove(0)) == VaultElemental.class) {
            cls = VaultElemental.random();
        }
        return Reflection.newInstance(cls);
    }

    public void returnMob(Class<? extends Mob> cls) {
        this.mobsToSpawn.add(0, cls);
    }

    @Override
    protected void createMobs() {
    }

    @Override
    public void occupyCell(Char ch) {
        Room r;
        super.occupyCell(ch);
        if (ch == Dungeon.hero && Imp.Quest.isCompleted() && !Imp.Quest.isOld()) {
            VaultLevel.beforeTransition();
            InterlevelScene.curTransition = new LevelTransition(Dungeon.level, Dungeon.hero.pos, LevelTransition.Type.BRANCH_ENTRANCE, Dungeon.depth, 0, LevelTransition.Type.BRANCH_EXIT);
            InterlevelScene.mode = InterlevelScene.Mode.ASCEND;
            Game.switchScene(InterlevelScene.class);
        } else if (ch == Dungeon.hero && (r = this.room(ch.pos)) instanceof VaultFinalRoom) {
            ((VaultFinalRoom)r).processHeroStep((Hero)ch);
        }
    }

    @Override
    public Actor addRespawner() {
        return null;
    }

    @Override
    protected void createItems() {
        for (Item item : this.itemsToSpawn) {
            int cell = this.randomDropCell();
            this.drop((Item)item, (int)cell).type = Heap.Type.HEAP;
            if (this.map[cell] != 15 && this.map[cell] != 30) continue;
            this.map[cell] = 2;
            this.losBlocking[cell] = false;
        }
        if (Dungeon.isChallenged(32)) {
            Random.pushGenerator(Random.Long());
            for (int i = 0; i < 2; ++i) {
                int cell = this.randomDropCell();
                this.drop((Item)new Torch(), (int)cell).type = Heap.Type.HEAP;
                if (this.map[cell] != 15 && this.map[cell] != 30) continue;
                this.map[cell] = 2;
                this.losBlocking[cell] = false;
            }
            Random.popGenerator();
        }
    }

    @Override
    public void seal() {
        if (!this.locked) {
            this.locked = true;
        }
    }

    @Override
    public void unseal() {
        super.unseal();
        for (Room r : this.rooms) {
            if (!(r instanceof VaultFinalRoom)) continue;
            ((VaultFinalRoom)r).unlock();
        }
        Game.runOnRenderThread(new Callback(){

            @Override
            public void call() {
                Music.INSTANCE.fadeOut(5.0f, new Callback(){

                    @Override
                    public void call() {
                        Music.INSTANCE.end();
                    }
                });
            }
        });
    }

    public static class VaultFlameTrap
    extends Trap {
        public VaultFlameTrap() {
            this.color = 8;
            this.shape = 0;
            this.canBeHidden = false;
            this.active = false;
        }

        @Override
        public void activate() {
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", new Object[0]);
        }

        public static void setupTrap(Level level, int cell, int initialCD, int afterTriggerCD, int triggers) {
            VaultFlameTraps traps = Blob.seed(0, 0, VaultFlameTraps.class, level);
            traps.curCooldowns[cell] = initialCD;
            traps.afterTriggerCooldowns[cell] = afterTriggerCD;
            traps.triggersAfterCooldown[cell] = triggers;
            level.setTrap(new VaultFlameTrap().reveal(), cell);
            Painter.set(level, cell, 19);
        }
    }
}

