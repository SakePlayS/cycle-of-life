package by.sakeplays.cycle_of_life.common.data;

import by.sakeplays.cycle_of_life.common.data.adaptations.AdaptationsHolder;
import by.sakeplays.cycle_of_life.entity.util.ColorableBodyParts;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.*;

public class Nest {

    private final UUID matriarch;
    private final UUID patriarch;
    private UUID patriarchLifeUUID = PairData.UNSET;
    private UUID matriarchLifeUUID = PairData.UNSET;
    private final int maxEggsCount;
    private int eggsCount;
    private final BlockPos position;
    private boolean isPublic;
    private final int type;
    private SelectedColors patriarchColors;
    private SelectedColors matriarchColors;
    private String matriarchName = "";
    private String patriarchName = "";
    private List<UUID> queuedPlayers = new ArrayList<>();

    private AdaptationsHolder patriarchAdaptations;
    private AdaptationsHolder matriarchAdaptations;

    public Nest(UUID matriarch, UUID patriarch, int maxEggsCount, BlockPos pos, boolean isPublic, int type,
                SelectedColors patriarchColors, SelectedColors matriarchColors) {
        this.matriarch = matriarch;
        this.patriarch = patriarch;
        this.maxEggsCount = maxEggsCount;
        this.eggsCount = 0;
        this.position = pos;
        this.isPublic = isPublic;
        this.type = type;
        this.patriarchColors = patriarchColors;
        this.matriarchColors = matriarchColors;
    }

    public Nest(String matriarch, String patriarch, int maxEggsCount, BlockPos pos, boolean isPublic, int type,
                SelectedColors patriarchColors, SelectedColors matriarchColors) {
        this.matriarch = UUID.fromString(matriarch);
        this.patriarch = UUID.fromString(patriarch);
        this.maxEggsCount = maxEggsCount;
        this.eggsCount = 0;
        this.position = pos;
        this.isPublic = isPublic;
        this.type = type;
        this.patriarchColors = patriarchColors;
        this.matriarchColors = matriarchColors;
    }

    public UUID getMatriarch() {
        return matriarch;
    }

    public String getMatriarchAsString() {
        return matriarch.toString();
    }

    public String getPatriarchAsString() {
        return patriarch.toString();
    }

    public UUID getPatriarch() {
        return patriarch;
    }

    public int getMaxEggsCount() {
        return maxEggsCount;
    }

    public BlockPos getPos() {
        return position;
    }

    public int getX() {
        return position.getX();
    }

    public int getY() {
        return position.getY();
    }

    public int getZ() {
        return position.getZ();
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

    public UUID getPatriarchLifeUUID() {
        return patriarchLifeUUID;
    }

    public void setPatriarchLifeUUID(UUID patriarchLifeUUID) {
        this.patriarchLifeUUID = patriarchLifeUUID;
    }

    public UUID getMatriarchLifeUUID() {
        return matriarchLifeUUID;
    }

    public void setMatriarchLifeUUID(UUID matriarchLifeUUID) {
        this.matriarchLifeUUID = matriarchLifeUUID;
    }

    public CompoundTag saveToNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("Matriarch", getMatriarch());
        tag.putUUID("Patriarch", getPatriarch());
        tag.putUUID("MatriarchLifeUUID", getMatriarchLifeUUID());
        tag.putUUID("PatriarchLifeUUID", getPatriarchLifeUUID());
        tag.putInt("MaxEggs", getMaxEggsCount());
        tag.putInt("Eggs", eggsCount);
        tag.putInt("Type", type);
        tag.putBoolean("Public", isPublic);
        tag.putLong("Pos", getPos().asLong());

        tag.putString("MatriarchName", matriarchName);
        tag.putString("PatriarchName", patriarchName);

        CompoundTag pColor = new CompoundTag();
        patriarchColors.toNBT(pColor);
        CompoundTag mColor = new CompoundTag();
        matriarchColors.toNBT(mColor);


        tag.put("PatriarchColors", pColor);
        tag.put("MatriarchColors", mColor);

        tag.put("PatriarchAdaptations", patriarchAdaptations.toNBT());
        tag.put("MatriarchAdaptations", matriarchAdaptations.toNBT());

        return tag;
    }

    public static Nest loadFromNBT(CompoundTag tag) {
        UUID matriarch = tag.getUUID("Matriarch");
        UUID patriarch = tag.getUUID("Patriarch");
        UUID matriarchLifeUUID = tag.getUUID("MatriarchLifeUUID");
        UUID patriarchLifeUUID = tag.getUUID("PatriarchLifeUUID");
        int maxEggs = tag.getInt("MaxEggs");
        int eggs = tag.getInt("Eggs");
        int type = tag.getInt("Type");
        boolean isPublic = tag.getBoolean("Public");
        BlockPos pos = BlockPos.of(tag.getLong("Pos"));
        SelectedColors patriarchColors = SelectedColors.fromNBT(tag.getCompound("PatriarchColors"));
        SelectedColors matriarchColors = SelectedColors.fromNBT(tag.getCompound("MatriarchColors"));


        Nest nest = new Nest(matriarch, patriarch, maxEggs, pos, isPublic, type, patriarchColors, matriarchColors);
        nest.setEggsCount(eggs);
        nest.setMatriarchLifeUUID(matriarchLifeUUID);
        nest.setPatriarchLifeUUID(patriarchLifeUUID);
        nest.setPatriarchName(tag.getString("PatriarchName"));
        nest.setMatriarchName(tag.getString("MatriarchName"));
        nest.setMatriarchAdaptations(AdaptationsHolder.fromNBT(tag.getCompound("MatriarchAdaptations")));
        nest.setPatriarchAdaptations(AdaptationsHolder.fromNBT(tag.getCompound("PatriarchAdaptations")));

        return nest;
    }

    public SelectedColors getPatriarchColors() {
        return patriarchColors;
    }

    public void setPatriarchColors(SelectedColors patriarchColors) {
        this.patriarchColors = patriarchColors.copy();
    }

    public SelectedColors getMatriarchColors() {
        return matriarchColors;
    }

    public void setMatriarchColors(SelectedColors matriarchColors) {
        this.matriarchColors = matriarchColors.copy();
    }

    public String getMatriarchName() {
        return matriarchName;
    }

    public void setMatriarchName(String matriarchName) {
        this.matriarchName = matriarchName;
    }

    public String getPatriarchName() {
        return patriarchName;
    }

    public void setPatriarchName(String patriarchName) {
        this.patriarchName = patriarchName;
    }

    public static final StreamCodec<FriendlyByteBuf, Nest> NEST_CODEC =
            StreamCodec.of(
                    (buf, nest) -> {
                        buf.writeUUID(nest.getMatriarch());
                        buf.writeUUID(nest.getPatriarch());
                        buf.writeInt(nest.getMaxEggsCount());
                        buf.writeInt(nest.getX());
                        buf.writeInt(nest.getY());
                        buf.writeInt(nest.getZ());
                        buf.writeBoolean(nest.isPublic());
                        buf.writeInt(nest.getType());

                        for (ColorableBodyParts part : ColorableBodyParts.values()) {
                            buf.writeInt(nest.getPatriarchColors().getColor(part).first());
                            buf.writeInt(nest.getPatriarchColors().getColor(part).second());

                        }

                        for (ColorableBodyParts part : ColorableBodyParts.values()) {
                            buf.writeInt(nest.getMatriarchColors().getColor(part).first());
                            buf.writeInt(nest.getMatriarchColors().getColor(part).second());

                        }

                        buf.writeInt(nest.getEggsCount());
                        buf.writeUtf(nest.getMatriarchName());
                        buf.writeUtf(nest.getPatriarchName());

                        List<UUID> queue = nest.getQueuedPlayers();
                        buf.writeInt(queue.size());
                        for (UUID uuid : queue) {
                            buf.writeUUID(uuid);
                        }

                    },
                    buf -> {


                        Nest nest = new Nest(
                                buf.readUUID(),
                                buf.readUUID(),
                                buf.readInt(),
                                new BlockPos(buf.readInt(), buf.readInt(), buf.readInt()),
                                buf.readBoolean(),
                                buf.readInt(),
                                new SelectedColors()
                                        .setColor(ColorableBodyParts.EYES, buf.readInt(), buf.readInt())
                                        .setColor(ColorableBodyParts.MARKINGS, buf.readInt(), buf.readInt())
                                        .setColor(ColorableBodyParts.BODY, buf.readInt(), buf.readInt())
                                        .setColor(ColorableBodyParts.BELLY, buf.readInt(), buf.readInt())
                                        .setColor(ColorableBodyParts.MALE_DISPLAY, buf.readInt(), buf.readInt()),

                                new SelectedColors()
                                        .setColor(ColorableBodyParts.EYES, buf.readInt(), buf.readInt())
                                        .setColor(ColorableBodyParts.MARKINGS, buf.readInt(), buf.readInt())
                                        .setColor(ColorableBodyParts.BODY, buf.readInt(), buf.readInt())
                                        .setColor(ColorableBodyParts.BELLY, buf.readInt(), buf.readInt())
                                        .setColor(ColorableBodyParts.MALE_DISPLAY, buf.readInt(), buf.readInt())
                        );

                        nest.setEggsCount(buf.readInt());
                        nest.setMatriarchName(buf.readUtf());
                        nest.setPatriarchName(buf.readUtf());

                        int size = buf.readInt();
                        List<UUID> queue = new ArrayList<>(size);

                        for (int i = 0; i < size; i++) {
                            queue.add(buf.readUUID());
                        }
                        nest.setQueuedPlayers(queue);

                        return nest;
                    }
            );

    public List<UUID> getQueuedPlayers() {
        return queuedPlayers;
    }

    public void setQueuedPlayers(List<UUID> queuedPlayers) {
        this.queuedPlayers = queuedPlayers;
    }

    public void addToQueue(UUID uuid) {
        this.queuedPlayers.add(uuid);
    }

    public void setPatriarchAdaptations(AdaptationsHolder holder) {
        patriarchAdaptations = holder;
    }

    public void setMatriarchAdaptations(AdaptationsHolder holder) {
        matriarchAdaptations = holder;
    }

    public AdaptationsHolder getPatriarchAdaptations() {
        return patriarchAdaptations;
    }

    public AdaptationsHolder getMatriarchAdaptations() {
        return matriarchAdaptations;
    }
}


