package by.sakeplays.cycle_of_life.block;

import by.sakeplays.cycle_of_life.CycleOfLife;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CycleOfLife.MODID);

    public static final Supplier<BlockEntityType<DeinonychusNestBlockEntity>> DEINONYCHUS_NEST_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
            "deinonychus_nest_block_entity",
            () -> BlockEntityType.Builder.of(DeinonychusNestBlockEntity::new, ModBlocks.DEINONYCHUS_NEST.get())
                    .build(null));

    public static void register(IEventBus modBus) {
        BLOCK_ENTITY_TYPES.register(modBus);
    }
}
