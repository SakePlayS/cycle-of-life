package by.sakeplays.cycle_of_life.network.to_server.attacks.deinonychus;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.entity.util.HitboxType;
import by.sakeplays.cycle_of_life.network.ModCodecs;
import by.sakeplays.cycle_of_life.network.to_client.SyncAttackCooldown;
import by.sakeplays.cycle_of_life.network.to_server.attacks.pachycephalosaurus.RequestPachyBash;
import by.sakeplays.cycle_of_life.util.Util;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record RequestDeinonychusBite(int target, HitboxType hbType) implements CustomPacketPayload {

    public static final Type<RequestDeinonychusBite> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "request_deinonychus_bite"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, RequestDeinonychusBite> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, RequestDeinonychusBite::target,
            ModCodecs.enumCodec(HitboxType.class), RequestDeinonychusBite::hbType,
            RequestDeinonychusBite::new
    );
    public static void handleServer(final RequestDeinonychusBite packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            DinoData data = context.player().getData(DataAttachments.DINO_DATA);
            float newStam = Math.max(0, data.getStamina() - 7);

            data.setStamina(newStam);
            PacketDistributor.sendToAllPlayers(new SyncStamina(context.player().getId(), newStam));

            if (packet.hbType == HitboxType.NONE) {
                return;
            }

            if (context.player().level().getEntity(packet.target) instanceof Player targetPlayer) {

                if (context.player().getData(DataAttachments.ATTACK_COOLDOWN) > 0) return;
                context.player().setData(DataAttachments.ATTACK_COOLDOWN, 15);
                PacketDistributor.sendToAllPlayers(new SyncAttackCooldown(context.player().getId(), 15));


                if (!Util.isAttackValid(context.player(), targetPlayer)) return;

                Util.attemptToHitPlayer(targetPlayer, 15f, 0.01f, true, HitboxType.fromString(packet.hbType().toString()));
            }
        });
    }
}
