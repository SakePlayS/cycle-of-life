package by.sakeplays.cycle_of_life.network.to_server.attacks.pachycephalosaurus;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.DinoData;
import by.sakeplays.cycle_of_life.entity.util.Dinosaurs;
import by.sakeplays.cycle_of_life.entity.util.HitboxType;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncKnockdownTime;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncStamina;
import by.sakeplays.cycle_of_life.network.to_client.ApplyKnockback;
import by.sakeplays.cycle_of_life.network.to_client.SyncAttackCooldown;
import by.sakeplays.cycle_of_life.util.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RequestPachyBash(int target, String hbType) implements CustomPacketPayload {

    public static final Type<RequestPachyBash> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "request_pachy_bash"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, RequestPachyBash> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, RequestPachyBash::target,
            ByteBufCodecs.STRING_UTF8, RequestPachyBash::hbType,
            RequestPachyBash::new
    );


    public static void handleServer(final RequestPachyBash packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            DinoData dataSource = context.player().getData(DataAttachments.DINO_DATA);

            float newStam = Math.max(0, dataSource.getStamina() - 25);
            float damageModifier = dataSource.getWeight() / Dinosaurs.PACHYCEPHALOSAURUS.getWeight();

            dataSource.setStamina(newStam);
            PacketDistributor.sendToAllPlayers(new SyncStamina(context.player().getId(), newStam));

            if (HitboxType.fromString(packet.hbType()) == HitboxType.NONE) return;


            if (context.player().level().getEntity(packet.target) instanceof Player targetPlayer) {

                DinoData dataTarget = targetPlayer.getData(DataAttachments.DINO_DATA);

                if (context.player().getData(DataAttachments.ATTACK_COOLDOWN) > 0) return;
                context.player().setData(DataAttachments.ATTACK_COOLDOWN, 20);
                PacketDistributor.sendToAllPlayers(new SyncAttackCooldown(context.player().getId(), 20));



                if (!Util.isAttackValid(context.player(), targetPlayer)) return;

                float dx = (float) -Math.sin(context.player().getData(DataAttachments.PLAYER_ROTATION));
                float dz = (float) Math.cos(context.player().getData(DataAttachments.PLAYER_ROTATION));

                Util.attemptToHitPlayer(targetPlayer, 70f * damageModifier, 0f, true, HitboxType.fromString(packet.hbType().toString()));

                if (targetPlayer.getData(DataAttachments.KNOCKDOWN_TIME) < -10 && dataSource.getWeight() * 1.5f > dataTarget.getWeight()) {
                    targetPlayer.setData(DataAttachments.KNOCKDOWN_TIME, 35);
                    PacketDistributor.sendToAllPlayers(new SyncKnockdownTime(targetPlayer.getId(), 35));
                    PacketDistributor.sendToPlayer((ServerPlayer) targetPlayer, new ApplyKnockback(targetPlayer.getId(), dx, 0.2f, dz, 0.65f));
                }

                targetPlayer.level().playSound(null, targetPlayer.getX(), targetPlayer.getY(), targetPlayer.getZ(),
                        SoundEvents.ZOMBIE_ATTACK_WOODEN_DOOR, SoundSource.PLAYERS, 1f ,1.4f +
                                (float) ((Math.random() - 0.5) / 4));
            }
        });
    }
}
