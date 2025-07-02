package by.sakeplays.cycle_of_life.network.to_server;

import by.sakeplays.cycle_of_life.CycleOfLife;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RequestMovement(double dX, double dY, double dZ) implements CustomPacketPayload {

    public static final Type<RequestMovement> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "request_movement_c2s"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, RequestMovement> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, RequestMovement::dX,
            ByteBufCodecs.DOUBLE, RequestMovement::dY,
            ByteBufCodecs.DOUBLE, RequestMovement::dZ,
            RequestMovement::new
    );

    public static void handleServer(final RequestMovement packet, final IPayloadContext context) {
        context.enqueueWork(() -> context.player().setDeltaMovement(packet.dX(), packet.dY(), packet.dZ()));

    }
}
