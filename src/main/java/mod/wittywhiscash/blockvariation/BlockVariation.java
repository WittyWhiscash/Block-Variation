package mod.wittywhiscash.blockvariation;

import com.google.common.util.concurrent.AtomicDouble;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import mod.wittywhiscash.blockvariation.feature.BlockVariationFeature;
import mod.wittywhiscash.blockvariation.util.*;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Mod("blockvariation")
public class BlockVariation {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "blockvariation";
    private static List<BlockList> list = new ArrayList<>();

    public BlockVariation() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {

        // Initialize JSON parser and get configuration file paths.
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File configurationDirectory = FMLPaths.CONFIGDIR.get().toFile();
        Path configurationPath = Paths.get(configurationDirectory.toString(), MOD_ID);

        // If the directory does not exist, create it.
        if (!Files.exists(configurationPath)) {
            try {
                Files.createDirectory(configurationPath);
            } catch (IOException exception) {
                LOGGER.error(exception);
            }
        }

        // Define path for the example files.
        Path exampleJson = Paths.get(configurationPath.toString(), "example.json");
        Path exampleSimplexJson = Paths.get(configurationPath.toString(), "example-simplex.json");

        // If the example files do not exist, create them.
        BufferedWriter writer = null;
        if (!Files.exists(exampleJson)) {
            try {
                writer = Files.newBufferedWriter(exampleJson);
                gson.toJson(new BlockList.BlockListFactory()
                        .mask(Blocks.STONE.getRegistryName().toString())
                        .randomType(BlockList.RANDOM_TYPE.RANDOM)
                        .chanceMap(new HashMap<String, Double>() {
                            {
                                put(Blocks.STONE.getRegistryName().toString(), 0.5);
                                put(Blocks.COBBLESTONE.getRegistryName().toString(), 0.25);
                                put(Blocks.MOSSY_COBBLESTONE.getRegistryName().toString(), 0.25);
                            }
                        })
                        .depthRange(new Integer[] {0, 255})
                        .scale(null)
                        .create(), writer);
                writer.close();
            } catch (IOException exception) {
                LOGGER.error(exception);
            }
        }
        if (!Files.exists(exampleSimplexJson)) {
            try {
                writer = Files.newBufferedWriter(exampleSimplexJson);
                gson.toJson(new BlockList.BlockListFactory()
                        .mask(Blocks.STONE.getRegistryName().toString())
                        .randomType(BlockList.RANDOM_TYPE.SIMPLEX)
                        .chanceMap(new HashMap<String, Double>() {
                            {
                                put(Blocks.STONE.getRegistryName().toString(), 0.5);
                                put(Blocks.COBBLESTONE.getRegistryName().toString(), 0.25);
                                put(Blocks.MOSSY_COBBLESTONE.getRegistryName().toString(), 0.25);
                            }
                        })
                        .depthRange(new Integer[] {0, 50})
                        .scale(4)
                        .create(), writer);
                writer.close();
            } catch (IOException exception) {
                LOGGER.error(exception);
            }
        }

        try {
            // Find all files that the user has put in the configuration directory and parse them for feature registration, excluding the examples.
            List<File> fileList = Arrays.stream(configurationPath.toFile().listFiles())
                    .filter(file -> !file.toPath().equals(exampleJson) && !file.toPath().equals(exampleSimplexJson))
                    .collect(Collectors.toList());
            for (File file : fileList) {
                BufferedReader reader = Files.newBufferedReader(file.toPath());
                BlockList blockList = gson.fromJson(reader, BlockList.class);
                list.add(blockList);
                if (blockList.getRandomType() == BlockList.RANDOM_TYPE.SIMPLEX) {
                    LOGGER.info(String.format("Loaded in file named %s with chance scheme %2s (scale of %3d) with a mask for %4s and block list %5s operating from Y%6d to Y%7d",
                            file.getName(),
                            blockList.getRandomType(),
                            blockList.getScale(),
                            blockList.getMask(),
                            blockList.getBlockChanceMap(),
                            blockList.getDepthRange()[0],
                            blockList.getDepthRange()[1]
                    ));
                }
                if (blockList.getRandomType() == BlockList.RANDOM_TYPE.RANDOM) {
                    LOGGER.info(String.format("Loaded in file named %s with chance scheme %2s with a mask for %3s and block list %4s operating from Y%5d to Y%6d",
                            file.getName(),
                            blockList.getRandomType(),
                            blockList.getMask(),
                            blockList.getBlockChanceMap().toString(),
                            blockList.getDepthRange()[0],
                            blockList.getDepthRange()[1]
                    ));
                }
                // Verify that chances add up to 100, and warn the user if they don't add up correctly.
                AtomicDouble chance = new AtomicDouble();
                blockList.getBlockChanceMap().forEach((key, keyChance) -> chance.addAndGet(keyChance));
                if (chance.doubleValue() > 1.0) {
                    LOGGER.warn(String.format("All the chances don't add up to 100 percent in file named %s! Unexpected chance behaviour may occur.", file.getName()));
                }
            }
        } catch (IOException exception) {
            LOGGER.error(exception);
        }

        // Register the worldgen feature that makes the magic happen.
        ForgeRegistries.BIOMES.forEach(biome -> biome.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, new BlockVariationFeature(NoFeatureConfig::deserialize, list).withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.NOPE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG))));

    }
}