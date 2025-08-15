package by.sakeplays.cycle_of_life.client.screen;


import by.sakeplays.cycle_of_life.common.data.ClientNestData;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.DinoData;
import by.sakeplays.cycle_of_life.common.data.PairData;
import by.sakeplays.cycle_of_life.network.to_server.AcceptOrDeclineJoinRequest;
import by.sakeplays.cycle_of_life.network.to_server.SyncNestPrivacy;
import by.sakeplays.cycle_of_life.util.Util;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;


public class NestScreen extends Screen {

    Button acceptButton;
    Button declineButton;
    Button accessModeButton;
    Button uuidButton;
    private boolean isPublic = false;

    public NestScreen(Component title) {
        super(title);
    }

    @Override
    public void tick() {
        super.tick();

        if (ClientNestData.ownNest == null) return;

        accessModeButton.setMessage(Component.literal(isPublic ? "Public" : "Private"));
        uuidButton.setTooltip(Tooltip.create(Component.literal(
                Minecraft.getInstance().keyboardHandler.getClipboard().equals(ClientNestData.ownNest.getPatriarchAsString()) ?
                "Copied!" : "Click to copy")));

        updateQueueButtons();
    }

    private void updateQueueButtons() {
        acceptButton.active = !ClientNestData.ownNest.getQueuedPlayers().isEmpty();
        declineButton.active = !ClientNestData.ownNest.getQueuedPlayers().isEmpty();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        Player player = Minecraft.getInstance().player;
        DinoData dinoData = player.getData(DataAttachments.DINO_DATA);
        PairData pairData = player.getData(DataAttachments.PAIRING_DATA);

        List<Pair<String, Integer>> stats = new ArrayList<>();

        if (ClientNestData.ownNest == null) return;

        if (!dinoData.isMale()) {
            if (pairData.getStoredEggs() <= 0) {
                stats.add(Pair.of("Eggs: Gestating", Util.rgbaToInt(255, 255, 60, 1)));
            } else {
                stats.add(Pair.of("Eggs: Ready " + "(" + pairData.getStoredEggs() + "/" + Util.getDino(player).getMaxEggs() + ")"
                        , Util.rgbaToInt(60, 255, 60, 1)));
            }
        }

        stats.add(Pair.of("Coordinates: " + ClientNestData.ownNest.getPos().toString(), Util.rgbaToInt(150, 255, 200, 1)));
        stats.add(Pair.of("Eggs: " + ClientNestData.ownNest.getEggsCount(), Util.rgbaToInt(150, 255, 200, 1)));
        stats.add(Pair.of("Patriarch: " + ClientNestData.ownNest.getPatriarchName(), Util.rgbaToInt(150, 255, 200, 1)));
        stats.add(Pair.of("Matriarch: " + ClientNestData.ownNest.getMatriarchName(), Util.rgbaToInt(150, 255, 200, 1)));

        renderStats(guiGraphics, stats);

        if (!ClientNestData.ownNest.getQueuedPlayers().isEmpty()) {
            Player queuedPlayer = player.level().getPlayerByUUID(ClientNestData.ownNest.getQueuedPlayers().getFirst());

            if (queuedPlayer != null) {
                guiGraphics.drawString(font, queuedPlayer.getName().getString(), width/2 - 60, height - 25,
                        Util.rgbaToInt(255, 255, 255,1));
            }
        }
    }


    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void init() {
        super.init();

        accessModeButton = Button.builder(Component.literal("Private"), button -> {
            isPublic = !isPublic;
            PacketDistributor.sendToServer(new SyncNestPrivacy(ClientNestData.ownNest.getPatriarchAsString(), isPublic));
        }).tooltip(Tooltip.create(Component.literal("Privacy")))
                .pos(width/2 - 60, height - 45)
                .size(60, 15)
                .build();

        uuidButton = Button.builder(Component.literal("UUID"), button -> {
                Minecraft.getInstance().keyboardHandler.setClipboard(ClientNestData.ownNest.getPatriarchAsString());

                }).tooltip(Tooltip.create(Component.literal("Click to copy")))
                .pos(width/2, height - 45)
                .size(60, 15)
                .build();

        acceptButton = Button.builder(Component.literal("Accept"), button -> {
                    PacketDistributor.sendToServer(new AcceptOrDeclineJoinRequest(true));
                }).tooltip(Tooltip.create(Component.literal("Accept")))
                .pos(width/2 - 20, height - 25)
                .size(40, 15)
                .build();

        declineButton = Button.builder(Component.literal("Decline"), button -> {
                    PacketDistributor.sendToServer(new AcceptOrDeclineJoinRequest(false));
                }).tooltip(Tooltip.create(Component.literal("Decline")))
                .pos(width/2 + 20, height - 25)
                .size(40, 15)
                .build();


        addRenderableWidget(accessModeButton);
        addRenderableWidget(uuidButton);
        addRenderableWidget(acceptButton);
        addRenderableWidget(declineButton);
    }


    public void renderStats(GuiGraphics guiGraphics, List<Pair<String, Integer>> stats) {
        int y = height/2 - 120;

        for (Pair<String, Integer> s : stats) {
            guiGraphics.drawString(Minecraft.getInstance().font, s.first(), width/2 - 60, y, s.second());
            y += Minecraft.getInstance().font.lineHeight;
        }
    }
}
