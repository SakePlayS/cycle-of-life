package by.sakeplays.cycle_of_life.network.bidirectional;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncTurningState(String val, int playerID) implements CustomPacketPayload {

    public static final Type<SyncTurningState> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "sync_turning_state"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, SyncTurningState> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, SyncTurningState::val,
            ByteBufCodecs.INT, SyncTurningState::playerID,
            SyncTurningState::new
    );

    public static void handleClient(final SyncTurningState packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerID) instanceof Player player) {
                player.setData(DataAttachments.TURNING_STATE, packet.val);
            }
        });
    }

    public static void handleServer(final SyncTurningState packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            context.player().setData(DataAttachments.TURNING_STATE, packet.val);

        }).thenRun(() -> PacketDistributor.sendToPlayersTrackingEntity(context.player(), packet));
    }
}
