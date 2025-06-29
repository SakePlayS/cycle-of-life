package by.sakeplays.cycle_of_life.network.to_client;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.DataAttachments;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncTurnDegree2C(int playerId, float turnDegree) implements CustomPacketPayload {

    public static final Type<SyncTurnDegree2C> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "sync_turn_degree_2c"));


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, SyncTurnDegree2C> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SyncTurnDegree2C::playerId,
            ByteBufCodecs.FLOAT, SyncTurnDegree2C::turnDegree,
            SyncTurnDegree2C::new
    );

    public static void handleClient(final SyncTurnDegree2C packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerId()) instanceof Player player) {

                player.setData(DataAttachments.TURN_DEGREE, packet.turnDegree());

            }
        });
    }
}
