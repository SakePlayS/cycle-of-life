package by.sakeplays.cycle_of_life.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class ModCodecs {

    public static final <E extends Enum<E>> StreamCodec<FriendlyByteBuf, E> enumCodec(Class<E> enumClass) {
        return StreamCodec.of(
                (buf, value) -> buf.writeVarInt(value.ordinal()),
                buf -> {
                    int ordinal = buf.readVarInt();
                    E[] constants = enumClass.getEnumConstants();
                    if (ordinal < 0 || ordinal >= constants.length) {
                        throw new IllegalArgumentException("Invalid ordinal " + ordinal + " for enum " + enumClass.getSimpleName());
                    }
                    return constants[ordinal];
                }
        );
    }
}
