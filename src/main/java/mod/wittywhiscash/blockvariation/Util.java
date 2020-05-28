package mod.wittywhiscash.blockvariation;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class Util {

    // Get the block instance from a string.
    public static Block parseBlockFromString(String string) {
        return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(string));
    }

}
