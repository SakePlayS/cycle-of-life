package by.sakeplays.cycle_of_life.network.to_client;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.DinoData;
import by.sakeplays.cycle_of_life.common.data.DinosaurFood;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncDiets(int playerId, float carbs, float lipids, float vitamins, float proteins) implements CustomPacketPayload {

    public static final Type<SyncDiets> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "sync_diets"));


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, SyncDiets> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SyncDiets::playerId,
            ByteBufCodecs.FLOAT, SyncDiets::carbs,
            ByteBufCodecs.FLOAT, SyncDiets::lipids,
            ByteBufCodecs.FLOAT, SyncDiets::vitamins,
            ByteBufCodecs.FLOAT, SyncDiets::proteins,

            SyncDiets::new
    );

    public static void handleClient(final SyncDiets packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(packet.playerId()) instanceof Player player) {

                DinoData dinoData = player.getData(DataAttachments.DINO_DATA);

                dinoData.setVitamins(packet.vitamins());
                dinoData.setCarbs(packet.carbs());
                dinoData.setProteins(packet.proteins());
                dinoData.setLipids(packet.lipids());

            }
        });
    }
}
