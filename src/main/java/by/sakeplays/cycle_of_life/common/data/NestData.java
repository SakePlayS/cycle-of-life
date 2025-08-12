package by.sakeplays.cycle_of_life.common.data;

import by.sakeplays.cycle_of_life.CycleOfLife;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.*;

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


    // The nest's UUID is always equal to the patriarch's UUID
    public Nest getNestByID(UUID id) {
        for (Nest nest : NESTS) {
            if (nest.getPatriarch().equals(id)) return nest;
        }

        return null;
    }

    public Nest getNestByPlayer(Player player) {

        if (!player.getData(DataAttachments.PAIRING_DATA).isPaired()) return null;

        UUID patriarch = player.getData(DataAttachments.DINO_DATA).isMale() ? player.getUUID()
                : player.getData(DataAttachments.PAIRING_DATA).getMateUUID();

        for (Nest nest : NESTS) {
            if (nest.getPatriarch().equals(patriarch)) return nest;
        }

        return null;
    }

    public void removeNestByID(UUID id) {
        int i = 0;

        for (Nest nest : NESTS) {
            if (nest.getPatriarch().equals(id)) {
                NESTS.remove(i);
                setDirty();
                break;
            }

            i++;
        }
    }

    public void addNest(Nest nest) {

        if (containsNestID(nest.getPatriarch())) {
            CycleOfLife.LOGGER.warn("Couldn't create a nest: a nest with such ID ({}) is already present. Skipping.", nest.getPatriarch());
            return;
        }

        NESTS.add(nest);
        setDirty();
    }

    public void addNest(UUID matriarchID, UUID patriarchID, int maxEggsCount, BlockPos pos, boolean isPublic, int type) {

        if (containsNestID(patriarchID)) {
            CycleOfLife.LOGGER.warn("Couldn't create a nest: a nest with such ID ({}) is already present. Skipping.", patriarchID);
            return;
        }

        NESTS.add(new Nest(matriarchID, patriarchID, maxEggsCount, pos, isPublic, type));
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

    public void removeEgg(Nest nest) {

        if (nest.getEggsCount() <= 0) return;

        nest.setEggsCount(nest.getEggsCount() - 1);
        setDirty();
    }

    public void addEgg(Nest nest, ServerPlayer player) {

        PairData data = player.getData(DataAttachments.PAIRING_DATA);

        if (data.getStoredEggs() <= 0 || nest.getEggsCount() >= nest.getMaxEggsCount()) return;

        data.setStoredEggs(data.getStoredEggs() - 1);
        nest.setEggsCount(nest.getEggsCount() + 1);
        setDirty();
    }

    public void setPublic(Nest nest, boolean val) {
        nest.setPublic(val);
        setDirty();
    }

    public boolean containsNestID(UUID id) {
        return getNestByID(id) != null;
    }
}
