package by.sakeplays.cycle_of_life.network.to_server.attacks.deinonychus;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.util.Util;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.DinoData;
import by.sakeplays.cycle_of_life.entity.COLEntities;
import by.sakeplays.cycle_of_life.entity.DinosaurEntity;
import by.sakeplays.cycle_of_life.entity.MeatChunkEntity;
import by.sakeplays.cycle_of_life.entity.util.Diet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record RequestGrabFood() implements CustomPacketPayload {

    public static final Type<RequestGrabFood> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "request_grab_food"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, RequestGrabFood> STREAM_CODEC = StreamCodec.unit(new RequestGrabFood());

    public static void handleServer(final RequestGrabFood packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (Util.getDino(context.player()).getID() == 0) return;
            Player player = context.player();
            DinoData dinoData = player.getData(DataAttachments.DINO_DATA);

            if (player.getData(DataAttachments.DINO_DATA).isCarryingItem()) return;


            if (!player.getData(DataAttachments.HITBOXES_INITIALIZED)) return;

            if (Util.getDino(player).getDiet() == Diet.CARNIVORE) grabCarnivore(player, dinoData);


        });
    }

    private static void grabCarnivore(Player player, DinoData dinoData) {
        if (player.level().getEntity(player.getData(DataAttachments.HITBOX_DATA).getHeadId()) == null) return;
        AABB head = player.level().getEntity(player.getData(DataAttachments.HITBOX_DATA).getHeadId()).getBoundingBox();
        AABB grabHitbox = head.inflate(0.2 * dinoData.getGrowth(), 1.5 * dinoData.getGrowth(), 0.2 * dinoData.getGrowth());

        List<MeatChunkEntity> meatChunks = player.level().getEntities(EntityTypeTest.forClass(MeatChunkEntity.class),
                grabHitbox, e -> true);

        List<DinosaurEntity> corpses = player.level().getEntities(EntityTypeTest.forClass(DinosaurEntity.class),
                grabHitbox, DinosaurEntity::isBody);


        for (DinosaurEntity corpse : corpses) {
            MeatChunkEntity chunk = new MeatChunkEntity(COLEntities.MEAT_CHUNK.get(), player.level());
            float chunkWeight = Math.min(corpse.getRemainingWeight(), player.getData(DataAttachments.DINO_DATA).getWeight() / 50f);

            chunk.setSize(chunkWeight);
            chunk.setCarrier(player.getId());
            corpse.setRemainingWeight(corpse.getRemainingWeight() - chunkWeight);
            chunk.setPos(
                    player.getData(DataAttachments.HITBOX_DATA).getGrabHandlerPos().x(),
                    player.getData(DataAttachments.HITBOX_DATA).getGrabHandlerPos().y(),
                    player.getData(DataAttachments.HITBOX_DATA).getGrabHandlerPos().z());


            player.level().addFreshEntity(chunk);
            player.getData(DataAttachments.DINO_DATA).setCarryingItem(true);
            return;
        }


        for (MeatChunkEntity meatChunk : meatChunks) {
            float maxWeight = player.getData(DataAttachments.DINO_DATA).getWeight() / 50f;

            if (meatChunk.getSize() > maxWeight) {
                MeatChunkEntity newChunk = new MeatChunkEntity(COLEntities.MEAT_CHUNK.get(), player.level());

                newChunk.setSize(maxWeight);
                newChunk.setCarrier(player.getId());
                meatChunk.setSize(meatChunk.getSize() - maxWeight);
                newChunk.setPos(
                        player.getData(DataAttachments.HITBOX_DATA).getGrabHandlerPos().x(),
                        player.getData(DataAttachments.HITBOX_DATA).getGrabHandlerPos().y(),
                        player.getData(DataAttachments.HITBOX_DATA).getGrabHandlerPos().z());


                player.level().addFreshEntity(newChunk);
                player.getData(DataAttachments.DINO_DATA).setCarryingItem(true);

                return;
            } else {
                meatChunk.setCarrier(player.getId());
                player.getData(DataAttachments.DINO_DATA).setCarryingItem(true);

                return;
            }
        }
    }
}
