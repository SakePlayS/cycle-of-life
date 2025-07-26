package by.sakeplays.cycle_of_life.client;

import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.Position;
import by.sakeplays.cycle_of_life.entity.util.Dinosaurs;
import by.sakeplays.cycle_of_life.entity.util.HitboxType;
import by.sakeplays.cycle_of_life.util.AssociatedAABB;
import by.sakeplays.cycle_of_life.util.Util;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClientHitboxData {

    public static Map<Integer, ArrayList<AssociatedAABB>> hitboxMap = new HashMap<>();
    public static ArrayList<AssociatedAABB> HITBOXES = new ArrayList<>();


    public static void updateHitboxes(GeoBone head, GeoBone body1, GeoBone body2, GeoBone tail1, GeoBone tail2,
                                      Player player, float partialTick) {
        float growth = player.getData(DataAttachments.DINO_DATA).getGrowth();

        ArrayList<AssociatedAABB> list = new ArrayList<>(5);
        AssociatedAABB headAABB = createAABB(head, player, growth, partialTick);
        AssociatedAABB body1AABB = createAABB(body1, player, growth, partialTick);
        AssociatedAABB body2AABB = createAABB(body2, player, growth, partialTick);
        AssociatedAABB tail1AABB = createAABB(tail1, player, growth, partialTick);
        AssociatedAABB tail2AABB = createAABB(tail2, player, growth, partialTick);

        headAABB.setType(HitboxType.HEAD);
        body2AABB.setType(HitboxType.BODY2);
        tail1AABB.setType(HitboxType.TAIL1);
        tail2AABB.setType(HitboxType.TAIL2);

        list.add(headAABB);
        list.add(body1AABB);
        list.add(body2AABB);
        list.add(tail1AABB);
        list.add(tail2AABB);

        HITBOXES.removeIf(aabb -> aabb.getPlayer() == player);
        HITBOXES.addAll(list);

        hitboxMap.put(player.getId(), list);

    }

    private static AssociatedAABB createAABB(GeoBone bone, Player player, float growth, float partialTick) {

        GeoCube cube = bone.getCubes().getFirst();
        Position boneWorldPos = new Position(
                player.getPosition(partialTick).x + bone.getWorldPosition().x,
                player.getPosition(partialTick).y + bone.getWorldPosition().y,
                player.getPosition(partialTick).z + bone.getWorldPosition().z);

        return new AssociatedAABB(
                (boneWorldPos.x() - cube.size().x/32 * growth),
                (boneWorldPos.y() - cube.size().y/32 * growth),
                (boneWorldPos.z() - cube.size().z/32 * growth),

                (boneWorldPos.x() + cube.size().x/32 * growth),
                (boneWorldPos.y() + cube.size().y/32 * growth),
                (boneWorldPos.z() + cube.size().z/32 * growth),
                player
        );
    }

}
