package by.sakeplays.cycle_of_life.network.bidirectional;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RequestDrinking(boolean isDrinking, int playerID) implements CustomPacketPayload {

    public static final Type<RequestDrinking> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "request_drinking"));


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, RequestDrinking> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, RequestDrinking::isDrinking,
            ByteBufCodecs.INT, RequestDrinking::playerID,
            RequestDrinking::new
    );

    public static void handleClient(final RequestDrinking packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerID) instanceof Player player) {
                player.getData(DataAttachments.DINO_DATA).setDrinking(packet.isDrinking());
            }
        });
    }

    public static void handleServer(final RequestDrinking packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            double x = context.player().getData(DataAttachments.HITBOX_DATA).getHeadHitboxPos().x();
            double y = context.player().getY() - 0.5;
            double z = context.player().getData(DataAttachments.HITBOX_DATA).getHeadHitboxPos().z();
            int id = context.player().getId();

            if (context.player().getData(DataAttachments.DINO_DATA).getWaterLevel() > 0.985f) return;

            if (packet.isDrinking()) {
                if (context.player().level().getBlockState(BlockPos.containing(x, y, z)).is(Blocks.WATER)) {
                    context.player().getData(DataAttachments.DINO_DATA).setDrinking(true);
                    PacketDistributor.sendToPlayersTrackingEntity(context.player(), new RequestDrinking(true, id));
                    PacketDistributor.sendToPlayer((ServerPlayer) context.player(), new RequestDrinking(true, id));

                } else {
                    context.player().getData(DataAttachments.DINO_DATA).setDrinking(false);
                    PacketDistributor.sendToPlayersTrackingEntity(context.player(), new RequestDrinking(false, id));
                    PacketDistributor.sendToPlayer((ServerPlayer) context.player(), new RequestDrinking(false, id));

                }
            } else {
                context.player().getData(DataAttachments.DINO_DATA).setDrinking(false);
                PacketDistributor.sendToPlayersTrackingEntity(context.player(), new RequestDrinking(false, id));
                PacketDistributor.sendToPlayer((ServerPlayer) context.player(), new RequestDrinking(false, id));
            }
        });
    }
}
