package com.circulation.only_one_item.handler;

import com.circulation.only_one_item.util.OOIMekRecipe;
import mekanism.common.recipe.RecipeHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

public class InitHandler {

    public static void allPreInit(){
        MatchItemHandler.preItemStackInit();
        MatchFluidHandler.preFluidStackInit();

        if (Loader.isModLoaded("mekanism"))MekInit();
    }

    @Optional.Method(modid = "mekanism")
    public static void MekInit(){
        RecipeHandler.Recipe.values().forEach(recipe -> ((OOIMekRecipe) recipe).ooi$refresh());
    }

    @SubscribeEvent
    public void onOreRegister(OreDictionary.OreRegisterEvent event) {
        MatchItemHandler.onOreRegister(event);
    }
}
