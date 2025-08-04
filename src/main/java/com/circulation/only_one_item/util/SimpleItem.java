package com.circulation.only_one_item.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Objects;

public class SimpleItem{
    private final String item;
    private final int meta;
    private final NBTTagCompound nbt;

    private SimpleItem(Item item, int meta, NBTTagCompound nbt){
        this(item.getRegistryName() == null ? "" : item.getRegistryName().toString(),meta,nbt);
    }

    private SimpleItem(String item, int meta, NBTTagCompound nbt){
        this.item = item;
        this.meta = meta;
        this.nbt = nbt;
    }

    public static SimpleItem getInstance(String item, int meta, NBTTagCompound nbt){
        return new SimpleItem(item,meta,nbt);
    }

    public static SimpleItem getInstance(ItemStack stack){
        return new SimpleItem(stack.getItem(),stack.getMetadata(),stack.getTagCompound());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SimpleItem that = (SimpleItem) o;
        return meta == that.meta && Objects.equals(item, that.item) && Objects.equals(nbt, that.nbt);
    }

    @Override
    public int hashCode() {
        int result = item.hashCode();
        result = 31 * result + meta;
        result = 31 * result + (nbt != null ? nbt.hashCode() : 0);
        return result;
    }
}
