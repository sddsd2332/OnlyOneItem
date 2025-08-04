package com.circulation.only_one_item.handler;

import com.circulation.only_one_item.OOIConfig;
import com.circulation.only_one_item.conversion.ItemConversionTarget;
import com.circulation.only_one_item.crt.CrtConversionItemTarget;
import com.circulation.only_one_item.util.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

import java.lang.ref.WeakReference;
import java.util.*;

public class MatchItemHandler {

    public static MatchItemHandler INSTANCE = new MatchItemHandler();

    private MatchItemHandler() {

    }

    @SubscribeEvent
    public void onOreRegister(OreDictionary.OreRegisterEvent event){
        var od = event.getName();
        if (odToTargetMap.containsKey(od)){
            var ore = event.getOre();
            var m = MatchItem.getInstance(ore);
            itemIdToTargetMap
                    .computeIfAbsent(m.id(), k -> new HashMap<>())
                    .put(m.meta(), odToTargetMap.get(od));
            OreDictionary.getOres(od).remove(ore);
            ((OOIItemStack)(Object)ore).ooi$ooiInit();
        }
    }

    private static final HashMap<String, Map<Integer, ItemConversionTarget>> itemIdToTargetMap = new HashMap<>();
    private static final HashMap<String, ItemConversionTarget> odToTargetMap = new HashMap<>();
    public static final HashSet<BlackMatchItem> finalBlockSet = new HashSet<>();
    public static final HashSet<SimpleItem> allTarget = new HashSet<>();

    private static ArrayList<WeakReference<OOIItemStack>> list = new ArrayList<>();

    public static void preItemStackInit() {
        if (list == null)
            throw new RuntimeException("[OOI] Initialization should not be performed multiple times");
        ((OOIItemStack)(Object)ItemStack.EMPTY).ooi$init();
        list.parallelStream()
                .forEach(ref -> {
                    var item = ref.get();
                    if (item != null) item.ooi$ooiInit();
                });
        list.clear();
        allTarget.clear();

        list = null;

        var sl = FurnaceRecipes.instance().getSmeltingList();
        var slc = new HashMap<>(sl);

        var el = FurnaceRecipes.instance().experienceList;
        var elc = new HashMap<>(el);

        sl.clear();
        el.clear();

        Map<SimpleItem, ItemStack> uniqueKeys = new HashMap<>(sl.size());

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

    public static synchronized void Clear(){
        itemIdToTargetMap.clear();
        finalBlockSet.clear();
    }

    public static synchronized void InitTarget(){
        BlackInit();
        Init();
        if (!Loader.isModLoaded("crafttweaker")) {
            InitHandler.allPreInit();
        }
    }

    public static synchronized void addPreItemStack(OOIItemStack i){
        if (list == null)
            throw new RuntimeException("[OOI] It should not be added again after initialization");
        list.add(new WeakReference<>(i));
    }

    public static ItemConversionTarget match(Object obj) {
        if (!(obj instanceof ItemStack stack))return null;
        if (stack.isEmpty())return null;

        MatchItem key = MatchItem.getInstance(stack);
        if (finalBlockSet.contains(BlackMatchItem.getInstance(key)) || finalBlockSet.contains(BlackMatchItem.getModIDInstance(stack))){
            return null;
        }

        String id = key.id();
        int meta = key.meta();

        return itemIdToTargetMap
                .getOrDefault(id, Collections.emptyMap())
                .get(meta);
    }

    private static void Init() {
        for (ItemConversionTarget t : OOIConfig.items) {
            MatchItemHandler.allTarget.add(SimpleItem.getInstance(t.getTargetID(), t.getTargetMeta(), null));
            for (MatchItem matchItem : t.getMatchItems()) {
                if (matchItem.oreName() != null) {
                    var list = OreDictionary.getOres(matchItem.oreName(), false);
                    list.stream()
                            .map(MatchItem::getInstance)
                            .filter(matchItem1 -> !allTarget.contains(SimpleItem.getInstance(matchItem1.id(), matchItem1.meta(), null)))
                            .forEach(matchItem1 -> itemIdToTargetMap
                                    .computeIfAbsent(matchItem1.id(), k -> new HashMap<>())
                                    .put(matchItem1.meta(), t));
                    var listC = new ArrayList<>(list);
                    list.clear();
                    for (ItemStack stack : listC) {
                        var matchItem2 = MatchItem.getInstance(stack);
                        if (finalBlockSet.contains(BlackMatchItem.getInstance(matchItem2))) {
                            list.add(stack);
                        }
                        if (allTarget.contains(SimpleItem.getInstance(matchItem2.id(), matchItem2.meta(), null))) {
                            list.add(stack);
                        }
                    }
                    odToTargetMap.put(matchItem.oreName(),t);
                } else if (matchItem.id() != null) {
                    if (!allTarget.contains(SimpleItem.getInstance(matchItem.id(), matchItem.meta(), null))) {
                        itemIdToTargetMap
                                .computeIfAbsent(matchItem.id(), k -> new HashMap<>())
                                .put(matchItem.meta(), t);
                    }
                }
            }
        }
        OOIConfig.items.clear();
    }

    private static void BlackInit(){
        for (BlackMatchItem matchItem : OOIConfig.blackList) {
            switch (matchItem.type()){
                case Item,ModID -> finalBlockSet.add(matchItem);
                case OreDict -> OreDictionary.getOres(matchItem.name()).stream()
                        .map(BlackMatchItem::getInstance)
                        .forEach(finalBlockSet::add);
            }
        }
        OOIConfig.blackList.clear();
    }

    @Optional.Method(modid = "crafttweaker")
    public static void CrtInit(){
        for (ItemConversionTarget t : CrtConversionItemTarget.list) {
            allTarget.add(SimpleItem.getInstance(t.getTargetID(), t.getTargetMeta(), null));
            for (MatchItem matchItem : t.getMatchItems()) {
                if (matchItem.oreName() != null) {
                    var list = OreDictionary.getOres(matchItem.oreName(), false);
                    list.stream()
                            .map(MatchItem::getInstance)
                            .filter(matchItem1 -> !allTarget.contains(SimpleItem.getInstance(matchItem1.id(), matchItem1.meta(), null)))
                            .forEach(m -> itemIdToTargetMap
                                    .computeIfAbsent(m.id(), k -> new HashMap<>())
                                    .put(m.meta(), t));
                    var listC = new ArrayList<>(list);
                    list.clear();
                    for (ItemStack stack : listC) {
                        var matchItem2 = BlackMatchItem.getInstance(stack);
                        if (finalBlockSet.contains(matchItem2)) {
                            list.add(stack);
                        }
                        if (allTarget.contains(SimpleItem.getInstance(matchItem2.name(), matchItem2.meta(), null))) {
                            list.add(stack);
                        }
                    }
                    odToTargetMap.put(matchItem.oreName(),t);
                } else if (matchItem.id() != null) {
                    if (!allTarget.contains(SimpleItem.getInstance(matchItem.id(), matchItem.meta(), null))) {
                        itemIdToTargetMap
                                .computeIfAbsent(matchItem.id(), k -> new HashMap<>())
                                .put(matchItem.meta(), t);
                    }
                }
            }
        }
        CrtConversionItemTarget.list.clear();
    }
}
