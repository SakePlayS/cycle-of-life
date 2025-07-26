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

public record SyncPlayerRotation(float turnDegree, int playerID) implements CustomPacketPayload {

    public static final Type<SyncPlayerRotation> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "sync_player_rotation"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, SyncPlayerRotation> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, SyncPlayerRotation::turnDegree,
            ByteBufCodecs.INT, SyncPlayerRotation::playerID,
            SyncPlayerRotation::new
    );

    public static void handleClient(final SyncPlayerRotation packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerID) instanceof Player player) {
                player.setData(DataAttachments.PLAYER_ROTATION, packet.turnDegree);
            }
        });
    }

    public static void handleServer(final SyncPlayerRotation packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            player.setData(DataAttachments.PLAYER_ROTATION, packet.turnDegree);
        }).thenRun(() -> PacketDistributor.sendToPlayersTrackingEntity(context.player(), packet));
    }
}
