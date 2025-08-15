package by.sakeplays.cycle_of_life.network.to_server;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.Nest;
import by.sakeplays.cycle_of_life.common.data.NestData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record SyncNestPrivacy(String uuid, boolean isPublic) implements CustomPacketPayload {

    public static final Type<SyncNestPrivacy> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "sync_nest_privacy"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<FriendlyByteBuf, SyncNestPrivacy> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, SyncNestPrivacy::uuid,
            ByteBufCodecs.BOOL, SyncNestPrivacy::isPublic,
            SyncNestPrivacy::new
    );

    public static void handleServer(final SyncNestPrivacy packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            MinecraftServer server = context.player().getServer();

            Nest nest = NestData.get(server).getNestByPlayer(context.player());

            if (nest == null) {
                context.player().sendSystemMessage(Component.literal("Couldn't update your nest's privacy"));
                return;
            }

            NestData.get(server).setPublic(nest, packet.isPublic());
        });

    }
}
