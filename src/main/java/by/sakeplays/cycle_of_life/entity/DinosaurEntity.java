package by.sakeplays.cycle_of_life.entity;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public abstract class DinosaurEntity extends LivingEntity {

    public DinosaurEntity(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    public volatile Integer playerId;
    public boolean isForScreenRendering = false;

    public Player getPlayer() {

        if (level().getEntity(playerId) instanceof Player player && playerId != null) {

            return player;
        }

        return null;
    }


    @Override
    public @NotNull Iterable<ItemStack> getArmorSlots() {
        return NonNullList.withSize(4, ItemStack.EMPTY);
    }

    @Override
    public @NotNull ItemStack getItemBySlot(EquipmentSlot equipmentSlot) {
        Player player = getPlayer();

        if (player != null) {
            return player.getItemBySlot(equipmentSlot);
        }

        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot equipmentSlot, ItemStack itemStack) {
        Player player = getPlayer();

        if (player != null) {
            player.setItemSlot(equipmentSlot, itemStack);
        }
    }

    @Override
    public @NotNull HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 10D)
                .add(Attributes.MOVEMENT_SPEED, 0D)
                .add(Attributes.FOLLOW_RANGE, 24D);
    }




}
