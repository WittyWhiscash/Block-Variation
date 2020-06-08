package mod.wittywhiscash.blockvariation;

import com.google.common.util.concurrent.AtomicDouble;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import mod.wittywhiscash.blockvariation.feature.BlockVariationFeature;
import net.minecraft.block.Block;
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
import java.util.HashMap;

@Mod("blockvariation")
public class BlockVariation {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "blockvariation";
    public static HashMap<Block, HashMap<Block, Double>> list = new HashMap<>();

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

        // Define path for the example file.
        Path exampleJson = Paths.get(configurationPath.toString(), "example.json");

        // If the file does not exist, create it.
        BufferedWriter writer = null;
        if (!Files.exists(exampleJson)) {
            try {
                writer = Files.newBufferedWriter(exampleJson);
                gson.toJson(new BlockList().new DefaultBlockList(), writer);
                writer.close();
            } catch (IOException exception) {
                LOGGER.error(exception);
            }
        }

        try {
            // Find all files that the user has put in the configuration directory and parse them for feature registration, excluding the example.
            File[] fileList = configurationPath.toFile().listFiles(file -> !file.equals(exampleJson.toFile()));
            for (File file : fileList) {
                BufferedReader reader = Files.newBufferedReader(file.toPath());
                BlockList blockList = gson.fromJson(reader, BlockList.class);
                HashMap<Block, Double> parsedBlockChanceMap = new HashMap<>();
                Block maskBlock = Util.parseBlockFromString(blockList.getMask());
                for (String key : blockList.getBlockChanceMap().keySet()) {
                    Block variationBlock = Util.parseBlockFromString(key);
                    parsedBlockChanceMap.put(variationBlock, blockList.getBlockChanceMap().get(key));
                }
                list.put(maskBlock, parsedBlockChanceMap);
                LOGGER.debug(String.format("Loaded in map for mask %s with block list %2s", blockList.getMask(), blockList.getBlockChanceMap().toString()));
                // Verify that chances add up to 100, and warn the user if they don't add up correctly.
                AtomicDouble chance = new AtomicDouble();
                blockList.getBlockChanceMap().forEach((key, keyChance) -> chance.addAndGet(keyChance));
                if (chance.doubleValue() > 1.0) {
                    LOGGER.warn(String.format("All the chances don't add up to 100 percent in map with mask %s and block list %2s! Unexpected chance behaviour may occur.", blockList.getMask(), blockList.getBlockChanceMap().toString()));
                }
            }
        } catch (IOException exception) {
            LOGGER.error(exception);
        }

        // Register the worldgen feature that makes the magic happen.
        ForgeRegistries.BIOMES.forEach(biome -> biome.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, new BlockVariationFeature(NoFeatureConfig::deserialize, list).withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.NOPE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG))));

    }
}