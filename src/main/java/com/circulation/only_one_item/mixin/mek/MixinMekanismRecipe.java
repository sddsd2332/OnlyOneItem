package com.circulation.only_one_item.mixin.mek;

import com.circulation.only_one_item.util.OOIMekRecipe;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.MachineInput;
import mekanism.common.recipe.machines.MachineRecipe;
import mekanism.common.recipe.outputs.MachineOutput;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;

@Mixin(value = RecipeHandler.Recipe.class,remap = false)
public abstract class MixinMekanismRecipe<INPUT extends MachineInput<INPUT>, OUTPUT extends MachineOutput<OUTPUT>, RECIPE extends MachineRecipe<INPUT, OUTPUT, RECIPE>> implements OOIMekRecipe {

    @Shadow
    @Final
    private HashMap<INPUT, RECIPE> recipes;

    @Unique
    @Override
    public void ooi$refresh(){
        HashMap<INPUT, RECIPE> map = new HashMap<>(recipes);
        recipes.clear();
        recipes.putAll(map);
    }

    @Unique
    @Override
    public void ooi$clear(){
        this.recipes.clear();
    }
}
