package by.sakeplays.cycle_of_life.common.data;

import by.sakeplays.cycle_of_life.entity.util.ColorableBodyParts;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.*;

public class SelectedColors {

    private EnumMap<ColorableBodyParts, Pair<Integer, Integer>> colors;

    public SelectedColors() {
        colors = new EnumMap<>(ColorableBodyParts.class);
        for (ColorableBodyParts part : ColorableBodyParts.values()) {
            colors.put(part, Pair.of(0, 0));
        }
    }

    public SelectedColors copy() {
        return new SelectedColors().override(this);
    }

    public SelectedColors override(SelectedColors newColors) {
        for (Map.Entry<ColorableBodyParts, Pair<Integer, Integer>> e : newColors.colors.entrySet()) {
            colors.put(e.getKey(), e.getValue());
        }
        return this;
    }

    public SelectedColors setColor(ColorableBodyParts part, int primary, int secondary) {
        colors.put(part, Pair.of(primary, secondary));
        return this;
    }

    public Pair<Integer, Integer> getColor(ColorableBodyParts part) {
        return colors.getOrDefault(part, Pair.of(0, 0));
    }

    public static SelectedColors fromNBT(CompoundTag tag) {
        SelectedColors selectedColors = new SelectedColors();
        Set<String> keys = tag.getAllKeys();


        for (String key : keys) {
            CompoundTag savedColors = tag.getCompound(key);
            selectedColors.setColor(ColorableBodyParts.fromString(key), savedColors.getInt("Primary"), savedColors.getInt("Secondary"));
        }

        return selectedColors;
    }

    public void toNBT(CompoundTag tag) {
        for (Map.Entry<ColorableBodyParts, Pair<Integer, Integer>> entry : colors.entrySet()) {

            CompoundTag colors = new CompoundTag();
            colors.putInt("Primary", entry.getValue().first());
            colors.putInt("Secondary", entry.getValue().second());

            tag.put(entry.getKey().toString().toLowerCase(Locale.ROOT), colors);
        }
    }

    public static final StreamCodec<FriendlyByteBuf, SelectedColors> CODEC =
            StreamCodec.of(
                    (buf, colors) -> {
                        for (ColorableBodyParts part : ColorableBodyParts.values()) {
                            buf.writeInt(colors.getColor(part).first());
                            buf.writeInt(colors.getColor(part).second());
                        }
                    },
                    buf -> {
                        SelectedColors colors = new SelectedColors();
                        for (ColorableBodyParts part : ColorableBodyParts.values()) {
                            colors.setColor(part, buf.readInt(), buf.readInt());
                        }
                        return colors;
                    }
            );
}
