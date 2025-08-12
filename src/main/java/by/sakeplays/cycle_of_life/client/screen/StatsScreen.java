package by.sakeplays.cycle_of_life.client.screen;


import by.sakeplays.cycle_of_life.common.data.PairData;
import by.sakeplays.cycle_of_life.util.Util;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.DinoData;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;


public class StatsScreen extends Screen {



    private Button ADAPTATIONS;
    private Button NEST;
    private Button STATUS;


    public StatsScreen(Component title) {
        super(title);
    }

    @Override
    public void tick() {
        super.tick();
        Player player = Minecraft.getInstance().player;
        DinoData dinoData = player.getData(DataAttachments.DINO_DATA);
        PairData pairData = player.getData(DataAttachments.PAIRING_DATA);

        NEST.active = (dinoData.getGrowth() > 0.999f) && (pairData.isPaired());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        List<Pair<String, Integer>> stats = new ArrayList<>();

        Player player = Minecraft.getInstance().player;

        DinoData data = player.getData(DataAttachments.DINO_DATA);
        PairData pairData = player.getData(DataAttachments.PAIRING_DATA);

        float maxSpeed = Util.calculateMaxSpeed(player);
        String mateName = pairData.getMateName();
        String onlineStatus = " (Online)";


        stats.add(Pair.of("Weight: " + data.getWeight() + " kg", Util.rgbaToInt(150, 255, 255, 1)));
        stats.add(Pair.of("Growth: " + (int)(data.getGrowth() * 100) + "%", Util.rgbaToInt(150, 255, 255, 1)));
        stats.add(Pair.of("Max speed: " + maxSpeed + " b/s", Util.rgbaToInt(150, 255, 255, 1)));

        if (pairData.isPaired()) {
            if (player.level().getPlayerByUUID(pairData.getMateUUID()) == null) onlineStatus = " (Offline)";
            stats.add(Pair.of("Paired with " + mateName + onlineStatus, Util.rgbaToInt(150, 255, 200, 1)));
        }


        renderStats(guiGraphics, stats);
    }


    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void init() {
        super.init();

        ADAPTATIONS = new Button.Builder(Component.literal("Adaptations"), button -> {
            Minecraft.getInstance().setScreen(new AdaptationsScreen(Component.literal("Adaptations")));
        }).size(120, 18).pos(width/2 - 60, height/2 + 120).build();

        NEST = new Button.Builder(Component.literal("Nest"), button -> {
            Minecraft.getInstance().setScreen(new NestScreen(Component.literal("Nest")));
        }).size(120, 18).pos(width/2 - 60, height/2 + 102).build();

        STATUS = new Button.Builder(Component.literal("Status"), button -> {
            Minecraft.getInstance().setScreen(new StatusScreen(Component.literal("Status")));
        }).size(120, 18).pos(width/2 - 60, height/2 + 84).build();

        addRenderableWidget(ADAPTATIONS);
        addRenderableWidget(NEST);
        addRenderableWidget(STATUS);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    public void renderStats(GuiGraphics guiGraphics, List<Pair<String, Integer>> stats) {
        int y = height/2 - 120;

        for (Pair<String, Integer> s : stats) {
            guiGraphics.drawString(Minecraft.getInstance().font, s.first(), width/2 - 60, y, s.second());
            y += Minecraft.getInstance().font.lineHeight;
        }
    }
}
