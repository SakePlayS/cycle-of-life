package by.sakeplays.cycle_of_life.network.to_server.attacks.deinonychus;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.Util;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.DinoData;
import by.sakeplays.cycle_of_life.entity.HitboxEntity;
import by.sakeplays.cycle_of_life.entity.util.Dinosaurs;
import by.sakeplays.cycle_of_life.event.client.HandleKeys;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncAttackTurnaround;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncStamina;
import by.sakeplays.cycle_of_life.network.to_client.SyncBloodLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record RequestDeinonychusDoubleSlashStart(int playerID) implements CustomPacketPayload {

    public static final Type<RequestDeinonychusDoubleSlashStart> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "request_deinonychus_double_slash_start"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, RequestDeinonychusDoubleSlashStart> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, RequestDeinonychusDoubleSlashStart::playerID,
            RequestDeinonychusDoubleSlashStart::new
    );

    public static void handleClient(final RequestDeinonychusDoubleSlashStart packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerID()) instanceof Player player) {

            }
        });
    }

    public static void handleServer(final RequestDeinonychusDoubleSlashStart packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (Util.getDino(context.player()).getID() != Dinosaurs.DEINONYCHUS.getID()) return;
            Player player = context.player();
            DinoData dinoData = player.getData(DataAttachments.DINO_DATA);

            if (player.getData(DataAttachments.ATTACK_COOLDOWN) > 0) return;

            player.setData(DataAttachments.ATTACK_COOLDOWN, 20);

            float yRot = player.getYRot();
            float additionalTurn = player.getData(DataAttachments.ADDITIONAL_TURN) * Mth.DEG_TO_RAD;
            float turnDegree = player.getData(DataAttachments.PLAYER_TURN);
            float targetYaw = yRot + additionalTurn;

            player.setData(DataAttachments.DESIRED_ATTACK_ANGLE,
                    Mth.wrapDegrees(targetYaw - turnDegree * Mth.RAD_TO_DEG - additionalTurn) * Mth.DEG_TO_RAD);

            float newStam = Math.max(0, dinoData.getStamina() - 35);
            player.getData(DataAttachments.DINO_DATA).setStamina(newStam);
            PacketDistributor.sendToAllPlayers(new SyncStamina(player.getId(), newStam));

            player.setData(DataAttachments.ATTACK_TURNAROUND, true);
            PacketDistributor.sendToAllPlayers(new SyncAttackTurnaround(true, player.getId()));

            player.setData(DataAttachments.ATTACK_TIMER, 16);

            PacketDistributor.sendToPlayer((ServerPlayer) context.player(), new RequestDeinonychusDoubleSlashStart(player.getId()));

        });
    }
}
