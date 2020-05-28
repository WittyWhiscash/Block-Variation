package mod.wittywhiscash.blockvariation;

import blue.endless.jankson.Comment;

import java.util.HashMap;

// A block list class.
public class BlockList {

    // The block mask. Will be parsed into a block on load.
    private String mask;
    // A map of blocks and their chances of spawning. All values SHOULD match up to 100.
    private HashMap<String, Double> blockChanceMap;

    public String getMask() {
        return mask;
    }

    public HashMap<String, Double> getBlockChanceMap() {
        return blockChanceMap;
    }

}
