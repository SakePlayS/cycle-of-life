package by.sakeplays.cycle_of_life.network.bidirectional;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncPairingWith(int v, int playerID) implements CustomPacketPayload {

    public static final Type<SyncPairingWith> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "sync_pairing_with"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, SyncPairingWith> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SyncPairingWith::v,
            ByteBufCodecs.INT, SyncPairingWith::playerID,
            SyncPairingWith::new
    );

    public static void handleClient(final SyncPairingWith packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerID) instanceof Player player) {
                player.getData(DataAttachments.DINO_DATA).setPairingWith(packet.v());
            }
        });
    }

    public static void handleServer(final SyncPairingWith packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerID) instanceof Player player) {
                player.getData(DataAttachments.DINO_DATA).setPairingWith(packet.v());
            }
        }).thenRun(() -> {
            PacketDistributor.sendToPlayersTrackingEntity(context.player(), packet);

            if (context.player().level().getEntity(packet.v) instanceof Player targetPlayer) {

                if ((context.player().getData(DataAttachments.DINO_DATA).getPairingWith() == targetPlayer.getId())
                        && (targetPlayer.getData(DataAttachments.DINO_DATA).getPairingWith() == context.player().getId())) {

                    context.player().getData(DataAttachments.DINO_DATA).setPaired(true);
                    PacketDistributor.sendToAllPlayers(new SyncIsPaired(true, context.player().getId()));
                    context.player().sendSystemMessage(Component.literal("Pairing complete. With: " + targetPlayer.getName().getString()));

                    targetPlayer.getData(DataAttachments.DINO_DATA).setPaired(true);
                    PacketDistributor.sendToAllPlayers(new SyncIsPaired(true, targetPlayer.getId()));
                    targetPlayer.sendSystemMessage(Component.literal("Pairing complete. With: " + context.player().getName().getString()));

                }
            }
        });
    }
}
