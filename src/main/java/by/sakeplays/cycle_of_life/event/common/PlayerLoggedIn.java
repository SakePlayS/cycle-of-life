package by.sakeplays.cycle_of_life.event.common;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.Util;
import by.sakeplays.cycle_of_life.common.data.*;
import by.sakeplays.cycle_of_life.network.bidirectional.*;
import by.sakeplays.cycle_of_life.network.to_client.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

@EventBusSubscriber(modid = CycleOfLife.MODID, bus = EventBusSubscriber.Bus.GAME)
public class PlayerLoggedIn {

    @SubscribeEvent
    public static void onPlayerLogIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();

        if (!player.level().isClientSide) {

            player.setData(DataAttachments.REST_FACTOR, 1f);

            AdaptationData data = player.getData(DataAttachments.ADAPTATION_DATA);

            PacketDistributor.sendToPlayer((ServerPlayer) player,
                    new SyncAdaptation("SALTWATER_TOLERANCE", data.SALTWATER_TOLERANCE.getProgress(),
                            data.SALTWATER_TOLERANCE.getLevel(), player.getId(), data.SALTWATER_TOLERANCE.isUpgraded()));

            PacketDistributor.sendToPlayer((ServerPlayer) player,
                    new SyncAdaptation("ENHANCED_STAMINA", data.ENHANCED_STAMINA.getProgress(),
                            data.ENHANCED_STAMINA.getLevel(), player.getId(), data.ENHANCED_STAMINA.isUpgraded()));

            PacketDistributor.sendToAllPlayers(
                    new SyncSelectedDinosaur(player.getId(), player.getData(DataAttachments.DINO_DATA).getSelectedDinosaur()));

        }
    }
}
