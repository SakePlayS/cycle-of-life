package by.sakeplays.cycle_of_life.common.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.*;

public class LifeData extends SavedData {



    private Map<UUID, UUID> LIFE_UUIDS = new HashMap<>();

    private static final Factory<LifeData> FACTORY =
            new Factory<>(LifeData::new, LifeData::load);

    public static LifeData get(MinecraftServer level) {
        return level.overworld().getDataStorage().computeIfAbsent(FACTORY, "life_data");
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        ListTag listTag = new ListTag();

        for (Map.Entry<UUID, UUID> entry : LIFE_UUIDS.entrySet()) {
            CompoundTag tag = new CompoundTag();

            tag.putUUID("Player", entry.getKey());
            tag.putUUID("LifeUUID", entry.getValue());

            listTag.add(tag);
        }

        compoundTag.put("LifeList", listTag);

        return compoundTag;
    }

    public static LifeData load(CompoundTag tag, HolderLookup.Provider provider) {
        LifeData data = new LifeData();

        ListTag lifeList = tag.getList("LifeList", Tag.TAG_COMPOUND);

        for (Tag t : lifeList) {
            CompoundTag lifeTag = (CompoundTag) t;
            UUID player = lifeTag.getUUID("Player");
            UUID lifeUuid = lifeTag.getUUID("LifeUUID");
            data.LIFE_UUIDS.put(player, lifeUuid);
        }

        return data;
    }

    public void updateFor(Player player) {
        LIFE_UUIDS.remove(player.getUUID());
        LIFE_UUIDS.put(player.getUUID(), player.getData(DataAttachments.PAIRING_DATA).getLifeUUID());

        setDirty();
    }

    public UUID getLifeOf(UUID playerUUID) {
        return LIFE_UUIDS.get(playerUUID);
    }
}
