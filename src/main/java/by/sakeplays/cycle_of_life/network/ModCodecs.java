package by.sakeplays.cycle_of_life.network;

import by.sakeplays.cycle_of_life.common.data.Nest;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.UUID;

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


    public static final StreamCodec<FriendlyByteBuf, UUID> UUID_CODEC =
            StreamCodec.composite(
                    ModCodecs.LONG, UUID::getMostSignificantBits,
                    ModCodecs.LONG, UUID::getLeastSignificantBits,
                    UUID::new
            );



    public static final StreamCodec<FriendlyByteBuf, Long> LONG =
            StreamCodec.of(
                    FriendlyByteBuf::writeLong,
                    FriendlyByteBuf::readLong
            );
}
