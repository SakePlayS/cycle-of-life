package by.sakeplays.cycle_of_life.client.screen;


import by.sakeplays.cycle_of_life.Util;
import by.sakeplays.cycle_of_life.common.data.AdaptationData;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.DinoData;
import by.sakeplays.cycle_of_life.common.data.adaptations.Adaptation;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class StatsScreen extends Screen {



    private Button ADAPTATIONS;


    public StatsScreen(Component title) {
        super(title);
    }

    @Override
    public void tick() {
        super.tick();

    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        List<Pair<String, Integer>> stats = new ArrayList<>();



        Player player = Minecraft.getInstance().player;
        DinoData data = player.getData(DataAttachments.DINO_DATA);

        float growth = data.getGrowth();
        float maxSpeed = Util.getDino(player).getSprintSpeed();
        float adjustedSpeed = (float) Math.pow(growth, 1f/3f);
        maxSpeed = maxSpeed * (Mth.lerp(adjustedSpeed, 0.1f, 1f) * 20);

        stats.add(Pair.of("Weight: " + data.getWeight() + " kg", Util.rgbaToInt(150, 255, 255, 1)));
        stats.add(Pair.of("Growth: " + (int)(data.getGrowth() * 100) + "%", Util.rgbaToInt(150, 255, 255, 1)));
        if (data.isPaired()) stats.add(Pair.of("Paired with " + data.getPairingWith(), Util.rgbaToInt(150, 255, 255, 1)));
        stats.add(Pair.of("Max speed: " + maxSpeed + " b/s", Util.rgbaToInt(150, 255, 255, 1)));


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

        addRenderableWidget(ADAPTATIONS);


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
