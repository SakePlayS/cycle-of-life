package by.sakeplays.cycle_of_life.block;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.block.herbi_food.CrimsonweedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CycleOfLife.MODID);

    public static final DeferredBlock<Block> DEINONYCHUS_NEST = BLOCKS.registerBlock("deinonychus_nest", DeinonychusNestBlock::new, BlockBehaviour.Properties.of().noOcclusion());

    public static final DeferredBlock<Block> CRIMSONWEED = BLOCKS.registerBlock("crimsonweed", CrimsonweedBlock::new, BlockBehaviour.Properties.of().noOcclusion().noCollission().sound(SoundType.AZALEA).instabreak());

    public static void register(IEventBus modBus) {
        BLOCKS.register(modBus);
    }
}
