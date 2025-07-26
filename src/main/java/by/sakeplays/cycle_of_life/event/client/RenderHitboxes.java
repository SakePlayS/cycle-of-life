package by.sakeplays.cycle_of_life.event.client;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.client.ClientHitboxData;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.Position;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;


@EventBusSubscriber(modid = CycleOfLife.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class RenderHitboxes {


    @SubscribeEvent
    public static void onRenderWorld(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;

        if (Minecraft.getInstance().options.reducedDebugInfo().get()) return;
        if (!Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) return;

        if (ClientHitboxData.hitboxMap.isEmpty()) return;

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        Camera camera = event.getCamera();
        Level level = Minecraft.getInstance().level;
        Vec3 camPos = camera.getPosition();
        LocalPlayer localPlayer = Minecraft.getInstance().player;

        if (localPlayer == null) return;

        float partialTick = event.getPartialTick().getGameTimeDeltaTicks();
        for (Player player : level.players()) {

            poseStack.pushPose();

            float growth = player.getData(DataAttachments.DINO_DATA).getGrowth();
            Position headPos = player.getData(DataAttachments.HITBOX_DATA).getHeadHitboxPos();

            var list = ClientHitboxData.hitboxMap.get(player.getId());
            if (list == null || list.size() < 5) {
                poseStack.popPose(); // Avoid stack corruption
                continue;
            }


            renderAABBWireframe(ClientHitboxData.hitboxMap.get(player.getId()).get(0), poseStack, bufferSource, camPos, 1f, 0f, 0f, 1f);
            renderAABBWireframe(ClientHitboxData.hitboxMap.get(player.getId()).get(1), poseStack, bufferSource, camPos, 1f, 0f, 0f, 1f);
            renderAABBWireframe(ClientHitboxData.hitboxMap.get(player.getId()).get(2), poseStack, bufferSource, camPos, 1f, 0f, 0f, 1f);
            renderAABBWireframe(ClientHitboxData.hitboxMap.get(player.getId()).get(3), poseStack, bufferSource, camPos, 1f, 0f, 0f, 1f);
            renderAABBWireframe(ClientHitboxData.hitboxMap.get(player.getId()).get(4), poseStack, bufferSource, camPos, 1f, 0f, 0f, 1f);

            poseStack.popPose();

        }

        bufferSource.endBatch();

    }


    private static AABB createHitbox(Position center, float width, float height) {
        return new AABB(
                center.x() - width / 2, center.y() - height / 2, center.z() - width / 2,
                center.x() + width / 2, center.y() + height / 2, center.z() + width / 2
        );
    }

    public static void renderAABBWireframe(AABB box, PoseStack poseStack, MultiBufferSource bufferSource, Vec3 cameraPos,
                                           float red, float green, float blue, float alpha) {
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.lines());
        Matrix4f matrix = poseStack.last().pose();

        // Offset box by camera to render relative to view
        AABB shiftedBox = box.move(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        Vec3[] corners = {
                new Vec3(shiftedBox.minX, shiftedBox.minY, shiftedBox.minZ),
                new Vec3(shiftedBox.maxX, shiftedBox.minY, shiftedBox.minZ),
                new Vec3(shiftedBox.maxX, shiftedBox.minY, shiftedBox.maxZ),
                new Vec3(shiftedBox.minX, shiftedBox.minY, shiftedBox.maxZ),
                new Vec3(shiftedBox.minX, shiftedBox.maxY, shiftedBox.minZ),
                new Vec3(shiftedBox.maxX, shiftedBox.maxY, shiftedBox.minZ),
                new Vec3(shiftedBox.maxX, shiftedBox.maxY, shiftedBox.maxZ),
                new Vec3(shiftedBox.minX, shiftedBox.maxY, shiftedBox.maxZ)
        };

        int[][] edges = {
                {0, 1}, {1, 2}, {2, 3}, {3, 0},
                {4, 5}, {5, 6}, {6, 7}, {7, 4},
                {0, 4}, {1, 5}, {2, 6}, {3, 7}
        };

        for (int[] edge : edges) {
            Vec3 from = corners[edge[0]];
            Vec3 to = corners[edge[1]];

            consumer.addVertex(matrix, (float) from.x, (float) from.y, (float) from.z)
                    .setColor(red, green, blue, alpha)
                    .setNormal(0, 1, 0);

            consumer.addVertex(matrix, (float) to.x, (float) to.y, (float) to.z)
                    .setColor(red, green, blue, alpha)
                    .setNormal(0, 1, 0);
        }
    }

}
