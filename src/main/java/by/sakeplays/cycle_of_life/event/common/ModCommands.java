package by.sakeplays.cycle_of_life.event.common;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.DinoData;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncFullReset;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncGrowth;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncHealth;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncStamina;
import by.sakeplays.cycle_of_life.network.to_client.SyncBloodLevel;
import by.sakeplays.cycle_of_life.network.to_client.SyncWaterLevel;
import by.sakeplays.cycle_of_life.util.Util;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = CycleOfLife.MODID, bus = EventBusSubscriber.Bus.GAME)
public class ModCommands {


    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("cycle-of-life").requires(source -> source.hasPermission(2)) // only ops
                .then(Commands.literal("set")
                        .then(Commands.literal("growth")
                                        .then(Commands.argument("target", EntityArgument.player())
                                                .then(Commands.argument("value", FloatArgumentType.floatArg(0f, 1f))
                                                        .executes(ctx -> {
                                                            ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
                                                            float value = FloatArgumentType.getFloat(ctx, "value");
                                                            DinoData data = target.getData(DataAttachments.DINO_DATA);
                                                            data.setGrowth(value);
                                                            PacketDistributor.sendToAllPlayers(new SyncGrowth(value, target.getId()));
                                                            ctx.getSource().sendSuccess(() -> Component.literal("Set growth of " + target.getName().getString() + " to " + value), false);
                                                            return Command.SINGLE_SUCCESS;
                                                        })
                                                )
                                        )
                                )
                                .then(Commands.literal("water")
                                        .then(Commands.argument("target", EntityArgument.player())
                                                .then(Commands.argument("value", FloatArgumentType.floatArg(0f, 1f))
                                                        .executes(ctx -> {
                                                            ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
                                                            float value = FloatArgumentType.getFloat(ctx, "value");
                                                            DinoData data = target.getData(DataAttachments.DINO_DATA);
                                                            data.setWaterLevel(value);
                                                            PacketDistributor.sendToAllPlayers(new SyncWaterLevel(target.getId(), value));
                                                            ctx.getSource().sendSuccess(() -> Component.literal("Set water of " + target.getName().getString() + " to " + value), false);
                                                            return Command.SINGLE_SUCCESS;
                                                        })
                                                )
                                        )
                                )
                        .then(Commands.literal("stamina")
                                .then(Commands.argument("target", EntityArgument.player())
                                        .then(Commands.argument("value", FloatArgumentType.floatArg(0f, 1f))
                                                .executes(ctx -> {
                                                    ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
                                                    float value = FloatArgumentType.getFloat(ctx, "value");
                                                    float maxStam = Util.getStaminaUpgraded(target);
                                                    float stam = maxStam * value;

                                                    DinoData data = target.getData(DataAttachments.DINO_DATA);
                                                    data.setStamina(stam);
                                                    PacketDistributor.sendToAllPlayers(new SyncStamina(target.getId(), stam));
                                                    ctx.getSource().sendSuccess(() -> Component.literal("Set stamina of " + target.getName().getString()
                                                            + " to " + stam + " (" + value * 100 + "%)"), false);
                                                    return Command.SINGLE_SUCCESS;
                                                })
                                        )
                                )
                        )
                        .then(Commands.literal("health")
                                .then(Commands.argument("target", EntityArgument.player())
                                        .then(Commands.argument("value", FloatArgumentType.floatArg(0f, 1f))
                                                .executes(ctx -> {
                                                    ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
                                                    float value = FloatArgumentType.getFloat(ctx, "value");
                                                    float maxHealth = target.getData(DataAttachments.DINO_DATA).getWeight();
                                                    float health = maxHealth * value;

                                                    DinoData data = target.getData(DataAttachments.DINO_DATA);
                                                    data.setHealth(health);
                                                    PacketDistributor.sendToAllPlayers(new SyncHealth(target.getId(), health));
                                                    ctx.getSource().sendSuccess(() -> Component.literal("Set health of " + target.getName().getString()
                                                            + " to " + health + " (" + value * 100 + "%)"), false);
                                                    return Command.SINGLE_SUCCESS;
                                                })
                                        )
                                )
                        )
                        .then(Commands.literal("blood")
                                .then(Commands.argument("target", EntityArgument.player())
                                        .then(Commands.argument("value", FloatArgumentType.floatArg(0f, 1f))
                                                .executes(ctx -> {
                                                    ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
                                                    float value = FloatArgumentType.getFloat(ctx, "value");
                                                    float maxBlood = target.getData(DataAttachments.DINO_DATA).getWeight();
                                                    float blood = maxBlood * value;

                                                    DinoData data = target.getData(DataAttachments.DINO_DATA);
                                                    data.setBloodLevel(blood);
                                                    PacketDistributor.sendToAllPlayers(new SyncBloodLevel(target.getId(), blood));
                                                    ctx.getSource().sendSuccess(() -> Component.literal("Set blood of " + target.getName().getString()
                                                            + " to " + blood + " (" + value * 100 + "%)"), false);
                                                    return Command.SINGLE_SUCCESS;
                                                })
                                        )
                                )
                        )
                )
                .then(Commands.literal("reset")
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(ctx -> {
                                    ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
                                    DinoData data = target.getData(DataAttachments.DINO_DATA);
                                    data.fullReset();
                                    PacketDistributor.sendToAllPlayers(new SyncFullReset(target.getId()));
                                    ctx.getSource().sendSuccess(() -> Component.literal("Reset dino data for " + target.getName().getString()), false);
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
        );
    }
}
