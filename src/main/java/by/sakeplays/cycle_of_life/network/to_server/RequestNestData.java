package by.sakeplays.cycle_of_life.network.to_server;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.Nest;
import by.sakeplays.cycle_of_life.common.data.NestData;
import by.sakeplays.cycle_of_life.network.to_client.SendNestsData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

public record RequestNestData() implements CustomPacketPayload {

    public static final Type<RequestNestData> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "request_nest_data"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, RequestNestData> STREAM_CODEC = StreamCodec.unit(
            new RequestNestData()
    );

    public static void handleServer(final RequestNestData packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            MinecraftServer minecraftServer = player.getServer();

            ArrayList<Nest> nests = NestData.get(minecraftServer).getAllNests();

            nests.removeIf(nest -> !nest.isPublic() || nest.getEggsCount() < 1);

            PacketDistributor.sendToPlayer((ServerPlayer) player, new SendNestsData(nests));
        });
    }
}
