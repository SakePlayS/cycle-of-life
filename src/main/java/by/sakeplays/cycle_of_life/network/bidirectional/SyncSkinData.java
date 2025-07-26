package by.sakeplays.cycle_of_life.network.bidirectional;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncSkinData(int playerId, int eyesColor, int markingsColor, int bodyColor, int flankColor, int bellyColor, int maleDisplayColor) implements CustomPacketPayload {

    public static final Type<SyncSkinData> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "sync_skin_data"));


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, SyncSkinData> STREAM_CODEC =
            new StreamCodec<FriendlyByteBuf, SyncSkinData>() {
                @Override
                public SyncSkinData decode(FriendlyByteBuf buf) {
                    return new SyncSkinData(
                            buf.readInt(), buf.readInt(), buf.readInt(),
                            buf.readInt(), buf.readInt(), buf.readInt(),
                            buf.readInt()
                    );
                }

                @Override
                public void encode(FriendlyByteBuf buf, SyncSkinData val) {
                    buf.writeInt(val.playerId);
                    buf.writeInt(val.eyesColor);
                    buf.writeInt(val.markingsColor);
                    buf.writeInt(val.bodyColor);
                    buf.writeInt(val.flankColor);
                    buf.writeInt(val.bellyColor);
                    buf.writeInt(val.maleDisplayColor);
                }
            };

    public static void handleClient(final SyncSkinData packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerId()) instanceof Player player) {
                player.getData(DataAttachments.SKIN_DATA).setBellyColor(packet.bellyColor);
                player.getData(DataAttachments.SKIN_DATA).setBodyColor(packet.bodyColor);
                player.getData(DataAttachments.SKIN_DATA).setEyesColor(packet.eyesColor);
                player.getData(DataAttachments.SKIN_DATA).setMarkingsColor(packet.markingsColor);
                player.getData(DataAttachments.SKIN_DATA).setMaleDisplayColor(packet.maleDisplayColor);
                player.getData(DataAttachments.SKIN_DATA).setFlankColor(packet.flankColor);

            }
        });
    }

    public static void handleServer(final SyncSkinData packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerId()) instanceof Player player) {
                player.getData(DataAttachments.SKIN_DATA).setBellyColor(packet.bellyColor);
                player.getData(DataAttachments.SKIN_DATA).setBodyColor(packet.bodyColor);
                player.getData(DataAttachments.SKIN_DATA).setEyesColor(packet.eyesColor);
                player.getData(DataAttachments.SKIN_DATA).setMarkingsColor(packet.markingsColor);
                player.getData(DataAttachments.SKIN_DATA).setMaleDisplayColor(packet.maleDisplayColor);
                player.getData(DataAttachments.SKIN_DATA).setFlankColor(packet.flankColor);
            }
        }).thenRun(() -> PacketDistributor.sendToAllPlayers(packet));
    }
}
