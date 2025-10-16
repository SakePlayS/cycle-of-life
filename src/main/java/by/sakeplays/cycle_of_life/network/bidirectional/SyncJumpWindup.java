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

public record SyncJumpWindup(boolean val, int playerID) implements CustomPacketPayload {

    public static final Type<SyncJumpWindup> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "send_jump_windup"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, SyncJumpWindup> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, SyncJumpWindup::val,
            ByteBufCodecs.INT, SyncJumpWindup::playerID,
            SyncJumpWindup::new
    );

    public static void handleClient(final SyncJumpWindup packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerID) instanceof Player player) {
                player.setData(DataAttachments.JUMP_WINDUP, packet.val());
            }
        });
    }

    public static void handleServer(final SyncJumpWindup packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            PacketDistributor.sendToPlayersTrackingEntity(context.player(), packet);
        });
    }
}
