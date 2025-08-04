package com.circulation.only_one_item.util;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.IShapedRecipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class RecipeSignature {
    private final String outputSignature;
    private final List<String> inputSignatures;
    private final boolean shaped;

    public RecipeSignature(IRecipe recipe) {
        this.outputSignature = createItemStackSignature(recipe.getRecipeOutput());
        this.inputSignatures = createInputSignatures(recipe);
        this.shaped = recipe instanceof IShapedRecipe;
    }

    private String createItemStackSignature(ItemStack stack) {
        if (stack.isEmpty()) return "EMPTY";
        ResourceLocation registryName = stack.getItem().getRegistryName();
        int count = stack.getCount();
        NBTTagCompound nbt = stack.getTagCompound();
        return registryName + ":" + count + (nbt != null ? ":" + nbt.hashCode() : "");
    }

    private List<String> createInputSignatures(IRecipe recipe) {
        List<Ingredient> ingredients = recipe.getIngredients();
        List<String> signatures = new ArrayList<>();

        for (Ingredient ingredient : ingredients) {
            if (ingredient == Ingredient.EMPTY) {
                signatures.add("EMPTY");
            } else {
                ItemStack[] matching = ingredient.getMatchingStacks();
                if (matching.length > 0) {
                    signatures.add(createItemStackSignature(matching[0]));
                } else {
                    signatures.add("UNKNOWN");
                }
            }
        }

        if (!(recipe instanceof IShapedRecipe)) {
            Collections.sort(signatures);
        }

        return signatures;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecipeSignature that = (RecipeSignature) o;
        return shaped == that.shaped &&
                Objects.equals(outputSignature, that.outputSignature) &&
                Objects.equals(inputSignatures, that.inputSignatures);
    }

    @Override
    public int hashCode() {
        return Objects.hash(outputSignature, inputSignatures, shaped);
    }
}
