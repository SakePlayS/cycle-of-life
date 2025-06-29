package by.sakeplays.cycle_of_life.event.client;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.DataAttachments;
import by.sakeplays.cycle_of_life.entity.util.DinosaursList;
import by.sakeplays.cycle_of_life.network.to_server.SyncTurnDegree2S;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = CycleOfLife.MODID, value = Dist.CLIENT)
public class HandleKeys {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {

        if (Minecraft.getInstance().player == null) {
            return;
        }

        if (KeyMappingsEvent.FORWARD_MAPPING.isDown()) {
            LocalPlayer player = Minecraft.getInstance().player;

            if (player == null) {
                return;
            }

            double playerRot = (player.getData(DataAttachments.TURN_DEGREE));

            double dx = Math.sin(playerRot);
            double dz = Math.cos(playerRot);


            Vec3 deltaMovement = player.getDeltaMovement();

            deltaMovement = deltaMovement.add(dx/10, 0, dz/10);

            player.setDeltaMovement(deltaMovement);
        }

        float turnSpeed = Mth.DEG_TO_RAD * getTurnSpeed(Minecraft.getInstance().player);

        if (KeyMappingsEvent.LEFT_MAPPING.isDown()) {
            LocalPlayer player = Minecraft.getInstance().player;

            player.setData(DataAttachments.TURN_DEGREE, player.getData(DataAttachments.TURN_DEGREE) + turnSpeed);
            PacketDistributor.sendToServer(new SyncTurnDegree2S(player.getData(DataAttachments.TURN_DEGREE)));   // sync turn degree
        }

        if (KeyMappingsEvent.RIGHT_MAPPING.isDown()) {
            LocalPlayer player = Minecraft.getInstance().player;

            player.setData(DataAttachments.TURN_DEGREE, player.getData(DataAttachments.TURN_DEGREE) - turnSpeed);
            PacketDistributor.sendToServer(new SyncTurnDegree2S(player.getData(DataAttachments.TURN_DEGREE)));    // sync turn degree
        }
    }


    private static float getTurnSpeed(Player player) {
        int selectedDino = (player.getData(DataAttachments.SELECTED_DINOSAUR).getValue());

        if (selectedDino == DinosaursList.PACHYCEPHALOSAURUS.getID()) return DinosaursList.PACHYCEPHALOSAURUS.getTurnSpeed();
        if (selectedDino == DinosaursList.DEINONYCHUS.getID()) return DinosaursList.DEINONYCHUS.getTurnSpeed();

        return 0f;
    }
}
