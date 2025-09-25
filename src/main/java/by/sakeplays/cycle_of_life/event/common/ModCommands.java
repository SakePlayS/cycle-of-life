package by.sakeplays.cycle_of_life.event.common;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.DinoData;
import by.sakeplays.cycle_of_life.common.data.PairData;
import by.sakeplays.cycle_of_life.network.bidirectional.*;
import by.sakeplays.cycle_of_life.network.to_client.SyncBloodLevel;
import by.sakeplays.cycle_of_life.network.to_client.SyncGestationCountdown;
import by.sakeplays.cycle_of_life.network.to_client.SyncIsMale;
import by.sakeplays.cycle_of_life.network.to_client.SyncWaterLevel;
import by.sakeplays.cycle_of_life.util.Util;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import oshi.util.tuples.Pair;

@EventBusSubscriber(modid = CycleOfLife.MODID, bus = EventBusSubscriber.Bus.GAME)
public class ModCommands {


    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("cycle-of-life").requires(source -> source.hasPermission(2))
                .then(Commands.literal("set")
                        .then(Commands.literal("growth")
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .then(Commands.argument("value", FloatArgumentType.floatArg(0f, 1f))
                                                        .executes(ctx -> {
                                                            ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
                                                            float value = FloatArgumentType.getFloat(ctx, "value");
                                                            DinoData data = target.getData(DataAttachments.DINO_DATA);
                                                            data.setGrowth(value);
                                                            PacketDistributor.sendToAllPlayers(new SyncGrowth(value, target.getId()));
                                                            ctx.getSource().sendSuccess(() -> Component.literal("Set growth of "
                                                                    + target.getName().getString() + " to " + value), true);
                                                            return Command.SINGLE_SUCCESS;
                                                        })
                                                )
                                        )
                                )
                                .then(Commands.literal("water")
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .then(Commands.argument("value", FloatArgumentType.floatArg(0f, 1f))
                                                        .executes(ctx -> {
                                                            ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
                                                            float value = FloatArgumentType.getFloat(ctx, "value");
                                                            DinoData data = target.getData(DataAttachments.DINO_DATA);
                                                            data.setWaterLevel(value);
                                                            PacketDistributor.sendToAllPlayers(new SyncWaterLevel(target.getId(), value));
                                                            ctx.getSource().sendSuccess(() -> Component.literal("Set water of " +
                                                                    target.getName().getString() + " to " + value), true);
                                                            return Command.SINGLE_SUCCESS;
                                                        })
                                                )
                                        )
                                )
                        .then(Commands.literal("stamina")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("value", FloatArgumentType.floatArg(0f, 1f))
                                                .executes(ctx -> {
                                                    ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
                                                    float value = FloatArgumentType.getFloat(ctx, "value");
                                                    float maxStam = Util.getStaminaUpgraded(target);
                                                    float stam = maxStam * value;

                                                    DinoData data = target.getData(DataAttachments.DINO_DATA);
                                                    data.setStamina(stam);
                                                    PacketDistributor.sendToAllPlayers(new SyncStamina(target.getId(), stam));
                                                    ctx.getSource().sendSuccess(() -> Component.literal("Set stamina of " + target.getName().getString()
                                                            + " to " + stam + " (" + value * 100 + "%)"), true);
                                                    return Command.SINGLE_SUCCESS;
                                                })
                                        )
                                )
                        )
                        .then(Commands.literal("gestation_countdown")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("value", IntegerArgumentType.integer(0, 5400))
                                                .executes(ctx -> {
                                                    ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
                                                    int value = IntegerArgumentType.getInteger(ctx, "value");


                                                    PairData data = target.getData(DataAttachments.PAIRING_DATA);
                                                    data.setGestationCountdown(value);
                                                    PacketDistributor.sendToPlayer(target, new SyncGestationCountdown(value, target.getId()));
                                                    ctx.getSource().sendSuccess(() -> Component.literal("Set gestation countdown of " + target.getName().getString()
                                                            + " to " + value), true);
                                                    return Command.SINGLE_SUCCESS;
                                                })
                                        )
                                )
                        )
                        .then(Commands.literal("health")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("value", FloatArgumentType.floatArg(0f, 1f))
                                                .executes(ctx -> {
                                                    ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
                                                    float value = FloatArgumentType.getFloat(ctx, "value");
                                                    float maxHealth = target.getData(DataAttachments.DINO_DATA).getWeight();
                                                    float health = maxHealth * value;

                                                    DinoData data = target.getData(DataAttachments.DINO_DATA);
                                                    data.setHealth(health);
                                                    PacketDistributor.sendToAllPlayers(new SyncHealth(target.getId(), health));
                                                    ctx.getSource().sendSuccess(() -> Component.literal("Set health of " + target.getName().getString()
                                                            + " to " + health + " (" + value * 100 + "%)"), true);
                                                    return Command.SINGLE_SUCCESS;
                                                })
                                        )
                                )
                        )
                        .then(Commands.literal("blood")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("value", FloatArgumentType.floatArg(0f, 1f))
                                                .executes(ctx -> {
                                                    ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
                                                    float value = FloatArgumentType.getFloat(ctx, "value");
                                                    float maxBlood = target.getData(DataAttachments.DINO_DATA).getWeight();
                                                    float blood = maxBlood * value;

                                                    DinoData data = target.getData(DataAttachments.DINO_DATA);
                                                    data.setBloodLevel(blood);
                                                    PacketDistributor.sendToAllPlayers(new SyncBloodLevel(target.getId(), blood));
                                                    ctx.getSource().sendSuccess(() -> Component.literal("Set blood of " + target.getName().getString()
                                                            + " to " + blood + " (" + value * 100 + "%)"), true);
                                                    PacketDistributor.sendToAllPlayers(new SyncIsMale(target.getId(), data.isMale()));


                                                    return Command.SINGLE_SUCCESS;
                                                })
                                        )
                                )
                        )
                        .then(Commands.literal("gender")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.literal("male")
                                                .executes(ctx -> {
                                                    ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
                                                    DinoData data = target.getData(DataAttachments.DINO_DATA);

                                                    data.setMale(true);
                                                    ctx.getSource().sendSuccess(() -> Component.literal("Set gender of " + target.getName().getString()
                                                            + "to male"), true);
                                                    PacketDistributor.sendToAllPlayers(new SyncIsMale(target.getId(), data.isMale()));

                                                    return Command.SINGLE_SUCCESS;
                                                })
                                        )
                                        .then(Commands.literal("female")
                                                .executes(ctx -> {
                                                    ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
                                                    DinoData data = target.getData(DataAttachments.DINO_DATA);

                                                    data.setMale(false);
                                                    ctx.getSource().sendSuccess(() -> Component.literal("Set gender of " + target.getName().getString()
                                                            + "to female"), true);

                                                    return Command.SINGLE_SUCCESS;
                                                })
                                        )
                                )
                        )
                )
                .then(Commands.literal("reset")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(ctx -> {
                                    ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
                                    DinoData data = target.getData(DataAttachments.DINO_DATA);
                                    PairData pairData = target.getData(DataAttachments.PAIRING_DATA);
                                    pairData.reset(true);
                                    data.fullReset();
                                    PacketDistributor.sendToAllPlayers(new SyncFullReset(target.getId()));
                                    PacketDistributor.sendToAllPlayers(new SyncPairingReset(target.getId()));

                                    ctx.getSource().sendSuccess(() -> Component.literal("Reset dino data for " + target.getName().getString()), false);
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                ).then(Commands.literal("toggle-build-mode")
                        .executes(ctx -> {
                            Entity entity = ctx.getSource().getEntity();

                            if (entity instanceof ServerPlayer player) {
                                DinoData data = player.getData(DataAttachments.DINO_DATA);

                                data.setBuildMode(!data.isInBuildMode());

                                ctx.getSource().sendSuccess(() -> Component.literal("Set build mode to " + data.isInBuildMode()), true);

                                return Command.SINGLE_SUCCESS;
                            } else {

                                ctx.getSource().sendFailure(Component.literal("The entity must be a player"));
                                return 0;
                            }

                        })
                )
        );
    }
}
