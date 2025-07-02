package by.sakeplays.cycle_of_life.common.data;

import by.sakeplays.cycle_of_life.CycleOfLife;
import com.mojang.serialization.Codec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.ArrayList;
import java.util.function.Supplier;

public class DataAttachments {

    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, CycleOfLife.MODID);

    public static final Supplier<AttachmentType<DinoData>> DINO_DATA = ATTACHMENT_TYPES.register(
            "dino_data", () -> AttachmentType.serializable(() -> new DinoData(0, 1f, 1f,
                    1f, 0f, 1f, false, false, 0, 0.25f, 10f)).build());

    public static final Supplier<AttachmentType<ArrayList<Float>>> Y_HISTORY = ATTACHMENT_TYPES.register(
            "y_history", () -> AttachmentType.builder(() -> new ArrayList<Float>()).build());

    public static final Supplier<AttachmentType<ArrayList<Float>>> TURN_HISTORY = ATTACHMENT_TYPES.register(
            "turn_history", () -> AttachmentType.builder(() -> new ArrayList<Float>()).build());

    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }


}
