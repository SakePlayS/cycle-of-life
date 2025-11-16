package by.sakeplays.cycle_of_life.network.to_server.attacks.pteranodon;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.DinoData;
import by.sakeplays.cycle_of_life.entity.util.Dinosaurs;
import by.sakeplays.cycle_of_life.entity.util.HitboxType;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncStamina;
import by.sakeplays.cycle_of_life.network.to_client.SyncAttackCooldown;
import by.sakeplays.cycle_of_life.util.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RequestPteranodonFlightPeck(int target, String hbType) implements CustomPacketPayload {

    public static final Type<RequestPteranodonFlightPeck> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "request_pteranodon_flight_peck"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
    public static final StreamCodec<FriendlyByteBuf, RequestPteranodonFlightPeck> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, RequestPteranodonFlightPeck::target,
            ByteBufCodecs.STRING_UTF8, RequestPteranodonFlightPeck::hbType,
            RequestPteranodonFlightPeck::new
    );


    public static void handleServer(final RequestPteranodonFlightPeck packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            DinoData data = context.player().getData(DataAttachments.DINO_DATA);
            float newStam = Math.max(0, data.getStamina() - 12);
            float damageModifier = data.getWeight() / Dinosaurs.PTERANODON.getWeight();

            data.setStamina(newStam);
            PacketDistributor.sendToAllPlayers(new SyncStamina(context.player().getId(), newStam));

            if (HitboxType.fromString(packet.hbType()) == HitboxType.NONE) return;

            if (context.player().level().getEntity(packet.target) instanceof Player targetPlayer) {

                if (context.player().getData(DataAttachments.ATTACK_COOLDOWN) > 0) return;
                context.player().setData(DataAttachments.ATTACK_COOLDOWN, 25);
                PacketDistributor.sendToAllPlayers(new SyncAttackCooldown(context.player().getId(), 25));


                if (!Util.isAttackValid(context.player(), targetPlayer)) return;

                Util.attemptToHitPlayer(targetPlayer, 25f * damageModifier, 0f, true, HitboxType.fromString(packet.hbType()));
            }
        });
    }
}
