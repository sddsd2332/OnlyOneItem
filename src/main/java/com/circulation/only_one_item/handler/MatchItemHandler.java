package com.circulation.only_one_item.handler;

import com.circulation.only_one_item.OOIConfig;
import com.circulation.only_one_item.crt.CrtConversionTarget;
import com.circulation.only_one_item.util.BlackMatchItem;
import com.circulation.only_one_item.util.ItemConversionTarget;
import com.circulation.only_one_item.util.OOIItemStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.oredict.OreDictionary;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Collections;

import static com.circulation.only_one_item.OOIConfig.blackList;

public class MatchItemHandler {

    private static final HashMap<String, Map<Integer, ItemConversionTarget>> itemIdToTargetMap = new HashMap<>();
    public static final HashSet<BlackMatchItem> finalBlockSet = new HashSet<>();

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
        list = null;
    }

    public static synchronized void Clear(){
        itemIdToTargetMap.clear();
        finalBlockSet.clear();
    }

    public static synchronized void InitTarget(){
        BlackInit();
        Init();
        if (Loader.isModLoaded("crafttweaker"))CrtInit();
    }

    public static synchronized void addPreItemStack(OOIItemStack i){
        if (list == null)
            throw new RuntimeException("[OOI] It should not be added again after initialization");
        list.add(new WeakReference<>(i));
    }

    public static ItemConversionTarget match(Object obj) {
        if (!(obj instanceof ItemStack stack))return null;
        if (stack.isEmpty())return null;

        ItemConversionTarget.MatchItem key = ItemConversionTarget.MatchItem.getInstance(stack);
        if (finalBlockSet.contains(BlackMatchItem.getInstance(key)) || finalBlockSet.contains(BlackMatchItem.getModIDInstance(stack))){
            return null;
        }

        String id = key.id();
        int meta = key.meta();

        return itemIdToTargetMap
                .getOrDefault(id, Collections.emptyMap())
                .get(meta);
    }

    private static void Init(){
        for (ItemConversionTarget t : OOIConfig.items) {
            for (ItemConversionTarget.MatchItem matchItem : t.getMatchItems()) {
                if (matchItem.oreName() != null) {
                    var list = OreDictionary.getOres(matchItem.oreName(),false);
                    list.stream()
                            .map(ItemConversionTarget.MatchItem::getInstance)
                            .filter(matchItem1 -> !matchItem1.id().equals(t.getTargetID()) || matchItem1.meta() != t.getTargetMeta())
                            .forEach(m -> itemIdToTargetMap
                                    .computeIfAbsent(m.id(), k -> new HashMap<>())
                                    .put(m.meta(), t));
                    var listC = new ArrayList<>(list);
                    list.clear();
                    for (ItemStack stack : listC) {
                        var matchItem2 = ItemConversionTarget.MatchItem.getInstance(stack);
                        if (finalBlockSet.contains(BlackMatchItem.getInstance(matchItem2))){
                            list.add(stack);
                        }
                        if (t.getTargetID().equals(matchItem2.id()) && t.getTargetMeta() == matchItem2.meta()){
                            list.add(stack);
                        }
                    }
                } else if (matchItem.id() != null) {
                    itemIdToTargetMap
                            .computeIfAbsent(matchItem.id(), k -> new HashMap<>())
                            .put(matchItem.meta(), t);
                }
            }
        }
    }

    private static void BlackInit(){
        for (BlackMatchItem matchItem : blackList) {
            switch (matchItem.type()){
                case Item,ModID -> finalBlockSet.add(matchItem);
                case OreDict -> OreDictionary.getOres(matchItem.name()).stream()
                        .map(BlackMatchItem::getInstance)
                        .forEach(finalBlockSet::add);
            }
        }
    }

    @Optional.Method(modid = "crafttweaker")
    private static void CrtInit(){
        for (ItemConversionTarget t : CrtConversionTarget.set) {
            for (ItemConversionTarget.MatchItem matchItem : t.getMatchItems()) {
                if (matchItem.oreName() != null) {
                    var list = OreDictionary.getOres(matchItem.oreName(),false);
                    list.stream()
                            .map(ItemConversionTarget.MatchItem::getInstance)
                            .filter(matchItem1 -> !matchItem1.id().equals(t.getTargetID()) || matchItem1.meta() != t.getTargetMeta())
                            .forEach(m -> itemIdToTargetMap
                                    .computeIfAbsent(m.id(), k -> new HashMap<>())
                                    .put(m.meta(), t));
                    var listC = new ArrayList<>(list);
                    list.clear();
                    for (ItemStack stack : listC) {
                        var matchItem2 = BlackMatchItem.getInstance(stack);
                        if (finalBlockSet.contains(matchItem2)){
                            list.add(stack);
                        }
                        if (t.getTargetID().equals(matchItem2.name()) && t.getTargetMeta() == matchItem2.meta()){
                            list.add(stack);
                        }
                    }
                } else if (matchItem.id() != null) {
                    itemIdToTargetMap
                            .computeIfAbsent(matchItem.id(), k -> new HashMap<>())
                            .put(matchItem.meta(), t);
                }
            }
        }
    }

}
