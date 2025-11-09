package by.sakeplays.cycle_of_life.network.bidirectional;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.Nest;
import by.sakeplays.cycle_of_life.common.data.SelectedColors;
import by.sakeplays.cycle_of_life.network.to_client.SyncOwnNest;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncSkinData(int playerId, SelectedColors colors) implements CustomPacketPayload {

    public static final Type<SyncSkinData> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "sync_skin_data"));


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, SyncSkinData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SyncSkinData::playerId,
            SelectedColors.CODEC, SyncSkinData::colors,
            SyncSkinData::new
    );


    public static void handleClient(final SyncSkinData packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerId()) instanceof Player player) {
                player.getData(DataAttachments.SKIN_DATA).getColors().override(packet.colors);
            }
        });
    }

    public static void handleServer(final SyncSkinData packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            context.player().getData(DataAttachments.SKIN_DATA).getColors().override(packet.colors);

            PacketDistributor.sendToAllPlayers(packet);
        });
    }
}
