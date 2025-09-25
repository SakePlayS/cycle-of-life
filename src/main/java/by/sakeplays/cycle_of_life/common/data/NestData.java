package by.sakeplays.cycle_of_life.common.data;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.network.to_client.SyncOwnNest;
import by.sakeplays.cycle_of_life.network.to_client.SyncStoredEggs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;

public class NestData extends SavedData {

    private List<Nest> NESTS = new ArrayList<>();

    private static final SavedData.Factory<NestData> FACTORY =
            new SavedData.Factory<>(NestData::new, NestData::load);

    public static NestData get(MinecraftServer level) {
        return level.overworld().getDataStorage().computeIfAbsent(FACTORY, "nest_data");
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


    public Nest getNestByID(UUID id) {
        for (Nest nest : NESTS) {
            if (nest.getPatriarch().equals(id)) return nest;
        }

        return null;
    }

    public List<Nest> getAvailableOfType(int type, Level level) {
        List<Nest> list = new ArrayList<>(NESTS);
        list.removeIf(nest -> nest.getType() != type || nest.getEggsCount() < 1 ||
                nest.getQueuedPlayers().size() >= nest.getEggsCount() || !nest.isPublic() ||
                (level.getPlayerByUUID(nest.getMatriarch()) == null && level.getPlayerByUUID(nest.getPatriarch()) == null));
        return list;
    }

    public Nest getRandomAvailableOfType(int type, Level level) {
        List<Nest> list = getAvailableOfType(type, level);
        if (list.isEmpty()) {
            return null;
        }
        return list.get((int) (list.size() * Math.random()));
    }

    public Nest getNestByPlayer(Player player) {

        UUID lifeUUID = LifeData.get(player.level().getServer()).getLifeOf(player.getUUID());

        for (Nest nest : NESTS) {
            if (
                    nest.getPatriarch().equals(player.getUUID()) && nest.getPatriarchLifeUUID().equals(lifeUUID) ||
                    nest.getMatriarch().equals(player.getUUID()) && nest.getMatriarchLifeUUID().equals(lifeUUID)
            ) return nest;
        }

        return null;
    }

    public Nest getNestByBlockPos(BlockPos pos) {

        for (Nest nest : NESTS) {
            if (nest.getPos().equals(pos)) return nest;
        }

        return null;
    }


    public void addNest(Nest nest) {

        if (containsNestID(nest.getPatriarch())) {
            CycleOfLife.LOGGER.warn("Couldn't create a nest: a nest with such ID ({}) is already present. Skipping.", nest.getPatriarch());
            return;
        }

        NESTS.add(nest);
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
        PacketDistributor.sendToPlayer(player, new SyncStoredEggs(data.getStoredEggs(), player.getId()));
        setDirty();
    }

    public void forceSetEggs(Nest nest, int val) {

        nest.setEggsCount(val);
        setDirty();
    }

    public void setPublic(Nest nest, boolean val) {
        nest.setPublic(val);
        setDirty();
    }

    public void update(MinecraftServer server) {
        LifeData lifeData = LifeData.get(server);

        NESTS.removeIf(nest -> validateNest(nest, lifeData));

        for (Nest nest : NESTS) {
            List<UUID> oldQueue = new ArrayList<>(nest.getQueuedPlayers());
            nest.getQueuedPlayers().removeIf(uuid -> {
                ServerPlayer serverPlayer = server.getPlayerList().getPlayer(uuid);
                 return serverPlayer == null || serverPlayer.getData(DataAttachments.DINO_DATA).getSelectedDinosaur() != 0;
            });

            if (!oldQueue.equals(nest.getQueuedPlayers())) {
                ServerPlayer patriarch = server.getPlayerList().getPlayer(nest.getPatriarch());
                ServerPlayer matriarch = server.getPlayerList().getPlayer(nest.getMatriarch());

                if (patriarch != null) PacketDistributor.sendToPlayer(patriarch, new SyncOwnNest(nest));
                if (matriarch != null) PacketDistributor.sendToPlayer(matriarch, new SyncOwnNest(nest));
            }
        }
    }

    private boolean validateNest(Nest nest, LifeData lifeData) {

        if (lifeData == null) {

            CycleOfLife.LOGGER.warn("LifeData is null, skipping validation for nest {} ({}, {})",
                    nest.getPatriarch(), nest.getPatriarchName(), nest.getMatriarchName());
            return false;
        }

        if (lifeData.getLifeOf(nest.getPatriarch()) == null || lifeData.getLifeOf(nest.getMatriarch()) == null) {

            CycleOfLife.LOGGER.warn("One of the life UUIDs for nest {} ({}, {}) is null, skipping.",
                    nest.getPatriarch(), nest.getPatriarchName(), nest.getMatriarchName());
            return false;
        }

        if (!nest.getMatriarchLifeUUID().equals(lifeData.getLifeOf(nest.getMatriarch())) &&
                !nest.getPatriarchLifeUUID().equals(lifeData.getLifeOf(nest.getPatriarch()))) {

            CycleOfLife.LOGGER.info("Removing nest {} ({}, {}) as both matriarch and patriarch are dead.",
                    nest.getPatriarch().toString(), nest.getPatriarchName(), nest.getMatriarchName());
            setDirty();
            return true;
        }

        return false;
    }

    public ArrayList<Nest> getAllNests() {
        return new ArrayList<>(NESTS);
    }

    public boolean isPlayerQueued(Player player) {
        for (Nest nest : NESTS) {
            for (UUID uuid : nest.getQueuedPlayers()) {
                if (uuid.equals(player.getUUID())) return true;
            }
        }

        return false;
    }

    public boolean containsNestID(UUID id) {
        return getNestByID(id) != null;
    }
}
