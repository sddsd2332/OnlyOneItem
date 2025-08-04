package com.circulation.only_one_item.conversion;

import com.circulation.only_one_item.util.MatchItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class ItemConversionTarget {
    private Set<MatchItem> matchItems;

    private final String targetID;
    private final int targetMeta;

    public int getTargetMeta() {
        return targetMeta;
    }

    public String getTargetID() {
        return targetID;
    }

    public Set<MatchItem> getMatchItems() {
        return matchItems;
    }

    public Item getTarget(){
        return Item.getByNameOrId(targetID);
    }

    public ItemConversionTarget(String targetID,int targetMeta){
        this.targetID = targetID;
        this.targetMeta = targetMeta;
    }

    public ItemConversionTarget addMatchItem(ItemStack... stacks){
        if (matchItems == null){
            matchItems = new HashSet<>();
        }
        Collections.addAll(matchItems,MatchItem.getInstance(stacks));
        return this;
    }

    public ItemConversionTarget setMatchItem(Set<MatchItem> matchItems){
        this.matchItems = matchItems;
        return this;
    }
}