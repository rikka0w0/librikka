package rikka.librikka;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;

public class ForgeConfigHelper {
    public static BooleanValue boolVal(ForgeConfigSpec.Builder builder, String modID, String key, boolean defaultVal, String comment) {
        return builder
                .comment(comment + "\r\nDefault: "+ defaultVal)
                .translation(modID + ".config." + key.toLowerCase().replace(' ', '_'))
                .define(key, defaultVal);
    }

    public static ForgeConfigSpec.IntValue intVal(ForgeConfigSpec.Builder builder, String modID, String key, int defaultVal, String comment) {
        return intVal(builder, modID, key, defaultVal, Integer.MIN_VALUE, Integer.MAX_VALUE, comment);
    }

    public static ForgeConfigSpec.IntValue intVal(ForgeConfigSpec.Builder builder, String modID, String key, int defaultVal, int min, int max, String comment) {
        return builder
                .comment(comment + "\r\nDefault: "+ defaultVal)
                .translation(modID + ".config." + key.toLowerCase().replace(' ', '_'))
                .defineInRange(key, defaultVal, min, max);
    }
    
    public static ForgeConfigSpec.DoubleValue doubleVal(ForgeConfigSpec.Builder builder, String modID, String key, double defaultVal, double min, double max, String comment) {
        return builder
                .comment(comment + "\r\nDefault: "+ defaultVal)
                .translation(modID + ".config." + key.toLowerCase().replace(' ', '_'))
                .defineInRange(key, defaultVal, min, max);
    }

    public static ForgeConfigSpec.ConfigValue<String> stringVal(ForgeConfigSpec.Builder builder, String modID, String key, String defaultVal, String comment) {
        return builder
                .comment(comment + "\r\nDefault: "+ defaultVal)
                .translation(modID + ".config." + key.toLowerCase().replace(' ', '_'))
                .define(key, defaultVal);
    }
}
