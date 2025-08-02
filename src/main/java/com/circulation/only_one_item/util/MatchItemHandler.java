package com.circulation.only_one_item.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.oredict.OreDictionary;

import java.util.*;

import static com.circulation.only_one_item.OOIConfig.blackList;

public class MatchItemHandler {

    private static final Map<ItemConversionTarget, Set<ItemConversionTarget.MatchItem>> map = new HashMap<>();
    private static final Set<ItemConversionTarget.MatchItem> finalBlockMap = new HashSet<>();

    public static boolean match(ItemConversionTarget t, String id,int meta){
        return match(t,ItemConversionTarget.MatchItem.getInstance(id,meta));
    }

    public static boolean match(ItemConversionTarget t,Object obj){
        ItemConversionTarget.MatchItem m;
        if (obj instanceof ItemStack stack){
            m = ItemConversionTarget.MatchItem.getInstance(stack);
        } else if (obj instanceof ItemConversionTarget.MatchItem mi){
            m = mi;
        } else {
            return false;
        }

        if (finalBlockMap.isEmpty() && !blackList.isEmpty()){
            for (ItemConversionTarget.MatchItem matchItem : blackList) {
                if (matchItem.oreName() != null){
                    OreDictionary.getOres(matchItem.oreName()).stream()
                            .map(ItemConversionTarget.MatchItem::getInstance)
                            .forEach(finalBlockMap::add);
                } else {
                    finalBlockMap.add(matchItem);
                }
            }
            if (Loader.isModLoaded("crafttweaker"))CrtBlackInit();
        }

        if (!map.containsKey(t)){
            Set<ItemConversionTarget.MatchItem> matchItems = new HashSet<>();
            for (ItemConversionTarget.MatchItem matchItem : t.getMatchItems()) {
                if (matchItem.oreName() != null){
                    var list = OreDictionary.getOres(matchItem.oreName(),false);
                    list.stream()
                            .map(ItemConversionTarget.MatchItem::getInstance)
                            .filter(matchItem1 -> !matchItem1.id().equals(t.getTargetID()) || matchItem1.meta() != t.getTargetMeta())
                            .forEach(matchItems::add);
                    var listC = new ArrayList<>(list);
                    list.clear();
                    for (ItemStack stack : listC) {
                        var matchItem2 = ItemConversionTarget.MatchItem.getInstance(stack);
                        if (finalBlockMap.contains(matchItem2)){
                            list.add(stack);
                        }
                        if (t.getTargetID().equals(matchItem2.id()) && t.getTargetMeta() == matchItem2.meta()){
                            list.add(stack);
                        }
                    }
                } else {
                    matchItems.add(matchItem);
                }
            }
            if (Loader.isModLoaded("crafttweaker"))CrtInit(matchItems);
            map.put(t,matchItems);
        }

        if (finalBlockMap.contains(m))return false;
        if (Objects.equals(m.id(), t.getTargetID()) && m.meta() == t.getTargetMeta())return false;
        return map.get(t).contains(m);
    }

    @Optional.Method(modid = "crafttweaker")
    private static void CrtInit(Set<ItemConversionTarget.MatchItem> matchItems){

    }

    @Optional.Method(modid = "crafttweaker")
    private static void CrtBlackInit(){

    }

}
