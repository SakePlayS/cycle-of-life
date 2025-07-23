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

public record SyncAdditionalTurn(int playerId, float V) implements CustomPacketPayload {

    public static final Type<SyncAdditionalTurn> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "sync_additional_turn"));


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, SyncAdditionalTurn> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SyncAdditionalTurn::playerId,
            ByteBufCodecs.FLOAT, SyncAdditionalTurn::V,
            SyncAdditionalTurn::new
    );

    public static void handleClient(final SyncAdditionalTurn packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerId()) instanceof Player player) {

                player.setData(DataAttachments.ADDITIONAL_TURN, packet.V());

            }
        });
    }

    public static void handleServer(final SyncAdditionalTurn packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            context.player().setData(DataAttachments.ADDITIONAL_TURN, packet.V());

            PacketDistributor.sendToPlayersTrackingEntity(context.player(), packet);
        });
    }
}
