package by.sakeplays.cycle_of_life.common.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

public class HitboxData implements INBTSerializable<CompoundTag> {

    private Position headHitboxPos;
    private Position body1Pos;
    private Position body2Pos;
    private Position tail1Pos;
    private Position tail2Pos;

    public HitboxData (Position headHitboxPos, Position body1Pos, Position body2Pos, Position tail1Pos, Position tail2Pos) {
        this.headHitboxPos = headHitboxPos;
        this.body1Pos = body1Pos;
        this.body2Pos = body2Pos;
        this.tail1Pos = tail1Pos;
        this.tail2Pos = tail2Pos;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        return null;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag compoundTag) {

    }


    public void setHeadHitboxPos(Position headHitboxPos) {
        this.headHitboxPos = headHitboxPos;
    }

    public Position getHeadHitboxPos() {
        return headHitboxPos;
    }

    public Position getBody1Pos() {
        return body1Pos;
    }

    public Position getTail2Pos() {
        return tail2Pos;
    }

    public Position getTail1Pos() {
        return tail1Pos;
    }

    public Position getBody2Pos() {
        return body2Pos;
    }

    public void setBody1Pos(Position body1Pos) {
        this.body1Pos = body1Pos;
    }

    public void setBody2Pos(Position body2Pos) {
        this.body2Pos = body2Pos;
    }

    public void setTail1Pos(Position tail1Pos) {
        this.tail1Pos = tail1Pos;
    }

    public void setTail2Pos(Position tail2Pos) {
        this.tail2Pos = tail2Pos;
    }
}
