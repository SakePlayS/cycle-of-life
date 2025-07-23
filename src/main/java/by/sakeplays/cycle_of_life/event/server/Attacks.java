package by.sakeplays.cycle_of_life.event.server;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.Util;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.DinoData;
import by.sakeplays.cycle_of_life.common.data.SkinData;
import by.sakeplays.cycle_of_life.common.data.adaptations.Adaptation;
import by.sakeplays.cycle_of_life.common.data.adaptations.EnhancedStamina;
import by.sakeplays.cycle_of_life.entity.DinosaurEntity;
import by.sakeplays.cycle_of_life.entity.HitboxEntity;
import by.sakeplays.cycle_of_life.entity.util.Dinosaurs;
import by.sakeplays.cycle_of_life.event.client.HandleKeys;
import by.sakeplays.cycle_of_life.network.bidirectional.*;
import by.sakeplays.cycle_of_life.network.to_client.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

//  Some attacks have to be handled server-side.


@EventBusSubscriber(modid = CycleOfLife.MODID, bus = EventBusSubscriber.Bus.GAME)
public class Attacks {
    @SubscribeEvent
    public static void onTick(EntityTickEvent.Post event) {
        if (event.getEntity() instanceof Player player && !player.level().isClientSide) {

            player.setData(DataAttachments.ATTACK_TIMER, player.getData(DataAttachments.ATTACK_TIMER) - 1);

            deinonychus(player);

        }
    }


    public static void deinonychus(Player player) {

        PacketDistributor.sendToPlayer((ServerPlayer) player, new SyncTurningLock(player.getId(),
                player.getData(DataAttachments.ATTACK_TIMER) > 0));

        if (player.getData(DataAttachments.ATTACK_TIMER) > 8) {
            float newTurnDegree = player.getData(DataAttachments.PLAYER_TURN) +
                    (player.getData(DataAttachments.DESIRED_ATTACK_ANGLE) / 7f);

            player.setData(DataAttachments.PLAYER_TURN, newTurnDegree);
            PacketDistributor.sendToAllPlayers(new SyncTurnDegree(newTurnDegree, player.getId()));
        }

        if (player.getData(DataAttachments.ATTACK_TIMER) == 8) {

            DinoData dinoData = player.getData(DataAttachments.DINO_DATA);

            Player target = null;

            if (!player.getData(DataAttachments.HITBOXES_INITIALIZED)) return;

            if (player.level().getEntity(player.getData(DataAttachments.HITBOX_DATA).getHeadId()) == null) return;
            AABB head = player.level().getEntity(player.getData(DataAttachments.HITBOX_DATA).getHeadId()).getBoundingBox();
            AABB slash = head.inflate(0.2 * dinoData.getGrowth(), 0.5 * dinoData.getGrowth(), 0.2 * dinoData.getGrowth())
                    .move(0, -0.8 * dinoData.getGrowth(), 0);

            List<HitboxEntity> possibleTargets = player.level().getEntities(EntityTypeTest.forClass(HitboxEntity.class), slash, e -> true);

            for (HitboxEntity hitbox : possibleTargets) {
                if (hitbox.getPlayer() != player) {
                    target = hitbox.getPlayer();
                    break;
                }
            }

            if (target == null) return;

            Util.attemptToHitPlayer(target, slash, 20f * dinoData.getGrowth(), 0.45f * dinoData.getGrowth(), true);
        }
    }
}
