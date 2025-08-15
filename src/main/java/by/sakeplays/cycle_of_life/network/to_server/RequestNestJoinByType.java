package by.sakeplays.cycle_of_life.network.to_server;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.Nest;
import by.sakeplays.cycle_of_life.common.data.NestData;
import by.sakeplays.cycle_of_life.network.to_client.SendNestFeedback;
import by.sakeplays.cycle_of_life.network.to_client.SyncOwnNest;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RequestNestJoinByType(int nestType) implements CustomPacketPayload {

    public static final Type<RequestNestJoinByType> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "request_nest_join_by_type"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, RequestNestJoinByType> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, RequestNestJoinByType::nestType,
            RequestNestJoinByType::new
    );

    public static void handleServer(final RequestNestJoinByType packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            MinecraftServer server = context.player().getServer();

            Nest nest = NestData.get(server).getRandomAvailableOfType(packet.nestType(), context.player().level());

            if (nest == null) {
                PacketDistributor.sendToPlayer((ServerPlayer) context.player(), new SendNestFeedback("Couldn't find a nest to queue you in!"));
                return;
            }

            ServerPlayer matriarch = server.getPlayerList().getPlayer(nest.getMatriarch());
            ServerPlayer patriarch = server.getPlayerList().getPlayer(nest.getPatriarch());

            nest.addToQueue(context.player().getUUID());

            PacketDistributor.sendToPlayer((ServerPlayer) context.player(), new SendNestFeedback("Join request sent!"));

            if (matriarch != null) PacketDistributor.sendToPlayer(matriarch, new SyncOwnNest(nest));
            if (patriarch != null) PacketDistributor.sendToPlayer(patriarch, new SyncOwnNest(nest));

        });

    }
}
