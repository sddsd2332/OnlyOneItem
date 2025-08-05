package com.circulation.only_one_item.util;

import com.circulation.only_one_item.OnlyOneItem;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreIngredient;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.RegistryManager;

import java.util.*;

public class RecipeSignature {
    private final SimpleItem outputSignature;
    private final int outputAmount;
    private final List<Object> inputSignatures;
    private final Multiset<Object> cleanInputSignatures = HashMultiset.create();
    private final boolean shaped;
    private final int hashCode;
    private final int height;
    private final int width;
    private static final ForgeRegistry<IRecipe> fr = RegistryManager.ACTIVE.getRegistry(GameData.RECIPES);

    Object obs;
    boolean repeat = true;

    public RecipeSignature(IRecipe recipe) {
        this.outputSignature = SimpleItem.getInstance(recipe.getRecipeOutput());
        this.outputAmount = recipe.getRecipeOutput().getCount();
        this.inputSignatures = createInputSignatures(recipe);
        this.shaped = cleanInputSignatures.size() != 1 && recipe instanceof IShapedRecipe && !(repeat && cleanInputSignatures.size() == 9);
        this.hashCode = shaped ?
                Objects.hash(outputSignature, outputAmount, inputSignatures)
                : Objects.hash(outputSignature, outputAmount, cleanInputSignatures);
        if (shaped) {
            height = ((IShapedRecipe) recipe).getRecipeHeight();
            width = ((IShapedRecipe) recipe).getRecipeWidth();
        } else {
            height = 0;
            width = 0;
        }
        obs = null;
    }

    private List<Object> createInputSignatures(IRecipe recipe) {
        List<Ingredient> ingredients = recipe.getIngredients();
        List<Object> signatures = new ArrayList<>();

        int ii = 0;
        for (Ingredient ingredient : ingredients) {
            Multiset<SimpleItem> set = HashMultiset.create();
            ItemStack[] matching = ingredient.getMatchingStacks();
            Map<Integer, Integer> map = new HashMap<>();
            if (isOD(map, signatures, matching, set)) {
                String odName = "";
                int max = 0;
                for (Map.Entry<Integer, Integer> integerIntegerEntry : map.entrySet()) {
                    var od = integerIntegerEntry.getKey();
                    var i = integerIntegerEntry.getValue();

                    if (i > max) {
                        max = i;
                        odName = OreDictionary.getOreName(od);
                    }
                }
                signatures.add(odName);
                if (!odName.isEmpty()) {
                    cleanInputSignatures.add(odName);
                }
                if (obs == null) obs = odName;
                else if (repeat) {
                    if (!obs.equals(odName)) {
                        repeat = false;
                    }
                }
            }
        }

        if (cleanInputSignatures.size() != 1 && recipe instanceof IShapedRecipe && !(repeat && cleanInputSignatures.size() == 9)) {
            cleanInputSignatures.clear();
        } else {
            signatures.clear();
        }

        return signatures;
    }

    private boolean isOD(Map<Integer, Integer> map, List<Object> signatures, ItemStack[] matching, Multiset<SimpleItem> set) {
        for (ItemStack stack : matching) {
            var ods = stack.isEmpty() ? new int[0] : OreDictionary.getOreIDs(stack);
            if (ods.length == 0) {
                Arrays.stream(matching)
                        .map(SimpleItem::getInstance)
                        .forEach(set::add);
                signatures.add(set);
                cleanInputSignatures.add(set);
                if (obs == null) obs = set;
                else if (repeat) {
                    if (!obs.equals(set)) {
                        repeat = false;
                    }
                }
                return false;
            }
            for (int oreID : ods) {
                map.put(oreID, map.getOrDefault(oreID, 1));
            }
        }
        return true;
    }

    public SimpleItem getOutputSignature() {
        return outputSignature;
    }

    public void rebuildRecipe() {
        var NAME = getRecipeName();
        var out = outputSignature.getItemStack(outputAmount);
        if (shaped) {
            NonNullList<Ingredient> inputs = NonNullList.create();
            for (Object input : inputSignatures) {
                if (input instanceof String od) {
                    inputs.add(od.isEmpty() ?
                            Ingredient.EMPTY
                            : new OreIngredient(od));
                } else if (input instanceof Multiset<?> items) {
                    inputs.add(
                            Ingredient.fromStacks(
                                    items.stream()
                                            .map(ii -> {
                                                if (ii instanceof SimpleItem s) {
                                                    return s.getItemStack(1);
                                                }
                                                return ItemStack.EMPTY;
                                            })
                                            .toArray(ItemStack[]::new)
                            )
                    );
                } else {
                    inputs.add(Ingredient.EMPTY);
                }
            }
            fr.register(new ShapedRecipes("", width, height, inputs,out).setRegistryName(OnlyOneItem.MOD_ID, NAME));
        } else {
            Ingredient[] inputs = new Ingredient[cleanInputSignatures.size()];
            int i = 0;
            for (Object input : cleanInputSignatures) {
                if (input instanceof String od) {
                    inputs[i++] = od.isEmpty() ?
                            Ingredient.EMPTY
                            : new OreIngredient(od);
                } else if (input instanceof Multiset<?> items) {
                    inputs[i++] = Ingredient.fromStacks(items.stream()
                            .map(ii -> {
                                if (ii instanceof SimpleItem s) {
                                    return s.getItemStack(1);
                                }
                                return ItemStack.EMPTY;
                            })
                            .toArray(ItemStack[]::new));
                }
            }
            GameRegistry.addShapelessRecipe(
                    new ResourceLocation(OnlyOneItem.MOD_ID, NAME),
                    null,
                    out,
                    inputs
            );
        }
    }

    private String getRecipeName() {
        return (shaped ? "shaped" : "shapeless")
                + outputSignature.getItem()
                + "-"
                + outputSignature.getMeta()
                + "-"
                + hashCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecipeSignature that = (RecipeSignature) o;
        if (shaped) {
            return outputAmount == that.outputAmount &&
                    Objects.equals(outputSignature, that.outputSignature) &&
                    Objects.equals(inputSignatures, that.inputSignatures);
        } else {
            return outputAmount == that.outputAmount &&
                    Objects.equals(outputSignature, that.outputSignature) &&
                    Objects.equals(cleanInputSignatures, that.cleanInputSignatures);
        }
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

}
