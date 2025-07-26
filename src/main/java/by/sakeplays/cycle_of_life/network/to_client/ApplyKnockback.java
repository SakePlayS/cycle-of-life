package by.sakeplays.cycle_of_life.network.to_client;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.event.client.HandleKeys;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ApplyKnockback(int playerId, float dx, float dy, float dz, float speed) implements CustomPacketPayload {

    public static final Type<ApplyKnockback> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "apply_knockback"));


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, ApplyKnockback> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ApplyKnockback::playerId,
            ByteBufCodecs.FLOAT, ApplyKnockback::dx,
            ByteBufCodecs.FLOAT, ApplyKnockback::dy,
            ByteBufCodecs.FLOAT, ApplyKnockback::dz,
            ByteBufCodecs.FLOAT, ApplyKnockback::speed,
            ApplyKnockback::new
    );

    public static void handleClient(final ApplyKnockback packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerId()) instanceof Player player) {
                HandleKeys.dz = packet.dz();
                HandleKeys.dx = packet.dx();
                HandleKeys.speed = packet.speed();

                player.setDeltaMovement(player.getDeltaMovement().x(), packet.dy, player.getDeltaMovement().z());

            }
        });
    }
}
