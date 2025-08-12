package by.sakeplays.cycle_of_life.common.data;

import by.sakeplays.cycle_of_life.entity.util.Dinosaurs;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.UUID;

public class Nest {

    private final UUID MATRIARCH;
    private final UUID PATRIARCH;
    private final int MAX_EGGS_COUNT;
    private int eggsCount;
    private final BlockPos POSITION;
    private boolean isPublic;
    private final int type;

    public Nest(UUID matriarch, UUID patriarch, int maxEggsCount, BlockPos pos, boolean isPublic, int type) {
        this.MATRIARCH = matriarch;
        this.PATRIARCH = patriarch;
        this.MAX_EGGS_COUNT = maxEggsCount;
        this.eggsCount = 0;
        this.POSITION = pos;
        this.isPublic = isPublic;
        this.type = type;
    }

    public UUID getMatriarch() {
        return MATRIARCH;
    }

    public UUID getPatriarch() {
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

    protected void setEggsCount(int eggsCount) {
        this.eggsCount = eggsCount;
    }

    public boolean isPublic() {
        return isPublic;
    }

    protected void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public int getType() {
        return type;
    }

    public CompoundTag saveToNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("Matriarch", getMatriarch());
        tag.putUUID("Patriarch", getPatriarch());
        tag.putInt("MaxEggs", getMaxEggsCount());
        tag.putInt("Eggs", eggsCount);
        tag.putInt("Type", type);
        tag.putBoolean("Public", isPublic);
        tag.putLong("Pos", getPos().asLong());
        return tag;
    }

    public static Nest loadFromNBT(CompoundTag tag) {
        UUID matriarch = tag.getUUID("Matriarch");
        UUID patriarch = tag.getUUID("Patriarch");
        int maxEggs = tag.getInt("MaxEggs");
        int eggs = tag.getInt("Eggs");
        int type = tag.getInt("Type");
        boolean isPublic = tag.getBoolean("Public");
        BlockPos pos = BlockPos.of(tag.getLong("Pos"));

        Nest nest = new Nest(matriarch, patriarch, maxEggs, pos, isPublic, type);
        nest.setEggsCount(eggs);
        return nest;
    }


}
