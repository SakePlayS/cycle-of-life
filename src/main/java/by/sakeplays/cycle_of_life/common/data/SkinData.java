package by.sakeplays.cycle_of_life.common.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

public class SkinData implements INBTSerializable<CompoundTag> {


    private int eyesColor;
    private int maleDisplayColor;
    private int bodyColor;
    private int flankColor;
    private int bellyColor;
    private int markingsColor;

    public SkinData() {
        eyesColor = 0;
        maleDisplayColor = 0;
        bodyColor = 0;
        flankColor = 0;
        bellyColor = 0;
        markingsColor = 0;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("EyesColor", eyesColor);
        nbt.putInt("BellyColor", bellyColor);
        nbt.putInt("MarkingsColor", markingsColor);
        nbt.putInt("MaleDisplayColor", maleDisplayColor);
        nbt.putInt("BodyColor", bodyColor);
        nbt.putInt("FlankColor", flankColor);


        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        this.eyesColor = nbt.getInt("EyesColor");
        this.bellyColor = nbt.getInt("BellyColor");
        this.markingsColor = nbt.getInt("MarkingsColor");
        this.maleDisplayColor = nbt.getInt("MaleDisplayColor");
        this.bodyColor = nbt.getInt("BodyColor");
        this.flankColor = nbt.getInt("FlankColor");

    }

    public int getEyesColor() {
        return eyesColor;
    }

    public void setEyesColor(int eyesColor) {
        this.eyesColor = eyesColor;
    }

    public int getMaleDisplayColor() {
        return maleDisplayColor;
    }

    public void setMaleDisplayColor(int maleDisplayColor) {
        this.maleDisplayColor = maleDisplayColor;
    }

    public int getBodyColor() {
        return bodyColor;
    }

    public void setBodyColor(int bodyColor) {
        this.bodyColor = bodyColor;
    }

    public int getFlankColor() {
        return flankColor;
    }

    public void setFlankColor(int flankColor) {
        this.flankColor = flankColor;
    }

    public int getBellyColor() {
        return bellyColor;
    }

    public void setBellyColor(int bellyColor) {
        this.bellyColor = bellyColor;
    }

    public int getMarkingsColor() {
        return markingsColor;
    }

    public void setMarkingsColor(int markingsColor) {
        this.markingsColor = markingsColor;
    }
}


