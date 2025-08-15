package by.sakeplays.cycle_of_life.common.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PairData implements INBTSerializable<CompoundTag> {

    private BlockPos nestPos;
    private UUID mateUUID;
    private UUID nestUUID;
    private boolean isPaired;
    private String mateName;
    private int storedEggs;

    // since the player's uuid persists, we need another uuid to
    // know whether the player is still living the same life.
    // if not, the pair is broken.
    // a random one is generated every time the player dies
    private UUID lifeUUID;
    private UUID mateLifeUUID;

    // keep track of this to prevent pairing
    // between members of the same family/bloodline.
    // each nest has 2 unique bloodline uuids
    // (patriarch's and matriarch's lifeUUIDs)
    // which are inherited by the babies.
    private List<UUID> bloodlineUUIDs = new ArrayList<>();


    public static final UUID UNSET = UUID.fromString("fc93a82d-6096-4510-ba96-5229fe5b6b56");


    public PairData() {
        mateUUID = UNSET;
        isPaired = false;
        lifeUUID = UUID.randomUUID();
        mateName = "";
        mateLifeUUID = UNSET;
        bloodlineUUIDs.add(lifeUUID);
        storedEggs = 0;
        nestUUID = UNSET;
        nestPos = BlockPos.ZERO;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();

        List<Integer> nestPos = new ArrayList<>();

        nestPos.add(this.nestPos.getX());
        nestPos.add(this.nestPos.getY());
        nestPos.add(this.nestPos.getZ());

        nbt.putUUID("PairingTarget", mateUUID);
        nbt.putBoolean("IsPaired", isPaired);
        nbt.putUUID("LifeUUID", lifeUUID);
        nbt.putUUID("MateLifeUUID", mateLifeUUID);
        nbt.putUUID("NestUUID", nestUUID);
        nbt.putString("MateName", mateName);
        nbt.putInt("StoredEggs", storedEggs);
        nbt.putIntArray("NestPos", nestPos);

        ListTag listTag = new ListTag();
        for (UUID uuid : bloodlineUUIDs) {
            listTag.add(NbtUtils.createUUID(uuid));
        }
        nbt.put("BloodlineUUIDs", listTag);

        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        this.mateUUID = nbt.getUUID("PairingTarget");
        this.isPaired = nbt.getBoolean("IsPaired");
        this.lifeUUID = nbt.getUUID("LifeUUID");
        this.mateLifeUUID = nbt.getUUID("MateLifeUUID");
        this.nestUUID = nbt.getUUID("NestUUID");
        this.mateName = nbt.getString("MateName");
        this.storedEggs = nbt.getInt("StoredEggs");
        int[] nestPos = nbt.getIntArray("NestPos");
        this.nestPos = new BlockPos(nestPos[0], nestPos[1], nestPos[2]);

        bloodlineUUIDs.clear();
        ListTag listTag = nbt.getList("BloodlineUUIDs", Tag.TAG_INT_ARRAY);
        for (Tag t : listTag) {
            bloodlineUUIDs.add(NbtUtils.loadUUID(t));
        }
    }

    public UUID getMateUUID() {
        return mateUUID;
    }

    public void setMateUUID(UUID uuid) {
        this.mateUUID = uuid;
    }

    public boolean isPaired() {
        return isPaired;
    }

    public void setPaired(boolean paired) {
        isPaired = paired;
    }

    public UUID getLifeUUID() {
        return lifeUUID;
    }

    public String getMateName() {
        return mateName;
    }

    public void setMateName(String mateName) {
        this.mateName = mateName;
    }

    public UUID getMateLifeUUID() {
        return mateLifeUUID;
    }

    public void setMateLifeUUID(UUID mateLifeUUID) {
        this.mateLifeUUID = mateLifeUUID;
    }

    public void addBloodlineEntry(UUID uuid) {
        if (!bloodlineUUIDs.contains(uuid)) bloodlineUUIDs.add(uuid);
    }

    public void reset(boolean fullReset) {
        mateUUID = UNSET;
        isPaired = false;
        if (fullReset) lifeUUID = UUID.randomUUID();
        mateName = "";
        mateLifeUUID = UNSET;
        if (fullReset) bloodlineUUIDs.clear();
        if (fullReset) bloodlineUUIDs.add(lifeUUID);
        if (fullReset) storedEggs = 0;
    }

    public int getStoredEggs() {
        return storedEggs;
    }

    public void setStoredEggs(int storedEggs) {
        this.storedEggs = storedEggs;
    }

    public List<UUID> getBloodlineUUIDs() {
        return new ArrayList<>(bloodlineUUIDs);
    }

    public UUID getNestUUID() {
        return nestUUID;
    }

    public void setNestUUID(UUID nestUUID) {
        this.nestUUID = nestUUID;
    }

    public BlockPos getNestPos() {
        return nestPos;
    }

    public void setNestPos(BlockPos nestPos) {
        this.nestPos = nestPos;
    }
}


