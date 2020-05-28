package mod.wittywhiscash.blockvariation;

import blue.endless.jankson.Comment;
import net.minecraft.block.Blocks;

import java.util.HashMap;

public class DefaultBlockList {

    @Comment("The block (namespaced string, eg: minecraft:stone) you want to replace with your block variety combination")
    String mask;

    @Comment("A map of blocks (namespaced strings, eg: minecraft:cobblestone) and their chance of appearing, in decimal format. Chances should add up to 100, or there might be wonky behaviour.")
    HashMap<String, Double> blockChanceMap = new HashMap<String, Double>();

    public DefaultBlockList() {
        mask = String.format("%s:%2s", Blocks.STONE.getRegistryName().getNamespace(), Blocks.STONE.getRegistryName().getPath());
        blockChanceMap.put(String.format("%s:%2s", Blocks.STONE.getRegistryName().getNamespace(), Blocks.STONE.getRegistryName().getPath()), 0.5);
        blockChanceMap.put(String.format("%s:%2s", Blocks.MOSSY_COBBLESTONE.getRegistryName().getNamespace(), Blocks.MOSSY_COBBLESTONE.getRegistryName().getPath()), 0.25);
        blockChanceMap.put(String.format("%s:%2s", Blocks.COBBLESTONE.getRegistryName().getNamespace(), Blocks.COBBLESTONE.getRegistryName().getPath()), 0.25);
    }

}
