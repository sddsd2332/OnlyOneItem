package com.circulation.only_one_item.crt;

import com.circulation.only_one_item.conversion.ItemConversionTarget;
import com.circulation.only_one_item.util.MatchItem;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.oredict.IOreDictEntry;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.*;

@ZenRegister
@ZenClass("mods.ooi.ConversionItem")
public class CrtConversionItemTarget {

    public static final List<ItemConversionTarget> list = new ArrayList<>();

    private final Set<MatchItem> matchItems = new HashSet<>();

    private final String targetID;
    private final int targetMeta;

    public CrtConversionItemTarget(String id, int meta){
        this.targetID = id;
        this.targetMeta = meta;
    }

    @ZenMethod
    public static CrtConversionItemTarget create(IItemStack target){
        return new CrtConversionItemTarget(target.getDefinition().getId(), target.getMetadata());
    }

    @ZenMethod
    public CrtConversionItemTarget addMatchItem(IItemStack... stacks){
        Collections.addAll(matchItems, MatchItem.getInstance(CraftTweakerMC.getItemStacks(stacks)));
        return this;
    }

    @ZenMethod
    public CrtConversionItemTarget addMatchItem(IOreDictEntry... oreDictEntry){
        for (IOreDictEntry iOreDictEntry : oreDictEntry) {
            matchItems.add(MatchItem.getInstance(iOreDictEntry.getName()));
        }
        return this;
    }

    @ZenMethod
    public void register(){
        list.add(new ItemConversionTarget(targetID,targetMeta).setMatchItem(matchItems));
    }
}
