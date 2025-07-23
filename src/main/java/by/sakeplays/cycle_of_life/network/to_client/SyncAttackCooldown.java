package by.sakeplays.cycle_of_life.network.to_client;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncAttackCooldown(int playerId, int i) implements CustomPacketPayload {

    public static final Type<SyncAttackCooldown> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "sync_cooldown"));


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, SyncAttackCooldown> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SyncAttackCooldown::playerId,
            ByteBufCodecs.INT, SyncAttackCooldown::i,
            SyncAttackCooldown::new
    );

    public static void handleClient(final SyncAttackCooldown packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerId()) instanceof Player player) {

                player.setData(DataAttachments.ATTACK_COOLDOWN, packet.i);

            }
        });
    }
}
