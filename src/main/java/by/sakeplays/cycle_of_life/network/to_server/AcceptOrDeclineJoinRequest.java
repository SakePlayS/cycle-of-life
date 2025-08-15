package by.sakeplays.cycle_of_life.network.to_server;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.*;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncGrowth;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncSkinData;
import by.sakeplays.cycle_of_life.network.to_client.FinishNestJoining;
import by.sakeplays.cycle_of_life.network.to_client.SendNestFeedback;
import by.sakeplays.cycle_of_life.network.to_client.SyncOwnNest;
import by.sakeplays.cycle_of_life.network.to_client.SyncSelectedDinosaur;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record AcceptOrDeclineJoinRequest(boolean accepted) implements CustomPacketPayload {

    public static final Type<AcceptOrDeclineJoinRequest> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "accept_or_decline_join_request"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, AcceptOrDeclineJoinRequest> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, AcceptOrDeclineJoinRequest::accepted,
            AcceptOrDeclineJoinRequest::new
    );

    public static void handleServer(final AcceptOrDeclineJoinRequest packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            MinecraftServer server = context.player().getServer();

            Nest nest = NestData.get(server).getNestByPlayer(context.player());

            if (packet.accepted()) {

                ServerPlayer player = server.getPlayerList().getPlayer(nest.getQueuedPlayers().getFirst());
                nest.getQueuedPlayers().removeFirst();
                if (player == null) return;
                if (player.getData(DataAttachments.DINO_DATA).getSelectedDinosaur() != 0) return;

                NestData.get(server).removeEgg(nest);

                player.connection.teleport(nest.getX() + 0.5, nest.getY() + 1, nest.getZ() + 0.5, player.getYRot(), player.getXRot());
                SkinData skinData = player.getData(DataAttachments.SKIN_DATA);
                DinoData dinoData = player.getData(DataAttachments.DINO_DATA);
                PairData pairData = player.getData(DataAttachments.PAIRING_DATA);

                skinData.setMarkingsColor(Math.random() < 0.5 ? nest.getMatriarchColors().markings : nest.getPatriarchColors().markings);
                skinData.setMaleDisplayColor(dinoData.isMale() ? nest.getPatriarchColors().maleDisplay : nest.getMatriarchColors().maleDisplay);
                skinData.setEyesColor(Math.random() < 0.5 ? nest.getMatriarchColors().eyes : nest.getPatriarchColors().eyes);
                skinData.setBellyColor(Math.random() < 0.5 ? nest.getMatriarchColors().belly : nest.getPatriarchColors().belly);
                skinData.setBodyColor(Math.random() < 0.5 ? nest.getMatriarchColors().body : nest.getPatriarchColors().body);
                skinData.setFlankColor(Math.random() < 0.5 ? nest.getMatriarchColors().flank : nest.getPatriarchColors().flank);

                PacketDistributor.sendToAllPlayers(new SyncSkinData(player.getId(), skinData.getEyesColor(), skinData.getMarkingsColor(),
                        skinData.getBodyColor(), skinData.getFlankColor(), skinData.getBellyColor(), skinData.getMaleDisplayColor()));

                dinoData.setGrowth(0f);
                PacketDistributor.sendToAllPlayers(new SyncGrowth(0, player.getId()));

                dinoData.setSelectedDinosaur(nest.getType());
                PacketDistributor.sendToAllPlayers(new SyncSelectedDinosaur(player.getId(), nest.getType()));

                pairData.addBloodlineEntry(nest.getMatriarchLifeUUID());
                pairData.addBloodlineEntry(nest.getPatriarchLifeUUID());

                PacketDistributor.sendToPlayer(player, new FinishNestJoining());

                ServerPlayer matriarch = server.getPlayerList().getPlayer(nest.getMatriarch());
                ServerPlayer patriarch = server.getPlayerList().getPlayer(nest.getPatriarch());

                if (matriarch != null) {
                    PacketDistributor.sendToPlayer(matriarch, new SyncOwnNest(nest));
                    matriarch.sendSystemMessage(Component.literal("Remaining eggs: " + nest.getEggsCount()));
                }

                if (patriarch != null) {
                    PacketDistributor.sendToPlayer(patriarch, new SyncOwnNest(nest));
                    patriarch.sendSystemMessage(Component.literal("Remaining eggs: " + nest.getEggsCount()));
                }

            } else {
                Player player = server.getPlayerList().getPlayer(nest.getQueuedPlayers().getFirst());
                nest.getQueuedPlayers().removeFirst();

                if (player != null) PacketDistributor.sendToPlayer((ServerPlayer) player, new SendNestFeedback("Your join request has been declined!"));

                ServerPlayer matriarch = server.getPlayerList().getPlayer(nest.getMatriarch());
                ServerPlayer patriarch = server.getPlayerList().getPlayer(nest.getPatriarch());

                if (matriarch != null) PacketDistributor.sendToPlayer(matriarch, new SyncOwnNest(nest));
                if (patriarch != null) PacketDistributor.sendToPlayer(patriarch, new SyncOwnNest(nest));
            }
        });
    }
}
