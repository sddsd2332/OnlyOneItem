package com.circulation.only_one_item.util;

import com.circulation.only_one_item.OOIConfig;
import com.circulation.only_one_item.crt.CrtConversionTarget;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.oredict.OreDictionary;

import java.util.*;

import static com.circulation.only_one_item.OOIConfig.blackList;

public class MatchItemHandler {

    private static final Map<String, Map<Integer, ItemConversionTarget>> itemIdToTargetMap = new HashMap<>();
    public static final Set<ItemConversionTarget.MatchItem> finalBlockSet = new HashSet<>();

    private static boolean init;

    public static synchronized void Clear(){
        init = false;
        itemIdToTargetMap.clear();
        finalBlockSet.clear();
    }

    public static synchronized void InitTarget(){
        init = true;
        BlackInit();
        Init();
        if (Loader.isModLoaded("crafttweaker"))CrtInit();
    }

    public static ItemConversionTarget match(Object obj) {
        if (!init || !(obj instanceof ItemStack stack))return null;
        if (stack.isEmpty())return null;

        ItemConversionTarget.MatchItem key = ItemConversionTarget.MatchItem.getInstance(stack);
        if (finalBlockSet.contains(key)) {
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
                        if (finalBlockSet.contains(matchItem2)){
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
        for (ItemConversionTarget.MatchItem matchItem : blackList) {
            if (matchItem.oreName() != null) {
                OreDictionary.getOres(matchItem.oreName()).stream()
                        .map(ItemConversionTarget.MatchItem::getInstance)
                        .forEach(finalBlockSet::add);
            } else {
                finalBlockSet.add(matchItem);
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
                        var matchItem2 = ItemConversionTarget.MatchItem.getInstance(stack);
                        if (finalBlockSet.contains(matchItem2)){
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

}
