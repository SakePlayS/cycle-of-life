package by.sakeplays.cycle_of_life.event.common;


import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.entity.ModEntities;
import by.sakeplays.cycle_of_life.entity.DinosaurEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@EventBusSubscriber(modid = CycleOfLife.MODID, bus = EventBusSubscriber.Bus.MOD)
public class OnRegisterAttributes {

    @SubscribeEvent
    public static void event(EntityAttributeCreationEvent event) {

        AttributeSupplier supplier = DinosaurEntity.createAttributes().build();

        event.put(ModEntities.PACHYCEPHALOSAURUS.get(), supplier);
        event.put(ModEntities.DEINONYCHUS.get(), supplier);
        event.put(ModEntities.PTERANODON.get(), supplier);

    }
}
