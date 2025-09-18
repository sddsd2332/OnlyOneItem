package com.circulation.only_one_item.conversion;

import com.circulation.only_one_item.util.MatchItem;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.Getter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.Set;

@Getter
public final class ItemConversionTarget {
    private Set<MatchItem> matchItems;

    private final String targetID;
    private final int targetMeta;

    public Item getTarget() {
        return Item.getByNameOrId(targetID);
    }

    public ItemStack getItemStack() {
        return new ItemStack(getTarget(), 1, targetMeta);
    }

    public ItemConversionTarget(String targetID, int targetMeta) {
        this.targetID = targetID;
        this.targetMeta = targetMeta;
    }

    public ItemConversionTarget addMatchItem(ItemStack... stacks) {
        if (matchItems == null) {
            matchItems = new ObjectOpenHashSet<>();
        }
        Collections.addAll(matchItems, MatchItem.getInstance(stacks));
        return this;
    }

    public ItemConversionTarget setMatchItem(Set<MatchItem> matchItems) {
        this.matchItems = matchItems;
        return this;
    }
}