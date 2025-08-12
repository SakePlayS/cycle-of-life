package by.sakeplays.cycle_of_life.block;

import by.sakeplays.cycle_of_life.CycleOfLife;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CycleOfLife.MODID);

    public static final DeferredBlock<Block> DEINONYCHUS_NEST = BLOCKS.registerBlock("deinonychus_nest", DeinonychusNestBlock::new, BlockBehaviour.Properties.of());

    public static void register(IEventBus modBus) {
        BLOCKS.register(modBus);
    }
}
