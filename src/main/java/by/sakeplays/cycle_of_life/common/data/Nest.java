package by.sakeplays.cycle_of_life.common.data;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

public class Nest {

    private final int MATRIARCH;
    private final int PATRIARCH;
    private final int MAX_EGGS_COUNT;
    private int eggsCount;
    private final BlockPos POSITION;
    private boolean isPublic;

    public Nest(int matriarch, int patriarch, int maxEggsCount, BlockPos pos, boolean isPublic) {
        this.MATRIARCH = matriarch;
        this.PATRIARCH = patriarch;
        this.MAX_EGGS_COUNT = maxEggsCount;
        this.eggsCount = 0;
        this.POSITION = pos;
        this.isPublic = isPublic;
    }

    public int getMatriarch() {
        return MATRIARCH;
    }

    public int getPatriarch() {
        return PATRIARCH;
    }

    public int getMaxEggsCount() {
        return MAX_EGGS_COUNT;
    }

    public BlockPos getPos() {
        return POSITION;
    }

    public int getEggsCount() {
        return eggsCount;
    }

    public void setEggsCount(int eggsCount) {
        this.eggsCount = eggsCount;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public CompoundTag saveToNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Matriarch", getMatriarch());
        tag.putInt("Patriarch", getPatriarch());
        tag.putInt("MaxEggs", getMaxEggsCount());
        tag.putInt("Eggs", eggsCount);
        tag.putBoolean("Public", isPublic);
        tag.putLong("Pos", getPos().asLong());
        return tag;
    }

    public static Nest loadFromNBT(CompoundTag tag) {
        int matriarch = tag.getInt("Matriarch");
        int patriarch = tag.getInt("Patriarch");
        int maxEggs = tag.getInt("MaxEggs");
        int eggs = tag.getInt("Eggs");
        boolean isPublic = tag.getBoolean("Public");
        BlockPos pos = BlockPos.of(tag.getLong("Pos"));

        Nest nest = new Nest(matriarch, patriarch, maxEggs, pos, isPublic);
        nest.setEggsCount(eggs);
        return nest;
    }


}
