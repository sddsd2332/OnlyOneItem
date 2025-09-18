package com.circulation.only_one_item;

import com.circulation.only_one_item.handler.InitHandler;
import com.circulation.only_one_item.handler.MatchItemHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@Mod(modid = OnlyOneItem.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION,
        dependencies = "required-after:mixinbooter@[8.0,);"
)
public class OnlyOneItem {
    public static final String MOD_ID = Tags.MOD_ID;

    public static final SimpleNetworkWrapper NET_CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID);

    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);

    @Mod.Instance(MOD_ID)
    public static OnlyOneItem instance = null;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new InitHandler());
        if (Loader.isModLoaded("unidict")) {
            LOGGER.warn("OnlyOneItem and UniDict are incompatible, which may cause some errors to occur!");
        }
        try {
            OOIConfig.readConfig();
        } catch (IOException ignored) {

        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        if (!Loader.isModLoaded("crafttweaker")) {
            InitHandler.allPreInit();
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        if (!Loader.isModLoaded("unidict")) {
            MatchItemHandler.clearRecipe();
        }
    }

    @Mod.EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        MatchItemHandler.postODProcess();
    }

}