package by.sakeplays.cycle_of_life.network.to_server;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.Nest;
import by.sakeplays.cycle_of_life.common.data.NestData;
import by.sakeplays.cycle_of_life.network.to_client.SendNestFeedback;
import by.sakeplays.cycle_of_life.network.to_client.SyncOwnNest;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record RequestNestJoinByUUID(String uuid) implements CustomPacketPayload {

    public static final Type<RequestNestJoinByUUID> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "request_nest_join_by_uuid"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, RequestNestJoinByUUID> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, RequestNestJoinByUUID::uuid,
            RequestNestJoinByUUID::new
    );

    public static void handleServer(final RequestNestJoinByUUID packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            MinecraftServer server = context.player().getServer();

            UUID nestId;
            try {
                nestId = UUID.fromString(packet.uuid());
            } catch (IllegalArgumentException e) {
                PacketDistributor.sendToPlayer((ServerPlayer) context.player(), new SendNestFeedback("Invalid UUID format!"));
                return;
            }

            Nest nest = NestData.get(server).getNestByID(nestId);

            if (NestData.get(server).isPlayerQueued(context.player())) {
                PacketDistributor.sendToPlayer((ServerPlayer) context.player(), new SendNestFeedback("You are already queued for a nest!"));
                return;
            }

            if (nest == null) {
                PacketDistributor.sendToPlayer((ServerPlayer) context.player(), new SendNestFeedback("Couldn't find a nest with such UUID!"));
                return;
            }

            if (nest.getEggsCount() < 1) {
                PacketDistributor.sendToPlayer((ServerPlayer) context.player(), new SendNestFeedback("This nest has no eggs!"));
                return;
            }

            if (nest.getQueuedPlayers().size() >= nest.getEggsCount()) {
                PacketDistributor.sendToPlayer((ServerPlayer) context.player(), new SendNestFeedback("This nest's queue is full!"));
                return;
            }

            ServerPlayer matriarch = server.getPlayerList().getPlayer(nest.getMatriarch());
            ServerPlayer patriarch = server.getPlayerList().getPlayer(nest.getPatriarch());

            if (matriarch == null && patriarch == null) {
                PacketDistributor.sendToPlayer((ServerPlayer) context.player(), new SendNestFeedback("Cannot request: both parents are offline!"));
                return;
            }

            nest.addToQueue(context.player().getUUID());

            PacketDistributor.sendToPlayer((ServerPlayer) context.player(), new SendNestFeedback("Request sent!"));

            if (matriarch != null) PacketDistributor.sendToPlayer(matriarch, new SyncOwnNest(nest));
            if (patriarch != null) PacketDistributor.sendToPlayer(patriarch, new SyncOwnNest(nest));
        });
    }
}
