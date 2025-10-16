package by.sakeplays.cycle_of_life.event.client;

import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.function.Consumer;

public enum AttackType {

    DEINONYCHUS_SLASH(false, false, 0, 0, 15, 0, AttackTrigger.MAIN_2,
            Map.of(0, action -> {})
    ),
    DEINONYCHUS_SLASH_ALT(true, true, 8, 16, 24, 0, AttackTrigger.ALT_1,
            Map.of(0, action -> {})
    ),
    DEINONYCHUS_BITE(false, false, 0, 0, 15, 0, AttackTrigger.MAIN_1,
            Map.of(0, action -> {})
    ),
    PACHYCEPHALOSAURUS_CHARGED_BASH(false, false, 0, 0, 30, 3, AttackTrigger.MAIN_1,
            Map.of(0, action -> {})
    ),
    PACHYCEPHALOSAURUS_BASH(true, false, 8, 16, 24, 3, AttackTrigger.ALT_1,
            Map.of(0, action -> {})
    )
    ;


    private final boolean isAltAttack;
    private final boolean allowSprintAltAttack;
    private final int ticksToAlign;
    private final int altAttackLength;
    private final int cooldown;
    private final int minPriority;
    private final AttackTrigger attackTrigger;
    private final Map<Integer, Consumer<Player>> specialActions;

    AttackType(boolean isAltAttack, boolean allowSprintAltAttack, int ticksToAlign, int altAttackLength, int cooldown, int minPriority, AttackTrigger attackTrigger, Map<Integer, Consumer<Player>> specialActions) {
        this.isAltAttack = isAltAttack;
        this.allowSprintAltAttack = allowSprintAltAttack;
        this.ticksToAlign = ticksToAlign;
        this.altAttackLength = altAttackLength;
        this.cooldown = cooldown;
        this.minPriority = minPriority;
        this.attackTrigger = attackTrigger;
        this.specialActions = specialActions;
    }

    public boolean isAltAttack() {
        return isAltAttack;
    }

    public int getTicksToAlign() {
        return ticksToAlign;
    }

    public int getCooldown() {
        return cooldown;
    }

    public boolean allowsSprintAltAttack() {
        return allowSprintAltAttack;
    }

    public int getAltAttackLength() {
        return altAttackLength;
    }

    public int getMinPriority() {
        return minPriority;
    }

    public AttackTrigger getAttackTrigger() {
        return attackTrigger;
    }

    public Map<Integer, Consumer<Player>> getSpecialActions() {
        return specialActions;
    }
}

