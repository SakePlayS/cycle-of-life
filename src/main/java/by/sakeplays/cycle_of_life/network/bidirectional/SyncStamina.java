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

public record SyncStamina(int playerId, float stamina) implements CustomPacketPayload {

    public static final Type<SyncStamina> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "sync_stam"));


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, SyncStamina> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SyncStamina::playerId,
            ByteBufCodecs.FLOAT, SyncStamina::stamina,
            SyncStamina::new
    );

    public static void handleClient(final SyncStamina packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerId()) instanceof Player player) {

                player.getData(DataAttachments.DINO_DATA).setStamina(packet.stamina());

            }
        });
    }

    public static void handleServer(final SyncStamina packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerId()) instanceof Player player) {
                player.getData(DataAttachments.DINO_DATA).setStamina(packet.stamina());
            }
        }).thenRun(() -> PacketDistributor.sendToPlayersTrackingEntity(context.player(), packet));
    }
}
