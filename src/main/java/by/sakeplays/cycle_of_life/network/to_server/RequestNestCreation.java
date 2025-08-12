package by.sakeplays.cycle_of_life.network.to_server;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.*;
import by.sakeplays.cycle_of_life.network.to_client.SyncFullNestData;
import by.sakeplays.cycle_of_life.network.to_client.SyncSelectedDinosaur;
import by.sakeplays.cycle_of_life.util.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record RequestNestCreation() implements CustomPacketPayload {

    public static final Type<RequestNestCreation> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "request_nest_creation"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, RequestNestCreation> STREAM_CODEC = StreamCodec.unit(
            new RequestNestCreation()
    );

    public static void handleServer(final RequestNestCreation packet, final IPayloadContext context) {
        context.enqueueWork(() -> {

            Player player = context.player();
            ServerLevel serverLevel = (ServerLevel) player.level();
            PairData data = player.getData(DataAttachments.PAIRING_DATA);

            if (!data.isPaired()) return;
            if (data.getMateUUID().equals(PairData.NO_MATE)) return;
            if (data.getMateName().isEmpty()) return;

            player.sendSystemMessage(Component.literal("Starting nest creation..."));

            Player mate = serverLevel.getPlayerByUUID(data.getMateUUID());
            if (mate == null) return;

            mate.sendSystemMessage(Component.literal("Your mate started nest creation."));
            player.sendSystemMessage(Component.literal("Mate found..."));

            if (!Util.getDino(player).equals(Util.getDino(mate))) return;

            mate.sendSystemMessage(Component.literal("Dino type validated..."));
            player.sendSystemMessage(Component.literal("Dino type validated..."));

            UUID patriarchUUID = player.getData(DataAttachments.DINO_DATA).isMale() ? player.getUUID() : mate.getUUID();
            UUID matriarchUUID = player.getData(DataAttachments.DINO_DATA).isMale() ? mate.getUUID() : player.getUUID();
            NestData nestData = NestData.get(serverLevel);

            if (nestData.containsNestID(patriarchUUID)) return;

            Nest nest = new Nest(patriarchUUID, matriarchUUID, Util.getDino(player).getMaxEggs(), player.getOnPos(),
                    false, player.getData(DataAttachments.DINO_DATA).getSelectedDinosaur());

            nestData.addNest(nest);
            PacketDistributor.sendToPlayer((ServerPlayer) player, new SyncFullNestData(nest));
            PacketDistributor.sendToPlayer((ServerPlayer) mate, new SyncFullNestData(nest));
            player.sendSystemMessage(Component.literal("Nest registered."));
            mate.sendSystemMessage(Component.literal("Nest registered."));

        });
    }
}
