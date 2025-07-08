package by.sakeplays.cycle_of_life.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class COLEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE,
            "cycle_of_life");

    public static final Supplier<EntityType<HitboxEntity>> HITBOX = ENTITY_TYPES.register
            ("hitbox", () -> EntityType.Builder.of(HitboxEntity::new, MobCategory.MISC)
                    .sized(0.75f, 0.75f)
                    .updateInterval(1)
                    .clientTrackingRange(64).build("hitbox"));


    public static final Supplier<EntityType<Pachycephalosaurus>> PACHYCEPHALOSAURUS = ENTITY_TYPES.register
            ("pachycephalosaurus", () -> EntityType.Builder.of(Pachycephalosaurus::new, MobCategory.MISC)
            .sized(0.75f, 0.75f).build("pachycephalosaurus"));

    public static final Supplier<EntityType<Deinonychus>> DEINONYCHUS = ENTITY_TYPES.register
            ("deinonychus", () -> EntityType.Builder.of(Deinonychus::new, MobCategory.MISC)
                    .sized(0.75f, 0.75f).build("deinonychus"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
