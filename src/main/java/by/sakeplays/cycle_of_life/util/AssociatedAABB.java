package by.sakeplays.cycle_of_life.util;

import by.sakeplays.cycle_of_life.entity.util.HitboxType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

public class AssociatedAABB extends AABB {

    private final Player player;
    private HitboxType type =  HitboxType.BODY1;


    public AssociatedAABB(double x1, double y1, double z1, double x2, double y2, double z2, Player player) {
        super(x1, y1, z1, x2, y2, z2);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public HitboxType getType() {
        return type;
    }

    public void setType(HitboxType type) {
        this.type = type;
    }
}
