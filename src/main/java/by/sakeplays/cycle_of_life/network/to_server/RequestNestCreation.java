package by.sakeplays.cycle_of_life.network.to_server;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.block.ModBlocks;
import by.sakeplays.cycle_of_life.client.screen.util.ColorHolder;
import by.sakeplays.cycle_of_life.common.data.*;
import by.sakeplays.cycle_of_life.network.to_client.SyncOwnNest;
import by.sakeplays.cycle_of_life.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record RequestNestCreation() implements CustomPacketPayload {

    public static final Type<RequestNestCreation> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "request_nest_creation"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, RequestNestCreation> STREAM_CODEC = StreamCodec.unit(
            new RequestNestCreation()
    );

    public static void handleServer(final RequestNestCreation packet, final IPayloadContext context) {
        context.enqueueWork(() -> {

            Player player = context.player();
            ServerLevel serverLevel = (ServerLevel) player.level();
            MinecraftServer minecraftServer = player.getServer();

            PairData data = player.getData(DataAttachments.PAIRING_DATA);

            if (!data.isPaired()) return;
            if (data.getMateUUID().equals(PairData.UNSET)) return;
            if (data.getMateName().isEmpty()) return;

            Player mate = serverLevel.getPlayerByUUID(data.getMateUUID());
            if (mate == null) return;

            Player male = player.getData(DataAttachments.DINO_DATA).isMale() ? player : mate;
            Player female = !player.getData(DataAttachments.DINO_DATA).isMale() ? player : mate;

            if (!Util.getDino(player).equals(Util.getDino(mate))) return;

            UUID patriarchUUID = male.getUUID();
            UUID matriarchUUID = female.getUUID();

            ColorHolder patriarchColors = ColorHolder.fromSkinData(male.getData(DataAttachments.SKIN_DATA));
            ColorHolder matriarchColors = ColorHolder.fromSkinData(female.getData(DataAttachments.SKIN_DATA));

            BlockPos pos = new BlockPos(player.getOnPos().getX(), player.getOnPos().getY() + 1, player.getOnPos().getZ());

            if (NestData.get(minecraftServer).containsNestID(patriarchUUID)) return;

            Nest nest = new Nest(matriarchUUID, patriarchUUID, Util.getDino(player).getMaxEggs(), pos,
                    false, player.getData(DataAttachments.DINO_DATA).getSelectedDinosaur(), patriarchColors, matriarchColors);

            nest.setPatriarchLifeUUID(male.getData(DataAttachments.PAIRING_DATA).getLifeUUID());
            nest.setMatriarchLifeUUID(female.getData(DataAttachments.PAIRING_DATA).getLifeUUID());
            nest.setPatriarchName(male.getName().getString());
            nest.setMatriarchName(female.getName().getString());

            NestData.get(minecraftServer).addNest(nest);
            NestData.get(minecraftServer).forceSetEggs(nest, 5);

            context.player().level().setBlock(nest.getPos(), ModBlocks.DEINONYCHUS_NEST.get().defaultBlockState(), Block.UPDATE_ALL);

            PacketDistributor.sendToPlayer((ServerPlayer) player, new SyncOwnNest(nest));
            PacketDistributor.sendToPlayer((ServerPlayer) mate, new SyncOwnNest(nest));
        });
    }
}
