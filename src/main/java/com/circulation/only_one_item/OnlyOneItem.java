package com.circulation.only_one_item;

import com.circulation.only_one_item.handler.InitHandler;
import com.circulation.only_one_item.handler.MatchFluidHandler;
import com.circulation.only_one_item.handler.MatchItemHandler;
import net.minecraft.launchwrapper.LogWrapper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
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
    public static final String MOD_ID = "only_one_item";

    public static final SimpleNetworkWrapper NET_CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID);

    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);

    @Mod.Instance(MOD_ID)
    public static OnlyOneItem instance = null;
    public static LogWrapper logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(MatchItemHandler.INSTANCE);
        try {
            OOIConfig.readConfig();
        } catch (IOException ignored) {

        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MatchItemHandler.InitTarget();
        MatchFluidHandler.Init();
        if (!Loader.isModLoaded("crafttweaker")) {
            InitHandler.allPreInit();
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        MatchItemHandler.clearRecipe();
    }

}
