package by.sakeplays.cycle_of_life.network.to_server;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.Position;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncHitboxes(double headX, double headY, double headZ,
                           double body1X, double body1Y, double body1Z,
                           double body2X, double body2Y, double body2Z,
                           double tail1X, double tail1Y, double tail1Z,
                           double tail2X, double tail2Y, double tail2Z) implements CustomPacketPayload {

    public static final Type<SyncHitboxes> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID, "sync_head_pos"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleServer(final SyncHitboxes packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            player.getData(DataAttachments.HITBOX_DATA).setHeadHitboxPos(new Position(packet.headX(), packet.headY() ,packet.headZ()));
            player.getData(DataAttachments.HITBOX_DATA).setBody1Pos(new Position(packet.body1X(), packet.body1Y() ,packet.body1Z()));
            player.getData(DataAttachments.HITBOX_DATA).setBody2Pos(new Position(packet.body2X(), packet.body2Y() ,packet.body2Z()));
            player.getData(DataAttachments.HITBOX_DATA).setTail1Pos(new Position(packet.tail1X(), packet.tail1Y() ,packet.tail1Z()));
            player.getData(DataAttachments.HITBOX_DATA).setTail2Pos(new Position(packet.tail2X(), packet.tail2Y() ,packet.tail2Z()));
        });
    }

    public static final StreamCodec<FriendlyByteBuf, SyncHitboxes> STREAM_CODEC =
            new StreamCodec<FriendlyByteBuf, SyncHitboxes>() {
                @Override
                public SyncHitboxes decode(FriendlyByteBuf buf) {
                    return new SyncHitboxes(
                            buf.readDouble(), buf.readDouble(), buf.readDouble(),
                            buf.readDouble(), buf.readDouble(), buf.readDouble(),
                            buf.readDouble(), buf.readDouble(), buf.readDouble(),
                            buf.readDouble(), buf.readDouble(), buf.readDouble(),
                            buf.readDouble(), buf.readDouble(), buf.readDouble()
                    );
                }

                @Override
                public void encode(FriendlyByteBuf buf, SyncHitboxes val) {
                    buf.writeDouble(val.headX());
                    buf.writeDouble(val.headY());
                    buf.writeDouble(val.headZ());

                    buf.writeDouble(val.body1X());
                    buf.writeDouble(val.body1Y());
                    buf.writeDouble(val.body1Z());

                    buf.writeDouble(val.body2X());
                    buf.writeDouble(val.body2Y());
                    buf.writeDouble(val.body2Z());

                    buf.writeDouble(val.tail1X());
                    buf.writeDouble(val.tail1Y());
                    buf.writeDouble(val.tail1Z());

                    buf.writeDouble(val.tail2X());
                    buf.writeDouble(val.tail2Y());
                    buf.writeDouble(val.tail2Z());
                }
            };
}
