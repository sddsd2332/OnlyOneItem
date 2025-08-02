package com.circulation.only_one_item.crt;

import com.circulation.only_one_item.util.ItemConversionTarget;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.oredict.IOreDictEntry;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import static com.circulation.only_one_item.util.MatchItemHandler.finalBlockSet;

@ZenRegister
@ZenClass("mods.ooi.BlockList")
public class CrtBlockList {

    @ZenMethod
    public static void addMatchItem(IItemStack... stacks){
        for (IItemStack stack : stacks) {
            finalBlockSet.add(ItemConversionTarget.MatchItem.getInstance(CraftTweakerMC.getItemStack(stack)));
        }
    }

    @ZenMethod
    public static void addMatchItem(IOreDictEntry... oreDictEntry){
        for (IOreDictEntry iOreDictEntry : oreDictEntry) {
            finalBlockSet.add(ItemConversionTarget.MatchItem.getInstance(iOreDictEntry.getName()));
        }
    }
}
