package com.circulation.only_one_item;

import com.circulation.only_one_item.util.ItemConversionTarget;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraftforge.fml.common.Loader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashSet;

public class OOIConfig {

    public static final HashSet<ItemConversionTarget> items = new HashSet<>();
    public static final HashSet<ItemConversionTarget.MatchItem> blackList = new HashSet<>();

    public static void readConfig() {
        var configPath = Loader.instance().getConfigDir().toPath();
        var ooiPath = configPath.resolve("ooi.json");
        var blackPath = configPath.resolve("ooi_black_list.json");

        var config = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

        if (Files.exists(ooiPath)){
            try {
                items.addAll(config.fromJson(new String(Files.readAllBytes(ooiPath)), (new TypeToken<HashSet<ItemConversionTarget>>() {}).getType()));
            } catch (IOException ignored) {

            }
        } else {
            ClassLoader classLoader = OOIConfig.class.getClassLoader();

            try (InputStream inputStream = classLoader.getResourceAsStream("ooi.json")) {
                if (inputStream != null) {
                    Files.copy(inputStream, ooiPath);
                }
            } catch (IOException ignored) {

            }
        }

        if (Files.exists(blackPath)){
            try {
                blackList.addAll(config.fromJson(new String(Files.readAllBytes(blackPath)), (new TypeToken<HashSet<ItemConversionTarget.MatchItem>>() {}).getType()));
            } catch (IOException ignored) {

            }
        } else {
            blackList.add(ItemConversionTarget.MatchItem.getInstance("minecraft:gold_ingot",0));
            try {
                Files.write(blackPath, config.toJson(blackList).getBytes());
            } catch (IOException ignored){

            }
        }
    }
}
