package com.circulation.only_one_item.util;

import net.minecraft.item.ItemStack;

public interface OOIItemStack {

    void ooi$init();

    void ooi$ooiInit();

    boolean ooi$isBeReplaced();

    ItemStack ooi$getThis();
}
