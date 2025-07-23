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
    private Position grabHandlerPos;
    private int headId;
    private int body1Id;
    private int body2Id;
    private int tail1Id;
    private int tail2Id;

    public HitboxData (Position headHitboxPos, Position body1Pos, Position body2Pos, Position tail1Pos, Position tail2Pos, Position grabHandlerPos) {
        this.headHitboxPos = headHitboxPos;
        this.body1Pos = body1Pos;
        this.body2Pos = body2Pos;
        this.tail1Pos = tail1Pos;
        this.tail2Pos = tail2Pos;
        this.grabHandlerPos = grabHandlerPos;

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

    public int getHeadId() {
        return headId;
    }

    public void setHeadId(int headId) {
        this.headId = headId;
    }

    public int getBody1Id() {
        return body1Id;
    }

    public void setBody1Id(int body1Id) {
        this.body1Id = body1Id;
    }

    public int getBody2Id() {
        return body2Id;
    }

    public void setBody2Id(int body2Id) {
        this.body2Id = body2Id;
    }

    public int getTail1Id() {
        return tail1Id;
    }

    public void setTail1Id(int tail1Id) {
        this.tail1Id = tail1Id;
    }

    public int getTail2Id() {
        return tail2Id;
    }

    public void setTail2Id(int tail2Id) {
        this.tail2Id = tail2Id;
    }

    public Position getGrabHandlerPos() {
        return grabHandlerPos;
    }

    public void setGrabHandlerPos(Position grabHandlerPos) {
        this.grabHandlerPos = grabHandlerPos;
    }
}
