package com.circulation.only_one_item.crt;

import com.circulation.only_one_item.emun.Type;
import com.circulation.only_one_item.util.BlackMatchItem;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.oredict.IOreDictEntry;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import static com.circulation.only_one_item.handler.MatchItemHandler.finalBlockSet;

@ZenRegister
@ZenClass("mods.ooi.BlackList")
public class CrtBlackList {

    @ZenMethod
    public static void addMatchItem(IItemStack... stacks){
        for (IItemStack stack : stacks) {
            finalBlockSet.add(BlackMatchItem.getInstance(CraftTweakerMC.getItemStack(stack)));
        }
    }

    @ZenMethod
    public static void addMatchItem(IOreDictEntry... oreDictEntry){
        for (IOreDictEntry iOreDictEntry : oreDictEntry) {
            finalBlockSet.add(BlackMatchItem.getInstance(Type.OreDict, iOreDictEntry.getName()));
        }
    }

    @ZenMethod
    public static void addMatchItem(String... modids){
        for (String modid : modids) {
            finalBlockSet.add(BlackMatchItem.getInstance(Type.ModID, modid));
        }
    }
}
