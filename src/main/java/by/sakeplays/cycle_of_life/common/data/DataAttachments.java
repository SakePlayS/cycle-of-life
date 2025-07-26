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
                    1f, 0f, 1f, false, false, 0, 0.25f,
                    10f, false, 1 ,0,  false)).build());

    public static final Supplier<AttachmentType<SkinData>> SKIN_DATA = ATTACHMENT_TYPES.register(
            "skin_data", () -> AttachmentType.serializable(SkinData::new).build());

    public static final Supplier<AttachmentType<AdaptationData>> ADAPTATION_DATA = ATTACHMENT_TYPES.register(
            "adaptations_data", () -> AttachmentType.serializable(AdaptationData::new).build());

    public static final Supplier<AttachmentType<HitboxData>> HITBOX_DATA = ATTACHMENT_TYPES.register(
            "hitbox_data", () -> AttachmentType.serializable(() -> new HitboxData(
                    new Position(0, 0, 0),
                    new Position(0, 0, 0),
                    new Position(0, 0, 0),
                    new Position(0, 0, 0),
                    new Position(0, 0, 0),
                    new Position(0, 0, 0))).build());

    public static final Supplier<AttachmentType<ArrayList<Float>>> Y_HISTORY = ATTACHMENT_TYPES.register(
            "y_history", () -> AttachmentType.builder(() -> new ArrayList<Float>()).build());

    public static final Supplier<AttachmentType<ArrayList<Float>>> TURN_HISTORY = ATTACHMENT_TYPES.register(
            "turn_history", () -> AttachmentType.builder(() -> new ArrayList<Float>()).build());

    public static final Supplier<AttachmentType<Float>> PLAYER_ROTATION = ATTACHMENT_TYPES.register(
            "player_rotation", () -> AttachmentType.builder(() -> 0f).serialize(Codec.FLOAT).build());

    public static final Supplier<AttachmentType<Float>> SPEED = ATTACHMENT_TYPES.register(
            "speed", () -> AttachmentType.builder(() -> 0f).serialize(Codec.FLOAT).build());

    public static final Supplier<AttachmentType<Float>> ADDITIONAL_TURN = ATTACHMENT_TYPES.register(
            "additional_turn", () -> AttachmentType.builder(() -> 0f).serialize(Codec.FLOAT).build());

    public static final Supplier<AttachmentType<Integer>> RESTING_STATE = ATTACHMENT_TYPES.register(
            "resting_state", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).build());

    public static final Supplier<AttachmentType<Integer>> PAIRING_STATE = ATTACHMENT_TYPES.register(
            "pairing_state", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).build());

    public static final Supplier<AttachmentType<Float>> TURN_PROGRESS = ATTACHMENT_TYPES.register(
            "turn_progress", () -> AttachmentType.builder(() -> 0f).serialize(Codec.FLOAT).build());

    public static final Supplier<AttachmentType<Float>> REST_FACTOR = ATTACHMENT_TYPES.register(
            "rest_factor", () -> AttachmentType.builder(() -> 1f).serialize(Codec.FLOAT).build());

    public static final Supplier<AttachmentType<Boolean>> ATTACK_MAIN_1 = ATTACHMENT_TYPES.register(
            "attack_main_one", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL).build());

    public static final Supplier<AttachmentType<Boolean>> ATTACK_MAIN_2 = ATTACHMENT_TYPES.register(
            "attack_main_two", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL).build());

    public static final Supplier<AttachmentType<Integer>> ATTACK_COOLDOWN = ATTACHMENT_TYPES.register(
            "attack_cooldown", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).build());

    public static final Supplier<AttachmentType<Integer>> KNOCKDOWN_TIME = ATTACHMENT_TYPES.register(
            "knockdown_time", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).build());

    public static final Supplier<AttachmentType<Float>> DESIRED_ATTACK_ANGLE = ATTACHMENT_TYPES.register(
            "desired_attack_angle", () -> AttachmentType.builder(() -> 0f).serialize(Codec.FLOAT).build());

    public static final Supplier<AttachmentType<Boolean>> ATTACK_TURNAROUND = ATTACHMENT_TYPES.register(
            "attack_turnaround", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL).build());

    public static final Supplier<AttachmentType<Boolean>> ATTEMPTING_PAIRING = ATTACHMENT_TYPES.register(
            "attempting_pairing", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL).build());

    public static final Supplier<AttachmentType<Boolean>> HITBOXES_INITIALIZED = ATTACHMENT_TYPES.register(
            "hitboxes_initialized", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL).build());

    public static final Supplier<AttachmentType<String>> TURNING_STATE = ATTACHMENT_TYPES.register(
            "turning_state", () -> AttachmentType.builder(() -> "STILL").serialize(Codec.STRING).build());

    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }


}
