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

public record SyncAttackMainOne(boolean attackState, int playerID) implements CustomPacketPayload {

    public static final Type<SyncAttackMainOne> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "sync_attack_main_ne"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, SyncAttackMainOne> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, SyncAttackMainOne::attackState,
            ByteBufCodecs.INT, SyncAttackMainOne::playerID,
            SyncAttackMainOne::new
    );

    public static void handleClient(final SyncAttackMainOne packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerID) instanceof Player player) {
                player.setData(DataAttachments.ATTACK_MAIN_1, packet.attackState);
            }
        });
    }

    public static void handleServer(final SyncAttackMainOne packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerID) instanceof Player player) {
                player.setData(DataAttachments.ATTACK_MAIN_1, packet.attackState);
            }
        }).thenRun(() -> PacketDistributor.sendToAllPlayers( packet));
    }
}
