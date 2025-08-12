package by.sakeplays.cycle_of_life.network.to_client;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record SyncNestPos(int x, int y, int z, int playerID) implements CustomPacketPayload {

    public static final Type<SyncNestPos> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "sync_nest_pos"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, SyncNestPos> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SyncNestPos::x,
            ByteBufCodecs.INT, SyncNestPos::y,
            ByteBufCodecs.INT, SyncNestPos::z,
            ByteBufCodecs.INT, SyncNestPos::playerID,
            SyncNestPos::new
    );

    public static void handleClient(final SyncNestPos packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerID) instanceof Player player) {
                player.getData(DataAttachments.PAIRING_DATA).setNestPos(new BlockPos(packet.x, packet.y, packet.z));
            }
        });
    }
}
