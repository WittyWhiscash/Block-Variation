package mod.wittywhiscash.blockvariation.util;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Path;
import java.util.*;

public class Util {

    // Get the block instance from a string.
    public static Block parseBlockFromString(String string) {
        return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(string));
    }

    // Sort the values in a hashmap from biggest to smallest. Return a linked hashmap with those values sorted.
    public static <K, V extends Comparable<? super V>> HashMap<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());

        HashMap<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
}
