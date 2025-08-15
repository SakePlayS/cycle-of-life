package by.sakeplays.cycle_of_life.network.to_server;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.DinoData;
import by.sakeplays.cycle_of_life.common.data.PairData;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncIsPaired;
import by.sakeplays.cycle_of_life.network.to_client.SyncMateName;
import by.sakeplays.cycle_of_life.network.to_client.SyncMateUUID;
import by.sakeplays.cycle_of_life.network.to_client.SyncNestUUID;
import by.sakeplays.cycle_of_life.network.to_client.SyncPairingState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record SyncPairing(int target, int source) implements CustomPacketPayload {

    public static final Type<SyncPairing> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "sync_pairing"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, SyncPairing> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SyncPairing::target,
            ByteBufCodecs.INT, SyncPairing::source,
            SyncPairing::new
    );

    public static void handleServer(final SyncPairing packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Entity entitySource = context.player().level().getEntity(packet.source);
            Entity entityTarget = context.player().level().getEntity(packet.target);


            if (entitySource instanceof Player sourcePlayer && entityTarget instanceof Player targetPlayer) {
                sourcePlayer.getData(DataAttachments.PAIRING_DATA).setMateUUID(targetPlayer.getUUID());

                if (pairingValid(targetPlayer, sourcePlayer)) {
                    applyPairData(targetPlayer, sourcePlayer);
                    applyPairData(sourcePlayer, targetPlayer);
                }

            }
        });
    }

    private static void applyPairData(Player source, Player receiver) {
        PairData receiverData = receiver.getData(DataAttachments.PAIRING_DATA);
        PairData sourceData = source.getData(DataAttachments.PAIRING_DATA);

        receiverData.setMateLifeUUID(sourceData.getLifeUUID()); // needed only on server

        receiverData.setMateName(source.getName().getString());
        PacketDistributor.sendToPlayer((ServerPlayer) receiver, new SyncMateName(source.getName().getString(), receiver.getId()));

        receiverData.setPaired(true);
        PacketDistributor.sendToPlayer((ServerPlayer) receiver, new SyncIsPaired(true, receiver.getId()));

        receiverData.setMateUUID(source.getUUID());
        PacketDistributor.sendToPlayer((ServerPlayer) receiver, new SyncMateUUID(source.getUUID().toString(), receiver.getId()));

        UUID nestUUID = receiver.getData(DataAttachments.DINO_DATA).isMale() ? receiver.getUUID() : source.getUUID();
        receiverData.setNestUUID(nestUUID);
        PacketDistributor.sendToPlayer((ServerPlayer) receiver, new SyncNestUUID(nestUUID.toString(), receiver.getId()));

        receiver.setData(DataAttachments.PAIRING_STATE, 1);
        PacketDistributor.sendToAllPlayers(new SyncPairingState(1, receiver.getId()));
    }

    private static boolean pairingValid(Player p1, Player p2) {

        DinoData dinoData1 = p1.getData(DataAttachments.DINO_DATA);
        DinoData dinoData2 = p2.getData(DataAttachments.DINO_DATA);

        PairData pairData1 = p1.getData(DataAttachments.PAIRING_DATA);
        PairData pairData2 = p2.getData(DataAttachments.PAIRING_DATA);

        if (pairData1.isPaired() || pairData2.isPaired()) {
            return false;
        }

        if (dinoData1.isMale() == dinoData2.isMale()) {
            p1.getData(DataAttachments.PAIRING_DATA).setMateUUID(PairData.UNSET);
            return false;

        }

        if (dinoData1.getGrowth() < 0.999f || dinoData2.getGrowth() < 0.999f) {
            p1.getData(DataAttachments.PAIRING_DATA).setMateUUID(PairData.UNSET);
            return false;
        }

        if (!pairData1.getMateUUID().equals(p2.getUUID()) || !pairData2.getMateUUID().equals(p1.getUUID())) {
            p1.getData(DataAttachments.PAIRING_DATA).setMateUUID(PairData.UNSET);
            return false;
        }

        return true;
    }
}
