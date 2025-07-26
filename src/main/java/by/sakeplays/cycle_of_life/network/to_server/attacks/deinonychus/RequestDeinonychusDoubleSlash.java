package by.sakeplays.cycle_of_life.network.to_server.attacks.deinonychus;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.DinoData;
import by.sakeplays.cycle_of_life.entity.util.HitboxType;
import by.sakeplays.cycle_of_life.network.ModCodecs;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncStamina;
import by.sakeplays.cycle_of_life.network.to_client.SyncAttackCooldown;
import by.sakeplays.cycle_of_life.network.to_server.RequestPlayHurtSound;
import by.sakeplays.cycle_of_life.util.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RequestDeinonychusDoubleSlash(int target, HitboxType hbType) implements CustomPacketPayload {

    public static final Type<RequestDeinonychusDoubleSlash> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "request_deinonychus_double_slash"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, RequestDeinonychusDoubleSlash> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, RequestDeinonychusDoubleSlash::target,
            ModCodecs.enumCodec(HitboxType.class), RequestDeinonychusDoubleSlash::hbType,
            RequestDeinonychusDoubleSlash::new
    );

    public static void handleServer(final RequestDeinonychusDoubleSlash packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            DinoData data = context.player().getData(DataAttachments.DINO_DATA);
            float newStam = Math.max(0, data.getStamina() - 35);

            data.setStamina(newStam);
            PacketDistributor.sendToAllPlayers(new SyncStamina(context.player().getId(), newStam));

            if (packet.hbType == HitboxType.NONE) {
                return;
            }

            if (context.player().level().getEntity(packet.target) instanceof Player targetPlayer) {

                if (context.player().getData(DataAttachments.ATTACK_COOLDOWN) > 0) return;
                context.player().setData(DataAttachments.ATTACK_COOLDOWN, 20);
                PacketDistributor.sendToAllPlayers(new SyncAttackCooldown(context.player().getId(), 20));

                if (!Util.isAttackValid(context.player(), targetPlayer)) return;

                Util.attemptToHitPlayer(targetPlayer, 25f, 0.2f, true, HitboxType.fromString(packet.hbType().toString()));
            }
        });
    }
}
