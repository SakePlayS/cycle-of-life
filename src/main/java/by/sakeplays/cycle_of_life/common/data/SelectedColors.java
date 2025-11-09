package by.sakeplays.cycle_of_life.common.data;

import by.sakeplays.cycle_of_life.client.screen.util.ColorHolder;
import by.sakeplays.cycle_of_life.entity.util.ColorableBodyParts;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.*;

public class SelectedColors {

    private EnumMap<ColorableBodyParts, Integer> colors;

    public SelectedColors() {
        colors = new EnumMap<>(ColorableBodyParts.class);
        for (ColorableBodyParts part : ColorableBodyParts.values()) {
            colors.put(part, 0);
        }
    }

    public SelectedColors copy() {
        return new SelectedColors().override(this);
    }

    public SelectedColors override(SelectedColors newColors) {
        colors = new EnumMap<>(newColors.colors);
        return this;
    }

    public void setColor(ColorableBodyParts part, int color) {
        colors.put(part, color);
    }

    public int getColor(ColorableBodyParts part) {
        return colors.getOrDefault(part, 0);
    }

    public static SelectedColors fromNBT(CompoundTag tag) {
        SelectedColors colors = new SelectedColors();
        Set<String> keys = tag.getAllKeys();

        for (String key : keys) {
            colors.setColor(ColorableBodyParts.fromString(key), tag.getInt(key));
        }

        return colors;
    }

    public void toNBT(CompoundTag tag) {
        for (Map.Entry<ColorableBodyParts, Integer> entry : colors.entrySet()) {
            tag.putInt(entry.getKey().toString().toLowerCase(Locale.ROOT), entry.getValue());
        }
    }

    public static final StreamCodec<FriendlyByteBuf, SelectedColors> CODEC =
            StreamCodec.of(
                    (buf, colors) -> {
                        for (ColorableBodyParts part : ColorableBodyParts.values()) {
                            buf.writeInt(colors.getColor(part));
                        }
                    },
                    buf -> {
                        SelectedColors colors = new SelectedColors();
                        for (ColorableBodyParts part : ColorableBodyParts.values()) {
                            colors.setColor(part, buf.readInt());
                        }
                        return colors;
                    }
            );
}
