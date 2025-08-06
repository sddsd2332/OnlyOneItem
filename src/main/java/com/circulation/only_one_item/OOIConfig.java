package com.circulation.only_one_item;

import com.circulation.only_one_item.conversion.FluidConversionTarget;
import com.circulation.only_one_item.conversion.ItemConversionTarget;
import com.circulation.only_one_item.emun.Type;
import com.circulation.only_one_item.util.BlackMatchItem;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Loader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OOIConfig {

    public static final List<ItemConversionTarget> items = new ArrayList<>();
    public static final List<FluidConversionTarget> fluids = new ArrayList<>();
    public static final Set<BlackMatchItem> blackList = new HashSet<>();

    public static void readConfig() throws IOException {
        var configPath = Loader.instance().getConfigDir().toPath().resolve("ooi");
        var ooiPath = configPath.resolve("ooi_item.json");
        var blackPath = configPath.resolve("ooi_item_black_list.json");
        var ooiFluidPath = configPath.resolve("ooi_fluid.json");

        Files.createDirectories(configPath);

        var config = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

        if (Files.exists(ooiPath)){
            items.addAll(config.fromJson(new String(Files.readAllBytes(ooiPath)), (new TypeToken<HashSet<ItemConversionTarget>>() {}).getType()));
        } else {
            ClassLoader classLoader = OOIConfig.class.getClassLoader();

            try (InputStream inputStream = classLoader.getResourceAsStream("ooi_item.json")) {
                if (inputStream != null) {
                    Files.copy(inputStream, ooiPath);
                }
            }
        }

        if (Files.exists(ooiFluidPath)){
            fluids.addAll(config.fromJson(new String(Files.readAllBytes(ooiFluidPath)), (new TypeToken<HashSet<FluidConversionTarget>>() {}).getType()));
        } else {
            fluids.add(new FluidConversionTarget(FluidRegistry.WATER.getName()).addMatchFluid(FluidRegistry.WATER.getName()));
            Files.write(ooiFluidPath, config.toJson(fluids).getBytes());
        }

        if (Files.exists(blackPath)){
            try {
                blackList.addAll(config.fromJson(new String(Files.readAllBytes(blackPath)), (new TypeToken<HashSet<BlackMatchItem>>() {}).getType()));
            } catch (IOException ignored) {

            }
        } else {
            blackList.add(BlackMatchItem.getInstance("minecraft:gold_ingot",0));
            blackList.add(BlackMatchItem.getInstance(Type.OreDict,"ingotGlod"));
            blackList.add(BlackMatchItem.getInstance(Type.ModID,"minecraft"));
            Files.write(blackPath, config.toJson(blackList).getBytes());
        }
    }
}
