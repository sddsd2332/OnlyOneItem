package com.circulation.only_one_item.handler;

import com.circulation.only_one_item.util.OOIMekRecipe;
import mekanism.common.recipe.RecipeHandler;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;

import java.lang.reflect.InvocationTargetException;

public class InitHandler {

    public static void allPreInit(){
        MatchItemHandler.preItemStackInit();
        MatchFluidHandler.preFluidStackInit();

        if (Loader.isModLoaded("mekanism"))MekInit();
    }

    @Optional.Method(modid = "mekanism")
    public static void MekInit(){
        RecipeHandler.Recipe.values().forEach(recipe -> ((OOIMekRecipe) recipe).ooi$clear());

        try {
            Class<?> clazz = Class.forName("mekanism.common.MekanismRecipe");
            clazz.getMethod("addRecipes")
                    .invoke(null);
            Class<?> clazz0 = Class.forName("mekanism.generators.common.MekanismGenerators");
            clazz0.getMethod("registerRecipes", RegistryEvent.Register.class)
                    .invoke(null, (Object) null);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                 InvocationTargetException ignored) {
            try {
                Class<?> clazz = Class.forName("mekanism.common.Mekanism");
                clazz.getMethod("addRecipes")
                        .invoke(null);
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                     InvocationTargetException ignored0) {

            }
        }
    }
}
