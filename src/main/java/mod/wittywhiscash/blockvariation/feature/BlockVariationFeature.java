package mod.wittywhiscash.blockvariation.feature;

import com.mojang.datafixers.Dynamic;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.HashMap;
import java.util.Random;
import java.util.function.Function;

public class BlockVariationFeature extends Feature<NoFeatureConfig> {

    HashMap<Block, HashMap<Block, Double>> blockVariationMap;

    public BlockVariationFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> configFactoryIn, HashMap<Block, HashMap<Block, Double>> list) {
        super(configFactoryIn);
        blockVariationMap = list;
    }

    // Loop over every block on generation, checking if it is part of one of the masks specified.
    // If so, check the chances in the block variation map for that mask. If the chances match,
    // replace the block with the specified block.
    @Override
    public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config) {
        BlockPos.Mutable blockPos$mutable = new BlockPos.Mutable();
        int startX = pos.getX();
        int startZ = pos.getZ();
        for (int x = 0; x < 16; ++x) {
            for (int y = 0; y < worldIn.getMaxHeight(); ++y) {
                for (int z = 0; z < 16; ++z) {
                    blockPos$mutable.setPos(startX + x, y, startZ + z);
                    Block existingBlock = worldIn.getBlockState(blockPos$mutable).getBlock();
                    for (Block maskBlock : blockVariationMap.keySet()) {
                        if (existingBlock == maskBlock) {
                            HashMap<Block, Double> variationMap = blockVariationMap.get(maskBlock);
                            for (Block block : variationMap.keySet()) {
                                if (rand.nextDouble() <= variationMap.get(block)) {
                                    worldIn.setBlockState(blockPos$mutable, block.getDefaultState(), 3);
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
