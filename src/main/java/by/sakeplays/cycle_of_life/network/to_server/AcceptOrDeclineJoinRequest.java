package by.sakeplays.cycle_of_life.network.to_server;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.*;
import by.sakeplays.cycle_of_life.common.data.adaptations.Adaptation;
import by.sakeplays.cycle_of_life.common.data.adaptations.AdaptationType;
import by.sakeplays.cycle_of_life.entity.util.ColorableBodyParts;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncGrowth;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncSkinData;
import by.sakeplays.cycle_of_life.network.to_client.*;
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

import java.util.HashMap;
import java.util.Map;

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

            if (nest.getQueuedPlayers().isEmpty()) return;

            if (packet.accepted()) {

                ServerPlayer player = server.getPlayerList().getPlayer(nest.getQueuedPlayers().getFirst());
                nest.getQueuedPlayers().removeFirst();
                if (player == null) return;
                if (player.getData(DataAttachments.DINO_DATA).getSelectedDinosaur() != 0) return;

                NestData.get(server).removeEgg(nest);

                player.connection.teleport(nest.getX() + 0.5, nest.getY() + 0.1, nest.getZ() + 0.5, player.getYRot(), player.getXRot());
                SkinData skinData = player.getData(DataAttachments.SKIN_DATA);
                DinoData dinoData = player.getData(DataAttachments.DINO_DATA);
                PairData pairData = player.getData(DataAttachments.PAIRING_DATA);
                AdaptationData adaptationData = player.getData(DataAttachments.ADAPTATION_DATA);

                adaptationData.fullReset();
                inheritAdaptations(player, nest, adaptationData);


                inheritColor(player, ColorableBodyParts.EYES, nest);
                inheritColor(player, ColorableBodyParts.BODY, nest);
                inheritColor(player, ColorableBodyParts.MARKINGS, nest);
                inheritColor(player, ColorableBodyParts.BELLY, nest);
                inheritColor(player, ColorableBodyParts.MALE_DISPLAY, nest);

                PacketDistributor.sendToAllPlayers(new SyncSkinData(player.getId(), skinData.getColors()));


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
                }

                if (patriarch != null) {
                    PacketDistributor.sendToPlayer(patriarch, new SyncOwnNest(nest));
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

    private static void inheritAdaptations(ServerPlayer player, Nest nest, AdaptationData data) {

        Map<String, Integer> matriarchAdaptations = new HashMap<>(nest.getMatriarchAdaptations().getMap());
        Map<String, Integer> patriarchAdaptations = new HashMap<>(nest.getPatriarchAdaptations().getMap());

        for (Adaptation adaptation : data.getAdaptationList()) {
            inherit(player, adaptation, matriarchAdaptations, patriarchAdaptations);
        }
    }

    private static void inheritColor(ServerPlayer player, ColorableBodyParts bodyPart, Nest nest) {
        SkinData skinData = player.getData(DataAttachments.SKIN_DATA);

        if (Math.random() < 0.5 || bodyPart == ColorableBodyParts.MALE_DISPLAY) {
            skinData.setColor(bodyPart, nest.getPatriarchColors().getColor(bodyPart).first(), nest.getPatriarchColors().getColor(bodyPart).second());
        } else {
            skinData.setColor(bodyPart, nest.getMatriarchColors().getColor(bodyPart).first(), nest.getMatriarchColors().getColor(bodyPart).second());
        }

    }

    private static void inherit(ServerPlayer player, Adaptation adaptation, Map<String, Integer> matriarchAdaptations, Map<String, Integer> patriarchAdaptations) {
        adaptation.setLevel(Math.max(0, Math.random() < 0.5 ?
                matriarchAdaptations.get(adaptation.getName()) - (int)(Math.random() + 0.5) :
                patriarchAdaptations.get(adaptation.getName()) - (int)(Math.random() + 0.5)
        ));

        adaptation.setUpgraded(false);

        PacketDistributor.sendToPlayer(player, new SyncAdaptation(adaptation.getType(),
                adaptation.getProgress(), adaptation.getLevel(),
                player.getId(), false));
    }


}
