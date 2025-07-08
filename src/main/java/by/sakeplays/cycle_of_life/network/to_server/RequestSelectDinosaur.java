package by.sakeplays.cycle_of_life.network.to_server;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.network.to_client.SyncSelectedDinosaur;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RequestSelectDinosaur(int selectedDinoID) implements CustomPacketPayload {

    public static final Type<RequestSelectDinosaur> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "request_select_dinosaur_c2s"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, RequestSelectDinosaur> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, RequestSelectDinosaur::selectedDinoID,
            RequestSelectDinosaur::new
    );

    public static void handleServer(final RequestSelectDinosaur packet, final IPayloadContext context) {
        context.enqueueWork(() -> context.player().getData(DataAttachments.DINO_DATA).setSelectedDinosaur(packet.selectedDinoID()))

                .thenRun(() -> PacketDistributor.sendToAllPlayers(new SyncSelectedDinosaur(context.player().getId(),
                        context.player().getData(DataAttachments.DINO_DATA).getSelectedDinosaur())));

    }
}
