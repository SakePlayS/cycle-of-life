package by.sakeplays.cycle_of_life.common;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.SelectedDinosaur;
import com.mojang.serialization.Codec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class DataAttachments {

    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, CycleOfLife.MODID);

    public static final Supplier<AttachmentType<SelectedDinosaur>> SELECTED_DINOSAUR = ATTACHMENT_TYPES.register(
            "selected_dinosaur", () -> AttachmentType.serializable(() -> new SelectedDinosaur(0)).build());

    public static final Supplier<AttachmentType<Float>> STAMINA = ATTACHMENT_TYPES.register(
            "stamina", () -> AttachmentType.builder(() -> 1f).serialize(Codec.FLOAT).build());

    public static final Supplier<AttachmentType<Float>> TURN_DEGREE = ATTACHMENT_TYPES.register(
            "turn_degree", () -> AttachmentType.builder(() -> 0f).serialize(Codec.FLOAT).build());

    public static final Supplier<AttachmentType<Float>> DINO_HEALTH = ATTACHMENT_TYPES.register(
            "dino_health", () -> AttachmentType.builder(() -> 1f).serialize(Codec.FLOAT).build());

    public static final Supplier<AttachmentType<Float>> DINO_WATER_LEVEL = ATTACHMENT_TYPES.register(
            "dino_water_level", () -> AttachmentType.builder(() -> 1f).serialize(Codec.FLOAT).build());

    public static final Supplier<AttachmentType<Float>> DINO_FOOD_LEVEL = ATTACHMENT_TYPES.register(
            "dino_food_level", () -> AttachmentType.builder(() -> 1f).serialize(Codec.FLOAT).build());


    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }


}
