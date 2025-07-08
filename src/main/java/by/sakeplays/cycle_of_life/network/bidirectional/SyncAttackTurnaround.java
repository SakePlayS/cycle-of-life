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

public record SyncAttackTurnaround(boolean attackState, int playerID) implements CustomPacketPayload {

    public static final Type<SyncAttackTurnaround> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "sync_attack_turnaround"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, SyncAttackTurnaround> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, SyncAttackTurnaround::attackState,
            ByteBufCodecs.INT, SyncAttackTurnaround::playerID,
            SyncAttackTurnaround::new
    );

    public static void handleClient(final SyncAttackTurnaround packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerID) instanceof Player player) {
                player.setData(DataAttachments.ATTACK_TURNAROUND, packet.attackState);
            }
        });
    }

    public static void handleServer(final SyncAttackTurnaround packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerID) instanceof Player player) {
                player.setData(DataAttachments.ATTACK_TURNAROUND, packet.attackState);
            }
        }).thenRun(() -> PacketDistributor.sendToAllPlayers( packet));
    }
}
