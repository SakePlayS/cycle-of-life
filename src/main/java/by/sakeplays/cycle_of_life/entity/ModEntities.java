package by.sakeplays.cycle_of_life.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE,
            "cycle_of_life");

    public static final Supplier<EntityType<HitboxEntity>> HITBOX = ENTITY_TYPES.register
            ("hitbox", () -> EntityType.Builder.of(HitboxEntity::new, MobCategory.MISC)
                    .sized(0.75f, 0.75f)
                    .updateInterval(1)
                    .clientTrackingRange(64)
                    .setShouldReceiveVelocityUpdates(true).build("hitbox"));


    public static final Supplier<EntityType<Pachycephalosaurus>> PACHYCEPHALOSAURUS = ENTITY_TYPES.register
            ("pachycephalosaurus", () -> EntityType.Builder.of(Pachycephalosaurus::new, MobCategory.MISC)
            .sized(0.75f, 0.75f).build("pachycephalosaurus"));

    public static final Supplier<EntityType<MeatChunkEntity>> MEAT_CHUNK = ENTITY_TYPES.register
            ("meat_chunk", () -> EntityType.Builder.of(MeatChunkEntity::new, MobCategory.MISC)
                    .sized(0.2f, 0.2f).build("meat_chunk"));

    public static final Supplier<EntityType<Deinonychus>> DEINONYCHUS = ENTITY_TYPES.register
            ("deinonychus", () -> EntityType.Builder.of(Deinonychus::new, MobCategory.MISC)
                    .sized(0.75f, 0.75f).build("deinonychus"));

    public static final Supplier<EntityType<Pteranodon>> PTERANODON = ENTITY_TYPES.register
            ("pteranodon", () -> EntityType.Builder.of(Pteranodon::new, MobCategory.MISC)
                    .sized(0.75f, 0.75f).build("pteranodon"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
