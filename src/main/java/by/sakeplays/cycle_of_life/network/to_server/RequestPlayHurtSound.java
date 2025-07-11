package by.sakeplays.cycle_of_life.network.to_server;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.ModSounds;
import by.sakeplays.cycle_of_life.Util;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.network.to_client.SyncSelectedDinosaur;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RequestPlayHurtSound(int target) implements CustomPacketPayload {

    public static final Type<RequestPlayHurtSound> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "request_play_hurt_sound"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, RequestPlayHurtSound> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, RequestPlayHurtSound::target,
            RequestPlayHurtSound::new
    );

    public static void handleServer(final RequestPlayHurtSound packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.target) instanceof Player player) {
                int dinoId = Util.getDino(player).getID();

                switch (dinoId) {
                    case 2 -> player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                            ModSounds.DEINONYCHUS_HURT.get(), SoundSource.PLAYERS, 1f ,1f +
                                    (float) ((Math.random() - 0.5) / 4));
                    default -> player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1f ,1f +
                                    (float) ((Math.random() - 0.5) / 4));
                }
            }
        });

    }
}
