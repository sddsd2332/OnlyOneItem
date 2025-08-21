package com.circulation.only_one_item.handler;

import com.circulation.only_one_item.OOIConfig;
import com.circulation.only_one_item.conversion.ItemConversionTarget;
import com.circulation.only_one_item.crt.CrtBlackList;
import com.circulation.only_one_item.crt.CrtConversionItemTarget;
import com.circulation.only_one_item.mixin.mc.AccessorFurnaceRecipes;
import com.circulation.only_one_item.util.BlackMatchItem;
import com.circulation.only_one_item.util.MatchItem;
import com.circulation.only_one_item.util.OOIItemStack;
import com.circulation.only_one_item.util.RecipeSignature;
import com.circulation.only_one_item.util.SimpleItem;
import com.google.common.collect.Multiset;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.RegistryManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
public class MatchItemHandler {

    public static MatchItemHandler INSTANCE = new MatchItemHandler();

    private MatchItemHandler() {

    }

    @SubscribeEvent
    public void onOreRegister(OreDictionary.OreRegisterEvent event) {
        var od = event.getName();
        var ore = event.getOre();
        if (finalODBlackSet.contains(od)) {
            finalItemBlackMap
                    .computeIfAbsent(ore.getItem(),item -> new ObjectOpenHashSet<>())
                    .add(ore.getMetadata());
            return;
        }
        var rl = ore.getItem().getRegistryName();
        if (rl != null){
            if (finalMODIDBlackSet.contains(rl.getNamespace())){
                finalItemBlackMap
                        .computeIfAbsent(ore.getItem(),item -> new ObjectOpenHashSet<>())
                        .add(ore.getMetadata());
                return;
            }
        }
        if (odToTargetMap.containsKey(od)) {
            itemIdToTargetMap
                    .computeIfAbsent(ore.getItem(), k -> new Int2ObjectOpenHashMap<>())
                    .put(ore.getMetadata(), odToTargetMap.get(od));
            OreDictionary.getOres(od).remove(ore);
            ((OOIItemStack) (Object) ore).ooi$ooiInit();
        }
    }

    private static final Map<Item, Int2ObjectOpenHashMap<ItemConversionTarget>> itemIdToTargetMap = new Object2ObjectOpenHashMap<>();
    private static final Map<String, ItemConversionTarget> odToTargetMap = new Object2ObjectOpenHashMap<>();
    private static final Map<Item,Set<Integer>> finalItemBlackMap = new Object2ObjectOpenHashMap<>();
    private static final Set<String> finalODBlackSet = new ObjectOpenHashSet<>();
    private static final Set<String> finalMODIDBlackSet = new ObjectOpenHashSet<>();
    private static final Set<SimpleItem> allTarget = new ObjectOpenHashSet<>();

    private static ArrayList<OOIItemStack> list = new ArrayList<>();

    public static void preItemStackInit() {
        odToTargetMap.keySet().forEach(od -> {
            var ods = OreDictionary.getOres(od);
            var listC = new ArrayList<>(ods);
            ods.clear();
            for (ItemStack stack : listC) {
                Item item = stack.getItem();
                ResourceLocation rl = item.getRegistryName();
                int meta = stack.getMetadata();
                if (rl == null)continue;
                if ((finalItemBlackMap.containsKey(item) && finalItemBlackMap.get(item).contains(meta))
                        || finalMODIDBlackSet.contains(rl.getNamespace())) {
                    ods.add(stack);
                }
                if (allTarget.contains(SimpleItem.getInstance(stack))) {
                    ods.add(stack);
                }
            }
        });

        if (list == null)
            throw new RuntimeException("[OOI] Initialization should not be performed multiple times");
        ((OOIItemStack) (Object) ItemStack.EMPTY).ooi$init();

        list.parallelStream().forEach(OOIItemStack::ooi$ooiInit);
        list.clear();
        list = null;

        var sl = FurnaceRecipes.instance().getSmeltingList();
        var slc = new Object2ObjectOpenHashMap<>(sl);

        var el = ((AccessorFurnaceRecipes)FurnaceRecipes.instance()).ooi$getExperienceList();
        var elc = new Object2ObjectOpenHashMap<>(el);

        sl.clear();
        el.clear();

        Map<SimpleItem, ItemStack> uniqueKeys = new Object2ObjectOpenHashMap<>(sl.size());

        for (Map.Entry<ItemStack, ItemStack> stack : slc.entrySet()) {
            ItemStack key = stack.getKey();
            var s = SimpleItem.getInstance(key);

            ItemStack canonicalKey = uniqueKeys.computeIfAbsent(s, k -> key);

            if (canonicalKey == key) {
                sl.put(key, stack.getValue());
            }
        }

        el.putAll(elc);
    }

    public static boolean isModify(String odName){
        return odToTargetMap.containsKey(odName);
    }

    public static boolean isModify(Multiset<SimpleItem> set){
        for (SimpleItem item : set) {
            Item ii;
            if ((itemIdToTargetMap.containsKey(ii = item.getItem())
                    && itemIdToTargetMap.get(ii).containsKey(item.getMeta()))
                    || allTarget.contains(item)) {
                return true;
            }
        }
        return false;
    }

    public static void clearRecipe() {
        Map<RecipeSignature, List<IRecipe>> recipes = new Object2ObjectOpenHashMap<>();
        Set<RecipeSignature> recipes0 = new ObjectOpenHashSet<>();
        List<ResourceLocation> cleanRecipes = new ObjectArrayList<>();
        final var a = RegistryManager.ACTIVE.<IRecipe>getRegistry(GameData.RECIPES);

        for (Map.Entry<ResourceLocation, IRecipe> s : a.getEntries()) {
            var recipe = s.getValue();

            if (((OOIItemStack)(Object)recipe.getRecipeOutput()).ooi$isBeReplaced() && recipe.getRecipeOutput().isEmpty()){
                cleanRecipes.add(recipe.getRegistryName());
                continue;
            }

            if (recipe.isDynamic())continue;

            var rs = new RecipeSignature(recipe);
            if (rs.getOutputSignature().isEmpty() || !rs.isModify())continue;

            recipes.computeIfAbsent(rs, v -> new ObjectArrayList<>())
                    .add(recipe);
        }

        allTarget.clear();

        recipes.forEach((r, recipe) -> {
            if (recipe.size() > 1) {
                boolean empty = false;
                for (IRecipe iRecipe : recipe) {
                    if (iRecipe != null) {
                        a.remove(iRecipe.getRegistryName());
                    }
                }
                recipes0.add(r);
            }
        });

        recipes.clear();
        cleanRecipes.forEach(a::remove);
        cleanRecipes.clear();
        recipes0.forEach(RecipeSignature::rebuildRecipe);
        recipes0.clear();
    }

    public static synchronized void Clear() {
        itemIdToTargetMap.clear();
        finalMODIDBlackSet.clear();
        finalItemBlackMap.clear();
        finalODBlackSet.clear();
    }

    public static synchronized void InitTarget() {
        BlackInit(OOIConfig.blackList);
        Init(OOIConfig.items);
    }

    public static synchronized void addPreItemStack(OOIItemStack i) {
        if (list == null)
            throw new RuntimeException("[OOI] It should not be added again after initialization");
        list.add(i);
    }

    private static final Int2ObjectOpenHashMap<ItemConversionTarget> defaultMap = new Int2ObjectOpenHashMap<>(0);

    public static ItemConversionTarget match(Item item,int meta) {
        if (item == null)return null;
        ResourceLocation rl = item.getRegistryName();
        if (rl == null)return null;
        if ((finalItemBlackMap.containsKey(item) && finalItemBlackMap.get(item).contains(meta))
                || finalMODIDBlackSet.contains(rl.getNamespace())) {
            return null;
        }

        return itemIdToTargetMap
                .getOrDefault(item, defaultMap)
                .get(meta);
    }

    private static void Init(List<ItemConversionTarget> items) {
        for (ItemConversionTarget t : items) {
            allTarget.add(SimpleItem.getInstance(t.getTargetID(), t.getTargetMeta(), null));
            for (MatchItem matchItem : t.getMatchItems()) {
                if (matchItem.oreName() != null) {
                    var list = OreDictionary.getOres(matchItem.oreName(), false);
                    list.stream()
                            .filter(stack -> {
                                Item item = stack.getItem();
                                ResourceLocation rl = item.getRegistryName();
                                int meta = stack.getMetadata();
                                if (rl == null) return false;
                                return !allTarget.contains(SimpleItem.getInstance(stack))
                                        && !((finalItemBlackMap.containsKey(item) && finalItemBlackMap.get(item).contains(meta))
                                        || finalMODIDBlackSet.contains(rl.getNamespace()));
                            })
                            .forEach(stack -> {
                                Item item = stack.getItem();
                                ResourceLocation rl = item.getRegistryName();
                                int meta = stack.getMetadata();
                                if (rl != null) {
                                    itemIdToTargetMap
                                            .computeIfAbsent(item, k -> new Int2ObjectOpenHashMap<>())
                                            .put(meta, t);
                                }
                            });
                    odToTargetMap.put(matchItem.oreName(), t);
                } else if (matchItem.id() != null) {
                    if (!allTarget.contains(SimpleItem.getInstance(matchItem.id(), matchItem.meta(), null))) {
                        itemIdToTargetMap
                                .computeIfAbsent(Item.getByNameOrId(matchItem.id()), k -> new Int2ObjectOpenHashMap<>())
                                .put(matchItem.meta(), t);
                    }
                }
            }
        }
        items.clear();
    }

    private static void BlackInit(Set<BlackMatchItem> blackSet) {
        for (BlackMatchItem matchItem : blackSet) {
            switch (matchItem.type()) {
                case Item -> finalItemBlackMap
                        .computeIfAbsent(Item.getByNameOrId(matchItem.name()),k -> new ObjectOpenHashSet<>())
                        .add(matchItem.meta());
                case ModID -> finalMODIDBlackSet.add(matchItem.name());
                case OreDict -> {
                    String od;
                    OreDictionary.getOres(od = matchItem.name())
                            .forEach(stack -> {
                                Item item = stack.getItem();
                                ResourceLocation rl = item.getRegistryName();
                                int meta = stack.getMetadata();
                                if (rl != null) {
                                    finalItemBlackMap
                                            .computeIfAbsent(item, k -> new ObjectOpenHashSet<>())
                                            .add(meta);
                                }
                            });
                    finalODBlackSet.add(od);
                }
            }
        }
        blackSet.clear();
    }

    @Optional.Method(modid = "crafttweaker")
    public static void CrtInit() {
        Init(CrtConversionItemTarget.list);
        BlackInit(CrtBlackList.list);
    }

}
