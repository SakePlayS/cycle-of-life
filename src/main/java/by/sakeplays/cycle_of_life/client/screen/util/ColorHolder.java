package by.sakeplays.cycle_of_life.client.screen.util;

import by.sakeplays.cycle_of_life.common.data.Nest;
import by.sakeplays.cycle_of_life.common.data.SkinData;
import by.sakeplays.cycle_of_life.entity.util.ColorableBodyParts;
import by.sakeplays.cycle_of_life.entity.util.Dinosaurs;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

import java.util.List;
import java.util.UUID;

public class ColorHolder {
    public int eyes, markings, body, flank, belly, maleDisplay;

    public ColorHolder(int eyes, int markings, int body, int flank, int belly, int maleDisplay) {
        this.eyes = eyes;
        this.markings = markings;
        this.body = body;
        this.flank = flank;
        this.belly = belly;
        this.maleDisplay = maleDisplay;
    }

    public CompoundTag saveToNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Eyes", eyes);
        tag.putInt("Markings", markings);
        tag.putInt("Body", body);
        tag.putInt("Flank", flank);
        tag.putInt("Belly", belly);
        tag.putInt("MaleDisplay", maleDisplay);

        return tag;
    }

    public static ColorHolder loadFromNBT(CompoundTag tag) {
        int eyes = tag.getInt("Eyes");
        int markings = tag.getInt("Markings");
        int body = tag.getInt("Body");
        int flank = tag.getInt("Flank");
        int belly = tag.getInt("Belly");
        int maleDisplay = tag.getInt("MaleDisplay");

        return new ColorHolder(eyes, markings, body, flank, belly, maleDisplay);
    }

    public static ColorHolder fromSkinData(SkinData data) {
        return new ColorHolder(
                data.getColor(ColorableBodyParts.EYES),
                data.getColor(ColorableBodyParts.MARKINGS),
                data.getColor(ColorableBodyParts.BODY),
                data.getColor(ColorableBodyParts.BODY),
                data.getColor(ColorableBodyParts.BELLY),
                data.getColor(ColorableBodyParts.MALE_DISPLAY)
        );
    }
}
