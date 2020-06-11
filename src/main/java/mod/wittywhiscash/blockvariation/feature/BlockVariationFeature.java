package mod.wittywhiscash.blockvariation.feature;

import com.google.common.util.concurrent.AtomicDouble;
import com.mojang.datafixers.Dynamic;
import com.sun.jna.platform.unix.X11;
import mod.wittywhiscash.blockvariation.util.*;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class BlockVariationFeature extends Feature<NoFeatureConfig> {

    List<BlockList> blockLists;

    public BlockVariationFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> configFactoryIn, List<BlockList> list) {
        super(configFactoryIn);
        blockLists = list;
    }

    @Override
    public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config) {
        // Get a mutable block pos that we can iterate over.
        BlockPos.Mutable blockPos$mutable = new BlockPos.Mutable();
        // Get a simplex instance based on the generator seed.
        SimplexNoise simplexNoise = new SimplexNoise(generator.getSeed());
        int startX = pos.getX();
        int startZ = pos.getZ();
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < worldIn.getMaxHeight(); y++) {
                for (int z = 0; z < 16; z++) {
                    // Set the position to the proper block.
                    blockPos$mutable.setPos(startX + x, y, startZ + z);
                    Block existingBlock = worldIn.getBlockState(blockPos$mutable).getBlock();
                    // Start iterating over every block list loaded in.
                    for (int index = 0; index < blockLists.size(); index++) {
                        BlockList list = blockLists.get(index);
                        Integer[] depthRange = list.getDepthRange();
                        Block maskBlock = Util.parseBlockFromString(list.getMask());
                        // Check if the Y level lands within the range of the currently loaded list.
                        if (y >= depthRange[0] && y <= depthRange[1]) {
                            // Check if the existing block is the block defined as the mask of the currently loaded list.
                            if (maskBlock == existingBlock) {
                                BlockList.RANDOM_TYPE randomType = list.getRandomType();
                                // Parse the string/double map into a block/double map for proper placement.
                                HashMap<Block, Double> variationMap = new HashMap<>();
                                for (String key : list.getBlockChanceMap().keySet()) {
                                    Block block = Util.parseBlockFromString(key);
                                    variationMap.put(block, list.getBlockChanceMap().get(key));
                                }
                                // Sort the values from highest value to lowest value.
                                variationMap = Util.sortByValue(variationMap);
                                if (randomType == BlockList.RANDOM_TYPE.SIMPLEX) {
                                    int scale = list.getScale();
                                    // Get the simplex number based on the position and correct the number so it sits between 0 and 1.
                                    Double noise = (simplexNoise.noise(((double) blockPos$mutable.getX()) / scale, ((double) blockPos$mutable.getY()) / scale, ((double) blockPos$mutable.getZ()) / scale) + 1) / 2;
                                    // Variables to track the chance of the random blocks.
                                    AtomicDouble origChance = new AtomicDouble(0);
                                    AtomicDouble newChance = new AtomicDouble(0);
                                    // For each block and double, check if the chance number variables are the same. If so, add the double so they are different.
                                    // Then check if the noise value is within the range, therefor allowing said block to spawn.
                                    // Finally, add the new chance and reiterate. The value will match one of the the blocks and their chance to spawn.
                                    // Lastly, reset the variables for the next masked block.
                                    variationMap.forEach((block, aDouble) -> {
                                        if (origChance.doubleValue() == newChance.doubleValue()) {
                                            newChance.addAndGet(aDouble);
                                        }
                                        if (noise >= origChance.doubleValue() && noise < newChance.doubleValue()) {
                                            worldIn.setBlockState(blockPos$mutable, block.getDefaultState(), 3);
                                        }
                                        origChance.addAndGet(newChance.doubleValue());
                                    });
                                    origChance.set(0);
                                    newChance.set(0);
                                }
                                if (randomType == BlockList.RANDOM_TYPE.RANDOM) {
                                    // Get the chance for a block from the random instance.
                                    Double chance = rand.nextDouble();
                                    // Variables to track the chance of the random blocks.
                                    AtomicDouble origChance = new AtomicDouble(0);
                                    AtomicDouble newChance = new AtomicDouble(0);
                                    // For each block and double, check if the chance number variables are the same. If so, add the double so they are different.
                                    // Then check if the chance value is within the range, therefor allowing said block to spawn.
                                    // Finally, add the new chance and reiterate. The value will match one of the the blocks and their chance to spawn.
                                    // Lastly, reset the variables for the next masked block.
                                    variationMap.forEach((block, aDouble) -> {
                                        if (origChance.doubleValue() == newChance.doubleValue()) {
                                            newChance.addAndGet(aDouble);
                                        }
                                        if (chance >= origChance.doubleValue() && chance < newChance.doubleValue()) {
                                            worldIn.setBlockState(blockPos$mutable, block.getDefaultState(), 3);
                                        }
                                        origChance.addAndGet(newChance.doubleValue());
                                    });
                                    origChance.set(0);
                                    newChance.set(0);
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}
