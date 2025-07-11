package by.sakeplays.cycle_of_life.common.data;

import by.sakeplays.cycle_of_life.CycleOfLife;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.List;

public class NestData extends SavedData {



    private List<Nest> NESTS = new ArrayList<>();

    private static final SavedData.Factory<NestData> FACTORY =
            new SavedData.Factory<>(NestData::new, NestData::load);

    public static NestData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(FACTORY, "nest_data");
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        ListTag nestsList = new ListTag();

        for (Nest nest : NESTS) {
            nestsList.add(nest.saveToNBT());
        }

        compoundTag.put("Nests", nestsList);
        return compoundTag;
    }


    // The nest's ID is always equal to the patriarch's ID
    public Nest getNestByID(int id) {
        for (Nest nest : NESTS) {
            if (nest.getPatriarch() == id) return nest;
        }

        return null;
    }

    public void removeNestByID(int id) {
        int i = 0;

        for (Nest nest : NESTS) {
            if (nest.getPatriarch() == id) {
                NESTS.remove(i);
                setDirty();
                break;
            }

            i++;
        }
    }

    public void addNest(int matriarchID, int patriarchID, int maxEggsCount, BlockPos pos, boolean isPublic) {

        if (containsNestID(patriarchID)) {
            CycleOfLife.LOGGER.warn("Couldn't create a nest: a nest with such ID ({}) is already present. Skipping.", patriarchID);
            return;
        }

        NESTS.add(new Nest(matriarchID, patriarchID, maxEggsCount, pos, isPublic));
        setDirty();
    }

    public static NestData load(CompoundTag tag, HolderLookup.Provider provider) {
        NestData data = new NestData();

        ListTag nestsList = tag.getList("Nests", Tag.TAG_COMPOUND);
        for (Tag t : nestsList) {
            CompoundTag nestTag = (CompoundTag) t;
            Nest nest = Nest.loadFromNBT(nestTag);
            data.NESTS.add(nest);
        }

        return data;
    }

    public List<Nest> getNests() {
        return new ArrayList<>(NESTS);
    }

    public boolean containsNestID(int id) {
        return getNestByID(id) != null;
    }

}
