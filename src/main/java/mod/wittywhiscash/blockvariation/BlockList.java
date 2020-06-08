package mod.wittywhiscash.blockvariation;

import net.minecraft.block.Blocks;

import java.util.HashMap;

// A block list class.
public class BlockList {

    // The block mask. Will be parsed into a block on load.
    private String mask;
    private static String defaultMask = Blocks.STONE.getRegistryName().toString();

    // A map of blocks and their chances of spawning. All values SHOULD match up to 100.
    private HashMap<String, Double> blockChanceMap = new HashMap<String, Double>();
    private static HashMap<String, Double> defaultChanceMap = new HashMap<String, Double>();

    public String getMask() {
        return mask;
    }

    public HashMap<String, Double> getBlockChanceMap() {
        return blockChanceMap;
    }

    public BlockList() { }

    public BlockList(String mask, HashMap<String, Double> blockChanceMap) {
        this.mask = mask;
        this.blockChanceMap = blockChanceMap;
    }

    public BlockList createDefaultBlockList() {
        defaultChanceMap.put(Blocks.STONE.getRegistryName().toString(), 0.5);
        defaultChanceMap.put(Blocks.COBBLESTONE.getRegistryName().toString(), 0.25);
        defaultChanceMap.put(Blocks.MOSSY_COBBLESTONE.getRegistryName().toString(), 0.25);
        return new BlockList(defaultMask, defaultChanceMap);
    }

}
