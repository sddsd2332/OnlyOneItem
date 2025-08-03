package com.circulation.only_one_item.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;

public final class ItemConversionTarget {
    private HashSet<MatchItem> matchItems;
    private int hashCode = 0;

    private final String targetID;
    private final int targetMeta;

    public int getTargetMeta() {
        return targetMeta;
    }

    public String getTargetID() {
        return targetID;
    }

    public HashSet<MatchItem> getMatchItems() {
        return matchItems;
    }

    public Item getTarget(){
        return Item.getByNameOrId(targetID);
    }

    public ItemConversionTarget(String targetID,int targetMeta){
        this.targetID = targetID;
        this.targetMeta = targetMeta;
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj)return true;
        if (obj instanceof ItemConversionTarget ict){
            return Objects.equals(this.targetID, ict.targetID) && this.targetMeta == ict.targetMeta;
        }
        return false;
    }

    @Override
    public int hashCode(){
        if (hashCode == 0) hashCode = Objects.hash(targetID,targetMeta);
        return hashCode;
    }

    public ItemConversionTarget addMatchItem(ItemStack... stacks){
        if (matchItems == null){
            matchItems = new HashSet<>();
        }
        Collections.addAll(matchItems,MatchItem.getInstance(stacks));
        return this;
    }

    public ItemConversionTarget setMatchItem(HashSet<MatchItem> matchItems){
        this.matchItems = matchItems;
        return this;
    }

}