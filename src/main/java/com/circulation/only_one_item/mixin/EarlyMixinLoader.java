package com.circulation.only_one_item.mixin;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zone.rong.mixinbooter.IEarlyMixinLoader;

import javax.annotation.Nullable;
import java.util.*;
import java.util.List;

public class EarlyMixinLoader implements IFMLLoadingPlugin, IEarlyMixinLoader {
    public static final Logger LOG = LogManager.getLogger("OOI");
    public static final String LOG_PREFIX = "[OOI]" + ' ';
    
    @Override
    public List<String> getMixinConfigs() {
        return Collections.singletonList("mixins.only_one_item.json");
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(final Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
