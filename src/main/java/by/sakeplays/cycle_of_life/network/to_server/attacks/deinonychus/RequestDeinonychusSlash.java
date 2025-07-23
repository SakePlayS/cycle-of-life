package by.sakeplays.cycle_of_life.network.to_server.attacks.deinonychus;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.Util;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.DinoData;
import by.sakeplays.cycle_of_life.entity.HitboxEntity;
import by.sakeplays.cycle_of_life.entity.util.Dinosaurs;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncAttackMainTwo;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncStamina;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record RequestDeinonychusSlash() implements CustomPacketPayload {

    public static final Type<RequestDeinonychusSlash> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "request_deinonychus_slash"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, RequestDeinonychusSlash> STREAM_CODEC = StreamCodec.unit(new RequestDeinonychusSlash());

    public static void handleServer(final RequestDeinonychusSlash packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (Util.getDino(context.player()).getID() != Dinosaurs.DEINONYCHUS.getID()) return;
            Player player = context.player();
            DinoData dinoData = player.getData(DataAttachments.DINO_DATA);

            if (player.getData(DataAttachments.ATTACK_COOLDOWN) > 0) return;

            player.setData(DataAttachments.ATTACK_COOLDOWN, 15);
            Player target = null;

            float newStam = Math.max(0, dinoData.getStamina() - 15);
            player.getData(DataAttachments.DINO_DATA).setStamina(newStam);
            PacketDistributor.sendToAllPlayers(new SyncStamina(player.getId(), newStam));

            if (!player.getData(DataAttachments.HITBOXES_INITIALIZED)) return;

            if (player.level().getEntity(player.getData(DataAttachments.HITBOX_DATA).getHeadId()) == null) return;

            player.setData(DataAttachments.ATTACK_MAIN_2, true);
            PacketDistributor.sendToAllPlayers(new SyncAttackMainTwo(true, player.getId()));
            AABB head = player.level().getEntity(player.getData(DataAttachments.HITBOX_DATA).getHeadId()).getBoundingBox();
            AABB biteHitbox = head.inflate(0.2 * dinoData.getGrowth(), 0.5 * dinoData.getGrowth(), 0.2 * dinoData.getGrowth())
                    .move(0, -0.8 * dinoData.getGrowth(), 0);

            List<HitboxEntity> possibleTargets = player.level().getEntities(EntityTypeTest.forClass(HitboxEntity.class), biteHitbox, e -> true);

            for (HitboxEntity hitbox : possibleTargets) {
                if (hitbox.getPlayer() != player) {
                    target = hitbox.getPlayer();
                    break;
                }
            }

            if (target == null) return;

            Util.attemptToHitPlayer(target, biteHitbox, 8f * dinoData.getGrowth(), 0.2f * dinoData.getGrowth(), true);
        });
    }
}
