package mod.wittywhiscash.blockvariation;

import net.minecraft.block.Blocks;

import java.util.HashMap;

// A block list class.
public class BlockList {

    // The block mask. Will be parsed into a block on load.
    private String mask;
    // A map of blocks and their chances of spawning. All values SHOULD match up to 100.
    private HashMap<String, Double> blockChanceMap = new HashMap<String, Double>();

    public String getMask() {
        return mask;
    }

    public HashMap<String, Double> getBlockChanceMap() {
        return blockChanceMap;
    }

    public class DefaultBlockList extends BlockList {

        public DefaultBlockList() {
            mask = String.format("%s:%2s", Blocks.STONE.getRegistryName().getNamespace(), Blocks.STONE.getRegistryName().getPath());
            blockChanceMap.put(String.format("%s:%2s", Blocks.STONE.getRegistryName().getNamespace(), Blocks.STONE.getRegistryName().getPath()), 0.5);
            blockChanceMap.put(String.format("%s:%2s", Blocks.MOSSY_COBBLESTONE.getRegistryName().getNamespace(), Blocks.MOSSY_COBBLESTONE.getRegistryName().getPath()), 0.25);
            blockChanceMap.put(String.format("%s:%2s", Blocks.COBBLESTONE.getRegistryName().getNamespace(), Blocks.COBBLESTONE.getRegistryName().getPath()), 0.25);

        }
    }

}
