package com.circulation.only_one_item.mixin;

import net.minecraftforge.fml.common.Loader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

@SuppressWarnings({"unused", "SameParameterValue"})
public class LateMixinLoader implements ILateMixinLoader {

    public static final Logger LOG = LogManager.getLogger("OOI_PRE");
    public static final String LOG_PREFIX = "[OOI]" + ' ';
    private static final Map<String, BooleanSupplier> MIXIN_CONFIGS = new LinkedHashMap<>();

    static {
        addModdedMixinCFG("mixins.only_one_item.crt.json","crafttweaker");
        addModdedMixinCFG("mixins.only_one_item.mek.json","mekanism");
    }

    @Override
    public List<String> getMixinConfigs() {
        return new ArrayList<>(MIXIN_CONFIGS.keySet());
    }

    @Override
    public boolean shouldMixinConfigQueue(final String mixinConfig) {
        BooleanSupplier supplier = MIXIN_CONFIGS.get(mixinConfig);
        if (supplier == null) {
            LOG.warn(LOG_PREFIX + "Mixin config {} is not found in config map! It will never be loaded.", mixinConfig);
            return false;
        }
        return supplier.getAsBoolean();
    }

    private static boolean modLoaded(final String modID) {
        return Loader.isModLoaded(modID);
    }

    private static void addModdedMixinCFG(final String mixinConfig, final String modID) {
        MIXIN_CONFIGS.put(mixinConfig, () -> modLoaded(modID));
    }

    private static void addModdedMixinCFG(final String mixinConfig, final String modID, final String... modIDs) {
        MIXIN_CONFIGS.put(mixinConfig, () -> modLoaded(modID) && Arrays.stream(modIDs).allMatch(Loader::isModLoaded));
    }

    private static void addMixinCFG(final String mixinConfig) {
        MIXIN_CONFIGS.put(mixinConfig, () -> true);
    }

    private static void addMixinCFG(final String mixinConfig, final BooleanSupplier conditions) {
        MIXIN_CONFIGS.put(mixinConfig, conditions);
    }
}
