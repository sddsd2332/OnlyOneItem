package com.circulation.only_one_item.crt;

import com.circulation.only_one_item.util.ItemConversionTarget;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.oredict.IOreDictEntry;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Collections;
import java.util.HashSet;

@ZenRegister
@ZenClass("mods.ooi.ConversionTarget")
public class CrtConversionTarget {

    public static final HashSet<ItemConversionTarget> set = new HashSet<>();

    private final HashSet<ItemConversionTarget.MatchItem> matchItems = new HashSet<>();

    private final String targetID;
    private final int targetMeta;

    public CrtConversionTarget(String id,int meta){
        this.targetID = id;
        this.targetMeta = meta;
    }

    @ZenMethod
    public static CrtConversionTarget create(IItemStack target){
        return new CrtConversionTarget(target.getDefinition().getId(), target.getMetadata());
    }

    @ZenMethod
    public CrtConversionTarget addMatchItem(IItemStack... stacks){
        Collections.addAll(matchItems, ItemConversionTarget.MatchItem.getInstance(CraftTweakerMC.getItemStacks(stacks)));
        return this;
    }

    @ZenMethod
    public CrtConversionTarget addMatchItem(IOreDictEntry... oreDictEntry){
        for (IOreDictEntry iOreDictEntry : oreDictEntry) {
            matchItems.add(ItemConversionTarget.MatchItem.getInstance(iOreDictEntry.getName()));
        }
        return this;
    }

    @ZenMethod
    public void register(){
        set.add(new ItemConversionTarget(targetID,targetMeta).setMatchItem(matchItems));
    }
}
