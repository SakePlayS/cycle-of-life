package by.sakeplays.cycle_of_life.network.to_server.attacks.deinonychus;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.ModSounds;
import by.sakeplays.cycle_of_life.Util;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.DinoData;
import by.sakeplays.cycle_of_life.entity.HitboxEntity;
import by.sakeplays.cycle_of_life.entity.util.Dinosaurs;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncAttackMainOne;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncStamina;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record RequestDeinonychusBite() implements CustomPacketPayload {

    public static final Type<RequestDeinonychusBite> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "request_deinonychus_bite"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, RequestDeinonychusBite> STREAM_CODEC = StreamCodec.unit(new RequestDeinonychusBite());

    public static void handleServer(final RequestDeinonychusBite packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (Util.getDino(context.player()).getID() != Dinosaurs.DEINONYCHUS.getID()) return;
            Player player = context.player();
            DinoData dinoData = player.getData(DataAttachments.DINO_DATA);

            if (player.getData(DataAttachments.ATTACK_COOLDOWN) > 0) return;

            player.setData(DataAttachments.ATTACK_COOLDOWN, 15);
            Player target = null;

            float newStam = Math.max(0, dinoData.getStamina() - 7);
            player.getData(DataAttachments.DINO_DATA).setStamina(newStam);
            PacketDistributor.sendToAllPlayers(new SyncStamina(player.getId(), newStam));

            if (!player.getData(DataAttachments.HITBOXES_INITIALIZED)) return;

            if (player.level().getEntity(player.getData(DataAttachments.HITBOX_DATA).getHeadId()) == null) return;

            player.setData(DataAttachments.ATTACK_MAIN_1, true);
            PacketDistributor.sendToAllPlayers(new SyncAttackMainOne(true, context.player().getId()));

            AABB head = player.level().getEntity(player.getData(DataAttachments.HITBOX_DATA).getHeadId()).getBoundingBox();
            AABB biteHitbox = head.inflate(0.2 * dinoData.getGrowth(), 0.3 * dinoData.getGrowth(), 0.2 * dinoData.getGrowth());

            List<HitboxEntity> possibleTargets = player.level().getEntities(EntityTypeTest.forClass(HitboxEntity.class), biteHitbox, e -> true);

            for (HitboxEntity hitbox : possibleTargets) {
                if (hitbox.getPlayer() != player) {
                    target = hitbox.getPlayer();
                    break;
                }
            }

            if (target == null) return;

            Util.attemptToHitPlayer(target, biteHitbox, 12f * dinoData.getGrowth(), 0.015f * dinoData.getGrowth(), true);
        });
    }
}
