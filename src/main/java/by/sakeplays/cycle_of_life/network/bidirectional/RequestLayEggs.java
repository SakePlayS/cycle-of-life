package by.sakeplays.cycle_of_life.network.bidirectional;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.DinoData;
import by.sakeplays.cycle_of_life.common.data.NestData;
import by.sakeplays.cycle_of_life.common.data.PairData;
import by.sakeplays.cycle_of_life.network.to_client.SyncLayingEggs;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RequestLayEggs() implements CustomPacketPayload {

    public static final Type<RequestLayEggs> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "request_lay_eggs"));


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, RequestLayEggs> STREAM_CODEC = StreamCodec.unit(
            new RequestLayEggs()
    );

    public static void handleServer(final RequestLayEggs packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();

            DinoData dinoData = player.getData(DataAttachments.DINO_DATA);

            if (!isRequestValid(player)) return;

            dinoData.setLayingEggs(!dinoData.isLayingEggs());
            PacketDistributor.sendToAllPlayers(new SyncLayingEggs(dinoData.isLayingEggs(), player.getId()));

        });
    }


    private static boolean isRequestValid(Player player) {
        PairData pairData = player.getData(DataAttachments.PAIRING_DATA);
        DinoData dinoData = player.getData(DataAttachments.DINO_DATA);
        BlockPos pos = player.getOnPos();
        NestData nestData = NestData.get(player.level().getServer());


        if (dinoData.isLayingEggs()) return true;
        if (dinoData.isMale()) return false;
        if (nestData.getNestByBlockPos(pos) == null) {
            return false;
        }

        if (!nestData.getNestByBlockPos(pos).equals(nestData.getNestByPlayer(player))) {
            return false;
        }
        if (pairData.getStoredEggs() <= 0) {
            return false;
        }
        if (dinoData.getGrowth() < 1) {
            return false;
        }

        return true;
    }
}
