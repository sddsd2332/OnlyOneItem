package com.circulation.only_one_item.mixin.mc;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(FurnaceRecipes.class)
public interface AccessorFurnaceRecipes {

    @Accessor("experienceList")
    Map<ItemStack, Float> ooi$getExperienceList();
}
