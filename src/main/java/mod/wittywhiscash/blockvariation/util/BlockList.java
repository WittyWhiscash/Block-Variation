package mod.wittywhiscash.blockvariation.util;

import javax.annotation.Nullable;
import java.util.HashMap;

// A block list class.
public class BlockList {

    // The block mask. Will be parsed into a block on load.
    private String mask;

    public String getMask() {
        return mask;
    }

    // An enum to define the way the random blocks will generate.
    public enum RANDOM_TYPE {
        RANDOM,
        SIMPLEX
    }

    private  BlockList.RANDOM_TYPE randomType;

    public BlockList.RANDOM_TYPE getRandomType() {
        return randomType;
    }

    // A map of blocks and their chances of spawning. All values SHOULD match up to 100.
    private HashMap<String, Double> blockChanceMap = new HashMap<String, Double>();

    public HashMap<String, Double> getBlockChanceMap() {
        return blockChanceMap;
    }

    // A pair of integers that represent the minimum and maximum depth this file operates on.
    private Integer[] depthRange = new Integer[2];

    public Integer[] getDepthRange() {
        return depthRange;
    }

    // A nullable value that defines the scale of the simplex. Larger number means bigger blobs.
    private Integer scale;

    public Integer getScale() {
        return scale;
    }

    // Constructor for JSON parsing.
    public BlockList() { }

    // Constructor for the BlockListFactory class. Used in the create function to return a new blocklist.
    public BlockList(String mask, BlockList.RANDOM_TYPE randomType, HashMap<String, Double> chanceMap, Integer[] depthRange, @Nullable Integer scale ) {
        this.mask = mask;
        this.randomType = randomType;
        this.blockChanceMap = chanceMap;
        this.depthRange = depthRange;
        this.scale = scale;
    }

    // BlockListFactory class: creates a BlockList using the pattern builder structure. Useful for creating example JSONs.
    public static class BlockListFactory {

        private String mask;
        private BlockList.RANDOM_TYPE randomType;
        private HashMap<String, Double> chanceMap = new HashMap<>();

        private Integer scale = 4;
        private Integer[] depthRange;

        public BlockListFactory mask(String mask) {
            this.mask = mask;
            return this;
        }

        public BlockListFactory randomType(BlockList.RANDOM_TYPE randomType) {
            this.randomType = randomType;
            return this;
        }

        public BlockListFactory chanceMap(HashMap<String, Double> chanceMap) {
            this.chanceMap = chanceMap;
            return this;
        }

        public BlockListFactory depthRange(Integer[] depthRange) {
            this.depthRange = depthRange;
            return this;
        }

        public BlockListFactory scale(Integer scale) {
            this.scale = scale;
            return this;
        }

        public BlockList create() {
            return new BlockList(mask, randomType, chanceMap, depthRange, scale);
        }



    }
}
