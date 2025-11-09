package by.sakeplays.cycle_of_life.entity;

import by.sakeplays.cycle_of_life.common.data.SelectedColors;
import by.sakeplays.cycle_of_life.entity.util.ColorableBodyParts;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModEntityDataSerializers {

    public static final DeferredRegister<EntityDataSerializer<?>> DATA_SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.ENTITY_DATA_SERIALIZERS,
            "cycle_of_life");

    public static final EntityDataSerializer<SelectedColors> SELECTED_COLORS = new EntityDataSerializer<>() {

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, SelectedColors> codec() {
            return SelectedColors.CODEC;
        }

        @Override
        public SelectedColors copy(SelectedColors value) {
            SelectedColors copy = new SelectedColors();
            for (ColorableBodyParts part : ColorableBodyParts.values()) {
                copy.setColor(part, value.getColor(part));
            }
            return copy;
        }
    };

    public static final Supplier<EntityDataSerializer<SelectedColors>> SELECTED_COLOR = DATA_SERIALIZERS.register(
            "selected_colors_serializer", () -> SELECTED_COLORS);



    public static void register(IEventBus bus) {
        DATA_SERIALIZERS.register(bus);
    }
}
